#include "powerupdialog.h"
#include "ui_powerupdialog.h"

// Constructor for the powerUp dialog
// @param p Current player selecting a power-up
powerUpDialog::powerUpDialog(QWidget *parent, Player *p) :
	QDialog(parent),
	ui(new Ui::powerUpDialog)
{
	ui->setupUi(this);
	selection = 0;
	currentPlayer = p;
	disableButtons();	// disable buttons according to player's power ups

	qDebug() << "use power-up dialog";
	connect(ui->doubleTeamBtn, &QAbstractButton::clicked, this, &powerUpDialog::DoubleTeamClicked);
	connect(ui->pick6Btn, &QAbstractButton::clicked, this, &powerUpDialog::Pick6Clicked);
	connect(ui->halftimeBtn, &QAbstractButton::clicked, this, &powerUpDialog::HalfTimeClicked);
}

// Destructor
powerUpDialog::~powerUpDialog() {
	delete ui;
}

// Disables the power-up buttons according to player's inventory.
// Calls getPowerUps() on the current player and disables buttons accordingly
void powerUpDialog::disableButtons() {
	// Double_Team, Pick_6, Halftime
	std::map<PowerUp, int> map = currentPlayer->getPowerUps();
	if (map.empty()) {
		ui->doubleTeamBtn->setEnabled(false);
		ui->pick6Btn->setEnabled(false);
		ui->halftimeBtn->setEnabled(false);
	} else {
		ui->doubleTeamBtn->setEnabled(map[PowerUp::Double_Team]>0);
		ui->pick6Btn->setEnabled(map[PowerUp::Pick_6]>0);
		ui->halftimeBtn->setEnabled(map[PowerUp::Halftime]>0);
	}
}

// Slot invoked when Double Team button is clicked
void powerUpDialog::DoubleTeamClicked() {
	selection = 1;
	qDebug() << "choice: 1";
	currentPlayer->subPowerUp(PowerUp::Double_Team);	// use the power-up
	this->close();
}

// Slot invoked when Pick-6 button is clicked
void powerUpDialog::Pick6Clicked() {
	selection = 2;
	qDebug() << "choice: 2";
	currentPlayer->subPowerUp(PowerUp::Pick_6);	// use the power-up
	this->close();
}

// Slot invoked when Halftime button is clicked
void powerUpDialog::HalfTimeClicked() {
	selection = 3;
	qDebug() << "choice: 3";
	currentPlayer->subPowerUp(PowerUp::Halftime);	// use the power-up
	this->close();
}
