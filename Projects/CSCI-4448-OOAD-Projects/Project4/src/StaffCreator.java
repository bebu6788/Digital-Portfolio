// *** Factory Pattern ***
public abstract class StaffCreator {
  abstract public Staff createStaff();  // method to be overwritten by concrete creators
}

// returns an Intern object
class InternCreator extends StaffCreator {
  public Staff createStaff() {
    return new Intern();
  }
}

// returns a Mechanic object
class MechanicCreator extends StaffCreator {
  public Staff createStaff() {
    return new Mechanic();
  }
}

// returns a Salesperson object
class SalespersonCreator extends StaffCreator {
  public Staff createStaff() {
    return new Salesperson();
  }
}

// returns a Driver object
class DriverCreator extends StaffCreator {
  public Staff createStaff() {
    return new Driver();
  }
}
