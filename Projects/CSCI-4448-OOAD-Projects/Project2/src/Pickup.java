import java.util.Random;

public class Pickup extends Vehicle {
  private double cost;
  private double bonusAmount;

  public Pickup() {
    super();
    setCost();
    bonusAmount = 25;
    stringify = "Pickup";
    setSalesPrice(cost);
  }

  public double getBonusAmount() { return bonusAmount; }
  public double getCost() { return cost; }

  // Randomly sets the cost between $10k and $40k
  public void setCost() { 
    Random random = new Random();
    cost = random.nextInt(40000 - 10000) + 10000; 
    updateCost();
  } // https://www.baeldung.com/java-generating-random-numbers-in-range

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
    return "Pickup" + Integer.toString(getNumber());
  }
}
