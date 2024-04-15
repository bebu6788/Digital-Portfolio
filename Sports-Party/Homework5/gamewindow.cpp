#include "gamewindow.h"
#include "ui_gamewindow.h"
#include <QtWidgets>
#include <QDebug>
#include <QThread>
#include <QRandomGenerator>

// Constructor for the GameWindow
GameWindow::GameWindow(QWidget *parent)
	: QMainWindow(parent)
	, ui(new Ui::GameWindow)
{
	ui->setupUi(this);

	board = new QGraphicsScene();
	ui->boardView->setScene(board);	// apply the scene to the Graphics View
	ui->boardView->setSceneRect(0, 0, ui->boardView->frameSize().width()-5, ui->boardView->frameSize().height()-5);

	QPixmap bg = QPixmap(":/stadium.jpg"); // add the background image
	board->addPixmap(bg.scaled(ui->boardView->frameSize().width()-5, ui->boardView->frameSize().height()-5));

	generateBoard();	// generate the board

	qDebug() << "Starting";

	// show the welcomeDialog
	wd = new welcomeDialog();
	wd->setModal(true);
	wd->exec();

	// show the numPlayersDialog
	npd = new numPlayersDialog();
	npd->setModal(true);
	npd->exec();

	numPlayers = npd->getNumber();	// retrieve the selected number of players

	qDebug() << "after dialog";

	// adding player objects to board area
	for (int i = 0; i < numPlayers; i++) {
		Player *p  = new Player(i+1, tiles.front());
		connect(p, &Player::MakeMove, this, &GameWindow::MakeMoveSlot);
		connect(p, &Player::MoveBackwards, this, &GameWindow::MoveBackwardsSlot);

		players.push_back(p);
		board->addItem(p);
	}

	currPlayer = players[0];
	pick6Mode = false;
	gameOver = false;

	disableAllButtons(true);	// disable all buttons except startBtn
	ui->startBtn->setDisabled(false);
	ui->infoText->setText("Click 'Start Game' to begin playing!!");

	connect(ui->rollBtn, &QPushButton::clicked, this, &GameWindow::DisplayRoll);
	connect(ui->startBtn, &QPushButton::clicked, this, &GameWindow::StartGame);
	connect(ui->endBtn, &QPushButton::clicked, this, &GameWindow::EndGame);
	connect(ui->powerUpBtn, &QPushButton::clicked, this, &GameWindow::UsePowerUp);
	connect(ui->quitBtn, &QPushButton::clicked, this, &GameWindow::Quit);
}

// Destructor
GameWindow::~GameWindow() {
	delete ui;
}

/* Places the various power-ups around the board.
 * @param sqrs All board tiles on playing area
*/
void GameWindow::placePowerUps(std::vector<BoardSquare*> &sqrs) {
	// randomly place power-ups in tiles
	std::vector<BoardSquare*> temp = sqrs;
	int p = 12;
	while (p > 0) {
		int randIndex = QRandomGenerator::global()->generate() % (temp.size()-2) + 1;
		int number = temp[randIndex]->getNumber();	// get the number of the tile selected

		if (p > 8) {	// apply Double Team to first 4 selected
			delete sqrs[number-1];
			sqrs[number-1] = BoardSquare::factoryMethod(PowerUp::Double_Team, number, ui->boardView);
		} else if (p > 4) {	// Halftime for the next 4 selected
			delete sqrs[number-1];
			sqrs[number-1] = BoardSquare::factoryMethod(PowerUp::Halftime, number, ui->boardView);
		} else {	// Pick-6 for the last 4 tiles selected
			delete sqrs[number-1];
			sqrs[number-1] = BoardSquare::factoryMethod(PowerUp::Pick_6, number, ui->boardView);
		}

		temp.erase(temp.begin()+randIndex);	// ensures no square is chosen twice
		p--;
	}
}

