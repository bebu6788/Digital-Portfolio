import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

// These are a few minimal tests for the FNCD class declaration and use.
// To run the tests I use the provided Run Tests functionality in the VS Code IDE.
// Also run default tests in main function of App.java
public class FNCDTest {
  FNCD dealer = new FNCD();

  @Test // test the constructor of FNCD
  public void defaultTests() {
    assertEquals(dealer.getBudget(), 500000, 0.5);
    assertEquals(dealer.getDays(), 0, 0.0);
    assertFalse(dealer.getStaff().isEmpty());
    assertTrue(dealer.getInventory().isEmpty());
    assertTrue(dealer.getDeparted().isEmpty());
    assertTrue(dealer.getSoldVehicles().isEmpty());
  }

  @Test // test the addVehicles method in FNCD
  public void addVehicleTests() {
    dealer.addVehicle();
    assertEquals(dealer.getInventory().size(), 24, 0);
    assertTrue(dealer.getSoldVehicles().isEmpty());
  }

  @Test // test various add and subtract methods of FNCD
  public void addTests() {
    dealer.addDay();
    assertEquals(dealer.getDays(), 1, 0.0);

    dealer.addToBudget(200000);
    assertEquals(dealer.getBudget(), 700000, 0.0);

    dealer.subFromBudget(200000);
    assertEquals(dealer.getBudget(), 500000, 0.0);

    dealer.updateBudget();  // should not add 250000 if budget > 0
    assertEquals(dealer.getBudget(), 500000, 0.0);

    dealer.subFromBudget(600000);
    dealer.updateBudget();
    assertEquals(dealer.getBudget(), 150000, 0.0);
  }
}
