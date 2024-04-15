import java.util.ArrayList;
import java.util.Random;

public abstract class Staff {
  private String name;
  private double bonusEarned;
  private double incomeEarned;
  private int daysWorked;
  protected String stringify;

  // added a bunch of names to an arraylist for when we hire employees
  private static ArrayList<String> names = new ArrayList<String>() {
    {
      add("Bob"); add("Jeff"); add("Emily"); add("Billy");
      add("Jeffery"); add("Zach"); add("Daquan"); add("Amanda");
      add("Joe"); add("Bella"); add("Henry"); add("Jack"); 
      add("Sophia"); add("Sam"); add("Carmen"); add("Jose");
      add("Gavin"); add("Ben"); add("Kevin"); add("Charlie");
      add("Will"); add("Kathryn"); add("Adin"); add("Monte");
      add("Brant"); add("Roxie"); add("Oliver"); add("Harry");
      add("Hannah"); add("Holly"); add("Jacob"); add("Ethan");
      add("Love"); add("Faith"); add("Hope"); add("Destiny");
      add("Daquarius"); add("Tron"); add("Bobby"); add("Ryan");
      add("Solomon"); add("Chang"); add("Tristan"); add("William"); 
      add("Bryan"); add("Don"); add("Brett"); add("Zander");
      add("Moses"); add("Mary"); add("Anne"); add("Frank");
      add("Alberto"); add("Braden"); add("Cat"); add("Derrek");
      add("Eliza"); add("Garrett"); add("Holden"); add("Isabele");
      add("John"); add("Kensley"); add("Livy"); add("Matthew");
      add("Nora"); add("Otto"); add("Peter"); add("Quantavius");
      add("Roger"); add("Silva"); add("Tiana"); add("Umar");
      add("Violet"); add("Wexler"); add("Xavier"); add("Yin");
      add("Zoey"); add("Ariana"); add("Kanye"); add("Pete");

    }
  };

  // constructor
  public Staff() {
    name = generateName();
    bonusEarned = 0.0;
    incomeEarned = 0.0;
    daysWorked = 0;
  }

  // param constructor
  public Staff(String n, double bonus, double income, int days) {
    name = n;
    bonusEarned = bonus;
    incomeEarned = income;
    daysWorked = days;
  }

  // getters and setters
  public String getName() { return name; }
  public void setName(String n) { name = n; }
  public double getBonusEarned() { return bonusEarned; }
  public void addBonus(double bonus) { bonusEarned += bonus; }
  public double getIncomeEarned() { return incomeEarned; }
  public void addIncome(double pay) { incomeEarned += pay; }
  public int getDaysWorked() { return daysWorked; }
  public void addDayWorked() { daysWorked++; }
  public abstract double getSalary(); // get salary of staff member
  public String getStringify() { return stringify;}

  // randomly select the name from the list of names
  public String generateName() {
    Random rand = new Random();
    int r = rand.nextInt(names.size());

    String n = names.get(r);
    names.remove(n); //remove a name once selected so if they quit we wont re-hire the same person
    return n;
  }
}
