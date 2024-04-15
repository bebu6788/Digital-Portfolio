import java.util.ArrayList;
import java.util.Random;

public class FNCD {
  // **Encapsulation: we hide the private attributes of the FNCD class and use getters/setters to access them
  private double budget;
  private int dayCount;
  private ArrayList<Staff> staff;
  private ArrayList<Staff> deparatedStaff;
  private ArrayList<Vehicle> inventory;
  private ArrayList<Vehicle> soldVehicles;

  // constructor
  public FNCD() {
    budget = 500000;
    dayCount = 0;
    staff = new ArrayList<Staff>() {
      {
        add(new Mechanic());
        add(new Mechanic());
        add(new Mechanic());
        add(new Salesperson());
        add(new Salesperson());
        add(new Salesperson());
        add(new Intern());
        add(new Intern());
        add(new Intern());
      }
    };
    deparatedStaff = new ArrayList<Staff>();
    inventory = new ArrayList<Vehicle>();
    soldVehicles = new ArrayList<Vehicle>();
  }

  // getters and setters
  public double getBudget() { return budget; }
  public void addToBudget(double m) { budget += m; }  // add amount to budget
  public void subFromBudget(double m) { budget -= m; }  // subtract from budget
  public int getDays() { return dayCount; }
  public void addDay() { dayCount++; }  // increment number of days
  public ArrayList<Staff> getStaff() { return staff; }
  public ArrayList<Staff> getDeparted() { return deparatedStaff; }
  public ArrayList<Vehicle> getInventory() { return inventory; }
  public ArrayList<Vehicle> getSoldVehicles() { return soldVehicles; }

  // Runs the dealership simulation for 'n' days
  public void runSimulation(int n) {
    while (dayCount < n) {
      Day d = new Day();
      dayCount++;

      System.out.println("*** FNCD Day " + Integer.toString(dayCount) + " ***");
      if (d.getNum() % 7 != 0) {  // Sundays are multiples of 7 - closed
        d.simulateDay(this);
      } else {
        System.out.println("\nFNCD is closed on Sundays! :)");
        System.out.println("Come back Tomorrow!\n");
      }
    }
  }

  // Hires new interns when there are not 3 present in the dealership
  public boolean hireNewStaff() {
    int internCount = 0;
    // first check how many are currently on staff
    for (Staff s: staff) {
      if (s.getStringify() == "Intern") { 
        internCount++;
      }
    }
    
    // add new interns until we have three
    while (internCount < 3) {
      Intern i = new Intern();
      staff.add(i);
      System.out.println("Hired Intern " + i.getName() + "\n");
      internCount++;
    }

    return true;
  }

  // Adds vehicles to inventory when 4 of each type are not present
  public boolean addVehicle() {
    int counts[] = {0, 0, 0};
    // count number of vehicles of each type
    for (Vehicle v: inventory) {
      if (v.getStringify() == "Car") {
        counts[0]++;
      } else if (v.getStringify() == "PerfCar") {
        counts[1]++;
      } else {
        counts[2]++;
      }
    }

    // check that we have 4 Cars, add more if needed
    if (counts[0] < 4) {
      for (int i = 0; i < 4-counts[0]; i++) {
        Car c = new Car();
        subFromBudget(c.getCost());
        System.out.println("Purchased " + c.getCondition().toString() + ", " + c.getCleannliness().toString() + " Car for $" + c.getCost() + "\n");
        inventory.add(c);
      }
    } 
    // check that we have 4 PerfCars, add more if needed
    if (counts[1] < 4) {
      for (int i = 0; i < 4-counts[1]; i++) {
        PerformanceCar pc = new PerformanceCar();
        subFromBudget(pc.getCost());
        System.out.println("Purchased " + pc.getCondition().toString() + ", " + pc.getCleannliness().toString() + " Performance Car for $" + pc.getCost() + "\n");
        inventory.add(pc);
      }
    }
    // check that we have 4 Pickups, add more if needed
    if (counts[2] < 4) {
      for (int i = 0; i < 4-counts[2]; i++) {
        Pickup pu = new Pickup();
        subFromBudget(pu.getCost());
        System.out.println("Purchased " + pu.getCondition().toString() + ", " + pu.getCleannliness().toString() + " Pickup for $" + pu.getCost() + "\n");
        inventory.add(pu);
      }
    }

    // sort inventory by sale price for easy selling
    sortInventory();
    return true;
  }