// Generates the board for the game
void GameWindow::generateBoard() {
	// generate the tiles with no power-ups
	for (int t = 1; t <= 36; t++) {
		BoardSquare *sqr = BoardSquare::factoryMethod(PowerUp::Empty, t, ui->boardView);
		tiles.push_back(sqr);
	}

	// update tiles with power-ups
	placePowerUps(tiles);

	for (BoardSquare* s : tiles) {
		board->addItem(s);

		QPixmap pm;
		QGraphicsPixmapItem *tm;
		switch (s->getPowerUp()) {
			case PowerUp::Double_Team:
				// place proper image on board
				pm = QPixmap(":/doubleTeam.png");
				tm = board->addPixmap(pm.scaled(s->getWidth(), s->getWidth()));
				tm->setPos(s->getX(), s->getY());
				break;
			case PowerUp::Halftime:
				// place proper image on board
				pm = QPixmap(":/halftime.png");
				tm = board->addPixmap(pm.scaled(s->getWidth(), s->getWidth()));
				tm->setPos(s->getX(), s->getY());
				break;
			case PowerUp::Pick_6:
				// place proper image on board
				pm = QPixmap(":/pick6.jpg");
				tm = board->addPixmap(pm.scaled(s->getWidth(), s->getWidth()));
				tm->setPos(s->getX(), s->getY());
				break;
			default:
				break;
		}
	}
	// create and place the structures (bench and trainer)
	// 0 index is bench 1 index
	structs.push_back(new Bench(200, 150, 60, 200));
	structs.push_back(new Trainer(425, 325, 100, 150));

	for (Structure *s : structs) {
		board->addItem(s);
	}

	// apply images to structures to add visual appeal
	QPixmap benchImage = QPixmap(":/bench.png");
	QTransform tr;
	tr.rotate(270);
	benchImage = benchImage.transformed(tr);
	QGraphicsPixmapItem *bm = board->addPixmap(benchImage.scaled(structs[0]->getWidth(), structs[0]->getHeight()));
	bm->setPos(structs[0]->getX(), structs[0]->getY());

	QPixmap trainerImage = QPixmap(":/trainer.png");
	QGraphicsPixmapItem *tm = board->addPixmap(trainerImage.scaled(structs[1]->getWidth(), structs[1]->getHeight()));
	tm->setPos(structs[1]->getX(), structs[1]->getY());
}

/* Generates a random number between 1 and n
 * @param n Max number of roll
*/
int GameWindow::rollDice(int n) {
	return QRandomGenerator::global()->generate() % n + 1;
}

/* Converts a PowerUp to a string
 * @param p PowerUp enum to convert to string
*/
std::string GameWindow::powerUpStringify(PowerUp p) {
	switch (p) {
		case PowerUp::Double_Team:
			return "Double Team";
		case PowerUp::Halftime:
			return "Halftime";
		case PowerUp::Pick_6:
			return "Pick-6";
		default:
			return "";
	}
}

/* Checks if the player has landed on a power-up at the end of their turn
 * @param p Player to check position of
*/
void GameWindow::checkPowerUp(Player *p) {
	if (p->getPos()->getPowerUp() != PowerUp::Empty) {
		p->addPowerUp(p->getPos()->getPowerUp());	// add the power-up to the player

		// display message box for acquiring a power-up
		std::string pstr = powerUpStringify(p->getPos()->getPowerUp());
		pstr = "Player " + std::to_string(currPlayer->getNumber()) + " acquired the " + pstr + " Power-Up!";
		QMessageBox msgBox;
		msgBox.setText(pstr.c_str());
		msgBox.exec();
	}
}

