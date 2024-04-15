import java.util.Random;

public class Limousine extends Vehicle {
  private int length;

  public Limousine() {
    name = generateVehicleName(); // **Polymorphism: different names are generated for each vehicle type
    number++;
    salePrice = 0;
    condition = randomCondition();
    cleanliness = randomCleanliness();
    wins = 0;
    racePos = RaceOutcome.NA;
    setCost(50000, 70000); // assumption - cost ranges
    bonusAmount = 70;
    stringify = "Limo";
    length = randomLength();
  }

  public int getLength() { return length; }
  public void setLength(int l) { length = l; }

  // Randomly set length of the Limo to anywhere between 30 - 100 ft
  public int randomLength() {
    Random rand = new Random();
    return rand.nextInt(100-30) + 30;
  }

  // Names the vehicle based on the type of vehicle and the number it is assigned when bought by FNCD
  @Override
  public String generateVehicleName() {
    return "Limo" + Integer.toString(getNumber());
  }
}