  // Sell a vehicle and remove it from inventory
  public void sellVehicle(Vehicle v) {
    inventory.remove(v);
    soldVehicles.add(v);
  }

  // Sort the current inventory by sale price
  // Bubble sort algorithm adapted from Geeks-for-Geeks
  public void sortInventory() {
    // https://www.geeksforgeeks.org/bubble-sort/
    int n = inventory.size();
    for (int i = 0; i < n - 1; i++) {
      for (int j = 0; j < n - i - 1; j++)
        if (inventory.get(j).getPrice() < inventory.get(j + 1).getPrice()) {
        // swap arr[j+1] and arr[j]
        Vehicle temp = inventory.get(j);
        inventory.set(j, inventory.get(j + 1));
        inventory.set(j + 1, temp);
      }
    }
  }
  
  // Pay each staff member their respective salary
  public void payStaff() {
    for (Staff s: staff) {
      s.addDayWorked();
      s.addIncome(s.getSalary());
      subFromBudget(s.getSalary());
    }
  }

  // Runs the quitting process for all staff members at days end
  public void staffQuit() {
    ArrayList<Intern> interns = getInterns();
    Staff toRemove = staff.get(0);  // used to store staff member to be removed
    Staff toAdd = staff.get(0);     // used to store staff member to be added (promotion)
    Intern promotedIntern = new Intern(); // store Intern to be removed after promotion

    Random rand = new Random();
    for (int i = 0; i < 3; i++) { // 'i' will signify what type of staff we are searching for
      int r = rand.nextInt(100-1)+1;

      if (r <= 10) {  // 10% chance per staff type that one of them quits
        for (Staff s: staff) {
          // first staff member of each type will be the one to quit
          if ((i == 0 && s.getStringify() == "Sales") || (i == 1 && s.getStringify() == "Mech")) {  // first we look for salesperson to quit
            toAdd = promote(s, interns.get(0));                                               // then a mechanic to quit on second loop
            promotedIntern = interns.get(0);  
            interns.remove(0);
            System.out.println( toAdd.getName() + " has been promoted to " + toAdd.getStringify() + "\n");

            toRemove = s;
            deparatedStaff.add(s);
            break;
          } else if (i == 2 && s.getStringify() == "Intern") { // last is for an intern to quit
            toRemove = s;
            deparatedStaff.add(s);
            break;
          }
        }
        staff.remove(toRemove);
        staff.remove(promotedIntern);
        addStaffMember(toAdd);  // functuion used to ensure we are not re-adding any staff members

        System.out.println(toRemove.getStringify() + " " + toRemove.getName() + " has quit FNCD \n");
      }
    }
  }

  // Promotes given intern to the given staff type and returns new staff member with same name
  public Staff promote(Staff s, Intern intern) { // promote to salesperson or mechanic
    if (s.getStringify() == "Sales") {
      return new Salesperson(intern.getName(), 
                             intern.getBonusEarned(),  
                             intern.getIncomeEarned(), 
                             intern.getDaysWorked());
    } else {
      return new Mechanic(intern.getName(), 
                          intern.getBonusEarned(), 
                          intern.getIncomeEarned(), 
                          intern.getDaysWorked());
    }
  }

  // Retrieve all current working interns and return them as an ArrayList
  public ArrayList<Intern> getInterns() {
    ArrayList<Intern> interns = new ArrayList<Intern>();
    for (Staff s: getStaff()) {
      if (s.getStringify() == "Intern") {
        interns.add(((Intern)s));
      }
    }
    return interns;
  }

  // Ensures that new staff member to be added does not already exist
  public void addStaffMember(Staff s1) {
    for (Staff s: staff) {
      if (s1.getName() == s.getName()) {
        return;
      }
    }

    staff.add(s1);
  }

  // Adds $250000 to budget when dealership runs out of money
  public void updateBudget() {
    if (budget <= 0) {
      budget += 250000;
    }
  }
}
