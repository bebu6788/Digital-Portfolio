import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// ***** MVC Pattern: Model part *****
// ***** Observer Pattern: Subject *****

// model interface to define functions for being a subject
public interface Model {
  public void addObserver(Observer o);
  public void removeObserver(Observer o);
  public void notifyObservers(int btnClicked, int move, String winner);
}

// Abstract class to take model interface and expand with data members
// and additional functions
abstract class GameModel implements Model {
  protected ArrayList<Observer> observers = new ArrayList<Observer>();
  protected Boolean activePlayer;
  protected Player player;
  protected Computer computer;
  protected Boolean gameOver;
  protected String winner;

  // Getters
  Boolean getActivePlayer() { return activePlayer; }
  Boolean getGameOver() { return gameOver; }

  // adds observer
  @Override
  public void addObserver(Observer o) {
    observers.add(o);
  }

  // removes an observer
  @Override
  public void removeObserver(Observer o) {
    observers.remove(o);
  }

  // Notifies the observers that an update was made
  @Override
  public void notifyObservers(int btnClicked, int move, String winner) {
    if (!winner.isEmpty()) {  // game is not over so just update
      for (Observer o : observers) {
        o.end(winner);
      }
      return;
    }
    // notify that the game has ended
    for (Observer o : observers) {
      o.update(btnClicked, move);
    }
  }

  abstract public void startGame(); // starts the game
  abstract public void endGame();   // ends the game
  abstract public void continueGame(int btnNum);  // progresses the game
}

// Tic Tac Toe game logic
class TicTacToeModel extends GameModel {
  private ArrayList<Integer> board; // 0  1  2   -1 computer, 0 empty, 1 player
                                    // 3  4  5
                                    // 6  7  8

  // Overrides start game
  // Sets each element in the array list to 0
  // initializes all other base elements
  @Override
  public void startGame() {
    board = new ArrayList<Integer>();
    for(int i = 0; i < 9; i++){
      board.add(0);
    }
    activePlayer = true;
    player = Player.getInstance();
    computer = new TicTacToeComp();
    gameOver = false;
    winner = "";
  }

  // Calls notifyObservers() so they know the game is ended
  @Override
  public void endGame() {
    notifyObservers(-1, -1, winner);
  }

  // Continues the tic tac toe game
  // Behavior determined based on if player or NPC turn
  // param btnNum is the button selected to make a move
  // calls notifyObservers() after each turn
  // calls updateBoard to update the display after legal move is made
  @Override
  public void continueGame(int btnNum) {
    if (activePlayer) { // player goes
      if (board.get(btnNum) == 0) { // if the space has not been marked with an x or o
        board.set(btnNum, 1);
        notifyObservers(btnNum, 1, winner);
        activePlayer = false;
      } else {
        System.out.println("Error, cannot move here.");
      }
    } else { // opponent goes
      TicTacToeComp ttt = (TicTacToeComp)computer;
      ttt.updateBoard(board);
      int move = computer.performMove();
      if (move != -1 && board.get(move) == 0) { // if the space has not been marked with an x or o
        board.set(move, -1);
        notifyObservers(move, -1, winner);
        activePlayer = true;
      } else {
        System.out.println("Error, cannot move here.");
      }
    }
  }

  // Checks if the game has been won
  // does this by checking for all possible winning states
  // Returns boolean true if the game has been wone, false otherwise
  // param n is -1 for NPC 1 for Player to know if we're checking x or o
  private boolean hasWon(int n){
    // across
    if(board.get(0) == n && board.get(1) ==  n && board.get(2) == n ) return true;
    if(board.get(3) == n && board.get(4) ==  n && board.get(5) == n ) return true;
    if(board.get(6) == n && board.get(7) ==  n && board.get(8) == n ) return true;

    // down
    if(board.get(0) == n && board.get(3) ==  n && board.get(6) == n ) return true;
    if(board.get(1) == n && board.get(4) ==  n && board.get(7) == n ) return true;
    if(board.get(2) == n && board.get(5) ==  n && board.get(8) == n ) return true;

    // diagonal
    if(board.get(0) == n && board.get(4) ==  n && board.get(8) == n ) return true;
    if(board.get(2) == n && board.get(4) ==  n && board.get(6) == n ) return true;

    return false;
  }

  // Checks if the game is over by ensuring no moves are left and noone has won
  // Calls hasWon() with 1 and -1 to check if player or NPC has won
  // notify the observers of the winner outcome
  // Also checks if there are any moves left, and if noone has won this results in a draw
  public void checkGameOver() {
    // int playerBoardNum = turnToBoardNum();

    if ((activePlayer ? hasWon(1) : hasWon(-1))) {
      winner = activePlayer ?  "Player" : "CPU";
      gameOver = true;
      notifyObservers(-1, -1, winner);
      return;
    }

    // checks for any moves left
    for (int i = 0; i<9; i++){
      if (board.get(i) == 0) {
        gameOver = false;
        return;
      }
    }

    winner = "Draw";
    notifyObservers(-1, -1, winner); // notify the observers of the winner
  }
}

