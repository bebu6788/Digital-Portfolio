public class Pickup extends Vehicle {

  public Pickup() {
    name = generateVehicleName(); // **Polymorphism: different names are generated for each vehicle type
    number++;
    salePrice = 0;
    condition = randomCondition();
    cleanliness = randomCleanliness();
    wins = 0;
    racePos = RaceOutcome.NA;
    setCost(10000, 40000);
    bonusAmount = 25;
    stringify = "Pickup";
  }

  // Names the vehicle based on the type of vehicle and the number it is assigned when bought by FNCD
  @Override
  public String generateVehicleName() {
    return "Pickup" + Integer.toString(getNumber());
  }
}
