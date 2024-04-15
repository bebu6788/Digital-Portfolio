import java.util.Random;
import java.util.Scanner;

// *** Command Pattern ***
public interface Command {
  public void execute(Scanner scan);
  public abstract void update(Day d);
}

// First command - selecting which FNCD store to shop at
class SelectFNCDCommand implements Command {
  Day day;

  public SelectFNCDCommand(Day d) {
    this.day = d;
  }

  @Override
  public void update(Day d) {
    this.day = d;
  }

  public void execute(Scanner scan) {
    int input = -1;
    while (input == -1) {
      System.out.println("\nWhich FNCD would you like to visit?");
      for (int i = 0; i < day.dealers.size(); i++) {
        System.out.println(Integer.toString(i+1) + ": " + day.dealers.get(i).name);
      }
      input = scan.nextInt();
      // sets the currentDealr of the day to option chosen by user
      if (input == 1) {
        day.currentDealer = day.dealers.get(0);
      } else if (input == 2) {
        day.currentDealer = day.dealers.get(1);
      } else {
        System.out.println("\nEnter a valid number.");
        input = -1;
      }
    }
    day.currentSeller = day.currentDealer.getSalespeople().get(0);  // gets seller from updated dealer
  }
}

// Second Command - get name of current salesperson
class GetNameCommand implements Command {
  Salesperson seller;

  public GetNameCommand(Salesperson s) {
    this.seller = s;
  }

  @Override
  public void update(Day d) {
    this.seller = d.currentSeller;
  }

  public void execute(Scanner scan) {
    System.out.println("\nMy name is " + seller.getName() + "!\n");
  }
}

// Third command - get current time
class GetTimeCommand implements Command {
  public GetTimeCommand() {}

  @Override
  public void update(Day d) {}

  public void execute(Scanner scan) {
    System.out.println("\nThe current time is " + java.time.LocalTime.now() + ".\n");
  }
}

// Fourth command - get a new seller form the same dealer
class NewSellerCommand implements Command {
  FNCD dealer;
  Day day;

  public NewSellerCommand(FNCD dealer, Day d) {
    this.dealer = dealer;
    this.day = d;
  }

  @Override
  public void update(Day d) {
    this.dealer = d.currentDealer;
    this.day = d;
  }

  public void execute(Scanner scan) {
    Random rand = new Random();
    // Randomly choose a seller until it is not the one that is the current seller
    while (true) {
      Salesperson temp = dealer.getSalespeople().get(rand.nextInt(dealer.getSalespeople().size()));
      if (temp.getName() != day.currentSeller.getName()) {
        day.currentSeller = temp;
        break;
      }
    }

    System.out.println("\nYour new salesperson is " + day.currentSeller.getName() + "!\n");
  }
}

// Fifth command - get inventory of current dealer to display
class GetInventoryCommand implements Command {
  FNCD dealer;

  public GetInventoryCommand(FNCD dealer) {
    this.dealer = dealer;
  }

  @Override
  public void update(Day d) {
    this.dealer = d.currentDealer;
  }

  public void execute(Scanner scan) {
    // display all vehicles and prices currently in the inventory
    System.out.println("\nCurrent Inventory:\n");
    for (Vehicle v : dealer.getInventory()) {
      System.out.format(" %-16s Price: $%-10.2f\n", v.getName(), v.getPrice());
    }
  }
}

// Sixth command - get the details of the current vehicle selected
class GetDetailsCommand implements Command {
  Vehicle vehicle;

  public GetDetailsCommand(Vehicle v) {
    this.vehicle = v;
  }

  @Override
  public void update(Day d) {
    this.vehicle = d.currentItem;
  }

  public void execute(Scanner scan) {
    System.out.println("\nName: " + vehicle.getName() + "\nPrice: $" + vehicle.getPrice() + "\nCondition: " +
                      vehicle.getCondition().toString() + "\nCleanliness: " + vehicle.getCleannliness().toString());
  }
}

// Seventh command - runs through the buying sequence of a car between seller and user
class BuyCommand implements Command {
  Salesperson seller;
  Vehicle vehicle;
  FNCD dealer;

  public BuyCommand(Salesperson s, Vehicle v, FNCD d) {
    this.seller = s;
    this.vehicle = v;
    this.dealer = d;
  }

