from controller import Robot, GPS, Keyboard
import math
import numpy as np
from matplotlib import pyplot as plt
from scipy.signal import convolve2d
import sys
import cv2
import os
import hashlib
import json
import random
from ultralytics import YOLO
from ultralytics.yolo.v8.detect.predict import DetectionPredictor
import torch
from ikpy.chain import Chain
import matplotlib.pyplot
from mpl_toolkits.mplot3d import Axes3D
import traceback
import skimage
from scipy.ndimage import label, center_of_mass, binary_dilation

#Initialization
print("=== Initializing Grocery Shopper...")
#Consts
MAX_SPEED = 7.0  # [rad/s]
MAX_SPEED_MS = 0.633 # [m/s]
AXLE_LENGTH = 0.4044 # m
MOTOR_LEFT = 10
MOTOR_RIGHT = 11
N_PARTS = 12
LIDAR_ANGLE_BINS = 667
LIDAR_SENSOR_MAX_RANGE = 5.5 # Meters
LIDAR_ANGLE_RANGE = math.radians(240)

# create the Robot instance.
robot = Robot()


# get the time step of the current world.
timestep = int(robot.getBasicTimeStep())

# The Tiago robot has multiple motors, each identified by their names below
part_names = ("head_2_joint", "head_1_joint", "torso_lift_joint", "arm_1_joint",
              "arm_2_joint",  "arm_3_joint",  "arm_4_joint",      "arm_5_joint",
              "arm_6_joint",  "arm_7_joint",  "wheel_left_joint", "wheel_right_joint",
              "gripper_left_finger_joint","gripper_right_finger_joint")


#TELEOP CODE
keyboard = robot.getKeyboard()
keyboard.enable(timestep)

# All motors except the wheels are controlled by position control. The wheels
# are controlled by a velocity controller. We therefore set their position to infinite.
target_pos = (0.0, 0.0, 0.35, 0.07, 1.02, -3.16, 1.27, 1.32, 0.0, 1.41, 'inf', 'inf',0.045,0.045)

robot_parts={}
for i, part_name in enumerate(part_names):
    robot_parts[part_name]=robot.getDevice(part_name)
    robot_parts[part_name].setPosition(float(target_pos[i]))
    robot_parts[part_name].setVelocity(robot_parts[part_name].getMaxVelocity() / 2.0)

# Enable gripper encoders (position sensors)
left_gripper_enc=robot.getDevice("gripper_left_finger_joint_sensor")
right_gripper_enc=robot.getDevice("gripper_right_finger_joint_sensor")
left_gripper_enc.enable(timestep)
right_gripper_enc.enable(timestep)

# Enable Camera
camera = robot.getDevice('camera')
camera.enable(timestep)
#camera.recognitionEnable(timestep)
camera.recognitionDisable()
camera.disableRecognitionSegmentation()
camera_width = camera.getWidth()
camera_height = camera.getHeight()
fov = camera.getFov()
pixel_angle = fov/camera_width
robot.getDevice("head_1_joint_sensor").enable(timestep)

# Enable GPS and compass localization
gps = robot.getDevice("gps")
gps.enable(timestep)
compass = robot.getDevice("compass")
compass.enable(timestep)

# Enable LiDAR
lidar = robot.getDevice('Hokuyo URG-04LX-UG01')
lidar.enable(timestep)
lidar.enablePointCloud()

# Enable display
display = robot.getDevice("display")

# We are using a GPS and compass to disentangle mapping and localization
gps = robot.getDevice("gps")
gps.enable(timestep)

compass = robot.getDevice("compass")
compass.enable(timestep)


# Odometry
pose_x     = 0
pose_y     = 0
pose_theta = 0

vL = 0
vR = 0

lidar_sensor_readings = [] # List to hold sensor readings
lidar_offsets = np.linspace(-LIDAR_ANGLE_RANGE/2., +LIDAR_ANGLE_RANGE/2., LIDAR_ANGLE_BINS)
lidar_offsets = lidar_offsets[83:len(lidar_offsets)-83] # Only keep lidar readings not blocked by robot chassis

#-------------------------------------------VARIABLES----------------------------------------------
RotationMatrix = np.array([[0, 1], [1, 0]])

world_height = 30
world_width = 16.1
display_height = int(world_height*30)
display_width = int(world_width*30)


scale_factor = 30 
offset = (241.5, 450) #display deminsions / 2

gripper_status="closed"
display_reset = 0

#for training machine learning model.
training_img_number = 0
prev_objects = []
r = random.randint(0,9)
lastKey = -1

#for Inverse Kinematics
my_chain = Chain.from_urdf_file("tiago_urdf.urdf", 
    last_link_vector=[0.004, 0,-0.1741], 
    base_elements=["base_link", "base_link_Torso_joint", "Torso", "torso_lift_joint", "torso_lift_link", "torso_lift_link_TIAGo front arm_11367_joint", "TIAGo front arm_11367"],#)
    active_links_mask = [False, False, True, False, True, True,  True,  True, True, True, True, False, False, False,  False])
print("=== Created IKPY chain")

