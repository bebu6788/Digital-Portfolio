import java.util.Random;

public class Van extends Vehicle {
  private int numSeats;

  public Van() {
    name = generateVehicleName(); // **Polymorphism: different names are generated for each vehicle type
    number++;
    salePrice = 0;
    condition = randomCondition();
    cleanliness = randomCleanliness();
    wins = 0;
    racePos = RaceOutcome.NA;
    setCost(35000, 50000); // assumption - cost ranges
    bonusAmount = 50;
    stringify = "Van";
    numSeats = randomNumSeats();
  }

  public int getNumSeats() { return numSeats; }
  public void setNumSeats(int n) { numSeats = n; }

  // Randomly set numSeats of the van to anywhere between 6 - 16 seats
  public int randomNumSeats() {
    Random rand = new Random();
    return rand.nextInt(16-6) + 6;
  }

  // Names the vehicle based on the type of vehicle and the number it is assigned when bought by FNCD
  @Override
  public String generateVehicleName() {
    return "Van" + Integer.toString(getNumber());
  }
}
