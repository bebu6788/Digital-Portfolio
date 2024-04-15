#ifndef NUMPLAYERSDIALOG_H
#define NUMPLAYERSDIALOG_H

#include <QDialog>

namespace Ui {
	class numPlayersDialog;
}

class numPlayersDialog : public QDialog {
	Q_OBJECT

	public:
		explicit numPlayersDialog(QWidget *parent = nullptr);
		~numPlayersDialog();

		int getNumber() const { return numPlayers; }	// gives access to number selected

	private slots:
		void NumberSelected();

	private:
		Ui::numPlayersDialog *ui;
		int numPlayers;
};

#endif // NUMPLAYERSDIALOG_H
