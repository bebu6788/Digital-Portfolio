#include "welcomedialog.h"
#include "ui_welcomedialog.h"

// Constructor
welcomeDialog::welcomeDialog(QWidget *parent) :
	QDialog(parent),
	ui(new Ui::welcomeDialog)
{
	ui->setupUi(this);

	image = new QGraphicsScene();
	ui->imageView->setScene(image);	// apply the scene to the Graphics View
	ui->imageView->setSceneRect(0, 0, ui->imageView->frameSize().width()-5, ui->imageView->frameSize().height()-5);

	QPixmap bg = QPixmap(":/welcome.PNG"); // add the welcome image
	image->addPixmap(bg.scaled(ui->imageView->frameSize().width()-5, ui->imageView->frameSize().height()-5));
}

// Destructor
welcomeDialog::~welcomeDialog() {
	delete ui;
}

// Close dialog when button is clicked
void welcomeDialog::on_playBtn_clicked() {
	this->close();
}

