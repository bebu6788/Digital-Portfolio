import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HyperCar extends Vehicle {
  private Engine engineType;

  public HyperCar() {
    name = generateVehicleName(); // **Polymorphism: different names are generated for each vehicle type
    number++;
    salePrice = 0;
    condition = randomCondition();
    cleanliness = randomCleanliness();
    wins = 0;
    racePos = RaceOutcome.NA;
    setCost(100000, 200000);
    bonusAmount = 100;
    stringify = "Hyper";
    engineType = generateEngineType();
  }

  public Engine getengineType() { return engineType; }
  public void setengineType(Engine type) { engineType = type; }

  // generates a random engine type for the vehicle
  public Engine generateEngineType() {
    // https://stackoverflow.com/questions/1972392/pick-a-random-value-from-an-enum
    Random rand = new Random();
    List<Engine> e = Collections.unmodifiableList(Arrays.asList(Engine.values()));

    return e.get(rand.nextInt(e.size()));
  }

  // Names the vehicle based on the type of vehicle and the number it is assigned when bought by FNCD
  @Override
  public String generateVehicleName() {
    return "Hyper" + Integer.toString(getNumber());
  }
}

enum Engine {
  Inline4,
  V6,
  V8,
  V12,
  W16
}
