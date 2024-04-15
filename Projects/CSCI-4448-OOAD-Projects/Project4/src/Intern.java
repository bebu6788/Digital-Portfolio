import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Intern extends Staff {
  private int vehiclesWashed;
  private double salary;
  private WashType washType;

  WashBehavior washBehavior;

  // constructor
  public Intern() {
    super();
    vehiclesWashed = 0;
    salary = 100;
    stringify = "Intern";
    washType = randomWashType();
    setWashBehvaior();
  }

  // getters and setters
  public int getVehiclesWashed() { return vehiclesWashed; }
  public WashType getWashType() { return washType; }
  public void addWash() { vehiclesWashed++; }
  public void resetWashes() { vehiclesWashed = 0; }
  public double getSalary() { return salary; }
  public void setSalary(double pay) { salary = pay; }

  // randomly select the interns wash type
  public WashType randomWashType() {
    // https://stackoverflow.com/questions/1972392/pick-a-random-value-from-an-enum
    Random rand = new Random();
    List<WashType> c = Collections.unmodifiableList(Arrays.asList(WashType.values()));

    return c.get(rand.nextInt(c.size()));
  }

  // Use the strategy pattern objects to set the wash Behavior
  public void setWashBehvaior() {
    if (washType == WashType.Chemical) {
      washBehavior = new Chemical();
    } else if (washType == WashType.Detailed) {
      washBehavior = new Detailed();
    } else if (washType == WashType.ElbowGrease) {
      washBehavior = new ElbowGrease();
    }
  }

  // based on the type of vehicle, v, they wash, add the wash Bonus amt
  public void addWashBonus(Vehicle v) {
    addBonus(v.getBonusAmount());
  }

  // Takes in a vehicle, v, and the intern attempts to wash it and improve the cleanliness
  // returns whether a bonus was earned for this washing activity
  public boolean washVehicle(Vehicle v, FNCD dealer) {
    if (washBehavior.performWash(v)) {
      addWashBonus(v);
      System.out.println("Intern " + getName() + " washed " + v.getName() +
                         " using a " + getWashType().toString() + " wash method and made it "
                         + v.getCleannliness() + " (earned $" + v.getBonusAmount() + ")\n");
      dealer.publishEvent(new Event("Intern " + getName() + " washed " + v.getName() +
                         " using a " + getWashType().toString() + " wash method and made it "
                         + v.getCleannliness() + " (earned $" + v.getBonusAmount() + ")\n", 0, 0, dealer.getDays()));
      addWash();
      return true;
    } else {
      System.out.println("Intern " + getName() + " washed " + v.getName() +
                         " using a " + getWashType().toString() + " wash method and made it "
                         + v.getCleannliness() + "\n");
      dealer.publishEvent(new Event("Intern " + getName() + " washed " + v.getName() +
                         " using a " + getWashType().toString() + " wash method and made it "
                         + v.getCleannliness() + "\n", 0, 0, dealer.getDays()));
    }

    addWash(); // call addWash() to increment the number of vehicles washed by the intern
    return false;
  }
}

enum WashType {
  Chemical,
  ElbowGrease,
  Detailed;
}