motors = []
arm_position = []
for link_id in range(len(my_chain.links)):
    link = my_chain.links[link_id]
    if my_chain.active_links_mask[link_id]:
        print("Initializing {}...".format(link.name))

        motor = robot.getDevice(link.name)
            
        position_sensor = motor.getPositionSensor()
        position_sensor.enable(timestep)
        motors.append(motor)
        arm_position.append(motor.getPositionSensor().getValue())
    else:
        arm_position.append(0)
        

map = np.zeros(shape=[display_height, display_width]) #[900, 483]
waypoints = [] 
waypointIndx = 0

#cubes = [[-3.2,2], [-2.77, 2], [.77, 2], [2.62, 2], [3.5, 2], [2.34, -2], [4.4, -2], [-1.33, -5.8], [3.3, 5.75], [5.4, 5.75], [-.865, 2]]
WORLD_WIDTH = 16.1
WORLD_HEIGHT = 30

#-------------------------------------------HELPER FUNCTIONS---------------------------------

# Converts world coords to display coords
def W2D(wx, wy):
    return (int(wx*30), int(wy*30))

#Converts lidar coords to world coords
def L2W(RotationMatrix, lidar_p, pose_r):
    return np.dot(np.concatenate((RotationMatrix, pose_r), axis=1), lidar_p)

#RRT
def rrt(map, start, goal, max_iter=5000, max_dist=200):
    """
    params:
        map = .npy file representing the map that rrt will build a path on
        start = starting point rrt will build from
        goal = goal to which rrt will build the path to
        max_iter = max iterations the algo will take to build a path
        max_dist = maximum distance between nodes
    returns: 
        path to the goal, and nodes taken to get there
    """
    
    nodes = [start]
    tree = {}
    tree[0] = []
    
    for i in range(max_iter):
        # Randomly sample a point
        if np.random.rand() < 0.05:
            sample = goal
            #print('goal:', sample)
        else:
            sample = np.random.randint(0, map.shape)
            #print('random:', sample)
        
        # Find the nearest node
        nearest_node = None
        min_dist = np.inf
        for j, node in enumerate(nodes):
            dist = np.linalg.norm(node - sample)
            if dist < min_dist:
                min_dist = dist
                nearest_node = node
                nearest_idx = j
        
        # Generate a new node in the direction of the sample
        distance = np.linalg.norm(sample - nearest_node)
        if distance > max_dist: 
            direction = (sample - nearest_node) / distance
            new_node = nearest_node + max_dist * direction
            new_node = new_node.astype(int)
        else:
            new_node = sample.astype(int)
        
        # Check if the new node collides with any obstacle
        if map[tuple(new_node)] == 1.0:
            continue
        
        # Check if the path between the nearest node and the new node collides with obstacles
        path = np.linspace(nearest_node, new_node, num=100, dtype=int)
        if np.any(map[tuple(path.T)] == 1.0):
            continue
        
        # Add the new node to the tree
        nodes.append(new_node)
        tree[len(nodes) - 1] = nearest_idx
        
        # Check if the new node is close enough to the goal
        if np.linalg.norm(new_node - goal) < 1e-5:
            nodes.append(goal)
            tree[len(nodes) - 1] = len(nodes) - 2
            break
    
    # Reconstruct the path
    path = [goal]
    current_node = len(nodes) - 1
    while current_node != 0:
        parent_node = tree[current_node]
        path.append(nodes[parent_node])
        current_node = parent_node
    path.append(start)
    path.reverse()
    
    return path, nodes
    

## Checks an RRT path for collisions in the map
# Retrun false in NO COLLISIONS 
def checkForCollisions(map, path):
    start = path[0]
    end = path[-1]
    x_vals = np.linspace(start[1], end[1], num=1000, dtype=int)
    y_vals = np.linspace(start[0], end[0], num=1000, dtype=int)
    for x, y in zip(x_vals, y_vals):
        if map[y, x] == 1:
            return True
    return False
    
##SMOOTHS THE PATH 
# Used psudocode from https://cs.brown.edu/courses/cs148/documents/asgn3_planning/btcohen/index.html
def pathSmoothing(path, map):
    newpath = []
    i = 0
    while i < len(path):
        newpath.append(path[i])
        j = len(path) - 1
        found = False
        while j != i and not found:
            if not checkForCollisions(map, [path[j], path[i]]):
                found = True
            else:
                j -= 1
        if found:
            i = j
        else:
            i += 1
    return newpath
    
    
# Converts numpy map coords to webots world coords
def map_to_world(point, scale_factor, offset):
    # point is a tuple containing the x and y coordinates in the map
    x_map, y_map = point
    
    # Translate the position by the offset
    x_map = x_map - offset[0]
    y_map = y_map - offset[1]
    
    x_map = x_map / scale_factor
    y_map = y_map / scale_factor
    
    return (y_map, x_map)


# Converts webots world coords to numpy map coords 
def world_to_map(point):
    # point is a tuple containing the x and y coordinates in the world
    x_world, y_world = point
    
    x_world = x_world + (world_height/2) # floor x is 30
    y_world =  (16.1/2) + y_world # floor y is 16.1
    
    x_map, y_map = W2D(x_world, y_world)
    
    return (y_map, x_map)


#______________________________________Computer Vision Model____________________________________________
# Load the existing object-class mapping from a file, if it exists
try:
    with open('object_class_mapping.json', 'r') as f:
        object_class_mapping = json.load(f)
