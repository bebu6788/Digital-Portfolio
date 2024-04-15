import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

// FNCD implements the Subject to connect the observers to the subject
public class FNCD implements Subject{
  // **Encapsulation: we hide the private attributes of the FNCD class and use getters/setters to access them
  String name;
  private double budget;
  private int dayCount;
  private ArrayList<Staff> staff;
  private ArrayList<Staff> deparatedStaff;
  private ArrayList<Vehicle> inventory;
  private ArrayList<Vehicle> soldVehicles;

  private ArrayList<Observer> observers;
  Hashtable<String, VehicleCreator> vehicleFactories; // all concrete vehicle factories
  Hashtable<String, StaffCreator> staffFactories; // all concrete staff factories

  // constructor
  public FNCD() {
    name = "";
    budget = 500000;
    dayCount = 0;
    deparatedStaff = new ArrayList<Staff>();
    inventory = new ArrayList<Vehicle>();
    soldVehicles = new ArrayList<Vehicle>();
    observers = new ArrayList<Observer>(); // create an arraylist to hold the tracker and logger observers

    vehicleFactories = new Hashtable<String, VehicleCreator>() {{
      put("Car", new CarCreator()); put("PerfCar", new PerformanceCarCreator());
      put("EV", new ElectricCarCreator()); put("Pickup", new PickupCreator());
      put("Monster", new MonsterTruckCreator()); put("Motor", new MotorcycleCreator());
      put("Hyper", new HyperCarCreator()); put("Van", new VanCreator()); put("Limo", new LimousineCreator());
    }};

    staffFactories = new Hashtable<String, StaffCreator>() {{
      put("Sales", new SalespersonCreator()); put("Intern", new InternCreator());
      put("Driver", new DriverCreator()); put("Mech", new MechanicCreator());
    }};

    staff = new ArrayList<Staff>() {
      {
        add(staffFactories.get("Sales").createStaff());
        add(staffFactories.get("Sales").createStaff());
        add(staffFactories.get("Sales").createStaff());
        add(staffFactories.get("Mech").createStaff());
        add(staffFactories.get("Mech").createStaff());
        add(staffFactories.get("Mech").createStaff());
        add(staffFactories.get("Intern").createStaff());
        add(staffFactories.get("Intern").createStaff());
        add(staffFactories.get("Intern").createStaff());
        add(staffFactories.get("Driver").createStaff());
        add(staffFactories.get("Driver").createStaff());
        add(staffFactories.get("Driver").createStaff());
      }
    };
  }

  // getters and setters
  public double getBudget() { return budget; }
  public void addToBudget(double m) { budget += m; }  // add amount to budget
  public int getDays() { return dayCount; }
  public void addDay() { dayCount++; }  // increment number of days
  public ArrayList<Staff> getStaff() { return staff; }
  public ArrayList<Staff> getDeparted() { return deparatedStaff; }
  public ArrayList<Vehicle> getInventory() { return inventory; }
  public ArrayList<Vehicle> getSoldVehicles() { return soldVehicles; }
  public void setName(String n) { name = n; }

  // subtract from budget
  public void subFromBudget(double m) {
    budget -= m;
    publishEvent(new Event(null, m, 0, dayCount));
  }

  // Observer functions
  public void addSubscriber(Observer observer) {
    observers.add(observer);
  }

  public void removeSubscriber(Observer observer) {
    observers.remove(observer);
  }

  public void publishEvent(Event event) {
    for (Observer o: observers) {
      o.update(event);
    }
  }

  // Hires new interns and Drivers when there are not 3 present in the dealership staff
  public boolean hireNewStaff() {
    int internCount = 0;
    int driverCount = 0;
    // first check how many are currently on staff
    for (Staff s: staff) {
      if (s.getStringify() == "Intern") {
        internCount++;
      }
      if (s.getStringify() == "Driver") {
        driverCount++;
      }
    }

    // add new interns until we have three
    while (internCount < 3) {
      Staff i = staffFactories.get("Intern").createStaff();
      staff.add(i);
      System.out.println("Hired Intern " + i.getName() + "\n");
      publishEvent(new Event("Hired Intern " + i.getName() + "\n", 0, 0, dayCount));
      internCount++;
    }

    // add new driver until we have three
    // Note: assumption that drivers are directly hired and interns are not promoted to drivers
    while (driverCount < 3) {
      Staff d = staffFactories.get("Driver").createStaff();
      staff.add(d);
      System.out.println("Hired Driver " + d.getName() + "\n");
      publishEvent(new Event("Hired Driver " + d.getName() + "\n", 0, 0, dayCount));
      driverCount++;
    }

    return true;
  }

