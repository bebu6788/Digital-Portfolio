// ***** MVC Pattern: Controller Part ******
// makse use of Strategy Pattern

// Interface with abstract functions to be overridden in subclasses
public interface GameController {
  public void startGame();
  public void stopGame();
  public void performMove(int btnNum);
  public void checkGameOver();
}

// Controller for Tic Tac Toe game
class TicTacToeController implements GameController {
  public TicTacToeModel gameModel;  // model to be used for game
  public TicTacToeFrame gameDisplay;  // frame for game window

  // Constructor
  public TicTacToeController(TicTacToeModel model) {
    this.gameModel = model;
    gameDisplay = new TicTacToeFrame(model, this);
  }

  // Starts the game by enabling buttons in view
  // and tell the model that the game is starting
  @Override
  public void startGame() {
    gameDisplay.setEnabledGrid(true);
    gameDisplay.startBtn.setEnabled(false);
    gameModel.startGame();
  }

  // tells the model that the game has ended
  @Override
  public void stopGame() {
    gameDisplay.setEnabledGrid(false);
    gameModel.endGame();
  }

  // Notifies the model that a move needs to be performed for this game
  @Override
  public void performMove(int btnNum) {
    gameModel.continueGame(btnNum);
  }

  // Tells the model to chekc that the game is over
  @Override
  public void checkGameOver() {
    gameModel.checkGameOver();
  }
}

// Controller for Connect4 game
class Connect4Controller implements GameController {
  public Connect4Model gameModel;
  public Connect4Frame gameDisplay;

  // Constructor
  public Connect4Controller(Connect4Model model) {
    this.gameModel = model;
    gameDisplay = new Connect4Frame(model, this);
  }

  // Enables column buttons on view
  // Tells model to start the game
  @Override
  public void startGame() {
    gameDisplay.setEnabledCols(true);
    gameDisplay.startBtn.setEnabled(false);
    gameModel.startGame();
  }

  // tells model to end the game
  @Override
  public void stopGame() {
    gameDisplay.setEnabledCols(false);
    gameModel.endGame();
  }

  // Tells model to check if the game is over
  @Override
  public void checkGameOver() {
    gameModel.checkGameOver();
  }

  // Notifies the model that a move needs to be performed
  @Override
  public void performMove(int btnNum) {
    gameModel.continueGame(btnNum);
  }
}

// Controller for BlackJack game
class BlackJackController implements GameController {
  public BlackJackModel gameModel;
  public BlackJackFrame gameDisplay;

  // Constructor
  public BlackJackController(BlackJackModel model) {
    this.gameModel = model;
    gameDisplay = new BlackJackFrame(model, this);
  }

  // Tells the model to start the game
  // Enables games buttons as needed
  @Override
  public void startGame() {
    gameDisplay.setEnabledBtns(true);
    gameDisplay.startBtn.setEnabled(false);
    gameModel.startGame();
  }

  // Tells the model to end the game
  @Override
  public void stopGame() {
    gameModel.endGame();
  }

  // Checks if the game is over and tells model to stop if needed
  @Override
  public void checkGameOver() {
    System.out.println("checking");
    if (gameModel.isBust(gameModel.getPlayerHand()) ||
        gameModel.isBust(gameModel.getDealerHand()) ||
        gameModel.getHandValue(gameModel.getDealerHand()) >= 17)
    {
      System.out.println("stopping");
      stopGame();
    }
  }

  // Notifies the model that a move was performed
  @Override
  public void performMove(int btnNum) {
    gameModel.continueGame(btnNum);
  }
}
