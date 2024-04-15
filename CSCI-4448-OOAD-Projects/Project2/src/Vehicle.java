import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class Vehicle {
  private String name;
  private static int number = 0;
  private double salePrice;
  private ConditionType condition;
  private CleanType cleanliness;
  protected String stringify;

  // constructor
  public Vehicle() {
    name = generateVehicleName(); // **Polymorphism: different names are generated for each vehicle type
    number++;
    salePrice = 0;
    condition = randomCondition();
    cleanliness = randomCleanliness();
  }

  public String getVehicleName() { return name; }
  public double getPrice() { return salePrice; }
  public int getNumber() { return number; }
  public ConditionType getCondition() { return condition; }
  public CleanType getCleannliness() { return cleanliness; }
  public void setCondition(ConditionType ct) { condition = ct; }
  public void setCleannliness(CleanType cl) { cleanliness = cl; }
  public void setSalesPrice(double cost) { salePrice = cost * 2; }  // sales price is set to double the cost
  public void updateSalesPrice(double mult) { salePrice *= mult; }  // sales price increases based on qualities
  public String getStringify() { return stringify; }
  public String getName() { return name; }
  public abstract double getCost();
  public abstract double getBonusAmount();
  

  public abstract String generateVehicleName(); // generates the name for vehicle by type

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
  Dirty
}