/* Checks if the game has concluded and displays the proper message box
 * @param btnClicked Whether the End Game button has been clicked to invoke this function
*/
void GameWindow::checkGameOver(bool btnClicked) {
	if (btnClicked) {
		// end game was clicked - need to display end game message and close
		QMessageBox msgBox;
		msgBox.setText("You Have Ended The Game!");
		msgBox.exec();
		gameOver = true;
	}
	// checks if too many players have quit
	if (players.size() < 2) {
		QMessageBox msgBox;
		msgBox.setText("Not Enough Players, Ending Game!");
		msgBox.exec();
		gameOver = true;
	}
	// checks if the current player has won the game
	if (currPlayer->hasWon()) {
		QMessageBox msgBox;
		std::string text = "Player " + std::to_string(currPlayer->getNumber()) + " has won!";
		msgBox.setText(text.c_str());
		msgBox.exec();
		gameOver = true;
	}

	if (gameOver) {
		this->close();
	}

	return;
}

// Progresses game to the next player
void GameWindow::nextPlayer() {
	qDebug() << "next player";
	checkGameOver(false);
	int currentPlayerIndex = 0;
	auto it = std::find(players.begin(), players.end(), currPlayer);

	// If element was found
	if (it != players.end()){
		currentPlayerIndex = it - players.begin();
	}
	currentPlayerIndex++; // update to next player
	if (currentPlayerIndex >= players.size()) {	// go back to first player if at end of player list
		currentPlayerIndex = 0;
	}

	currPlayer = players[currentPlayerIndex];

	ui->rollBtn->setDisabled(false);
	displayPlayer();    // display the new player
}

// Displays the current player's information to window
// Accounts for if the player is injured at the trainer or sitting out on the bench
void GameWindow::displayPlayer() {
	qDebug() << "display player";
	ui->startBtn->setDisabled(true);
	ui->boardView->setBackgroundBrush(QBrush(QColor(255, 255, 255)));
	ui->boardView->setCursor(Qt::ArrowCursor);

	ui->playerText->clear(); // clear the text
	// place player name in text area
	QColor c = currPlayer->getColor();
	QString text = "<span style=\"color: " + c.name() + "\"> Player " + std::to_string(currPlayer->getNumber()).c_str() + "</span>";
	ui->playerText->append(text);
	ui->playerText->setAlignment(Qt::AlignCenter);

	// reset roll and info text
	ui->rollText->clear();
	ui->infoText->clear();
	// reset power-up selection
	if (pud != nullptr) {
		pud->resetSelection();
	}

	ui->infoText->setStyleSheet("QTextEdit { background: transparent; color: " + c.name() + "; }");

	// Check if they are at the trainers
	if (currPlayer->getIsInjured()) {
		// call playerAbletoMove override function
		if (structs[1]->playerAbleToMove(currPlayer, -1) == false) {
			// display a pop up
			qDebug() << "injured. skipping player turn";
			QString popUpMessage = "Player " + QString::number(currPlayer->getNumber()) + " is injured!\nThey have " + QString::number(3 - currPlayer->getRoundsInjured()) + " round(s) of revocery with the trainer.";
			QMessageBox::information(nullptr, "Player Injured", popUpMessage);
			nextPlayer();
		}
	} // Display a pop up showing that the player is benched
	else if (currPlayer->getIsBenched()) {
		QString popUpMessage = "Player " + QString::number(currPlayer->getNumber()) + " is benched!\nRoll a 3 or higher to play!";
		QMessageBox::information(nullptr, "Player Benched", popUpMessage);
	}
}

/* Moves the player a single step on the board
 * @param p Player to be moved
*/
void GameWindow::singleStep(Player *p) {
	board->removeItem(p);
	p->setPos(tiles[p->getPos()->getNumber()]);
	board->addItem(p);
}

// Moves the player p a single step backwards
// Used for pick-6 power up
// @param p is a pointer to the player you are moving back
void GameWindow::singleStepBack(Player *p) {
	board->removeItem(p);
	p->setPos(tiles[p->getPos()->getNumber()-2]);
	board->addItem(p);
}

// Used when player chooses to use their pick-6 power up
// Disables all buttons, changes the cursor, and activates pick6mode
void GameWindow::pick6PlayerSelection() {
	disableAllButtons(true);
	ui->infoText->setText("You applied the Pick-6 power-up! Right-click on a player to send them back 6 spaces!!!");
	ui->boardView->setBackgroundBrush(QBrush(QColor(0, 0, 0)));
	ui->boardView->setCursor(Qt::CrossCursor);
	pick6Mode = true;
}

