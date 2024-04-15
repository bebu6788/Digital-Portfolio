import java.util.Random;

// **Inheritance: the Car class extends Vehichle and therefor inherits its attributes and methods
public class Car extends Vehicle {
  private double cost;
  private double bonusAmount;

  public Car() {
    super();
    setCost();
    bonusAmount = 10;
    stringify = "Car";
  }

  public double getBonusAmount() { return bonusAmount; }
  public double getCost() { return cost; }

  // Randomly sets the cost between $10k and $20k
  public void setCost() { 
    // https://www.baeldung.com/java-generating-random-numbers-in-range
    Random random = new Random();
    cost = random.nextInt(20000 - 10000) + 10000; 
    updateCost();
  } 

  // Updates the salesprice of the vehicle based on its condition
  public void updateCost() {
    ConditionType c = getCondition();

    if (c == ConditionType.Broken) { // broken is 50% off
      cost -= (cost * 0.5);
    } else if (c == ConditionType.Used) { // Used is 20% off
      cost -= (cost * 0.2);
    }

    setSalesPrice(cost);
  }
  
  // Names the vehicle based on the type of vehicle and the number it is assigned when bought by FNCD
  public String generateVehicleName() {
    return "Car" + Integer.toString(getNumber());
  }
}
