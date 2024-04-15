#ifndef GAMEWINDOW_H
#define GAMEWINDOW_H

#include <QMainWindow>
#include <QFile>
#include <iostream>
#include <QRandomGenerator>
#include <QTimer>
#include <QMessageBox>
#include "numplayersdialog.h"
#include "instructionsdialog.h"
#include "powerupdialog.h"
#include "welcomedialog.h"
#include "player.h"
#include "structure.h"
#include "boardsquare.h"

QT_BEGIN_NAMESPACE
namespace Ui { class GameWindow; }
QT_END_NAMESPACE

class GameWindow : public QMainWindow {
	Q_OBJECT

	public:
		GameWindow(QWidget *parent = nullptr);	// constructor
		~GameWindow();	// desctructor

		void generateBoard();	// generates the playing field
		void placePowerUps(std::vector<BoardSquare*> &sqrs);	// randomly places power-ups on playing field

		int rollDice(int n);	// generates a random roll from 1 to n
		std::string powerUpStringify(PowerUp p);	// converts PowerUp enum to string
		void checkPowerUp(Player *p);	// checks if the player has ended turn on a power-up
		void checkGameOver(bool btnClicked);	// checks if the game is over

		void nextPlayer();	// sets up window for the next player
		void displayPlayer();	// displays player information on window
		void singleStep(Player *p);	// moves the player a single step on playing field
		void singleStepBack(Player *p); // moves the player a single step back on playing field

		void pick6PlayerSelection();

		void disableAllButtons(bool b);	// disables all buttons on screen

	private slots:
		void StartGame();
		void EndGame();
		void MakeMoveSlot(Player *p);
		void MoveBackwardsSlot(Player *p);
		void DisplayRoll();
		void UsePowerUp();
		void Quit();

		void on_instrBtn_clicked();

	private:
		Ui::GameWindow *ui;
		QGraphicsScene *board;

		std::vector<BoardSquare*> tiles;	// board squares for playing field
		std::vector<Structure*> structs;	// bench and trainer structures
		std::vector<Player*> players;	// vector of all players

		Player *currPlayer;
		int numPlayers;
		bool pick6Mode;
		bool gameOver;

		// dialogs
		instructionsDialog *instr;
		numPlayersDialog *npd;
		powerUpDialog *pud;
		welcomeDialog *wd;
};

#endif // GAMEWINDOW_H
