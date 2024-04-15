#include "numplayersdialog.h"
#include "ui_numplayersdialog.h"

// Consturctor for the dialog
numPlayersDialog::numPlayersDialog(QWidget *parent) :
	QDialog(parent),
	ui(new Ui::numPlayersDialog)

{
	ui->setupUi(this);

	qDebug() << "players dialog";
	connect(ui->twoBtn, &QAbstractButton::clicked, this, &numPlayersDialog::NumberSelected);
	connect(ui->threeBtn, &QAbstractButton::clicked, this, &numPlayersDialog::NumberSelected);
	connect(ui->fourBtn, &QAbstractButton::clicked, this, &numPlayersDialog::NumberSelected);
}

// Destructor
numPlayersDialog::~numPlayersDialog() {
	delete ui;
}

// Determines the number of players selected based on which button
// is pressed and closes the modal
void numPlayersDialog::NumberSelected() {
	QPushButton* buttonSender = qobject_cast<QPushButton*>(sender());
	QString buttonText = buttonSender->text();

	bool isInt = false;
	numPlayers = buttonText.toInt(&isInt); // get the number of players based on button selected
	if (!isInt) {
		numPlayers = 0;
		return;
	}

	this->close();
}
