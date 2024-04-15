import java.util.ArrayList;
import java.util.Random;

public class Day {
  private static int dayNum = 0;
  private double dailySales;

  // constructor
  public Day() {
    dayNum++;
    dailySales = 0;
  }

  // getters and setters
  public int getNum() { return dayNum; }
  public void setNum(int n) { dayNum = n; }
  public double getSales() { return dailySales; }

  public void simulateDay(FNCD dealership) {
    // each day runs the same activities in the same order
    opening(dealership);
    washing(dealership);
    repairing(dealership);
    selling(dealership);
    if (dayNum % 7 == 0 || dayNum % 7 == 3) {
      racing(dealership);
    }
    ending(dealership);
  }

  // Runs opening activites for the dealership
  // these include hiring staff and buying vehicles
  public void opening(FNCD dealership) {
    dealership.updateBudget();
    System.out.println("Opening... (current budget $" + String.format("%.2f", dealership.getBudget()) + ")\n");
    dealership.publishEvent(new Event("Opening... (current budget $" + String.format("%.2f", dealership.getBudget()) + ")\n",
                            0, 0, dayNum));
    // check that we have 9 working staff
    if (dealership.getStaff().size() != 12) {
      dealership.hireNewStaff();
    }
    // check that inventory consists of 4 vehicles from each type (12 total)
    if (dealership.getInventory().size() != 24) {
      dealership.addVehicle();
    }
  }

  // Runs the washing activities each day - interns carry out this job
  public void washing(FNCD dealership) {
    System.out.println("\nWashing...\n");
    dealership.publishEvent(new Event("\nWashing...\n", 0, 0, dayNum));
    // retrieve the list of current interns
    ArrayList<Intern> interns = dealership.getInterns();

    // create list of dirty and clean cars that can be washed
    ArrayList<Vehicle> dirtyCars = new ArrayList<Vehicle>();
    ArrayList<Vehicle> cleanCars = new ArrayList<Vehicle>();

    for (Vehicle v: dealership.getInventory()) {
      if (v.getCleannliness() == CleanType.Dirty) {
        dirtyCars.add(v);
      } else if (v.getCleannliness() == CleanType.Clean) {
        cleanCars.add(v);
      }
    }

    // loop until each intern has washed two cars or there are no more cars to wash
    boolean bonusEarned = false;
    int vehiclesWashed = 0;
    while (vehiclesWashed < 6 && (!dirtyCars.isEmpty() || !cleanCars.isEmpty())) {
      // randomly select dirty car
      Random rand = new Random();
      Vehicle vehicle;
      if (!dirtyCars.isEmpty()) {
        vehicle = dirtyCars.get(rand.nextInt(dirtyCars.size()));
      } else {  // if no dirty cars random clean car
        vehicle = cleanCars.get(rand.nextInt(cleanCars.size()));
      }

      // start with first intern and check that they haven't already washed two cars
      // each intern will then wash 1 car before offered to wash a second
      if (vehiclesWashed % 3 == 0 && interns.get(0).getVehiclesWashed() < 2) {
        bonusEarned = interns.get(0).washVehicle(vehicle, dealership);  // **Abstraction: we do not care how a vehicle is washed just that it is
        vehiclesWashed++;
      } else if (vehiclesWashed % 3 == 1 && interns.get(1).getVehiclesWashed() < 2) {
        bonusEarned = interns.get(1).washVehicle(vehicle, dealership);
        vehiclesWashed++;
      } else if (vehiclesWashed % 3 == 2 && interns.get(2).getVehiclesWashed() < 2) {
        bonusEarned = interns.get(2).washVehicle(vehicle, dealership);
        vehiclesWashed++;
      }

      if (dirtyCars.contains(vehicle)) {
        dirtyCars.remove(vehicle);
      } else {
        cleanCars.remove(vehicle);
      }

      if (bonusEarned) {
        dealership.subFromBudget(vehicle.getBonusAmount()); // subtract the bonus ammount paid to the interns from FNCD budget
      }
    }

    // reset Intern's two daily washes each day
    for (Intern i: interns) {
      i.resetWashes();
    }
  }

