import java.util.ArrayList;
import java.util.Random;

// Interface with perfromMove function to be overridden in children
public interface Computer {
  public int performMove();
}

// Implements the Computer interface for tic tac toe
class TicTacToeComp implements Computer {
  private ArrayList<Integer> board;

  // Constructor
  public TicTacToeComp() {
    board = new ArrayList<Integer>();
  }

  public void updateBoard(ArrayList<Integer> b) { board = b; }

  // Overrides the abstract function performMove()
  // Calls getRandomMove() for computer movement
  @Override
  public int performMove() {
    return getRandomMove();
  }

  // Retrieves a random move from the available moves
  private int getRandomMove(){
    ArrayList<Integer> moves = new ArrayList<Integer>();

    // Find indices of elements that have a value of 0
    for (int i = 0; i < board.size(); i++) {
        if (board.get(i) == 0) {
          moves.add(i);
        }
    }

    // Select a random index from the list of indices
    Random rand = new Random();
    int randomIndex = (!moves.isEmpty()) ? moves.get(rand.nextInt(moves.size())) : -1;

    return randomIndex;
  }
}

// Implements the Computer interface for connect 4
class Connect4Comp implements Computer {
  private ArrayList<Integer> moves; // holds a list of legal moves

  // Constructor
  public Connect4Comp() {
    moves = new ArrayList<Integer>();
  }

  public void updateMoves(ArrayList<Integer> m) { moves = m; }

  // Overrides the abstract function performMove()
  // Generates a random move for the computer to place their piece
  @Override
  public int performMove() {
    // Select a random move from the list of moves
    Random rand = new Random();
    int random = (!moves.isEmpty()) ? moves.get(rand.nextInt(moves.size())) : -1;

    return random;
  }
}

// Implements the Computer interface for connect 4
class BlackJackComp implements Computer {
  private int currentNum; // the current value of the dealers hand

  // Constructor
  public BlackJackComp() {
    currentNum = 0;
  }

  public void updateNum(int v) { currentNum = v; }

  // Overrides the absttract function performMove()
  // Calls hasToHit() to see if the dealer must hit
  @Override
  public int performMove() {
    if (hasToHit()) {
      return 0; // hits
    } else {
      return 1; // is done
    }
  }

  // Returns boolean true if the dealer must hit
  // Blackjack rule is dealer must hit until they have 17 or more
  public Boolean hasToHit() {
    if (currentNum < 17) {
      return true;
    }
    return false;
  }
}