/* Disables/enables all buttons on window
 * @param b Whether to disable or enable buttons
*/
void GameWindow::disableAllButtons(bool b) {
	ui->rollBtn->setDisabled(b);
	ui->endBtn->setDisabled(b);
	ui->startBtn->setDisabled(b);
	ui->powerUpBtn->setDisabled(b);
	ui->quitBtn->setDisabled(b);
}

// *** Slot Definitions ***

// Starts the game when startBtn is clicked
void GameWindow::StartGame() {
	disableAllButtons(false);

	displayPlayer();
}

// Ends the game when endBtn is clicked
void GameWindow::EndGame() {
	checkGameOver(true);
}

/* Moves player once player object is clicked
 * @param p Player to be moved
*/
void GameWindow::MakeMoveSlot(Player *p) {
	if (p == currPlayer && !pick6Mode) {	// ensure only currentPlayer moves
		// retrieve dice roll
		int roll = ui->rollText->toPlainText().toInt();

		if (roll != 0) {
			// disable all buttons
			disableAllButtons(true);
			int loop = 1;
			for (int i = 0; i < roll; i++) {
				// move player one step at a time
				QTimer::singleShot(400*(i+1), [=, &loop]() mutable {
					if (currPlayer->getPos()->getNumber() != 36) {	// TODO: weird pause when player reaches end
						singleStep(p);
					}
					loop++;
				});
			}

			// used to wait for completion of dot movement before continuing onto next code segment
			while (loop <= roll) {
				qApp->processEvents();
			}

			// check ending on power up square
			checkPowerUp(p);
		}

		qApp->processEvents();
		QThread::msleep(600); // sleep so that after the player fully moves, there is a delay between turns

		// Injury: if they roll a 6 and were not just benched
		if (roll == 6 && QRandomGenerator::global()->bounded(2) == 0 && !currPlayer->getIsBenched()) {
			// Add a 50% chance to get injured when rolling a 6
			currPlayer->setIsInjured(true);
			int px = structs[1]->getX()+(structs[1]->getWidth()/2)-(p->getSize()/2);
			int py = structs[1]->getY()+(p->getNumber()*30)-10;
			p->moveToStruct(px, py);
			// display message that player has been injured
			QMessageBox msgBox;
			std::string text = "Player " + std::to_string(currPlayer->getNumber()) + " got Injured!";
			msgBox.setText(text.c_str());
			msgBox.exec();
		}
		// Bench: if they roll a 1, are NOT already benched
		if (roll == 1 && !currPlayer->getIsBenched() && QRandomGenerator::global()->bounded(4) == 0) {
			// Add a 25% chance to get benched when rolling a 1
			currPlayer->setIsBenched(true);
			int px = structs[0]->getX()+(structs[0]->getWidth()/2)-(p->getSize()/2);
			int py = structs[0]->getY()+(p->getNumber()*40)-10;
			p->moveToStruct(px, py);
			// display message that player has been benched
			QMessageBox msgBox;
			std::string text = "Player " + std::to_string(currPlayer->getNumber()) + " has been Benched!";
			msgBox.setText(text.c_str());
			msgBox.exec();
		}

		disableAllButtons(false);
		nextPlayer();	// go to next player
	}
}

