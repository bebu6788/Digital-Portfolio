// ***** SINGLETON ******

import java.util.ArrayList;

// Object to resemble the user of the application
public class Player {
  private String name;
  private int gamesWon;
  private int gamesPlayed;
  private static Player playerObj;

  // Getters
  String getName() { return name; }
  int getGamesWon() { return gamesWon; }
  int getGamesPlayed() { return gamesPlayed; }

  // Constructor
  private Player() {
    name = "";
    gamesWon = 0;
    gamesPlayed = 0;
  }

  // Gets an instance of the singleton player
  public static Player getInstance() {
    if (playerObj == null) {
      playerObj = new Player();
    }

    return playerObj;
  }

  // Setter Changes the players name
  public void changeName(String n) { name = n; }

  // Updates the players information for the leaderboard to use
  public void updatePlayerInfo(ArrayList<String> info) {
    if (info != null) {
      gamesWon = Integer.parseInt(info.get(1));
      gamesPlayed = Integer.parseInt(info.get(2));
    }
  }
}
