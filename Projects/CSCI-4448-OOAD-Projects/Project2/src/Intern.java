import java.util.Random;

public class Intern extends Staff {
  private int vehiclesWashed;
  private double salary;

  // constructor
  public Intern() {
    super();
    vehiclesWashed = 0;
    salary = 100;
    stringify = "Intern";
  }

  // getters and setters
  public int getVehiclesWashed() { return vehiclesWashed; }
  public void addWash() { vehiclesWashed++; }
  public void resetWashes() { vehiclesWashed = 0; }
  public double getSalary() { return salary; }
  public void setSalary(double pay) { salary = pay; }

  // based on the type of vehicle, v, they wash, add the wash Bonus amt
  public void addWashBonus(Vehicle v) {
    addBonus(v.getBonusAmount());
  }

  // Takes in a vehicle, v, and the intern attempts to wash it and improve the cleanliness
  public void washVehicle(Vehicle v) {
    Random rand = new Random();
    int r =  rand.nextInt(100 - 1) + 1;

    if (v.getCleannliness() == CleanType.Dirty) { // if the vehicle is dirty theres an 80% chance it becomes clean
      if (r <= 80) {
        v.setCleannliness(CleanType.Clean);
      } else if (r <= 90) { // if the vehicle is dirty theres an 10% chance it becomes sparkling
        v.setCleannliness(CleanType.Sparkling);
        addWashBonus(v); // add the bonus amt to the intern for making a vehicle sparkle
      }
    } else {
      if (r <= 5) { // if the vehicle is clean theres an 5% chance it becomes dirty 
        v.setCleannliness(CleanType.Dirty);
      } else if (r <= 35) { // if the vehicle is clean theres an 30% chance it becomes sparkling 
        v.setCleannliness(CleanType.Sparkling);
        addWashBonus(v); // add the bonus amt to the intern for making a vehicle sparkle
      }
    }

    if (v.getCleannliness() == CleanType.Sparkling) {
      System.out.println("Intern " + getName() + " washed " + v.getName() +
                         " and made it " + v.getCleannliness() + " (earned $" 
                         + v.getBonusAmount() + ")\n");
    } else {
      System.out.println("Intern " + getName() + " washed " + v.getName() +
                         " and made it " + v.getCleannliness() + "\n");
    }
    
    addWash(); // call addWash() to increment the number of vehicles washed by the intern
  }
}
