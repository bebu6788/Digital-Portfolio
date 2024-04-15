#ifndef INSTRUCTIONSDIALOG_H
#define INSTRUCTIONSDIALOG_H

#include <QDialog>

namespace Ui {
	class instructionsDialog;
}

class instructionsDialog : public QDialog {
	Q_OBJECT

	public:
		explicit instructionsDialog(QWidget *parent = nullptr);
		~instructionsDialog();

		void readFile();	// reads in instruction file

	private slots:
		void on_okBtn_clicked();

	private:
		Ui::instructionsDialog *ui;
};

#endif // INSTRUCTIONSDIALOG_H