  @Override
  public void update(Day d) {
    this.seller = d.currentSeller;
    this.vehicle = d.currentItem;
    this.dealer = d.currentDealer;
  }

  public void execute(Scanner scan) {
    // vehicle selection
    int num = 1;
    System.out.println("\nAvailable Vehicles: ");
    for (Vehicle v : dealer.getInventory()) {
      System.out.format(" %2d: %-16s Price: $%-10.2f\n", num, v.getName(), v.getPrice());
      num++;
    }

    // ensure vehicle selected is available
    int input = 0;
    while (input == 0) {
      System.out.println("\nSelect a vehicle to purchase: ");
      input = scan.nextInt();

      if (input < 1 || input > num) {
        input = 0;
        System.out.println("Enter a valid number.");
      }
    }

    vehicle = dealer.getInventory().get(input-1);

    // prompt do you want to buy?
    System.out.println("\nWould you like to buy " + vehicle.getName() + " for $" + vehicle.getPrice() + "? (Y/N)");
    String in_str = scan.next();

    // if yes prompt for each add on and add them according to user input
    if (in_str.equals("y") || in_str.equals("Y")) {
      System.out.println("\nExcellent! Would you like to purchase the Extended Warranty add on? (Y/N)");
      in_str = scan.next();

      if (in_str.equals("y") || in_str.equals("Y")) {
        System.out.println("\nExtended Warranty added. Price increased by $" + String.format("%.2f", vehicle.getPrice() * 0.2));
        dealer.publishEvent(new Event("Extended Warranty added. Price increased by $" + String.format("%.2f", vehicle.getPrice() * 0.2), 0, 0, 31));
        vehicle = new ExtendedWarranty(vehicle);
      }

      System.out.println("\nWould you like to purchase the Undercoating add on? (Y/N)");
      in_str = scan.next();

      if (in_str.equals("y") || in_str.equals("Y")) {
        System.out.println("\nUndercoating added. Price increased by $" + String.format("%.2f", vehicle.getPrice() * 0.05));
        dealer.publishEvent(new Event("Undercoating added. Price increased by $" + String.format("%.2f", vehicle.getPrice() * 0.05), 0, 0, 31));
        vehicle = new Undercoating(vehicle);
      }

      System.out.println("\nWould you like to purchase the Road Rescue Coverage add on? (Y/N)");
      in_str = scan.next();

      if (in_str.equals("y") || in_str.equals("Y")) {
        System.out.println("\nRoad Rescue Coverage added. Price increased by $" + String.format("%.2f", vehicle.getPrice() * 0.02));
        dealer.publishEvent(new Event("Road Rescue Coverage added. Price increased by $" + String.format("%.2f", vehicle.getPrice() * 0.02), 0, 0, 31));
        vehicle = new RoadRescueCoverage(vehicle);
      }

      System.out.println("\nWould you like to purchase the Satellite Radio add on? (Y/N)");
      in_str = scan.next();

      if (in_str.equals("y") || in_str.equals("Y")) {
        System.out.println("\nSatellite Radio added. Price increased by $" + String.format("%.2f", vehicle.getPrice() * 0.05));
        dealer.publishEvent(new Event("Satellite Radio added. Price increased by $" + String.format("%.2f", vehicle.getPrice() * 0.05), 0, 0, 31));
        vehicle = new SatelliteRadio(vehicle);
      }

      String out = "Salesperson " + seller.getName() + " sold " + vehicle.getCleannliness().toString()
                      + " " + vehicle.getCondition().toString() + " " + vehicle.getName() +
                      " to Buyer for $" + String.format("%.2f", vehicle.getPrice()) + " (earned $"
                      + vehicle.getBonusAmount() + ")\n";
      dealer.publishEvent(new Event(out, 0, 0, 31));
      System.out.println("\nThank you for purchasing " + vehicle.getName() + "!");
    } else {
      System.out.println("\nOk, Let me know how else I can assist you!");
    }
  }
}

// Eigth command - end the user interactions
class EndCommand implements Command {
  public EndCommand() {}

  @Override
  public void update(Day d) {}

  public void execute(Scanner scan) {
    System.out.println("\nGood-Bye\n");
  }
}