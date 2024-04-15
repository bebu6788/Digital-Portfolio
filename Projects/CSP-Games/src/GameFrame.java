import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URL;

// ***** MVC Pattern: View part *****
// ***** Observer and Composite Patterns *****

// Abstract class to be basis for all game frames
// implements actionListener and observer interfaces
abstract public class GameFrame extends JFrame implements ActionListener, Observer {
  protected JLabel title;
  protected JButton startBtn;
  protected JButton backBtn;
  protected GameModel gameModel;
  protected GameController controller;

  abstract public void createView();  // create the game view
  abstract public void resetView(); // reset game view to start new game
}

// JFrame to display Tic Tac Toe game
class TicTacToeFrame extends GameFrame {
  private JButton[] grid;
  private JPanel panel;

  // Constructor that takes in model and controller to implement MVC
  public TicTacToeFrame(GameModel model, GameController controller) {
    // setting up the model and controller
    this.gameModel = model;
    this.controller = controller;
    this.gameModel.addObserver(this); // add this frame as an observer of the model

    // add the LeaderboardHandler as an observer of the model too
    // specifically for tic tac toe
    LeaderboardHandler.getInstance().setGame("tictactoe");
    this.gameModel.addObserver(LeaderboardHandler.getInstance());

    // set up the main panel
    panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
    panel.setLayout(new GridLayout(3, 3));

    createView();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    pack();
    setSize(400, 400);
    setVisible(true);
  }

  // creates the game view and adds all necessary JComponents
  @Override
  public void createView() {
    title = new JLabel("Tic-Tac-Toe", SwingConstants.CENTER);
    add(new JPanel().add(title), BorderLayout.NORTH);

    // initialize and place the buttons for the grid
    grid = new JButton[9];
    for (int i = 0; i < grid.length; i++) {
      grid[i] = new JButton();
      grid[i].addActionListener(this);
      panel.add(grid[i]);
    }
    setEnabledGrid(false);
    add(panel, BorderLayout.CENTER);

    // buttons at the bottome of the screen
    startBtn = new JButton("Start Game");
    startBtn.addActionListener(this);

    backBtn = new JButton("Back");
    backBtn.addActionListener(this);

    JPanel bottom = new JPanel();
    bottom.add(startBtn);
    bottom.add(backBtn);
    add(bottom, BorderLayout.SOUTH);
  }

  // resets the view so a new game can be started
  @Override
  public void resetView() {
    setEnabledGrid(false);
    startBtn.setEnabled(true);
  }

  // enables or disables the grid buttons
  // @param enable Whether to enable of disable buttons
  public void setEnabledGrid(Boolean enable) {
    for (int i = 0; i < grid.length; i++) {
      grid[i].setEnabled(enable);
      grid[i].setText(null);
    }
  }

  // updates this view after a turn has been made
  @Override
  public void update(int btnClicked, int move) {
    System.out.println("updating");
    grid[btnClicked].setText((move == 1) ? "X" : "O");
    grid[btnClicked].setEnabled(false);
    controller.checkGameOver();
  }

  // displays that the game has concluded and who won
  // resets the view once dialog is closed
  @Override
  public void end(String winner) {
    if (winner == "Draw") {
      JOptionPane.showMessageDialog(this,
          "The game ended in a draw!",
          "Game Draw",
          JOptionPane.PLAIN_MESSAGE);
      return;
    }
    JOptionPane.showMessageDialog(this,
        winner + " won the game!",
        "Game Over",
        JOptionPane.PLAIN_MESSAGE);
    resetView();
  }

  // Performs various actions depending on what button is clicked
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == backBtn) {
      new GameMenuFrame(3);
      dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    } else if (e.getSource() == startBtn) {
      controller.startGame();
    } else {
      // if any grid button is clicked
      for (int i = 0; i < grid.length; i++) {
        if (e.getSource() == grid[i]) {
          controller.performMove(i);  // perform move for player
          if (!gameModel.getActivePlayer() && !gameModel.getGameOver()) {
            controller.performMove(-1); // perform move for computer
          }
        }
      }
    }
  }
}