  // Adds vehicles to inventory when 4 of each type are not present
  public boolean addVehicle() {
    Hashtable<String, Integer> vs = new Hashtable<String, Integer>() {{
      put("Car", 0); put("PerfCar", 0);
      put("EV", 0); put("Pickup", 0);
      put("Monster", 0); put("Motor", 0);
      put("Hyper", 0); put("Van", 0); put("Limo", 0);
    }};
    // count number of vehicles of each type
    for (Vehicle v: inventory) {
      vs.replace(v.getStringify(), vs.get(v.getStringify())+1);
    }

    // Get 6 of each type of car and purchase it to FNCD inventory
    for (String key: vs.keySet()) {
      while (vs.get(key) != 6) {
        Vehicle v = vehicleFactories.get(key).createVehicle();

        budget -= v.getCost(); // subtract this cost from the budget
        System.out.println("Purchased " + v.getCondition().toString() + ", " + v.getCleannliness().toString() + " Car for $" + v.getCost() + "\n");
        publishEvent(new Event("Purchased " + v.getCondition().toString() + ", " + v.getCleannliness().toString() + " Car for $" + v.getCost() + "\n", 0, 0, dayCount));
        inventory.add(v); // add the vehicle to the inventory
        vs.replace(key,  vs.get(key)+1);
        updateBudget();
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
    publishEvent(new Event(null, 0.0, v.getPrice(), dayCount));
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
      // only pay drivers on race days!
      if (s.getStringify() != "Driver" || dayCount % 7 == 0 || dayCount % 7 == 3) { // ensure drivers only get paid when they race (i.e. Wed / Sun)
        s.addDayWorked();
        s.addIncome(s.getSalary());
        subFromBudget(s.getSalary());
      }
    }
  }

  // Check if the driver is injured after a race
  public void checkInjured() {
    ArrayList<Driver> injured = new ArrayList<Driver>();
    for (Staff s: staff) {
      if (s.getStringify() == "Driver") {
        Driver d = (Driver)s;
        if (d.getIsInjured()) {
          injured.add(d);
        }
      }
    }

    // If the driver is injuerd they will quit FNCD
    for (Driver d: injured) {
      System.out.println(d.getStringify() + " " + d.getName() + " has quit FNCD due to injury\n");
      publishEvent(new Event(d.getStringify() + " " + d.getName() + " has quit FNCD due to injury\n", 0, 0, dayCount)); // Publish this event to the observers
      staff.remove(d);
      deparatedStaff.add(d);
    }
  }

  // Runs the quitting process for all staff members at days end
  public void staffQuit() {
    ArrayList<Intern> interns = getInterns();
    Staff toRemove = null;  // used to store staff member to be removed
    Staff toAdd = null;     // used to store staff member to be added (promotion)
    Intern promotedIntern = null; // store Intern to be removed after promotion

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
            System.out.println(toAdd.getName() + " has been promoted to " + toAdd.getStringify() + "\n");
            publishEvent(new Event(toAdd.getName() + " has been promoted to " + toAdd.getStringify() + "\n", 0, 0, dayCount));

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
        publishEvent(new Event(toRemove.getStringify() + " " + toRemove.getName() + " has quit FNCD \n", 0, 0, dayCount));
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

  // Retrieve all current working salespeople and return them as an ArrayList
  public ArrayList<Salesperson> getSalespeople() {
    ArrayList<Salesperson> sales = new ArrayList<Salesperson>();
    for (Staff s: getStaff()) {
      if (s.getStringify() == "Sales") {
        sales.add(((Salesperson)s));
      }
    }

    return sales;
  }

  // Ensures that new staff member to be added does not already exist
  public void addStaffMember(Staff s1) {
    if (s1 == null) {
      return;
    }

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
      System.out.println("--- Budget increased by $250000 ---\n");
      publishEvent(new Event("--- Budget increased by $250000 ---\n", 0, 0, dayCount));
    }
  }
}
