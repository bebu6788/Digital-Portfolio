import java.util.Random;

// *** Strategy Pattern ***
public interface WashBehavior {
  public abstract boolean performWash(Vehicle v); // make the abstract definition of wash() so each strategy type will override 
}

// First Strategy: Chemical Wash
class Chemical implements WashBehavior {

  @Override
  public boolean performWash(Vehicle v) {
    // generate random number between 1 - 100
    Random rand = new Random();
    int r = rand.nextInt(100 - 1) + 1;
    boolean isSparkle = false;

    if (v.getCleannliness() == CleanType.Dirty) { // if the vehicle is dirty theres an 80% chance it becomes clean
      if (r <= 80) {
        v.setCleannliness(CleanType.Clean);
      } else if (r <= 90) { // if the vehicle is dirty theres an 10% chance it becomes sparkling
        v.setCleannliness(CleanType.Sparkling);
        isSparkle = true; // add the bonus amt to the intern for making a vehicle sparkle
      }
    } else {
      if (r <= 10) { // if the vehicle is clean theres an 10% chance it becomes dirty
        v.setCleannliness(CleanType.Dirty);
      } else if (r >= 80) { // if the vehicle is clean theres an 20% chance it becomes sparkling
        v.setCleannliness(CleanType.Sparkling);
        isSparkle = true; // add the bonus amt to the intern for making a vehicle sparkle
      }
    }

    Random rand1 = new Random();
    int r1 = rand1.nextInt(10 - 1) + 1;

    if (r1 == 10) { // 10% chance a vehicle becomes broken after wash
      v.setCondition(ConditionType.Broken);
    }
    return isSparkle;
  }
}

// Second Strategy: ElbowGrease Wash
class ElbowGrease implements WashBehavior {

  @Override
  public boolean performWash(Vehicle v) {
    // generate random number between 1 - 100
    Random rand = new Random();
    int r = rand.nextInt(100 - 1) + 1;
    boolean isSparkle = false;

    if (v.getCleannliness() == CleanType.Dirty) { // if the vehicle is dirty theres an 70% chance it becomes clean
      if (r <= 70) {
        v.setCleannliness(CleanType.Clean);
      } else if (r >= 95) { // if the vehicle is dirty theres an 5% chance it becomes sparkling
        v.setCleannliness(CleanType.Sparkling);
        isSparkle = true; // add the bonus amt to the intern for making a vehicle sparkle
      }
    } else {
      if (r <= 15) { // if the vehicle is clean theres an 15% chance it becomes dirty
        v.setCleannliness(CleanType.Dirty);
      } else if (r >= 85) { // if the vehicle is clean theres an 15% chance it becomes sparkling
        v.setCleannliness(CleanType.Sparkling);
        isSparkle = true; // add the bonus amt to the intern for making a vehicle sparkle
      }
    }

    Random rand1 = new Random();
    int r1 = rand1.nextInt(10 - 1) + 1;

    if (r1 == 10) { // 10% chance a vehicle becomes likenew after wash
      v.setCondition(ConditionType.LikeNew);
    }
    return isSparkle;
  }
}

// Third Strategy: Detailed Wash
class Detailed implements WashBehavior {

  @Override
  public boolean performWash(Vehicle v) {
    // generate random number between 1 - 100
    Random rand = new Random();
    int r = rand.nextInt(100 - 1) + 1;
    boolean isSparkle = false;

    if (v.getCleannliness() == CleanType.Dirty) { // if the vehicle is dirty theres an 60% chance it becomes clean
      if (r <= 60) {
        v.setCleannliness(CleanType.Clean);
      } else if (r >= 80) { // if the vehicle is dirty theres an 20% chance it becomes sparkling
        v.setCleannliness(CleanType.Sparkling);
        isSparkle = true; // add the bonus amt to the intern for making a vehicle sparkle
      }
    } else {
      if (r <= 5) { // if the vehicle is clean theres an 5% chance it becomes dirty
        v.setCleannliness(CleanType.Dirty);
      } else if (r >= 60) { // if the vehicle is clean theres an 40% chance it becomes sparkling
        v.setCleannliness(CleanType.Sparkling);
        isSparkle = true; // add the bonus amt to the intern for making a vehicle sparkle
      }
    }

    return isSparkle;
  }
}