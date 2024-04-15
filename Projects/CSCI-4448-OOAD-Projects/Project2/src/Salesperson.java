public class Salesperson extends Staff {
  private int vehiclesSold;
  private double salary;

  // constructor
  public Salesperson() {
    super();
    vehiclesSold = 0;
    salary = 500;
    stringify = "Sales";
  }

  // param constructor
  public Salesperson(String name, double be, double ie, int days) {
    super(name, be, ie, days);
    vehiclesSold = 0;
    salary = 500;
    stringify = "Sales";
  }

  // getters and setters
  public int getVehiclesSold() { return vehiclesSold; }
  public void addSell() { vehiclesSold++; }
  public double getSalary() { return salary; }
  public void setSalary(double pay) { salary = pay; }
  
  // based on the type of vehicle, v, they sell, add the sales Bonus amt
  public void calculateSaleBonus(Vehicle v) {
    addBonus(v.getBonusAmount());
  }
}
