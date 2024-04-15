// *** Decorator Pattern ***
// Add ons will extend vehicle so that we can overwrite the sales price and description based on which add-ons the buyer chooses
public abstract class AddOnDecorator extends Vehicle{
  Vehicle vehicle;

  public abstract double addSalesPrice();
  public abstract String getDescription();
};

class ExtendedWarranty extends AddOnDecorator {

  public ExtendedWarranty(Vehicle v) {
    vehicle = v;
    name = generateVehicleName();
    salePrice = addSalesPrice();
    condition = v.getCondition();
    cleanliness = v.getCleannliness();
    wins = v.getWins();
    racePos = v.getRaceOutcome();
    cost = v.getCost();
    bonusAmount = v.getBonusAmount();
    stringify = getDescription();
  }

  // Override the description, Name, and Sales price to account for extended warrenty package
  @Override
  public String getDescription() {
    return "Extended Warranty" + vehicle.getStringify();
  }

  @Override
  public String generateVehicleName() {
    return "ExtWar " + vehicle.getName();
  }

  @Override
  public double addSalesPrice() { // note we assume that there is compounding price for add-ons
    double price = vehicle.getPrice();
    price *= 1.2; // extended warrenty increases the sales price by 20%
    return price;
  }
}

class Undercoating extends AddOnDecorator {

  public Undercoating(Vehicle v) {
    vehicle = v;
    name = generateVehicleName();
    salePrice = addSalesPrice();
    condition = v.getCondition();
    cleanliness = v.getCleannliness();
    wins = v.getWins();
    racePos = v.getRaceOutcome();
    cost = v.getCost();
    bonusAmount = v.getBonusAmount();
    stringify = getDescription();
  }

  // Override the description, Name, and Sales price to account for Undercoating package
  @Override
  public String getDescription() {
    return "Undercoating" + vehicle.getStringify();
  }

  @Override
  public String generateVehicleName() {
    return "Undercoat " + vehicle.getName();
  }

  @Override
  public double addSalesPrice() { // note we assume that there is compounding price for add-ons
    double price = vehicle.getPrice();
    price *= 1.05; // Undercoating increases the sales price by 5%
    return price;
  }
}

class RoadRescueCoverage extends AddOnDecorator {

  public RoadRescueCoverage(Vehicle v) {
    vehicle = v;
    name = generateVehicleName();
    salePrice = addSalesPrice();
    condition = v.getCondition();
    cleanliness = v.getCleannliness();
    wins = v.getWins();
    racePos = v.getRaceOutcome();
    cost = v.getCost();
    bonusAmount = v.getBonusAmount();
    stringify = getDescription();
  }

  // Override the description, Name, and Sales price to account for Road Rescue package
  @Override
  public String getDescription() {
    return "Road Rescue Coverage" + vehicle.getStringify();
  }

  @Override
  public String generateVehicleName() {
    return "RRC " + vehicle.getName();
  }

  @Override
  public double addSalesPrice() { // note we assume that there is compounding price for add-ons
    double price = vehicle.getPrice();
    price *= 1.02; // RoadRescueCoverage increases the sales price by 2%
    return price;
  }
}

class SatelliteRadio extends AddOnDecorator {

  public SatelliteRadio(Vehicle v) {
    vehicle = v;
    name = generateVehicleName();
    salePrice = addSalesPrice();
    condition = v.getCondition();
    cleanliness = v.getCleannliness();
    wins = v.getWins();
    racePos = v.getRaceOutcome();
    cost = v.getCost();
    bonusAmount = v.getBonusAmount();
    stringify = getDescription();
  }

  // Override the description, Name, and Sales price to account for radio package
  @Override
  public String getDescription() {
    return "Satellite Radio" + vehicle.getStringify();
  }

  @Override
  public String generateVehicleName() {
    return "SatRadio " + vehicle.getName();
  }

  @Override
  public double addSalesPrice() { // note we assume that there is compounding price for add-ons
    double price = vehicle.getPrice();
    price *= 1.05; // Satellite Radio increases the sales price by 5%
    return price;
  }
}