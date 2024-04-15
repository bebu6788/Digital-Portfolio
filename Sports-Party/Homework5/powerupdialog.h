#ifndef POWERUPDIALOG_H
#define POWERUPDIALOG_H

#include <QDialog>
#include "player.h"

namespace Ui {
	class powerUpDialog;
}

class powerUpDialog : public QDialog {
	Q_OBJECT

	public:
		explicit powerUpDialog(QWidget *parent = nullptr, Player *p = nullptr); // constructor
		~powerUpDialog(); // destructor

		// Getters and Setters
		Player* getPlayer() const { return currentPlayer; }
		void setPlayer(Player *p) { currentPlayer = p; }

		int getSelection() { return selection; }
		void resetSelection() { selection = 0; }
		void disableButtons();	// disables buttons on dialog

	private slots:
		void DoubleTeamClicked();
		void Pick6Clicked();
		void HalfTimeClicked();

	private:
		Ui::powerUpDialog *ui;
		Player *currentPlayer;
		int selection; // 1, 2, or 3 to know which power up is selected
};

#endif // POWERUPDIALOG_H
