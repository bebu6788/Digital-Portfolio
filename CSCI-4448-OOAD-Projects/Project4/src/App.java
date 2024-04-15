import java.util.ArrayList;

public class App {
  private static boolean runTests = false;
  public static void main(String[] args) throws Exception {
    // initialize company and run simulation
    // **Identity: creating an FNCD object with the identity 'company'
    if (!runTests) {
      // initialize the two FNCD companies North and South
      FNCD company = new FNCD();
      company.setName("FNCD North");
      FNCD company2 = new FNCD();
      company2.setName("FNCD South");

      // get Singleton simulator object to run simulation
      Simulator sim = Simulator.getInstance();
      sim.setDealerships(new ArrayList<FNCD>() {{add(company); add(company2);}});
      sim.runSimulation(30);
    } else {
      FNCDTest test = new FNCDTest(); // testing of FNCD
      test.defaultTests();
    }

  }
}
