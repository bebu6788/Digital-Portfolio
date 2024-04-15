import java.util.Random;

public class Motorcycle extends Vehicle {

  private double engineSize;

  public Motorcycle() {
    name = generateVehicleName(); // **Polymorphism: different names are generated for each vehicle type
    number++;
    salePrice = 0;
    condition = randomCondition();
    cleanliness = randomCleanliness();
    wins = 0;
    racePos = RaceOutcome.NA;
    setCost(10000, 15000);
    bonusAmount = 10;
    stringify = "Motor";
    engineSize = generateEngineSize();
  }

  public double getEngineSize() { return engineSize; }
  public void setEngineSize(double size) { engineSize = size; }

  public double generateEngineSize() {
    Random rand = new Random();
    double r = 0;

    do {
      r = rand.nextGaussian() * 300 + 700;  // https://canvas.colorado.edu/courses/91069/files/folder/Class%20FIles?preview=59346757
    } while (r < 50);
    return r;
  }

  // Names the vehicle based on the type of vehicle and the number it is assigned when bought by FNCD
  @Override
  public String generateVehicleName() {
    return "Motor" + Integer.toString(getNumber());
  }
}
