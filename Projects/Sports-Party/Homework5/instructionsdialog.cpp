#include "instructionsdialog.h"
#include "ui_instructionsdialog.h"
#include <QWidgetItem>
#include <QLabel>
#include <QFile>

// Constructor
instructionsDialog::instructionsDialog(QWidget *parent) :
	QDialog(parent),
	ui(new Ui::instructionsDialog)
{
	ui->setupUi(this);

	readFile(); // Reads in the instructions text file from Resources.qrc
}

// Destructor
instructionsDialog::~instructionsDialog() {
	delete ui;
}

// Reads in the instructions text file from Resources.qrc
// Sets the text in a QLabel to display in the dialog
void instructionsDialog::readFile() {
	QFile file(":/instructions.txt");

	// open the file and read it in
	if (!file.open(QIODevice::ReadOnly | QIODevice::Text)) {
		return;
	} else {
		QTextStream in(&file);
		QString s = in.readAll();

		QLabel *instr = new QLabel();
		instr->setText(s);
		instr->setWordWrap(true);
		instr->setParent(this);
		instr->setStyleSheet("QLabel { background-color: grey; color: white; padding-left: 5px }");
		instr->setGeometry(20, 20, this->width()-40, this->height()-70);
	}
}

// Closes the dialog when ok button is clicked
void instructionsDialog::on_okBtn_clicked() {
	this->close();
}

