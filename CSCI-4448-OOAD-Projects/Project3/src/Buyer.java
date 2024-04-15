import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Buyer {
  private BuyingType buyType;
  private Vehicle vehiclePreference;
  private double chanceOfBuy;

  // constructor
  public Buyer() {
    buyType = randomBuyType();
    vehiclePreference = randomPreference();
    setChance();
  }

  public BuyingType getBuyType() { return buyType; }
  public Vehicle getPreference() { return vehiclePreference; }
  public double getBuyChance() { return chanceOfBuy; }

  // Sets the % chance of buying a car based on the buyType of the buyer
  public void setChance() {
    if (buyType == BuyingType.JustLooking) {
      chanceOfBuy = 0.1;
    } else if (buyType == BuyingType.WantsOne) {
      chanceOfBuy = 0.4;
    } else {
      chanceOfBuy = 0.7;
    }
  }

  // Randomly selects a BuyType for each new buyer
  public BuyingType randomBuyType() {
    // https://stackoverflow.com/questions/1972392/pick-a-random-value-from-an-enum
    Random rand = new Random();
    List<BuyingType> b = Collections.unmodifiableList(Arrays.asList(BuyingType.values()));

    return b.get(rand.nextInt(b.size()));
  }

  // generates a random vehicle preference for each new buyer
  public Vehicle randomPreference() {
    // https://stackoverflow.com/questions/1972392/pick-a-random-value-from-an-enum
    Random rand = new Random();
    ArrayList<String> v = new ArrayList<String>() {
      {
        add("Car");
        add("PerfCar");
        add("Pickup");
        add("EV");
        add("Motor");
        add("Monster");
      }
    };

    String str = v.get(rand.nextInt(v.size()));

    if (str == "Car") {
      return new Car();
    } else if (str == "PerfCar") {
      return new PerformanceCar();
    } else if ( str == "Pickup") {
      return new Pickup();
    } else if (str == "EV") {
      return new ElectricCar();
    } else if ( str == "Motor") {
      return new Motorcycle();
    } else {
      return new MonsterTruck();
    }
  }

}

enum BuyingType {
  JustLooking,
  WantsOne,
  NeedsOne
}