// Used when a player clicks on another player in pick-6 mode
// Sets the player p selected back 6 squares by calling singleStepBack()
// @param p is a pointer to the player you are sending back with pick-6 powerup
void GameWindow::MoveBackwardsSlot(Player *p) {
		if (p != currPlayer && pick6Mode) { // Checks if a player has used a pick-6 power up
		disableAllButtons(true);
		int loop = 1;
		for (int i = 0; i < 6; i++) {
			// move player one step at a time
			QTimer::singleShot(500*(i+1), [=, &loop]() mutable {
				if (p->getPos()->getNumber() != 1) {
					singleStepBack(p); // set the player back 1 square at a time
				}
				loop++;
			});
		}

		// used to wait for completion of dot movement before continuing onto next code segment
		while (loop <= 6) {
			qApp->processEvents();
		}

		// after setting player p back, enable all buttons and exit pick6mode
		disableAllButtons(false);
		pick6Mode = false;

		qApp->processEvents();
		QThread::msleep(600);
		// Send back to trainer if injured
		if (p->getIsInjured()) {
			int px = structs[1]->getX()+(structs[1]->getWidth()/2)-(p->getSize()/2);
			int py = structs[1]->getY()+p->getNumber()*30;
			p->moveToStruct(px, py);
		}
		// send back to bench if benched
		if (p->getIsBenched()) {
			int px = structs[0]->getX()+(structs[0]->getWidth()/2)-(p->getSize()/2);
			int py = structs[0]->getY()+p->getNumber()*30;
			p->moveToStruct(px, py);
		}

		displayPlayer();
	}
}

// Displays the roll when rollBtn is clicked
// Clears the display and displays the new roll
// Calls rollDice() with different values depending on if Double Team or Halftime power-ups were used
void GameWindow::DisplayRoll() {
	ui->rollText->clear();
	int randNum = 0;
	// Call the rollDice function to generate a random integer
	if (pud != nullptr) {
		if (pud->getSelection() == 1) {	// if Double Team being used
			randNum = rollDice(12);
		} else if (pud->getSelection() == 3) {	// if Halftime being used
			randNum = rollDice(3);
		} else {
			randNum = rollDice(6);	// normal dice roll
		}
	} else {
		randNum = rollDice(6);
	}

	// Display all info to the UI
	ui->infoText->setText("Nice Roll!! Left-click on your pawn to move!!");
	ui->rollText->append(QString::number(randNum)); // Display the random integer in the QTextEdit
	ui->rollText->setAlignment(Qt::AlignCenter);
	ui->rollBtn->setDisabled(true);
	ui->powerUpBtn->setDisabled(true);

	// call playerAbleToMove override function
	// skip if roll is less than 3
	if (structs[0]->playerAbleToMove(currPlayer, randNum) == false){
		qDebug() << "Benched. skipping player turn";
		qApp->processEvents();
		QThread::msleep(1500);
		nextPlayer();
	}
}

// Invoked when Power Up button is clicked - prompts user with
// power-up selection dialog
void GameWindow::UsePowerUp() {
	pud = new powerUpDialog(nullptr, currPlayer);
	pud->setModal(true);
	pud->exec();

	if (pud->getSelection() != 0) {	// disable if power-up is selected
		ui->powerUpBtn->setDisabled(true);
		if (pud->getSelection() == 1) {	// if Double Team being used
			ui->infoText->setText("You applied the Double Team power-up!");
		} else if (pud->getSelection() == 3) {	// if Halftime being used
			ui->infoText->setText("You applied the Halftime power-up!");
		}
	}


	// set pick6Mode if Pick-6 is being used
	if (pud->getSelection() == 2) {
		pick6PlayerSelection();
	}
}

// User quits the game and is removed from board
void GameWindow::Quit() {
	Player *temp = currPlayer; // this fixes out of order bug

	// remove pawn from board
	board->removeItem(temp);

	// display message that player has quit
	QMessageBox msgBox;
	std::string text = "Player " + std::to_string(temp->getNumber()) + " has Quit!";
	msgBox.setText(text.c_str());
	msgBox.exec();

	nextPlayer();	// go to next player and remove player from list
	players.erase(std::remove(players.begin(), players.end(), temp), players.end());

	checkGameOver(false);
}

// When the instructions button is clicked
// Create new dialog to display the instructions to the user
void GameWindow::on_instrBtn_clicked() {
	instr = new instructionsDialog();
	instr->setModal(false);
	instr->show();
}