// Connect 4 game logic
class Connect4Model extends GameModel {
  private int[][] board; // 6 by 7 int array to store board state

  // Getter
  public int[][] getBoard() {
    return board;
  }

  // Overrides startGame()
  // Sets the board to 6x7 diminsion as standard with connect 4
  // Gets instance of player singleton
  @Override
  public void startGame() {
    board = new int[6][7]; // 6row 7col
    activePlayer = true;
    player = Player.getInstance();
    computer = new Connect4Comp();
    gameOver = false;
    winner = "";
  }

  // Calls notifyObservers() at end based on the winner
  @Override
  public void endGame() {
    notifyObservers(-1, -1, winner);
  }

  // Continues the connect4 game
  // Behavior determined based on if player or NPC turn
  // param btnNum is the button selected to make a move
  // calls notifyObservers() after each turn
  // calls updateBoard to update the display after legal move is made
  @Override
  public void continueGame(int btnNum) {
    if (activePlayer) { // player goes
      if (move(btnNum, 1)) {
        notifyObservers(btnNum, 1, winner);
        activePlayer = false;
      } else {
        System.out.println("Error, cannot move here.");
      }
    } else { // opponent goes
      Connect4Comp c4c = (Connect4Comp)computer;
      c4c.updateMoves(legalCols());
      int compMove = computer.performMove();
      if (compMove != -1 && move(compMove, -1)) {
        notifyObservers(compMove, -1, winner);
        activePlayer = true;
      } else {
        System.out.println("Error, cannot move here.");
      }
    }
  }

  // Checks if the game is over by ensuring no moves are left and noone has won
  // Calls hasWon() with -1 and 1 to check if NPC or Player has won, draw otherwise
  public void checkGameOver() {
    // int playerBoardNum = turnToBoardNum();

    if ((activePlayer ? hasWon(1) : hasWon(-1))) {
      winner = activePlayer ?  "Player" : "CPU";
      gameOver = true;
      notifyObservers(-1, -1, winner);
      return;
    }

    // checks for any moves left
    if (!isGameOver()) {
      return;
    }

    winner = "Draw";
    notifyObservers(-1, -1, winner);
  }

  // Moves the player by setting the board value based on column selected
  // @param col is the column youre attempting to place your piece down
  // @return boolean true if the move is successfully made, false otherwise
  public boolean move(int col, int player) {
    // check if the move is legal
    if (!isLegalMove(col)) {
      return false;
    }

    // find where there pice will fall to
    int row = getLowestEmptyRow(col);
    board[row][col] = player;

    return true;
  }

  // checks if placing a piece down a colum is legal
  // returns true if legal move, false otherwise
  public boolean isLegalMove(int col) {
    if (col < 0 || col >= 7) {
      return false;
    }

    // note in java arrays are automatically filled w all 0's
    return board[0][col] == 0;
  }

  // You have a column you want to place a piece on
  // This checks where the piece will fall to
  public int getLowestEmptyRow(int col) {
    int row = 5;
    while (row >= 0 && board[row][col] != 0) {
      row--;
    }
    return row;
  }

  // Finds the leagal columns that a piece can be dropped in
  // Used for computer to make a move
  public ArrayList<Integer> legalCols() {
    ArrayList<Integer> moves = new ArrayList<Integer>();
    for (int i = 0; i < 7; i++) {
      if (isLegalMove(i)) {
        moves.add(i);
        System.out.println(i);
      }
    }

    return moves;
  }

  // checks if a player has won
  // returns true if @param player has won
  public boolean hasWon(int player) {
      // Check horizontal
      for (int row = 0; row < 6; row++) {
          for (int col = 0; col <= 3; col++) {
              if (board[row][col] == player && board[row][col+1] == player && board[row][col+2] == player && board[row][col+3] == player) {
                  return true;
              }
          }
      }

      // Check vertical
      for (int row = 0; row <= 2; row++) {
          for (int col = 0; col < 7; col++) {
              if (board[row][col] == player && board[row+1][col] == player && board[row+2][col] == player && board[row+3][col] == player) {
                  return true;
              }
          }
      }

      // Check diagonal
      for (int row = 0; row <= 2; row++) {
          for (int col = 0; col <= 3; col++) {
              if (board[row][col] == player && board[row+1][col+1] == player && board[row+2][col+2] == player && board[row+3][col+3] == player) {
                  return true;
              }
          }
      }

      // Check other diagonal
      for (int row = 0; row <= 2; row++) {
          for (int col = 3; col < 7; col++) {
              if (board[row][col] == player && board[row+1][col-1] == player && board[row+2][col-2] == player && board[row+3][col-3] == player) {
                  return true;
              }
          }
      }

      return false;
  }

  // Checks if the game is over
  // returns true if the game is over, false otherwise
  public boolean isGameOver() {
    for (int col = 0; col < 7; col++) {
      if (isLegalMove(col)) {
        return false;
      }
    }
    return true;
  }
}

// Blackjack game logic
class BlackJackModel extends GameModel {
  private ArrayList<String> deck; // deck of 52 cards
  private ArrayList<String> pHand;
  private ArrayList<String> dHand;

