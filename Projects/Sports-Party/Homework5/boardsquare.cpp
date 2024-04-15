// TODO: Factory Method

#include "boardsquare.h"
#include <QtWidgets>

/* Constructor for the BoardSquare Class
 * @param num Tile number
*/
BoardSquare::BoardSquare(int num, PowerUp p, QColor c, int x, int y) {
	type = p;
	number = num;
	color = c;

	x_ = x;
	y_ = y;
}

// Override the boundingRect method of QGraphicsItem
// allows us to click on this object
QRectF BoardSquare::boundingRect() const {
	return QRectF(x_, y_, width_, width_);
}

// Override shape method to allow for easy object access
QPainterPath BoardSquare::shape() const {
	QPainterPath path;
	path.addRect(x_, y_, width_, width_);
	return path;
}

// Draws the shape of this object on its QGraphicsScene
void BoardSquare::paint(QPainter *painter, const QStyleOptionGraphicsItem *item, QWidget *widget) {
	Q_UNUSED(widget);

	QBrush b = painter->brush();
	painter->setBrush(QBrush(color));
	painter->drawRect(QRect(x_, y_, width_, width_));
	painter->drawText(x_+2, y_+12, QString(std::to_string(number).c_str()));
	painter->setBrush(b);
}

/* Factory method to create and return BoardSquares based on number and power-up.
 * @params p PowerUp for square, num Number of square, view Pointer to graphics view
*/
BoardSquare* BoardSquare::factoryMethod(PowerUp p, int num, QGraphicsView *view) {
	int x = 0, y = 0;
	// using the number to determine placement of square
	if (num < 11) {	// 1 - 10 (bottom)
		x = (9 * width_) - (((num - 1) % 10) * width_);
		y = (9 * width_);
	} else if (num < 20) {	// 11 - 19 (left)
		x = 0;
		y = (9 * width_) - ((num % 10) * width_);
	} else if (num < 29) {	// 20 - 28 (top)
		x = ((num % 10) + 1) * width_;
		y = 0;
	} else {	// 29 - 36 (right)
		x = (9 * width_);
		y = (((num % 10) + 2) % 10) * width_;
	}
	// update x and y to center on screen
	x += ((view->frameSize().width() / 2) - (width_ / 2)) - (4.5 * width_);
	y += ((view->frameSize().height() / 2) - (width_ / 2)) - (4.5 * width_);

	switch (p) {
		case PowerUp::Empty:
			return new BoardSquare(num, p, QColor(200, 200, 200), x, y);
		case PowerUp::Double_Team:
			return new BoardSquare(num, p, QColor(15, 190, 0), x, y);
		case PowerUp::Halftime:
			return new BoardSquare(num, p, QColor(225, 190, 0), x, y);
		case PowerUp::Pick_6:
			return new BoardSquare(num, p, QColor(220, 0, 0), x, y);
	}
}
