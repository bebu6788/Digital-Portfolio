import java.util.Hashtable;
import java.util.Random;

public class MonsterTruck extends Vehicle {
  private String stageName;
  // Store the names in a hash table for random selection
  static Hashtable<String, Integer> stageNames = new Hashtable<String, Integer>() { {
      put("Air Force Afterburner", 0); put("Avenger", 0); put("Bad News Travels Fast", 0); put("Batman", 0);
      put("Backwards Bob", 0); put("Bear Foot (1979)", 0); put("Bear Foot (F-150)", 0); put("Bear Foot (2xtreme)", 0);
      put("Bear Foot (Silverado)", 0); put("Bear Foot USA", 0); put("Bigfoot", 0); put("Black Stallion", 0);
      put("Blacksmith", 0); put("Blue Thunder", 0); put("Bounty Hunter", 0); put("Brutus", 0);
      put("Bulldozer", 0); put("Captain's Curse", 0); put("Cyborg", 0); put("El Toro Loco", 0);
      put("Game Over", 0); put("Grave Digger", 0); put("Grinder", 0); put("Gunslinger", 0);
      put("Jurrassic Attack", 0); put("King Krunch", 0); put("Lucas Oil Crusader", 0); put("Madusa", 0);
      put("Maximum Destruction", 0); put("Mohawk Warrior", 0); put("Monster Mutt", 0); put("Predator", 0);
    }
  };

  public MonsterTruck() {
    name = generateVehicleName(); // **Polymorphism: different names are generated for each vehicle type
    number++;
    salePrice = 0;
    condition = randomCondition();
    cleanliness = randomCleanliness();
    wins = 0;
    racePos = RaceOutcome.NA;
    setCost(30000, 50000);
    bonusAmount = 10;
    stringify = "Monster";
    stageName = generateStageName();
  }

  public String getStageName() { return stageName; }
  public void setStageName(String name) { stageName = name; }

  // Generates the stage name for the Monster Truck 
  public String generateStageName() {
    String ret = "";
    for (String s: stageNames.keySet()) {
      if (stageNames.get(s) == 0) {
        stageNames.replace(s, 1);
        ret = s;
        break;
      }
    }

    if (ret.isEmpty()) {
      // https://kodejava.org/how-to-get-random-key-value-pair-from-hashtable/
      String[] keys = stageNames.keySet().toArray(new String[stageNames.size()]);
      ret = keys[new Random().nextInt(keys.length)];
      stageNames.replace(ret, stageNames.get(ret)+1);
    }

    return ret;
  }

  // Names the vehicle based on the type of vehicle and the number it is assigned when bought by FNCD
  @Override
  public String generateVehicleName() {
    return "Monster" + Integer.toString(getNumber());
  }
}

// "Shell Camino", "Raminator", "Snake Bite", "Stone Crusher", "Swamp Thing", "The Destroyer", "The Felon", "War Wizard", "WCW Nitro Machine"