// JFrame to display Connect4 game
class Connect4Frame extends GameFrame {
  private JButton[] columns;
  private JPanel panel;
  private JLayeredPane board;
  private ImageIcon bImage;

  // Constructor that takes in model and controller to implement MVC
  public Connect4Frame(GameModel model, GameController controller) {
    // setting up the model and controller
    this.gameModel = model;
    this.controller = controller;
    // adding observers of the model (this view and leaderboard handler)
    this.gameModel.addObserver(this);
    LeaderboardHandler.getInstance().setGame("connect4");
    this.gameModel.addObserver(LeaderboardHandler.getInstance());

    // initializing main panel
    panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    panel.setLayout(new BorderLayout(10, 30));

    createView();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    pack();
    setSize(650, 700);
    setVisible(true);
  }

  // creates the game view and adds all necessary JComponents
  @Override
  public void createView() {
    title = new JLabel("Connect4", SwingConstants.CENTER);
    getContentPane().add(new JPanel().add(title), BorderLayout.NORTH);

    // creating and placing the column buttons
    JPanel cols = new JPanel(new GridLayout(1, 7, 10, 0));
    columns = new JButton[7];
    for (int i = 1; i <= columns.length; i++) {
      columns[i - 1] = new JButton(Integer.toString(i));
      columns[i - 1].addActionListener(this);
      columns[i - 1].setPreferredSize(new Dimension(65, 30));
      columns[i - 1].setEnabled(false);
      cols.add(columns[i - 1]);
    }

    panel.add(cols, BorderLayout.NORTH);

    // displaying the board image for playing field
    URL image = getClass().getResource("./images/Board4.png");

    if (image != null) {
      bImage = new ImageIcon(image);
    } else {
      System.out.println("Image Not Found!");
    }

    // placing image in JLayeredPane for easy image additions
    board = new JLayeredPane();
    JLabel bLabel = new JLabel(bImage);
    bLabel.setBounds(20, 20, bImage.getIconWidth(), bImage.getIconHeight());
    board.add(bLabel, 0, 1);

    startBtn = new JButton("Start Game");
    startBtn.addActionListener(this);

    backBtn = new JButton("Back");
    backBtn.addActionListener(this);

    JPanel bottom = new JPanel();
    bottom.add(startBtn);
    bottom.add(backBtn);

    panel.add(board, BorderLayout.CENTER);
    panel.add(bottom, BorderLayout.SOUTH);
    getContentPane().add(panel, BorderLayout.CENTER);
  }

  // resets the frame so that a new game can be started
  @Override
  public void resetView() {
    setEnabledCols(false);
    startBtn.setEnabled(true);
    board.removeAll();
    JLabel bLabel = new JLabel(bImage);
    bLabel.setBounds(20, 20, bImage.getIconWidth(), bImage.getIconHeight());
    board.add(bLabel, 0, 1);
  }

  // enables or disables all column buttons
  // @param enable Whetehr to enable or disable the buttons
  public void setEnabledCols(Boolean enable) {
    for (int i = 0; i < columns.length; i++) {
      columns[i].setEnabled(enable);
    }
  }

  // Used to place a token on the board based on player and computer moves
  // returns true if the column becomes full after this move
  public boolean placeToken(int btn, int player) {
    Connect4Model c4m = (Connect4Model)gameModel;
    // x and y of where to place the token
    int x = 75 * (btn);
    int y = 75 * (c4m.getLowestEmptyRow(btn)+1);
    ImageIcon checker;
    // get the right checker for which player is making the move
    if (player == 1) {
      checker = new ImageIcon(getClass().getResource("./images/Red.png"));
    } else {
      checker = new ImageIcon(getClass().getResource("./images/Yellow.png"));
    }

    // adding the checker to the board
    JLabel checkerLabel = new JLabel(checker);
    checkerLabel.setBounds(27 + x, 27 + y, checker.getIconWidth(), checker.getIconHeight());
    board.add(checkerLabel, 0, 0);

    // checking if the column is now full
    if (!c4m.isLegalMove(btn)) {
      return true;
    }
    return false;
  }

