import java.util.Random;

public class ElectricCar extends Vehicle{
  private int range;

  public ElectricCar() {
    name = generateVehicleName(); // **Polymorphism: different names are generated for each vehicle type
    number++;
    salePrice = 0;
    condition = randomCondition();
    cleanliness = randomCleanliness();
    wins = 0;
    racePos = RaceOutcome.NA;
    setCost(30000, 45000); // assumption - cost ranges
    bonusAmount = 10;
    stringify = "EV";
    range = randomRange();
    // add 100 to the range if the car arrives LikeNew
    if (super.getCondition() == ConditionType.LikeNew) {
      updateRange();
    }
  }

  public int getRange() { return range; }
  public void setRange(int r) { range = r; }
  public void updateRange() { range += 100; }

  // Randomly set range of the EV to anywhere between 60 - 400 miles
  public int randomRange() {
    Random rand = new Random();
    return rand.nextInt(400-60) + 60;
  }

  // Names the vehicle based on the type of vehicle and the number it is assigned when bought by FNCD
  @Override
  public String generateVehicleName() {
    return "EV" + Integer.toString(getNumber());
  }
}
