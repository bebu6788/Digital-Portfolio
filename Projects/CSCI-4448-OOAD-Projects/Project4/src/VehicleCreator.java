// *** Factory Pattern ***
public abstract class VehicleCreator {
  protected abstract Vehicle createVehicle(); // method to be overwritten in concrete creators
}

// returns a Car object
class CarCreator extends VehicleCreator {
  public Vehicle createVehicle() {
    return new Car();
  }
}


// returns a Performance Car object
class PerformanceCarCreator extends VehicleCreator {
  public Vehicle createVehicle() {
    return new PerformanceCar();
  }
}


// returns a Pickup object
class PickupCreator extends VehicleCreator {
  public Vehicle createVehicle() {
    return new Pickup();
  }
}


// returns a EV object
class ElectricCarCreator extends VehicleCreator {
  public Vehicle createVehicle() {
    return new ElectricCar();
  }
}


// returns a Motorcycle object
class MotorcycleCreator extends VehicleCreator {
  public Vehicle createVehicle() {
    return new Motorcycle();
  }
}


// returns a Monster truck object
class MonsterTruckCreator extends VehicleCreator {
  public Vehicle createVehicle() {
    return new MonsterTruck();
  }
}


// returns a Hyper Car object
class HyperCarCreator extends VehicleCreator {
  public Vehicle createVehicle() {
    return new HyperCar();
  }
}


// returns a Van object
class VanCreator extends VehicleCreator {
  public Vehicle createVehicle() {
    return new Van();
  }
}


// returns a Limo object
class LimousineCreator extends VehicleCreator {
  public Vehicle createVehicle() {
    return new Limousine();
  }
}