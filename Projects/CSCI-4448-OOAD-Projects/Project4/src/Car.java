// **Inheritance: the Car class extends Vehichle and therefor inherits its attributes and methods
public class Car extends Vehicle {

  public Car() {
    name = generateVehicleName(); // **Polymorphism: different names are generated for each vehicle type
    number++;
    salePrice = 0;
    condition = randomCondition();
    cleanliness = randomCleanliness();
    wins = 0;
    racePos = RaceOutcome.NA;
    setCost(10000, 20000);
    bonusAmount = 10;
    stringify = "Car";
  }

  // Names the vehicle based on the type of vehicle and the number it is assigned when bought by FNCD
  @Override
  public String generateVehicleName() {
    return "Car" + Integer.toString(getNumber());
  }
}