  // Getters
  public ArrayList<String> getPlayerHand() { return pHand; }
  public ArrayList<String> getDealerHand() { return dHand; }

  // Sets the 52 card deck using strings for easy use
  // Gets instance of singleton player and creates new blackjack NPC
  @Override
  public void startGame() {
    deck = new ArrayList<String>(List.of(
      "AS", "2S", "3S", "4S", "5S", "6S", "7S", "8S", "9S", "TS", "JS", "QS", "KS",
      "AH", "2H", "3H", "4H", "5H", "6H", "7H", "8H", "9H", "TH", "JH", "QH", "KH",
      "AD", "2D", "3D", "4D", "5D", "6D", "7D", "8D", "9D", "TD", "JD", "QD", "KD",
      "AC", "2C", "3C", "4C", "5C", "6C", "7C", "8C", "9C", "TC", "JC", "QC", "KC"
    ));

    pHand = new ArrayList<String>(); // player hand
    dHand = new ArrayList<String>(); // dealer hand

    activePlayer = true;
    player = Player.getInstance();
    computer = new BlackJackComp();
    gameOver = false;
    winner = "";

    generateHands();  // generates the beginning hands for the players
  }

  // Randomly Selects a card from the deck
  public String selectCard(){
    Random random = new Random();
    int index = random.nextInt(deck.size());
    return deck.get(index);
  }

  // Generates the player hand and dealer hand at the begining of the game
  public void generateHands() {
    dHand.add(selectCard());

    pHand.add(selectCard());
    pHand.add(selectCard());

    notifyObservers(0, -1, winner);
  }

  // Hit adds a card to your hand
  // @param isPlayer determines if the player is hitting or dealer is hitting
  public void hit(boolean isPlayer){
    String hitCard = selectCard();
    if (isPlayer) {
      pHand.add(hitCard);
    }
    else{
      dHand.add(hitCard);
    }
  }

  // Gets the number point value of a card
  // Aces are tricky to deal with so read comments for explanation
  // note this is done to avoid circular dependencies and infinite loops
  public int getCardValue(String card){
    char rank = card.charAt(0);
    int value;

    if (rank == 'A') { // ace is either 1 or 11 depending on the other cards in your hand
      value = -1; // so getHandValue() know that you got an ace
    }
    else if (rank == 'K' || rank == 'Q' || rank == 'J' || rank == 'T') {
      value = 10; // face cards and 10s are worth 10
    }
    else {
      value = Character.getNumericValue(rank); // numerical cards are worth their face value
    }

    return value;
  }

  // Gets the numerical value of a blackjack hand
  // also determines if an ace is 1 or 11 based on other cards in the hand
  public int getHandValue(ArrayList<String> hand){
    int handValue = 0;
    boolean hasAce = false;
    for (String card : hand) {
      // if they have an ACE
      if (getCardValue(card) == -1) {
        hasAce = true;
      }
      // if they have a Non-Ace
      else {
        handValue += getCardValue(card);
      }
    }

    // if they have an ace determine if it should be 1 or 11
    if (hasAce) {
      if(handValue + 11 > 21){
        handValue += 1;
      }
      else{
        handValue += 11;
      }
    }

    return handValue;
  }

  // Determines if there is Blackjack or not
  // Returns true if there is blackjack!
  public boolean isBlackjack(ArrayList<String> hand) {
    if (hand.size() == 2 && getHandValue(hand) == 21) {
      return true;
    }
    return false;
  }

  // Determines if the player/dealer has busted.. pause
  // Returns true if they have busted, false otherwise
  public boolean isBust(ArrayList<String> hand) {
    return getHandValue(hand) > 21;
  }

  // Determines the winner based on the player hand vs dealer hand
  // Determines hand values calling getHandValue()
  public String getWinner(ArrayList<String> playerHand, ArrayList<String> dealerHand) {
    int playerValue = getHandValue(playerHand);
    int dealerValue = getHandValue(dealerHand);

    if (playerValue == dealerValue) {
        return "Push";
    } else if (isBust(playerHand)) {
        return "Dealer";
    } else if (isBust(dealerHand)) {
        return "Player";
    } else if (playerValue > dealerValue) {
        return "Player";
    } else {
        return "Dealer";
    }
  }

  // Calls getWinner and notifies the observers based on the winner
  @Override
  public void endGame() {
    winner = getWinner(pHand, dHand);
    notifyObservers(-1, -1, winner);
  }

  // Continues the blackjack game
  // Behavior determined based on if player or NPC turn
  // param btnNum is the button selected to make a move
  // calls notifyObservers() after each turn
  // calls updateBoard to update the display after legal move is made
  @Override
  public void continueGame(int btnNum) {
    if (btnNum == 0) {
      hit(activePlayer);
      notifyObservers(-1, -1, winner);
    } else {
      activePlayer = false;
      System.out.println("comp playing");
      BlackJackComp bjc = (BlackJackComp)computer;
      bjc.updateNum(getHandValue(dHand));

      if (computer.performMove() == 0) {
        hit(activePlayer);
        notifyObservers(-1, -1, winner);
      }
    }
  }
}
