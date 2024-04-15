import java.util.ArrayList;
import java.util.Random;

public class Driver extends Staff {
  private int numRaces;
  private double salary;
  private int wins;
  private boolean isInjured;
  private Vehicle raceCar;

  // constructor
  public Driver() {
    super();
    numRaces = 0;
    salary = 2000.0;
    stringify = "Driver";
    wins = 0;
    isInjured = false;
  }

  // param constructor
  public Driver(String name, double be, double ie, int days) {
    super(name, be, ie, days);
    numRaces = 0;
    salary = 2000.0;
    stringify = "Driver";
  }

  // getters and setters
  public int getNumRaces() { return numRaces; }
  public void addRace() { numRaces++; }
  public double getSalary() { return salary; }
  public void setSalary(double pay) { salary = pay; }
  public int getWins() { return wins; }
  public boolean getIsInjured() { return isInjured; }
  public Vehicle getRaceCar() { return raceCar; }
  public void setRaceCar(Vehicle v) { raceCar = v; }

  // add bonus for when they win a race with a vehicle
  public void addRaceBonus() {
    addBonus(raceCar.getBonusAmount());
  }

  // The driver will race and based on their outcome they can become injured, or get a bonus.
  // The cars condition and race outcome can also be affected based on the outcome of a race
  public boolean performRace(int placing, FNCD dealer) {
    boolean ret = false;
    String str = "";

    // If the driver gets a top 3 finish set the cars race outcome to winner and pay the driver a bonus
    if (placing <= 3) { 
      wins++;
      raceCar.addWin();
      raceCar.setRaceOutcome(RaceOutcome.Winner);
      addRaceBonus();
      str = "Driver " + getName() + " placed " + Integer.toString(placing)
              + " in today's race using " + raceCar.getName() + " (earned $"
              + raceCar.getBonusAmount() + ")\n";
      ret = true;
    } 

    // If the driver gets a bottom 5 placement the car becomes broken and damaged and the driver has a change of becoming injured
    else if (placing >= 15) {
      raceCar.setRaceOutcome(RaceOutcome.Damaged);
      raceCar.setCondition(ConditionType.Broken);
      if ((new Random().nextInt(100-1) + 1) <= 30) { // 30% chance of injury with a bottom 5 placement
        isInjured = true;
      }
      str = "Driver " + getName() + " placed " + Integer.toString(placing)
            + " in today's race using " + raceCar.getName() + "\n";
    } else {
      str = "Driver " + getName() + " placed " + Integer.toString(placing)
            + " in today's race using " + raceCar.getName() + "\n";
    }

    System.out.println(str);
    dealer.publishEvent(new Event(str, 0, 0, dealer.getDays())); // Publish the race outcome as an event to the subscribers
    addRace();
    return ret;
  }

  public void selectRaceCar(ArrayList<Vehicle> v) {
    raceCar = v.get(0);
    v.remove(0);
  }
}
