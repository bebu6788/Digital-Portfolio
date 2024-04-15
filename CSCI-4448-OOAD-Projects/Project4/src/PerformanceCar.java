public class PerformanceCar extends Vehicle {

  public PerformanceCar() {
    name = generateVehicleName(); // **Polymorphism: different names are generated for each vehicle type
    number++;
    salePrice = 0;
    condition = randomCondition();
    cleanliness = randomCleanliness();
    wins = 0;
    racePos = RaceOutcome.NA;
    setCost(20000, 40000);
    bonusAmount = 50;
    stringify = "PerfCar";
  }

  // Names the vehicle based on the type of vehicle and the number it is assigned when bought by FNCD
  @Override
  public String generateVehicleName() {
    return "PerfCar" + Integer.toString(getNumber());
  }
}
