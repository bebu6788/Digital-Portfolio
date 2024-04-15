public class App {
  private static boolean runTests = false;
  public static void main(String[] args) throws Exception {
    // initialize company and run simulation
    // **Identity: creating an FNCD object with the identity 'company'
    if (!runTests) {
      FNCD company = new FNCD();
      company.runSimulation(30);
    } else {
      FNCDTest test = new FNCDTest(); // testing of FNCD
      test.defaultTests();
    }

  }
}
