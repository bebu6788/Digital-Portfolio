import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

// ***** Composite Pattern *****
// The JFrame for the Main Menu of the application
public class MainMenuFrame extends JFrame implements ActionListener {
  public JPanel panel;
  private JLabel player;
  private ImageIcon logo;
  private JButton viewBoards;
  private JButton changeName;
  private JButton playGame;

  // Constructor
  public MainMenuFrame() {
    panel = new JPanel(null);
    panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));

    // have player login on first start up
    if (Player.getInstance().getName().isEmpty()) {
      changeNameDialog();
    }

    createMenu();
    getContentPane().add(panel);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pack();
    setSize(615, 400);
    setVisible(true);
  }

  // Creates the menu frame
  // Sets the bounds for player and adds them to the panel
  // Also add the UI elemnts to the panel: logo, view boards, name changing, and play game
  public void createMenu() {
    player = new JLabel("Current Player: " + Player.getInstance().getName());
    panel.add(player);
    player.setBounds(400, 10, player.getPreferredSize().width, player.getPreferredSize().height);

    // adding the logo image to panel
    URL image = getClass().getResource("./images/logo.png");

    if (image != null) {
      logo = new ImageIcon(image);
      Image logoImage = logo.getImage().getScaledInstance(560, 500, java.awt.Image.SCALE_SMOOTH);
      logo = new ImageIcon(logoImage);
    } else {
      System.out.println("Image Not Found!");
    }

    JLabel logoLabel = new JLabel(logo);
    panel.add(logoLabel);
    logoLabel.setBounds(20, 40, logoLabel.getPreferredSize().width, (logoLabel.getPreferredSize().height/2));

    // adding the leaderboard viewing button
    viewBoards = new JButton("View Leaderboards");
    viewBoards.addActionListener(this);
    viewBoards.setPreferredSize(new Dimension(180, 50));
    panel.add(viewBoards);
    viewBoards.setBounds(20, 300, viewBoards.getPreferredSize().width, viewBoards.getPreferredSize().height);

    // add button to allow user to change their name
    changeName = new JButton("Change Name");
    changeName.addActionListener(this);
    changeName.setPreferredSize(new Dimension(180, 50));
    panel.add(changeName);
    changeName.setBounds(400, 300, changeName.getPreferredSize().width, changeName.getPreferredSize().height);

    // button to start playing games
    playGame = new JButton("Play Games");
    playGame.addActionListener(this);
    playGame.setPreferredSize(new Dimension(180, 50));
    panel.add(playGame);
    playGame.setBounds(210, 300, playGame.getPreferredSize().width, playGame.getPreferredSize().height);
  }

  // Pop up dialog that allows the user to change their name
  // Collects user input text and calls changeName() on the player
  public void changeNameDialog() {
    String result = (String)JOptionPane.showInputDialog(
      this,
      "New Name",
      "Enter Player Name",
      JOptionPane.PLAIN_MESSAGE,
      null,
      null,
      "username");
    System.out.println(result);
    if (result != null) {
      Player p = Player.getInstance(); // Calls get instance on singleton player
      p.changeName(result); // change the player name based on input
      LeaderboardHandler handler = LeaderboardHandler.getInstance();
      handler.setPlayer(result); // set the player in the handler
      p.updatePlayerInfo(handler.retrievePlayerInfo()); // update the player info

      // updates panel label with new player name
      if (panel.getComponentCount() != 0) {
        panel.remove(player);
        player = new JLabel("Current Player: " + result);
        panel.add(player, 0);
        revalidate();
      }
    }
  }

  // Overrides actionPerformed()
  // Navigates to other frames based on which button is pressed
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == playGame) {
      new GameMenuFrame(3);
      setVisible(false);
    } else if (e.getSource() == changeName) {
      changeNameDialog();
    } else if (e.getSource() == viewBoards) {
      new LeaderboardFrame();
    }
  }
}

// ***** Composite Pattern *****
// Game Menu frame that displays which games are available to play
class GameMenuFrame extends JFrame implements ActionListener {
  private JButton[] games;
  private JLabel title;
  private JPanel panel;
  private JButton back;