except FileNotFoundError:
    object_class_mapping = {}

def get_class_id(object_name):
    # Check if the object already has a class ID assigned
    if object_name in object_class_mapping:
        return object_class_mapping[object_name]
    else:
        # Generate a new class ID for the object
        class_id = len(object_class_mapping)
        object_class_mapping[object_name] = class_id

        # Save the updated object-class mapping to a file
        with open('object_class_mapping.json', 'w') as f:
            json.dump(object_class_mapping, f)

        return class_id

# KYLE Helper Functions for finding objects TODO
def worldToDisplay(worldX, worldY):
    displayX = -(worldY * display_width/WORLD_WIDTH) + display_width/2
    displayY = -(worldX * display_height/WORLD_HEIGHT) + display_height/2
    return int(displayX), int(displayY)

torch.cuda.is_available()
#model = YOLO("smallModel.pt")
model = YOLO("yoloWeights.pt")

toggle_vision_counter = 0
################NEWWWWWWWWWWWWWWWWWW################################################################
def VisionPointsToWorld(map, waypoints):
    #find nearest free space by searching out from the x-axis iteratively 
    #takes in exact object location list
    #returns list of closest free space to each exact object location
    world_points = []
    for node in waypoints:
        count = 0
        while True:
            #iterate out along the x axis until a free space is found
            if map[node[0], node[1]+count] == 0:
                world_points.append([node[0], node[1]+count, -np.pi/2])
                break
            elif map[node[0], node[1]-count] == 0:
                world_points.append([node[0], node[1]-count, +np.pi/2])
                break
            else:
                count+=1
    #print(world_points)
    return world_points

robot_row = 0
display.setOpacity(1)
display.setColor(0xFFFFFF)
shelvesMasks = []
shelvesMask = np.zeros([display_width, display_height])
lowY, highY = (worldToDisplay(4+15/2, 0)[1], worldToDisplay(4-15/2, 0)[1])
lowX, highX = (worldToDisplay(0, 7.04+.55)[0], worldToDisplay(0, 7.04)[0])
#display.fillRectangle(lowX, lowY, highX-lowX, highY-lowY)
shelvesMask[lowX:highX, lowY:highY] = 1
lowX, highX = (worldToDisplay(0, 3.95+.55)[0], worldToDisplay(0, 3.95)[0])
#display.fillRectangle(lowX, lowY, highX-lowX, highY-lowY)
shelvesMask[lowX:highX, lowY:highY] = 1
shelvesMasks.append(shelvesMask)
shelvesMask = np.zeros([display_width, display_height])
lowX, highX = (worldToDisplay(0, 3.95)[0], worldToDisplay(0, 3.95-.55)[0])
#display.fillRectangle(lowX, lowY, highX-lowX, highY-lowY)
shelvesMask[lowX:highX, lowY:highY] = 1
lowX, highX = (worldToDisplay(0, 0+.55)[0], worldToDisplay(0, 0)[0])
#display.fillRectangle(lowX, lowY, highX-lowX, highY-lowY)
shelvesMask[lowX:highX, lowY:highY] = 1
shelvesMasks.append(shelvesMask)
shelvesMask = np.zeros([display_width, display_height])
lowX, highX = (worldToDisplay(0, 0)[0], worldToDisplay(0, 0-.55)[0])
#display.fillRectangle(lowX, lowY, highX-lowX, highY-lowY)
shelvesMask[lowX:highX, lowY:highY] = 1
lowX, highX = (worldToDisplay(0, -3.95+.55)[0], worldToDisplay(0, -3.95)[0])
#display.fillRectangle(lowX, lowY, highX-lowX, highY-lowY)
shelvesMask[lowX:highX, lowY:highY] = 1
shelvesMasks.append(shelvesMask)
shelvesMask = np.zeros([display_width, display_height])
lowX, highX = (worldToDisplay(0, -3.95)[0], worldToDisplay(0, -3.95-.55)[0])
#display.fillRectangle(lowX, lowY, highX-lowX, highY-lowY)
shelvesMask[lowX:highX, lowY:highY] = 1
lowX, highX = (worldToDisplay(0, -7.04+.55)[0], worldToDisplay(0, -7.04)[0])
#display.fillRectangle(lowX, lowY, highX-lowX, highY-lowY)
shelvesMask[lowX:highX, lowY:highY] = 1
shelvesMasks.append(shelvesMask)
#shelvesMask = np.zeros([display_width, display_height])
display.setColor(0xFFF000)
cubeLocation = worldToDisplay(-0.865, 0.361)
#display.fillRectangle(cubeLocation[0]-2, cubeLocation[1]-2, 5, 5)
cubeLocation = worldToDisplay(-3.26, 3.63)
#display.fillRectangle(cubeLocation[0]-2, cubeLocation[1]-2, 5, 5)
#shelvesMask[344:374, 338:800] = 1
#shelvesMask[13:30, 338:800] = 1
#shelvesMask[451:466, 338:800] = 1
objectMap = np.zeros([display_width, display_height])
possibleObjectsMap = np.zeros([display_width, display_height])
tmpObjectMap = np.zeros([display_width, display_height])
detection_timeStamp = 0
robotRelativeCameraPosition = -0.24
#------------------------------------------- END HELPER FUNCTIONS / VARIABLES----------------------------------

