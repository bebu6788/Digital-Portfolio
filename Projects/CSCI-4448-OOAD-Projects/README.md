Collaborators: Kevin Barone, Ben Burkhalter


Java SE-11

Assumptions that we made in our implementation (in no particular order):

- The condition of the vehicle is random upon FNCD's purchase
- The sales, washing, and repair bonus are all the same dollar amount and entirely depend on the type of vehicle 
- A vehicles name will be the vehicle type (Car, PerfCar, Pickup) + the vehicle number in the order they were purchased by FNCD  (i.e. Pickup1, PerfCar2, Car3) 
- Buyers have a randomly selected vehicle preference and chance of buying preference 
- FNCD will only have 9 employees at any given time
- Staff will be paid their salary every day
- Only interns can be directly hired to FNCD
- A day at FNCD consists of opening, washing, repairing, selling, and closing in that order
- If FNCD runs out of money, they will go into debt for that day and add $25,000 to their budget next days opening
- A buyer only interacts with one salesperson before making their decision to purchase a vehicle
- The first Staff member of each type will be the one to quit
- Each Mechanic/Intern will repair/wash one vehicle each before anyone works on their second vehicle (i.e. the work is split evenly between the staff members)
- Mechanics have two attempted repairs, not necessarily two successful repairs per day
- Cost ranges for the newly added cars (Electric, Monster Truck, and Motorcycle) were random but inline with previous vehicle types
- Drivers are directly hired and interns are not promoted to being a Driver
- The ending report printing out all staff members and the inventory of the FNCD is not considered a publish event for Logger
- The files created from the Logger contained information about daily activites form both FNCDs.
- The Tracker kept information was the combined earnings form staff and the dealership across both FNCDs.
- The various FNCDs compete indifferent races on teh same day.
 


This repository contains the code and additional documents for submissions to projects 2-4 in CSCI 4448 - Object-Oriented Analysis &amp; Design.

