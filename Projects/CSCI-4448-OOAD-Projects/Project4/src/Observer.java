import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// *** Observer Pattern ***

// Observer interface
interface Observer {
  public void update(Event event);
  // here we pass in an Event which is an object that we create to monitor data changes in FNCD and notify the subscribers
}

// Subject interface
// We will override the functions in the Subject interface in FNCD
interface Subject {
  public void addSubscriber(Observer observer);

  public void removeSubscriber(Observer observer);

  public void publishEvent(Event event);
}

// Event class
// Publish the following events out to subscribers:
// • Adding money to the FNCD budget due to low funds
// • Washing, Repair, and Sales outcomes
// • Race attendance and results
// • Any changes in money (salary, bonus, sales, etc.) for staff or the FNCD
class Event {
  private String description;
  private double staffMoney;
  private double FNCDMoney;
  private int dayNum;


  public Event(String d, double s, double f, int n) {
    description = d;
    staffMoney = s;
    FNCDMoney = f;
    dayNum = n;
  }

  public String getDesc() { return description; }
  public double getStaffMoney() { return staffMoney; }
  public double getFNCDMoney() { return  FNCDMoney; }
  public int getDay() { return dayNum; }
}

// Tracker class - Eager Singleton
// Keeps track of staff earnings, FNCD earnings, and the Day
// Asssumption: staff earnings and FNCD earnings are combined across FNCDs
class Tracker implements Observer {
  double staffEarnings = 0;
  double FNCDEarnings = 0;
  int currDay = 0;
  private static Tracker instance = new Tracker();

  public static Tracker getInstance() {
    return instance;
  }

  // Override the update function update the tracked values in FNCD
  @Override
  public void update(Event event) {
    staffEarnings += event.getStaffMoney();
    FNCDEarnings += event.getFNCDMoney();
    currDay = event.getDay();
  }

  // Print the info to the tracker subscriber each day
  public void printDaily() {
    System.out.println("Tracker: Day " + Integer.toString(currDay));
    System.out.println("Total money earned by all Staff: $" + String.format("%.2f", staffEarnings));
    System.out.println("Total money earned by the FNCDs: $" + String.format("%.2f", FNCDEarnings) + "\n");
  }
}

// Logger class - Lazy Singleton
// Assumption: Ending report is not a published event.
// Assumption: Only one `Logger-n.txt` file is created for both FNCDs
class Logger implements Observer {
  int currDay;
  String filePath;
  private static Logger instance;

  // constructor
  private Logger() {
    currDay = 0;
    filePath = "";
  }

  // get the unique instance of the class
  public static Logger getInstance() {
    if (instance == null) {
      instance = new Logger();
    }

    return instance;
  }

  // create the new day file when currDay is outdated
  private void createDayFile(int day) {
    currDay = day;
    filePath = "./LoggerFiles/Logger-" + Integer.toString(currDay) + ".txt"; // set the file path for the output log to write to

    try { // check if the file exists in the file path, if not create one
       File file = new File(filePath);
      if (file.createNewFile()) {}
    } catch (IOException e) { // error check
      System.out.println("ERROR with Logger: File Creation");
      e.printStackTrace();
    }
  }

  // Override the update function to write to the output txt file
  @Override
  public void update(Event event) {
    if (event.getDesc() == null) { // make sure the event description is not null to avoid printing output runtime errors
      return;
    }

    if (event.getDay() != currDay) {
      createDayFile(event.getDay());
    }

    // https://stackoverflow.com/questions/59598323/how-to-tell-filewriter-to-close-any-files-before-making-a-new-file-in-java
    try {
      FileWriter fileWriter = new FileWriter(filePath, true);
      fileWriter.write("\n" + event.getDesc());
      fileWriter.close();
    } catch (IOException e) {
      System.out.println("ERROR with Logger: File Writer");
      e.printStackTrace();
    }
  }
}