  // Performs various actions depending on which button is clicked
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == backBtn) {
      new GameMenuFrame(3);
      dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    } else if (e.getSource() == startBtn) {
      controller.startGame();
    } else {
      for (int i = 0; i < columns.length; i++) {
        if (e.getSource() == columns[i]) {
          controller.performMove(i);  // player takes a move
          if (!gameModel.getActivePlayer() && !gameModel.getGameOver()) {
            controller.performMove(-1); // computer makes a move
          }
        }
      }
    }
  }

  // updates the board once a move is made
  @Override
  public void update(int btnClicked, int move) {
    System.out.println("updating");
    if (placeToken(btnClicked, move)) {
      columns[btnClicked].setEnabled(false);
    }
    controller.checkGameOver();
  }

  // displays end game message once game is over
  // resets the view for a new game to be started
  @Override
  public void end(String winner) {
    if (winner == "Draw") {
      JOptionPane.showMessageDialog(this,
          "The game ended in a draw!",
          "Game Draw",
          JOptionPane.PLAIN_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(this,
          winner + " won the game!",
          "Game Over",
          JOptionPane.PLAIN_MESSAGE);
    }
    resetView();
  }
}

// JFrame for the BlackJack game
class BlackJackFrame extends GameFrame {
  private JButton hitBtn;
  private JButton stayBtn;
  private JButton contBtn;
  private ImageIcon deckImage;
  private JPanel panel;
  private JPanel cardsPanel;

  // Constructor to set up this view utilizing MVC
  public BlackJackFrame(GameModel model, GameController controller) {
    // set up the model and the controller
    this.gameModel = model;
    this.controller = controller;
    // add the two needed observers to the model
    this.gameModel.addObserver(this);
    LeaderboardHandler.getInstance().setGame("blackjack");
    this.gameModel.addObserver(LeaderboardHandler.getInstance());

    // create the panel for where the cards are going to be displayed
    cardsPanel = new JPanel(null);
    cardsPanel.setBackground(new Color(20, 100, 0, 160));

    // initialize the main panel of the frame
    panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    panel.setLayout(new BorderLayout(10, 20));

    createView();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    pack();
    setSize(1500, 700);
    setVisible(true);
  }

  // Creates the starting view for the game frame
  @Override
  public void createView() {
    title = new JLabel("Black Jack", SwingConstants.CENTER);
    getContentPane().add(new JPanel().add(title), BorderLayout.NORTH);

    // retrieve image of deck of cards
    URL deck = getClass().getResource("./images/deck.jpg");

    if (deck != null) {
      deckImage = new ImageIcon(deck);
    } else {
      System.out.println("Image Not Found!");
    }

    panel.add(new JLabel(deckImage), BorderLayout.WEST);

    // initialize and add buttons for game functionality
    hitBtn = new JButton("Hit");
    hitBtn.setEnabled(false);
    hitBtn.addActionListener(this);
    stayBtn = new JButton("Stand");
    stayBtn.setEnabled(false);
    stayBtn.addActionListener(this);
    contBtn = new JButton("Continue");
    contBtn.setEnabled(false);
    contBtn.addActionListener(this);

    JPanel playBtns = new JPanel(new GridLayout(3, 1, 0, 10));
    playBtns.setPreferredSize(new Dimension(100, 100));
    playBtns.add(hitBtn);
    playBtns.add(stayBtn);
    playBtns.add(contBtn);
    panel.add(playBtns, BorderLayout.EAST);

    // add the bottom buttons for navigation
    startBtn = new JButton("Start Game");
    startBtn.addActionListener(this);

    backBtn = new JButton("Back");
    backBtn.addActionListener(this);

    JPanel bottom = new JPanel();
    bottom.add(startBtn);
    bottom.add(backBtn);

    panel.add(bottom, BorderLayout.SOUTH);

    // add the card panel to middle
    panel.add(cardsPanel, BorderLayout.CENTER);
    getContentPane().add(panel, BorderLayout.CENTER);
  }

