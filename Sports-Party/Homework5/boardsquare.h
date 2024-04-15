#ifndef BOARDSQUARE_H
#define BOARDSQUARE_H

#include <QObject>
#include <QGraphicsItem>
#include <QGraphicsView>

// 3 different types of resource represented by enum class
enum class PowerUp { Empty, Double_Team, Pick_6, Halftime };

class BoardSquare : public QObject, public QGraphicsItem {
	Q_OBJECT

	public:
		BoardSquare(int num, PowerUp p, QColor c, int x, int y); // constructor

		// Getters
		int getNumber() const { return number; }
		PowerUp getPowerUp() const { return type; }
		int getX() const { return x_; }
		int getY() const { return y_; }

		// Setters
		void setPowerUp(const PowerUp p) { type = p; }
		void setColor(const QColor c) { color = c; }

		QRectF boundingRect() const override;
		QPainterPath shape() const override;
		void paint(QPainter *painter, const QStyleOptionGraphicsItem *item, QWidget *widget) override;

		static int getWidth() { return width_; }

		// **** FACTORY PATTERN ****
		// Factory method to to create board squares
		static BoardSquare* factoryMethod(PowerUp p, int num, QGraphicsView *view);

	private:
		PowerUp type;
		int number;
		QColor color;

		int x_;
		int y_;

		static const int width_ = 50;
};

#endif // BOARDSQUARE_H
