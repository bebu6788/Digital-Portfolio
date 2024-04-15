#ifndef WELCOMEDIALOG_H
#define WELCOMEDIALOG_H

#include <QDialog>
#include <QGraphicsScene>

namespace Ui {
class welcomeDialog;
}

class welcomeDialog : public QDialog {
	Q_OBJECT

	public:
		explicit welcomeDialog(QWidget *parent = nullptr);
		~welcomeDialog();

	private slots:
		void on_playBtn_clicked();

	private:
		Ui::welcomeDialog *ui;
		QGraphicsScene *image;  // scene to display image
};

#endif // WELCOMEDIALOG_H