  // Runs the rapairing activities for the day
  public void repairing(FNCD dealership) {
    System.out.println("\nRepairing...\n");
    dealership.publishEvent(new Event("\nRepairing...\n", 0, 0, dayNum));
    // get list of all mechanics currently working
    ArrayList<Mechanic> mechs = new ArrayList<Mechanic>();
    for (Staff s: dealership.getStaff()) {
      if (s.getStringify() == "Mech") { // mechanic check
        mechs.add(((Mechanic)s));
      }
    }

    // get list of all cars that are not "LikeNew" condition
    ArrayList<Vehicle> repairable = new ArrayList<Vehicle>();
    for (Vehicle v: dealership.getInventory()) {
      if (v.getCondition() != ConditionType.LikeNew) {
        repairable.add(v);
      }
    }

    // loop until each mechanic has repaired two vehicles or no more cars to repair
    boolean bonusEarned = false;
    int vehiclesRepaired = 0;
    while (vehiclesRepaired < 6 && !repairable.isEmpty()) {
      // randomly select car to repair
      Random rand = new Random();
      Vehicle vehicle = repairable.get(rand.nextInt(repairable.size()));

      // start with first mechanic and check that they haven't already repaired two cars
      // each mechanic will then repair 1 car before offered to repair a second
      // an attempt counts as a repair - can only attempt two repairs a day
      if (vehiclesRepaired % 3 == 0 && mechs.get(0).getVehiclesRepaired() < 2) {
        bonusEarned = mechs.get(0).repairVehicle(vehicle, dealership);
        vehiclesRepaired++;
      } else if (vehiclesRepaired % 3 == 1 && mechs.get(1).getVehiclesRepaired() < 2) {
        bonusEarned = mechs.get(1).repairVehicle(vehicle, dealership);
        vehiclesRepaired++;
      } else if (vehiclesRepaired % 3 == 2 && mechs.get(2).getVehiclesRepaired() < 2) {
        bonusEarned = mechs.get(2).repairVehicle(vehicle, dealership);
        vehiclesRepaired++;
      }

      repairable.remove(vehicle);

      if (bonusEarned) {
        dealership.subFromBudget(vehicle.getBonusAmount());  // subtract the bonus ammount paid to the mecanics from FNCD budget
      }
    }

    // reset repair counts
    for (Mechanic m: mechs) {
      m.resetRepairs();
    }
  }

