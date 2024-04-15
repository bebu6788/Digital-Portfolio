import java.util.Random;

public class Mechanic extends Staff {
  private int vehiclesRepaired;
  private double salary;

  // constructor
  public Mechanic() {
    super();
    vehiclesRepaired = 0;
    salary = 1000;
    stringify = "Mech";
  }

  // param constructor
  public Mechanic(String name, double be, double ie, int days) {
    super(name, be, ie, days);
    vehiclesRepaired = 0;
    salary = 1000;
    stringify = "Mech";
  }

  // getters and setters
  public int getVehiclesRepaired() { return vehiclesRepaired; }
  public void addRepair() { vehiclesRepaired++; }
  public void resetRepairs() { vehiclesRepaired = 0; }
  public double getSalary() { return salary; }
  public void setSalary(double pay) { salary = pay; }

  // based on the type of vehicle, v, they repair, add the repair Bonus amt
  public void addRepairBonus(Vehicle v) {
    addBonus(v.getBonusAmount());
  }

  // **Cohesion: mechanics work on vehicles to repair them and receive money for the day
  // Takes in a vehicle, v, and the mechanic attempts to repair its condition
  public void repairVehicle(Vehicle v) {
    Random rand = new Random();
    int r =  rand.nextInt(100 - 1) + 1;

    if (r <= 80) { // 80% chance of repairing any vehicle
      if (v.getCondition() == ConditionType.Broken) { // if the vehicle is broken, its repaired to used
        v.setCondition(ConditionType.Used);
        v.updateSalesPrice(1.5); // when repaired to used sales price goes up 50%
      } else if (v.getCondition() == ConditionType.Used) { // if the vehicle is used, its repaired to like new
        v.setCondition(ConditionType.LikeNew);
        v.updateSalesPrice(1.25); // when repaired to like new sales price goes up 25%
      }
      addRepairBonus(v); // add the repair bonus for the mechanic

      System.out.println("Mechanic " + getName() + " repaired " + v.getName() +
                         " and made it " + v.getCondition().toString() + " (earned $" 
                         + v.getBonusAmount() + ")\n");
    }

    // all repaired cars go down one tier in cleanliness
    if (v.getCleannliness() == CleanType.Sparkling) {
      v.setCleannliness(CleanType.Clean);
    } else if (v.getCleannliness() == CleanType.Clean) {
      v.setCleannliness(CleanType.Dirty);
    }

    addRepair(); // call addRepair() to increment the number of vehicles repaired by the mechanic
  }
}
