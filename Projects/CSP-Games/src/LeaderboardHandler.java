import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

// ***** Observer Pattern ****** for Leaderboard
// ***** Singleton Pattern ******
public class LeaderboardHandler implements Observer {
  private static LeaderboardHandler handler;
  private String player;
  private String currGame;
  private String filePath;

  // Constructor
  private LeaderboardHandler() {
    player = "";
    currGame = "";
    filePath = "";
  }

  // Gets an instance of the leaderboard handler
  public static LeaderboardHandler getInstance() {
    if (handler == null) {
      handler = new LeaderboardHandler();
    }

    return handler;
  }

  // Setters
  public void setPlayer(String name) { player = name; }
  public void setGame(String g) { currGame = g; }

  // Reads in the player info csv file to retrive the info to be displayed
  // Parses the file comma seperated and adds it to a list to return
  public ArrayList<String> retrievePlayerInfo() {
    // https://stackoverflow.com/questions/18033750/read-one-line-of-a-csv-file-in-java
    filePath = "./leaderboards/players.csv";
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(filePath));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    ArrayList<String> info = new ArrayList<String>();
    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        info = new ArrayList<String>(Arrays.asList(line.split(",")));
        // check if this is the player we want info for
        if (info.get(0).equals(player)) {
          reader.close();
          return info;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    update(0, 0); // call update()
    return null;
  }

  // Gets the leaderboard based on the input param game
  // Calls sortedBoard() to sort by wins then returns
  public String[][] retrieveBoard(String game) {
    filePath = "./leaderboards/" + game + "Board.csv";
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(filePath));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    ArrayList<String> lines = new ArrayList<String>();
    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return sortedBoard(convertTo2DArray(lines));
  }

  // Converts the lines of the csv file after being read and parsed
  // Returns 2D string array
  private String[][] convertTo2DArray(ArrayList<String> lines) {
    String[][] arr = new String[lines.size()-1][4];

    for (int i = 1; i < lines.size(); i++) {
      ArrayList<String> l = new ArrayList<String>(Arrays.asList(lines.get(i).split(",")));
      for (int j = 0; j < l.size(); j++) {
        arr[i-1][j] = l.get(j);
      }
    }

    return arr;
  }

  // Sorts the leaderbaord for appropriate UI / UX
  // Uses bubble sort algorithm
  private String[][] sortedBoard(String[][] board) {
    // https://stackabuse.com/bubble-sort-in-java/
    boolean sorted = false;
    String[] temp;
    while (!sorted) {
      sorted = true;
      for (int i = 0; i < board.length - 1; i++) {
        if (Integer.parseInt(board[i][1]) < Integer.parseInt(board[i+1][1])) {
          temp = board[i];
          board[i] = board[i+1];
          board[i+1] = temp;
          sorted = false;
        }
      }
    }

    return board;
  }

  // Gets all the players in the leaderbaord csv file
  // Calls convertTo2DArray() then returns
  private String[][] retrieveAllPlayers() {
    filePath = "./leaderboards/players.csv";
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(filePath));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    ArrayList<String> info = new ArrayList<String>();
    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        info.add(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return convertTo2DArray(info);
  }

  // Updated the leaderboard csv file (param file) with the winner
  // param winner is the winner of the game that you are updating their wins / win %
  // Calls retrieveAllPlayers() to update a players stats on the leaderboard
  private void updateFile(String winner, String file) {
    String[][] info = null;
    if (file == "players") {
      info = retrieveAllPlayers();
    } else {
      info = retrieveBoard(file);
    }

    boolean playerFound = false;
    // update the user information to be written back to file
    for (int i = 0; i < info.length; i++) {
      // finding the player in the file
      if (info[i][0].equals(player)) {
        if (winner.equals("Player")) {
          // update win count for this player
          info[i][1] = Integer.toString(Integer.parseInt(info[i][1]) + 1);
        }
        // add one to games played
        info[i][2] = Integer.toString(Integer.parseInt(info[i][2]) + 1);
        // update winPercentage
        info[i][3] = Double.toString(Double.parseDouble(info[i][1]) / Double.parseDouble(info[i][2]));
        playerFound = true;
        break;
      }
    }

    // write all player info back to the file
    // with updated player info
    if (playerFound) {
      try {
        FileWriter fileWriter = new FileWriter(filePath, false);
        fileWriter.write("name,wins,gamesPlayed,win%\n");
        for (int i = 0; i < info.length; i++) {
          fileWriter.write(info[i][0] + "," + info[i][1] + "," + info[i][2] + "," + info[i][3] + "\n" );
        }
        fileWriter.close();
      } catch (IOException e) {
        System.out.println("ERROR with Logger: File Writer");
        e.printStackTrace();
      }
    } else {
      // player has not played this game yet so just add to end of file
      try {
        FileWriter fileWriter = new FileWriter(filePath, true);
        if (winner.equals("Player")) {
          fileWriter.write(player+",1,1,1.0\n");
        } else {
          fileWriter.write(player+",0,1,0.0\n");
        }
        fileWriter.close();
      } catch (IOException e) {
        System.out.println("ERROR with Logger: File Writer");
        e.printStackTrace();
      }
    }

  }

  // Observer Pattern Update
  // tells the leaderboard a new player logged in
  // and needs to be added to file
  @Override
  public void update(int btnClicked, int move) {
    filePath = "./leaderboards/info.csv";
    if (move == 0) {  // update coming to add user name
      // https://stackoverflow.com/questions/59598323/how-to-tell-filewriter-to-close-any-files-before-making-a-new-file-in-java
      try {
        FileWriter fileWriter = new FileWriter(filePath, true);
        fileWriter.write(player + ",0,0,0.0\n");
        fileWriter.close();
      } catch (IOException e) {
        System.out.println("ERROR with Logger: File Writer");
        e.printStackTrace();
      }
    } else {
      return; // don't care about updates during games
    }
  }

  // Calls updateFile with winner and current game
  // Signifies to the leaderboard handler observer that the game is over
  @Override
  public void end(String winner) {
    updateFile(winner, "players");
    updateFile(winner, currGame);
  }
}