  // Runs the selling activities for the day
  public void selling(FNCD dealership) {
    System.out.println("\nSelling...\n");
    dealership.publishEvent(new Event("\nSelling...\n", 0, 0, dayNum));
    // get list of all salespeople staff
    ArrayList<Salesperson> sellers = new ArrayList<Salesperson>();
    for (Staff s: dealership.getStaff()) {
      if (s.getStringify() == "Sales") { // salesperson check
        sellers.add(((Salesperson)s));
      }
    }

    // randomly generate number of buyers for the day
    Random rand = new Random();
    int r = 0;
    if (dayNum % 5 == 0 || dayNum % 6 == 0) { // Friday or Saturday range is 2-8 buyers
      r = rand.nextInt(8-2) + 2;
    } else {
      r = rand.nextInt(5);  // every other day has 0-5 buyers
    }

    // create the list of buyers
    ArrayList<Buyer> buyers = new ArrayList<Buyer>();
    for (int i = 0; i < r; i++) {
      buyers.add(new Buyer());
    }

    for (Buyer b: buyers) {
      double sellingChance = b.getBuyChance();  // adaptable selling chance
      boolean found = false;
      Salesperson s = sellers.get(rand.nextInt(sellers.size()));
      Vehicle foundVehicle = dealership.getInventory().get(0);

      // loop through vehicles until one of buyer's preference is found
      for (Vehicle v: dealership.getInventory()) {  // we have implemented a sorted inventory from most to least expensive
        if (v.getCondition() != ConditionType.Broken &&
            v.getStringify() == b.getPreference().getStringify()) { // first vehicle to match condition is most expensive matcher
          if (v.getCondition() == ConditionType.LikeNew) {  // update selling chance by 10% if 'LikeNew'
            sellingChance += 0.1;
          }
          if (v.getCleannliness() == CleanType.Sparkling) { // update selling chance by 10% if 'Sparkling'
            sellingChance += 0.1;
          }
          foundVehicle = v;
          found = true;
          break;
        }
      }
      if (!found) { // get most expensive vehicle if none of preferred type found
        sellingChance -= 0.2;
      }

      r = rand.nextInt(100-1)+1;
      // true if sale is completed
      if (r <= sellingChance * 100) {
        if (rand.nextInt(100-1)+1 <= 25) {
          System.out.println("Extended Warranty added. Price increased by $" + String.format("%.2f", foundVehicle.getPrice() * 0.2));
          dealership.publishEvent(new Event("Extended Warranty added. Price increased by $" + String.format("%.2f", foundVehicle.getPrice() * 0.2), 0, 0, dayNum));
          foundVehicle = new ExtendedWarranty(foundVehicle);
        }
        if (rand.nextInt(100-1)+1 <= 10) {
          System.out.println("Undercoating added. Price increased by $" + String.format("%.2f", foundVehicle.getPrice() * 0.05));
          dealership.publishEvent(new Event("Undercoating added. Price increased by $" + String.format("%.2f", foundVehicle.getPrice() * 0.05), 0, 0, dayNum));

          foundVehicle = new Undercoating(foundVehicle);
        }
        if (rand.nextInt(100-1)+1 <= 5) {
          System.out.println("Road Rescue Coverage added. Price increased by $" + String.format("%.2f", foundVehicle.getPrice() * 0.02));
          dealership.publishEvent(new Event("Road Rescue Coverage added. Price increased by $" + String.format("%.2f", foundVehicle.getPrice() * 0.02), 0, 0, dayNum));

          foundVehicle = new RoadRescueCoverage(foundVehicle);
        }
        if (rand.nextInt(100-1)+1 <= 40) {
          System.out.println("Satellite Radio added. Price increased by $" + String.format("%.2f", foundVehicle.getPrice() * 0.05));
          dealership.publishEvent(new Event("Satellite Radio added. Price increased by $" + String.format("%.2f", foundVehicle.getPrice() * 0.05), 0, 0, dayNum));
          foundVehicle = new SatelliteRadio(foundVehicle);
        }

        dailySales += foundVehicle.getPrice();
        dealership.addToBudget(foundVehicle.getPrice());
        s.calculateSaleBonus(foundVehicle);
        dealership.subFromBudget(foundVehicle.getBonusAmount());  // subtract the bonus ammount paid to the interns from FNCD budget
        dealership.sellVehicle(foundVehicle);

        String out = "Salesperson " + s.getName() + " sold " + foundVehicle.getCleannliness().toString()
                      + " " + foundVehicle.getCondition().toString() + " " + foundVehicle.getName() +
                      " to Buyer for $" + String.format("%.2f", foundVehicle.getPrice()) + " (earned $"
                      + foundVehicle.getBonusAmount() + ")\n";
        System.out.println(out);
        dealership.publishEvent(new Event(out, 0, 0, dayNum)); // notify the observers of the vehicle sale
      }
    }
  }

