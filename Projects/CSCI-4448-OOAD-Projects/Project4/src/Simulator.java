import java.util.ArrayList;

public class Simulator {
  int dayCount;
  ArrayList<FNCD> dealerships;
  private static Simulator instance;

  // constructor
  private Simulator() {
    dayCount = 0;
    dealerships = new ArrayList<FNCD>();
  }

  // Singletone - get unique instance of the simulator
  public static Simulator getInstance() {
    if (instance == null) {
      instance = new Simulator();
    }

    return instance;
  }

  // set the dealerships
  public void setDealerships(ArrayList<FNCD> dealers) {
    dealerships = dealers;
  }

  // Runs the dealership simulation for 'n' days
  public void runSimulation(int n) {
    Tracker track = Tracker.getInstance(); // retrieve instance of tracker object
    for (FNCD dealer: dealerships) {
      dealer.addSubscriber(track); // add the tracker to the subject's subscriber list
    }

    while (dayCount <= n) { // changed so last day can be interactive selling - Day 31
      Day d = new Day(dealerships);
      dayCount++;
      Logger log = Logger.getInstance(); // retreive instance of logger object
      for (FNCD dealer: dealerships) {
        dealer.addSubscriber(log); // add the logger to the subject's subscriber list
        dealer.addDay();
      }

      // since both dealerships are going to have the same observers only one dealership needs
      // to publish this day header event
      System.out.println("*** FNCD Day " + Integer.toString(dayCount) + " ***");
      dealerships.get(0).publishEvent(new Event("*** FNCD Day " + Integer.toString(dayCount) + " ***", 0, 0, dayCount)); // Publish the event to the subscribers
      d.simulateDay();
      for (FNCD dealer : dealerships) {
        dealer.removeSubscriber(log);
      }
      track.printDaily(); // Print to the tracker observer each day
    }
  }
}