state = "mapping"
manual_drive_enabled = True
print(state)
cube_indx = 0
cubes = []
#cubes = [[344, 313], [364, 286], [410, 90], [419, 272], [476, 272], [521, 154], [525, 327], [552, 327], [553, 436], [581, 154], [619, 436]]


#_________________________________________________MAIN LOOP_____________________________________________________
#-----------KEY CONTROLS----------
# ARROW KEYS: control robot up down left right
# W, A, S, D: Control robot arm using IK in XY-axis
# E, Q: Control robot arm using IK in Z axis
# 0: First iteration will display map, pixel inflated map,
    # on ALL iterations will call RRT and display original path and SMOOTHED PATH
    # Upon X-ing out of the plot windows moves into autonomous path finding mode
# N: Saves the map to map.npy file and obstacles using COMPUTER VISION to other map file
# V: Enables/Disables Computer Vision
# O: Opens the robot gripper
# C: Closes the robot gripper
# L: Moves arm to intermediate state after grabbing object off of lower/middle shelf using IK
# U: Moves arm to intermediate state after grabbing object off of upper shelf using IK
# B: Moves arm to random point over basket for cube to be dropped in using IK
#
while robot.step(timestep) != -1:
    key = keyboard.getKey()
    if manual_drive_enabled:
        if lastKey == ord("V"):
            if key != ord("V"):
                if state == "testing ML model":
                    objectMap = np.add(objectMap, np.multiply(possibleObjectsMap, tmpObjectMap))
                    objectMap[objectMap > 1] = 1
                    
                    possibleObjectsMap = np.add(tmpObjectMap, possibleObjectsMap)
                    possibleObjectsMap[possibleObjectsMap > 1] = 1
                    
                    tmpObjectMap = np.zeros([display_width, display_height])
                    print("Vision Off")
                    state = "mapping"
                else:
                    state = "testing ML model"
                    print("Vision On")
        #robot driving code 
        
        while(keyboard.getKey() != -1): pass
        if key == keyboard.LEFT :
            vL -= 0.01*MAX_SPEED
            vR += 0.05*MAX_SPEED
        elif key == keyboard.RIGHT:
            vL += 0.05*MAX_SPEED
            vR -= 0.01*MAX_SPEED
        elif key == keyboard.UP:
            vL += 0.05*MAX_SPEED
            vR += 0.05*MAX_SPEED
        elif key == keyboard.DOWN:
            vL -= 0.05*MAX_SPEED
            vR -= 0.05*MAX_SPEED
        elif key == ord(' '):
            vL = 0
            vR = 0
        elif key == ord('0'):
            #HIT M to end mapping 
            
            manual_drive_enabled = False #disable manual driving

            map = np.load("map.npy")
            map = np.multiply(map > 0.8, 1)
            
            if(state == "mapping"):
                print("Done mapping")
                
                plt.imshow(map)
                plt.title("MAP SCAN")
                plt.show()
                
                # Create a new array to store the configuration space
                config_space = np.zeros(map.shape)
                # Define the size of the squares to be drawn
                square_size = 25
                
                # Iterate over every pixel in the map
                for x in range(map.shape[0]):
                    for y in range(map.shape[1]):
                        # Check if the current pixel contains an obstacle
                        if map[x, y] == 1:
                            # Draw a square centered on the current pixel
                            x_min = max(0, x - square_size)
                            x_max = min(map.shape[0] - 1, x + square_size)
                            y_min = max(0, y - square_size)
                            y_max = min(map.shape[1] - 1, y + square_size)
                            config_space[x_min:x_max, y_min:y_max] = 1
            
            # Display the configuration space
                plt.imshow(config_space)
                plt.imshow(config_space)
                plt.title("MAP WITH PIXEL INFLATION")
                plt.show()
                
                ## Load in object map using COMPUTER VISION TRAINED DATA
                objectMapImage = np.load("ObjectMap.npy")
                objectMapImage = np.rot90(objectMapImage)
                objectMapImage = np.flip(objectMapImage, axis = 1)
                
                maxVal = objectMapImage.max()
                objectMapImage /= maxVal
                selem = np.ones((10, 10))
                s = np.ones((3, 3))
                objectMapImage = binary_dilation(objectMapImage, structure=selem).astype(int)
                labeled_array, num_features = label(objectMapImage > 0, structure = s)
                
                centers = [center_of_mass(objectMapImage, labeled_array, i) for i in range(1, num_features+1)]
                centers = np.array(centers).astype(int)

                cubes = VisionPointsToWorld(config_space, centers)
                
                # Sort by x, then by y
                cubes = sorted(cubes, key=lambda p: (p[1], p[0])) 
                
                
                #plt.imshow(np.logical_or(config_space, objectMapImage))
                #Plot the config space with the cube objects
                plt.imshow(config_space)
                plt.plot([node[1] for node in cubes], [node[0] for node in cubes], 'o');
                plt.title("CONFIG SPACE WITH OBSTACLES USING ML COMPUTER VISION")
                plt.show() 
                
            map = config_space 

            state = "RRT"
            print(state)

        elif key == ord('N'): 
            #save the NumPy map
            np.save("map.npy", map)
            np.save("ObjectMap.npy", objectMap)
            print("Map files saved")
       
            
        else: # slow down
            vL *= 0.8
            vR *= 0.8  
        if vL > MAX_SPEED:
            vL = MAX_SPEED
        if vL < -MAX_SPEED:
            vL = -MAX_SPEED
        if vR > MAX_SPEED:
            vR = MAX_SPEED
        if vR < -MAX_SPEED:
            vR = -MAX_SPEED
            
        #####IK ARM CODE########
        arm_position = []
        for link_id in range(len(my_chain.links)):
            link = my_chain.links[link_id]
            if my_chain.active_links_mask[link_id]:
                arm_position.append(robot.getDevice(link.name).getPositionSensor().getValue())
            else:
                arm_position.append(0)
        #print(arm_position)
        currentPos = my_chain.forward_kinematics(arm_position)
        #print(currentPos)
        xyzPos = currentPos[:3, 3]
        targetXYZ = []
        targetXYZ.append(xyzPos[0])
        targetXYZ.append(xyzPos[1])
        targetXYZ.append(xyzPos[2])
        
        move_dist = 0.02
        if key == 87: #W
            targetXYZ[0] += move_dist
        elif key == 83:#S
            targetXYZ[0] -= move_dist
        elif key == 65:#A
            targetXYZ[1] += move_dist
        elif key == 68:#D
            targetXYZ[1] -= move_dist
        elif key == 81:#Q
            targetXYZ[2] += move_dist
        elif key == 69:#E
            targetXYZ[2] -= move_dist
        elif key == ord("O"):
            robot_parts["gripper_left_finger_joint"].setPosition(0.045)
            robot_parts["gripper_right_finger_joint"].setPosition(0.045)
        elif key == ord("C"):
            robot_parts["gripper_left_finger_joint"].setPosition(0.0)
            robot_parts["gripper_right_finger_joint"].setPosition(0.0) 
            
        ### BEN ARM PRESET POINTS KEYS ###         
        elif key == ord("R"):  #Arm Straight Right
            targetXYZ = [0.19450656380266082, -0.9869153199099168, 0.6097397645259419]   
            try:
                newJointAngle = my_chain.inverse_kinematics(targetXYZ, target_orientation=[0, 0, 1], orientation_mode="Y",initial_position=arm_position)
                for link_id in range(len(my_chain.links)):
                    link = my_chain.links[link_id]
                    if my_chain.active_links_mask[link_id]:
                        #print("Moving", link.name, "to angle", new_position[link_id])
                        motor = robot.getDevice(link.name)
                        motor.setPosition(newJointAngle[link_id])
            except ValueError:
                
                #traceback.print_exc()  # print the traceback to help diagnose the issue
                for i, part_name in enumerate(part_names):
                    robot_parts[part_name]=robot.getDevice(part_name)
                    robot_parts[part_name].setPosition(float(target_pos[i]))
                    robot_parts[part_name].setVelocity(robot_parts[part_name].getMaxVelocity() / 2.0)
                print("Failed to find a feasible solution to the inverse kinematics problem. Continuing with program execution...")
                temp = 0
                while temp<10000000: #sleep function
                    temp+=1
                # handle the error appropriately, e.g., continue with program execution or terminate the program
                    
        elif key == ord("B"):  #Arm to Basket
            targetXYZlist = [ [0.3066239811327348, 0.0367993610842611, 0.5865546354680058],
            [0.2407574496382033, 0.1624923456845687, 0.5827888874182954],
            [0.3406160049380295, 0.20248311162839872, 0.5794489217886588],
            [0.34093422421294756, -0.07600677382527057, 0.5757307489327979],
            [0.3809235161849158, -0.15592593780130595, 0.5741902388007801],
            [0.20613994823713605, -0.13045968713455713, 0.5721600475496811],
            [0.2726424226051963, -0.007971027105880185, 0.5677870924695848] ]
            targetXYZ = random.choice(targetXYZlist)
            try:
                newJointAngle = my_chain.inverse_kinematics(targetXYZ, target_orientation=[0, 0, 1], orientation_mode="Y",initial_position=arm_position)
                for link_id in range(len(my_chain.links)):
                    link = my_chain.links[link_id]
                    if my_chain.active_links_mask[link_id]:
                        #print("Moving", link.name, "to angle", new_position[link_id])
                        motor = robot.getDevice(link.name)
                        motor.setPosition(newJointAngle[link_id])
            except ValueError:
                
                #traceback.print_exc()  # print the traceback to help diagnose the issue
                for i, part_name in enumerate(part_names):
                    robot_parts[part_name]=robot.getDevice(part_name)
                    robot_parts[part_name].setPosition(float(target_pos[i]))
                    robot_parts[part_name].setVelocity(robot_parts[part_name].getMaxVelocity() / 2.0)
                print("Failed to find a feasible solution to the inverse kinematics problem. Continuing with program execution...")
                temp = 0
                while temp<10000000: #sleep function
                    temp+=1
                # handle the error appropriately, e.g., continue with program execution or terminate the program
                    
        elif key == ord("L"): #lower shelf inbetween point
            targetXYZ = [0.6604401430094335, -0.04585032740017034, 0.7483976346960981]
            try:
                newJointAngle = my_chain.inverse_kinematics(targetXYZ, target_orientation=[0, 0, 1], orientation_mode="Y",initial_position=arm_position)
                for link_id in range(len(my_chain.links)):
                    link = my_chain.links[link_id]
                    if my_chain.active_links_mask[link_id]:
                        #print("Moving", link.name, "to angle", new_position[link_id])
                        motor = robot.getDevice(link.name)
                        motor.setPosition(newJointAngle[link_id])
            except ValueError:
                
                #traceback.print_exc()  # print the traceback to help diagnose the issue
                for i, part_name in enumerate(part_names):
                    robot_parts[part_name]=robot.getDevice(part_name)
                    robot_parts[part_name].setPosition(float(target_pos[i]))
                    robot_parts[part_name].setVelocity(robot_parts[part_name].getMaxVelocity() / 2.0)
                print("Failed to find a feasible solution to the inverse kinematics problem. Continuing with program execution...")
                temp = 0
                while temp<10000000: #sleep function
                    temp+=1
                # handle the error appropriately, e.g., continue with program execution or terminate the program
        elif key == ord("U"): #upper shelf inbetween point
            targetXYZ = [0.6628926812070992, -0.044470392650328816, 1.1559231847826439]
            try:
                newJointAngle = my_chain.inverse_kinematics(targetXYZ, target_orientation=[0, 0, 1], orientation_mode="Y",initial_position=arm_position)
                for link_id in range(len(my_chain.links)):
                    link = my_chain.links[link_id]
                    if my_chain.active_links_mask[link_id]:
                        #print("Moving", link.name, "to angle", new_position[link_id])
                        motor = robot.getDevice(link.name)
                        motor.setPosition(newJointAngle[link_id])
            except ValueError:
                
                #traceback.print_exc()  # print the traceback to help diagnose the issue
                for i, part_name in enumerate(part_names):
                    robot_parts[part_name]=robot.getDevice(part_name)
                    robot_parts[part_name].setPosition(float(target_pos[i]))
                    robot_parts[part_name].setVelocity(robot_parts[part_name].getMaxVelocity() / 2.0)
                print("Failed to find a feasible solution to the inverse kinematics problem. Continuing with program execution...")
                temp = 0
                while temp<10000000 :#sleep function
                    temp+=1
                # handle the error appropriately, e.g., continue with program execution or terminate the program         
        #print("Target XYZ:")
        #print(targetXYZ)          
        same = True
        for target, actual in zip(targetXYZ, xyzPos):
            if target != actual:
                same = False
        if not same:
            try:
                newJointAngle = my_chain.inverse_kinematics(targetXYZ, target_orientation=[0, 0, 1], orientation_mode="Y",initial_position=arm_position)
                for link_id in range(len(my_chain.links)):
                    link = my_chain.links[link_id]
                    if my_chain.active_links_mask[link_id]:
                        #print("Moving", link.name, "to angle", new_position[link_id])
                        motor = robot.getDevice(link.name)
                        motor.setPosition(newJointAngle[link_id])
            except ValueError:
                
                #traceback.print_exc()  # print the traceback to help diagnose the issue
                for i, part_name in enumerate(part_names):
                    robot_parts[part_name]=robot.getDevice(part_name)
                    robot_parts[part_name].setPosition(float(target_pos[i]))
                    robot_parts[part_name].setVelocity(robot_parts[part_name].getMaxVelocity() / 2.0)
                print("Failed to find a feasible solution to the inverse kinematics problem. Continuing with program execution...")
                temp = 0
                while temp<10000000: #sleep function
                    temp+=1
                # handle the error appropriately, e.g., continue with program execution or terminate the program
        #####IK ARM CODE########
                    
        robot_parts["wheel_left_joint"].setVelocity(vL)
        robot_parts["wheel_right_joint"].setVelocity(vR)   
        lastKey = key
        
        
     #FINDING TARGET LOCATIONS
    if state == "testing ML model":
        
        camera.saveImage("currentImage.jpg", 100)
        results = model.predict(source="currentImage.jpg", show=False, conf=0.5, verbose=False)
        boxes = results[0].boxes.to("cpu").numpy()
        targets = []
        for box in boxes:
            if box.cls == 3:
                confidence = box.conf[0]
                box = box.xywh[0]
                x1 = box[0]       
                y1 = box[1]
                x2 = box[2]
                y2 = box[3]
                targets.append((x1, y1, x2, y2, confidence))
        
        pose_x = gps.getValues()[0]
        pose_y = gps.getValues()[1]
        n = compass.getValues()
        rad = -((math.atan2(n[1], n[0])))+np.pi/2
        camera_angle = robot_parts["head_1_joint"].getPositionSensor().getValue()
        pose_theta = rad
        cameraPosX = pose_x+robotRelativeCameraPosition*np.cos(pose_theta)
        cameraPosY = pose_y+robotRelativeCameraPosition*np.sin(pose_theta)
        if pose_y > 3.95:
            robot_row = 0
        elif pose_y > 0:
            robot_row = 1
        elif pose_y > -3.95:
            robot_row = 2
        else:
            robot_row = 3

        display.setColor(0xFFFF00)
        for target in targets:
            display.setOpacity(.3*target[4])
            target_angle = ((camera_width/2)-target[0])*pixel_angle
            #print(target_angle)
            target_dist_max = 3#0.25/(target[2]*pixel_angle)
            target_dist_min = 0#0.02/(target[2]*pixel_angle)
            target_maxX = cameraPosX + (target_dist_max)*np.cos(pose_theta+target_angle)
            target_maxY = cameraPosY + target_dist_max*np.sin(pose_theta+target_angle)
            target_minX = cameraPosX + target_dist_min*np.cos(pose_theta+target_angle)
            target_minY = cameraPosY + target_dist_min*np.sin(pose_theta+target_angle)
            target_display_maxX, target_display_maxY = worldToDisplay(target_maxX, target_maxY)
            target_display_minX, target_display_minY = worldToDisplay(target_minX, target_minY)
            rr, cc, val = skimage.draw.line_aa(target_display_minX, target_display_minY, target_display_maxX, target_display_maxY)
            tmpObjectMap[rr, cc] = val#*target[4]
            tmpObjectMap = np.multiply(tmpObjectMap, shelvesMasks[robot_row])
            display.drawLine(target_display_minX, display_height - target_display_minY - 1, target_display_maxX, display_height - target_display_maxY - 1)
        
        display.setColor(0xFF0000)
        display.setOpacity(1)
        display_poseX, display_poseY = worldToDisplay(cameraPosX, cameraPosY)
        #display_faceX, display_faceY = worldToDisplay(pose_x+np.cos(pose_theta), pose_y+np.sin(pose_theta))
        display.drawPixel(display_poseX, display_poseY)
        #display.drawLine(display_poseX, display_poseY, display_faceX, display_faceY)
    #_____________________________________________________________________________________        

    if state == "mapping":
        manual_drive_enabled = True
        ##### MAPPING ######
        pose_x = gps.getValues()[0] + (world_height/2) # floor x is 30
        pose_y =  (world_width/2) - gps.getValues()[1] # floor y is 16.1
        
        n = compass.getValues()
        rad = -(math.atan2(n[1], n[0]) - math.pi/2)
        pose_theta = rad
        
        #print(pose_x, pose_y, pose_theta)
    
        lidar_sensor_readings = lidar.getRangeImage()
        lidar_sensor_readings = lidar_sensor_readings[83:len(lidar_sensor_readings)-83]
    
        for i, rho in enumerate(lidar_sensor_readings):
            alpha = lidar_offsets[i]
    
            if rho > LIDAR_SENSOR_MAX_RANGE:
                continue

            pose_r = np.array([[pose_x],[pose_y]])
            lidar_p = np.array([[rho * math.sin(alpha)], [rho * math.cos(alpha)], [1]])
            
            #rotation from the world coords to robot coords 
            M = np.dot(RotationMatrix, np.array([[math.cos(pose_theta), -math.sin(pose_theta)], [math.sin(pose_theta), math.cos(pose_theta)]]))
            wl = L2W(M, lidar_p, pose_r)
    
            mx, my = W2D(wl[0, 0], wl[1,0])

            if rho < LIDAR_SENSOR_MAX_RANGE:
    
                if mx >= display_height or my >= display_width:
                    continue
    
                if map[mx][my] < .995:
                    map[mx][my] += 0.005
                grey = map[mx][my]
                r = int(grey*255)<<(4*4)
                g = int(grey*255)<<(2*4)
                b = int(grey*255)
                color = r+g+b
    
                display.setColor(int(color))
                display.drawPixel(my, mx)

        dx, dy = W2D(pose_y, pose_x) #flip y and x!
    
        # Draw the robot's current pose on the display
        display.setColor(int(0xFF0000))
        display.drawPixel(dx, dy)
        
    if state == "RRT":
        if cube_indx >= len(cubes):
            print("No more cubes to find! End state has been reached!")
            state = "InverseKinematics"
            continue
        ##RRT HERE
        point = (gps.getValues()[0], gps.getValues()[1])
        start = np.flip( np.array( world_to_map(point) ) ) #get the starting position of robot

        #goal =  np.array( world_to_map(cubes[cube_indx]) )  #cubes
        goal_theta = cubes[cube_indx][2]
        goal = np.array( cubes[cube_indx][:2])
 
        #print(start, goal)

        path = []
        waypoints = []
        x = 0
        
        # Call RRT
        path, nodes = rrt(map, start, goal)
        # Plot it
        plt.imshow(map)
        plt.plot(start[1], start[0], 'ro')
        plt.plot(goal[1], goal[0], 'go')
        plt.plot([node[1] for node in path], [node[0] for node in path], '-b')
        plt.title("RRT")
        plt.show()
        
        # Call Path Smoothing
        path = pathSmoothing(path, map)
        #path[len(path)-1].append(goal[3])
        cube_indx += 1
        # Plot it
        plt.imshow(map)
        plt.plot(start[1], start[0], 'ro')
        plt.plot(goal[1], goal[0], 'go')
        plt.plot([node[1] for node in path], [node[0] for node in path], '-b')
        plt.title("RRT WITH PATH SMOOTHING")
        plt.show()
         
        # convert each node in the RRT path from map coords to webots world coords
        for i in range(len(path)):
            node = path[i][::-1]
            #print('node', node)
            #print('world', map_to_world(node, scale_factor, offset))
            if i == len(path)-1:
                waypointX, waypointY = map_to_world(node, scale_factor, offset)
                waypoints += [(waypointX, waypointY, goal_theta)]
            else:
                waypoints += [map_to_world(node, scale_factor, offset)]

        print("Waypoints stored, RRT Complete")
        state = "driving"
        print(state)
        
    if state == "driving":
        pose_x = gps.getValues()[0]
        pose_y = gps.getValues()[1]
        
        n = compass.getValues()
        rad = -(math.atan2(n[1], n[0])) + np.pi/2
        pose_theta = rad
        print("pose_x, pose_y, pose_theta", pose_x, pose_y, pose_theta)
        #print(pose_x, pose_y, pose_theta)
        #print(waypoints)
        end = waypoints[waypointIndx] 
        #print("end:", end)
        
        rho = math.sqrt((pose_x-end[0])**2 + (pose_y-end[1])**2)
        angle_goal = (math.atan2(-end[1]+pose_y, -end[0]+pose_x)) + np.pi
        #print("angle goal", angle_goal)
        if len(end) == 3:
            goal_theta = end[2]
            if rho > 2:
                p2 = 4.5
                p3 = 0
            else:
                p2 = 3
                p3 = -1.4
        else:
            goal_theta = 0
            p2 = 4
            p3 = 0
            
        alpha = angle_goal-pose_theta
        mu = goal_theta-pose_theta
        
        if alpha < -np.pi:
            alpha += np.pi*2
        if alpha > np.pi:
            alpha -= np.pi*2
        #print("alpha", alpha)
        
        #STEP 2: Controller

        p1 = 1
        
        #p3 = -.3
        dX = p1 * rho
        dTheta = p2*alpha+p3*mu
        #STEP 3: Compute wheelspeeds
        
        vL = (dX-(dTheta/2))
        vR = (dX+(dTheta/2))
        if (abs(vL) > abs(vR)):
            x = MAX_SPEED/vL
        else:
            x = MAX_SPEED/vR    
     
        vL = x*vL # Left wheel velocity in rad/s
        vR = x*vR # Right wheel velocity in rad/s
    
        # STEP 2.4: Clamp wheel speeds
        if vL > MAX_SPEED-.1:
            vL = MAX_SPEED-.1
        if vL < -MAX_SPEED-.1:
            vL = -MAX_SPEED-.1
        if vR > MAX_SPEED-.1:
            vR = MAX_SPEED-.1
        if vR < -MAX_SPEED-.1:
            vR = -MAX_SPEED-.1
        
        
        if alpha <= (-3*np.pi/5) or alpha >= (3*np.pi/5):
               vL = MAX_SPEED/2
               vR = -MAX_SPEED/2
        
        if rho <= .3:
            waypointIndx += 1
            print("Increasing waypoint index", waypointIndx)
        if waypointIndx > len(waypoints)-1: #if at the end of path, stop
            vL = 0
            vR = 0
            waypointIndx = 0
            state = "InverseKinematics"
            print(state)
            manual_drive_enabled = True
            
    
        robot_parts["wheel_left_joint"].setVelocity(vL)
        robot_parts["wheel_right_joint"].setVelocity(vR)
        

    if state == "training":   
        #Gathering training data for ML Model    
        this_objects = []
        objects = camera.getRecognitionObjects()
        # Loop through the recognized objects and output the bounding box annotations
        
        if r < 2:
            filename = "training_data/labels/val/training_img_" + str(training_img_number) + ".txt"        
        else:
            filename = "training_data/labels/training_img_" + str(training_img_number) + ".txt"
        for i in range(len(objects)):
            class_id = objects[i].getModel() # Use the object's model name as the class ID
            class_id = get_class_id(str(class_id))
            center_x, center_y = objects[i].getPositionOnImage()
            width = objects[i].getSizeOnImage()[0]
            height = objects[i].getSizeOnImage()[1]
        
            # Convert the center and size values to YOLOv5 format
            x_center = center_x / camera_width
            y_center = center_y / camera_height
            box_width = width / camera_width
            box_height = height / camera_height
            this_objects.append([center_x, center_y])
            # Output the annotation to a text file in the YOLOv5 format
            #directory = "training_data"
            with open(filename, "a") as f:
                f.write(f"{class_id} {x_center:.6f} {y_center:.6f} {box_width:.6f} {box_height:.6f}\n")
                #print("Added", class_id, x_center, y_center, box_width, box_height, "to training data number", training_img_number)
        if prev_objects != this_objects:
            prev_objects = this_objects
            if r < 2:
                camera.saveImage("training_data/images/val/training_img_" + str(training_img_number) + ".jpg", 100)    
            else:
                camera.saveImage("training_data/images/training_img_" + str(training_img_number) + ".jpg", 100)
            print(training_img_number)
            r = random.randint(0,9)
            training_img_number += 1      
       
    # Close gripper, note that this takes multiple time steps...
    if right_gripper_enc.getValue()<=0.005:
        gripper_status="closed"
    if left_gripper_enc.getValue()>=0.044:
        gripper_status="open"      
#_____________________________________________________________________________________________________________________  
    
