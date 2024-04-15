import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class Vehicle {
  String name;
  protected static int number = 0;
  protected double salePrice;
  protected ConditionType condition;
  protected CleanType cleanliness;
  protected double cost;
  protected int wins;
  protected RaceOutcome racePos;
  protected double bonusAmount;
  protected String stringify;

  public double getPrice() { return salePrice; }
  public int getNumber() { return number; }
  public ConditionType getCondition() { return condition; }
  public CleanType getCleannliness() { return cleanliness; }
  public void setCondition(ConditionType ct) { condition = ct; }
  public void setCleannliness(CleanType cl) { cleanliness = cl; }
  public void setSalesPrice(double price) { salePrice = price; }  // sales price is set to double the cost
  public void updateSalesPrice(double mult) { salePrice *= mult; }  // sales price increases based on qualities
  public String getStringify() { return stringify; }
  public String getName() { return name; }
  public double getBonusAmount() { return bonusAmount; }
  public double getCost() { return cost; }
  public int getWins() { return wins; }
  public void addWin() { wins++; }
  public RaceOutcome getRaceOutcome() { return racePos; }
  public void setRaceOutcome(RaceOutcome ro) { racePos = ro; }

  public abstract String generateVehicleName(); // generates the name for vehicle by type

  // Randomly sets the cost between $20k and $40k
  public void setCost(int low, int high) {
    Random random = new Random();
    cost = random.nextInt(high - low) + low;
    updateCost();
  } // source: https://www.baeldung.com/java-generating-random-numbers-in-range

// Updates the salesprice of the vehicle based on its condition
  public void updateCost() {
    ConditionType c = getCondition();

    if (c == ConditionType.Broken) { // broken is 50% off
      cost -= (cost * 0.5);
    } else if (c == ConditionType.Used) { // Used is 20% off
      cost -= (cost * 0.2);
    }

    setSalesPrice(cost * 2);
  }

  // Randomly selects the condition of a vehicle
  public ConditionType randomCondition() {
    // https://stackoverflow.com/questions/1972392/pick-a-random-value-from-an-enum
    Random rand = new Random();
    List<ConditionType> c = Collections.unmodifiableList(Arrays.asList(ConditionType.values()));

    return c.get(rand.nextInt(c.size()));
  }

  // Randomly selects the Cleanliness of a vehicle
  public CleanType randomCleanliness() {
    Random rand = new Random();
    int r = rand.nextInt(100 - 1) + 1;

    if (r <= 5) { //5% chance of sparkling
      return CleanType.Sparkling;
    } else if (r <= 40) { //35% chance clean
      return CleanType.Clean;
    } else { //60% chance dirty
      return CleanType.Dirty;
    }
  }
}

enum ConditionType {
  LikeNew,
  Used,
  Broken;
}

enum CleanType {
  Sparkling,
  Clean,
  Dirty;
}

// New enum for raced vehicles
enum RaceOutcome {
  Winner,
  NA,
  Damaged;
}
