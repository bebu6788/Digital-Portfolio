// Interface for the observer pattern
public interface Observer {
  public void update(int btnCLicked, int move); // standard update
  public void end(String winner); // end of game update
}