  // Constructor
  public GameMenuFrame(int gameCount) {
    panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
    panel.setLayout(new GridLayout(1, 3, 15, 0));

    createMenu(gameCount);
    add(panel, BorderLayout.CENTER);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    pack();
    setSize(600, 400);
    setVisible(true);
  }

  // Creates the menu for the three game options
  // Creates new  buttons for each game mode (TTT, C4, BJack)
  public void createMenu(int gameCount) {
    title = new JLabel("Game Menu", SwingConstants.CENTER);
    add(new JPanel().add(title), BorderLayout.NORTH);

    games = new JButton[gameCount];

    // Create new Jbuttons for each game
    if (games.length >= 3) {
      games[0] = new JButton("Tic-Tac-Toe");
      games[1] = new JButton("Connect4");
      games[2] = new JButton("BlackJack");
    }

    // Adds buttons to the panel
    for (JButton gameBtn : games) {
      gameBtn.addActionListener(this);
      panel.add(gameBtn);
    }

    back = new JButton("Back");
    back.addActionListener(this);
    add(new JPanel().add(back), BorderLayout.SOUTH);
  }

  // Overrides actionPerformed
  // Navigates to the correct Game Frame - first sets up model and contrller
  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == back) {
      new MainMenuFrame();
      dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    } else if (e.getSource() == games[0]) {
      new TicTacToeController(new TicTacToeModel());
      setVisible(false);
    } else if (e.getSource() == games[1]) {
      new Connect4Controller(new Connect4Model());
      setVisible(false);
    } else if (e.getSource() == games[2]) {
      new BlackJackController(new BlackJackModel());
      setVisible(false);
    }
  }
}

// ***** Composite Pattern *****
// JFrame to display the leaderboards
class LeaderboardFrame extends JFrame implements ActionListener {
  private JTable[] boards;
  private JLabel[] boardLabels;
  private JLabel title;
  private JPanel panel;

  // Constructor
  // Sets the border and layout for the leaderboard
  // Adds it to the panel and makes it visible
  public LeaderboardFrame() {
    panel = new JPanel();
    panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
    panel.setLayout(new GridBagLayout());

    displayBoards();
    add(new JScrollPane(panel), BorderLayout.CENTER);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    pack();
    setSize(600, 600);
    setVisible(true);
  }

  // Displays the boards
  // Retrieves all board information form the LeaderboardHandler
  // and the npopulates JTables to display data on the frame
  public void displayBoards() {
    GridBagConstraints gbc = new GridBagConstraints();
    title = new JLabel("Leaderboards", SwingConstants.CENTER);
    gbc.gridy = 0;
    panel.add(title, gbc);
    boardLabels = new JLabel[3];
    boards = new JTable[3];

    // retrieving the date from the handler and populating JTables
    if (boardLabels.length >= 3) {
      boardLabels[0] = new JLabel("Tic-Tac-Toe", SwingConstants.CENTER);
      boardLabels[1] = new JLabel("Connect4", SwingConstants.CENTER);
      boardLabels[2] = new JLabel("BlackJack", SwingConstants.CENTER);

      String[][] ttt = LeaderboardHandler.getInstance().retrieveBoard("tictactoe");
      String[][] connect4 = LeaderboardHandler.getInstance().retrieveBoard("connect4");
      String[][] blackjack = LeaderboardHandler.getInstance().retrieveBoard("blackjack");
      String[] cols = {"Player name", "Games Won", "Games Played", "Win %"};

      boards[0] = new JTable(ttt, cols);
      boards[1] = new JTable(connect4, cols);
      boards[2] = new JTable(blackjack, cols);
    }

    // adding the tables and lables to the panel for viewing
    for (int i = 0; i < boardLabels.length; i++) {
      boards[i].setBounds(0, 20, 100, 300);
      gbc.gridy = (i*2)+1;
      gbc.weighty = 2.0;
      gbc.insets = new Insets(20, 0, 0, 0);
      panel.add(boardLabels[i], gbc);
      gbc.gridy = (i*2)+2;
      panel.add(new JScrollPane(boards[i]), gbc);
    }
  }

  // Unused since no interactions with the leaderboard are made
  @Override
  public void actionPerformed(ActionEvent e) {}
}