  // Runs the racing activities for the day - only Sundays and Wednesdays
  public void racing(FNCD dealership) {
    System.out.println("\nRacing...\n");
    dealership.publishEvent(new Event("\nRacing...\n", 0, 0, dayNum)); // publish the racing event to the subscribers

    // Randomly select the type of car that will be raced
    Random rand = new Random();
    String[] types = {"Pickup", "PerfCar", "Monster", "Motor"};
    String raceType = types[rand.nextInt(types.length)];

    // get list of all cars that are going to race
    ArrayList<Vehicle> raceCars = new ArrayList<Vehicle>();
    for (Vehicle v: dealership.getInventory()) {
      if (v.getStringify() == raceType && v.getCondition() != ConditionType.Broken && raceCars.size() != 3) {
        raceCars.add(v);
      }
    }

    if (!raceCars.isEmpty()) {
      // add the drivers to an arraylist
      ArrayList<Driver> drivers = new ArrayList<Driver>();
      for (Staff s: dealership.getStaff()) {
        if (s.getStringify() == "Driver" && !raceCars.isEmpty()) {
          ((Driver)s).selectRaceCar(raceCars);
          drivers.add((Driver)s);
        }
      }

      // Create a list range 1-20 to determine race placings
      ArrayList<Integer> placings = new ArrayList<Integer>(20);
      for (int n = 1; n <= 20; n++) {
        placings.add(n);
      }

      for (int i = 0; i < drivers.size(); i++) {
        int r = rand.nextInt(placings.size());
        if (drivers.get(i).performRace(placings.get(r), dealership)) {
          dealership.subFromBudget(drivers.get(i).getRaceCar().getBonusAmount()); // if the get top 3, pay the drivers a bonus
        }
        placings.remove(r); // remove that place from the possible placing to avoid 2 drivers getting the same placement
      }
    } else {
      System.out.println("The FNCD cannot participate in today's race due to shortage of vehicles!");
      dealership.publishEvent(new Event("The FNCD cannot participate in today's race due to shortage of vehicles!", 0, 0, dayNum));
      return;
    }
  }

  // Runs the ending activities for the day
  // this includes paying staff, and staff quitting
  public void ending(FNCD dealership) {
    System.out.println("\nEnding...\n");
    dealership.publishEvent(new Event("\nEnding...\n", 0, 0, dayNum));

    dealership.payStaff();
    dealership.staffQuit();
    dealership.checkInjured();
    generateReport(dealership);
  }

  // Print out a daily report of Staff members and inventory
  // also prints the current operating budget and Total Sales for the day
  public void generateReport(FNCD fncd) {
    System.out.println("Ending Report:\n");
    System.out.println("Staff Members:");
    System.out.println("------------------------------------------------------------------------");
    System.out.format("%10s |%12s | %14s |%16s | %6s\n", "Name", "Days Worked", "Total Normal Pay", "Total Bonus Pay", "Status");
    System.out.println("------------------------------------------------------------------------");
    // current working staff
    for (Staff s: fncd.getStaff()) {
      System.out.format("%10s |%12d | $%-15.2f | $%-14.2f | Working\n", s.getName(), s.getDaysWorked(), s.getIncomeEarned(), s.getBonusEarned());
    }
    System.out.println("------------------------------------------------------------------------");
    // departed staff members
    for (Staff s: fncd.getDeparted()) {
      System.out.format("%10s |%12d | $%-15.2f | $%-14.2f | Quit\n", s.getName(), s.getDaysWorked(), s.getIncomeEarned(), s.getBonusEarned());
    }

    System.out.println("\n\nInventory:");
    System.out.println("-----------------------------------------------------------------------------------------------------");
    System.out.format("%38s |%10s |%12s |%10s |%12s | %8s\n", "Vehicle Name", "Cost", "Sales Price", "Condition", "Cleanliness", "In Stock");
    System.out.println("-----------------------------------------------------------------------------------------------------");
    // current inventory
    for (Vehicle v: fncd.getInventory()) {
      System.out.format("%38s | $%-8.2f | $%-10.2f |%10s |%12s | YES\n", v.getName(), v.getCost(), v.getPrice(), v.getCondition().toString(), v.getCleannliness().toString());
    }
    System.out.println("-----------------------------------------------------------------------------------------------------");
    // all sold vehicles
    for (Vehicle v: fncd.getSoldVehicles()) {
      System.out.format("%38s | $%-8.2f | $%-10.2f |%10s |%12s | NO \n", v.getName(), v.getCost(), v.getPrice(), v.getCondition().toString(), v.getCleannliness().toString());
    }

    System.out.println("\n");

    System.out.println("Total Remianing Budget: $" + String.format("%.2f", fncd.getBudget()));
    System.out.println("Total Day Sales: $" + String.format("%.2f", dailySales));

    System.out.println("\n");
  }
}