  // resets the frame so a new game can be started
  @Override
  public void resetView() {
    setEnabledBtns(false);
    startBtn.setEnabled(true);
    cardsPanel.removeAll();
    repaint();
  }

  // enables or disables game buttons
  public void setEnabledBtns(boolean b) {
    hitBtn.setEnabled(b);
    stayBtn.setEnabled(b);
    contBtn.setEnabled(b);
  }

  // Adds card images to the cardPanel for viewing
  public void dealCards() {
    System.out.println("deal");
    BlackJackModel bjm = (BlackJackModel)gameModel;
    // add the player hand
    for (int i = 0; i < bjm.getPlayerHand().size(); i++) {
      JLabel pCard = new JLabel(bjm.getPlayerHand().get(i), SwingConstants.CENTER);
      pCard.setFont(new Font("Serif", Font.PLAIN, 40));
      pCard.setPreferredSize(new Dimension(120, 200));
      pCard.setOpaque(true);
      pCard.setBackground(new Color(255, 255, 255));
      if (bjm.getPlayerHand().get(i).charAt(1) == 'D' || bjm.getPlayerHand().get(i).charAt(1) == 'H') {
        pCard.setForeground(new Color(200, 0, 0));
      }

      cardsPanel.add(pCard);
      pCard.setBounds((i*150)+10, 320, pCard.getPreferredSize().width, pCard.getPreferredSize().height);
    }
    // add the dealer hand
    for (int i = 0; i < bjm.getDealerHand().size(); i++) {
      JLabel dCard = new JLabel(bjm.getDealerHand().get(i), SwingConstants.CENTER);
      dCard.setFont(new Font("Serif", Font.PLAIN, 40));
      dCard.setPreferredSize(new Dimension(120, 200));
      dCard.setOpaque(true);
      dCard.setBackground(new Color(255, 255, 255));
      if (bjm.getDealerHand().get(i).charAt(1) == 'D' || bjm.getDealerHand().get(i).charAt(1) == 'H') {
        dCard.setForeground(new Color(200, 0, 0));
      }

      cardsPanel.add(dCard);
      dCard.setBounds((i*150)+10, 50, dCard.getPreferredSize().width, dCard.getPreferredSize().height);
    }
    // display them in frame - have to repaint the frame to update
    panel.add(cardsPanel, BorderLayout.CENTER);
    repaint();
    revalidate();
  }

  // performs various actions based on which button is clicked
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == backBtn) {
      new GameMenuFrame(3);
      dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    } else if (e.getSource() == startBtn) {
      controller.startGame();
      contBtn.setEnabled(false);
    } else if (e.getSource() == hitBtn) {
      controller.performMove(0);  // add card to player
    } else if (e.getSource() == stayBtn) {
      setEnabledBtns(false);
      contBtn.setEnabled(true);
      controller.performMove(1);  // add sedond dealer card
    } else if (e.getSource() == contBtn) {
      controller.performMove(2);  // add card to dealer
    }
  }

  // updates the view of cards once model has updates
  @Override
  public void update(int btnClicked, int move) {
    System.out.println("update1");
    dealCards();
    System.out.println("update2");
    controller.checkGameOver();
  }

  // displays end dialog once a player wins
  // also resets the view to beignning state
  @Override
  public void end(String winner) {
    if (winner == "Push") {
      JOptionPane.showMessageDialog(this,
          "The game ended in a push!",
          "Game Draw",
          JOptionPane.PLAIN_MESSAGE);
    } else {
      JOptionPane.showMessageDialog(this,
          winner + " won the game!",
          "Game Over",
          JOptionPane.PLAIN_MESSAGE);
    }
    resetView();
  }
}
