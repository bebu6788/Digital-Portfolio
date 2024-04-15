#include "structure.h"
#include <QtWidgets>

// Constructor for Structure
// Will be overridden by inherited classes bench and trainer
// @params x,y is the (x,y) position of the bench / trainer
// @params w,h is the width and heigh of the bench / trainer
Structure::Structure(int x, int y, int w, int h) {
	x_ = x;
	y_ = y;
	width_ = w;
	height_ = h;

	color = QColor(200, 200, 200); // set the color
}

// Sets the shape and position based on x,y and w,h of Structure
// @return QRectF object for the bench or trainer
QRectF Structure::boundingRect() const {
	return QRectF(x_, y_, width_, height_);
}

// Override shape method to allow for easy object access
QPainterPath Structure::shape() const {
	QPainterPath path;
	path.addRect(x_, y_, width_, height_);
	return path;
}

// Draws the shape of this object on its QGraphicsScene
void Structure::paint(QPainter *painter, const QStyleOptionGraphicsItem *item, QWidget *widget) {
	Q_UNUSED(widget);

	QBrush b = painter->brush();
	painter->setBrush(QBrush(color));
	painter->drawRect(QRect(x_, y_, width_, height_));
	painter->setBrush(b);
}

// Abstract function that inherited classes bench and trainer will override and implement
bool Structure::playerAbleToMove(Player *p, int roll) {
	return true;
}

// Constructor overrides Structure's constructor
// @params x,y is the (x,y) position of the bench
// @params w,h is the width and heigh of the bench
Bench::Bench(int x, int y, int w, int h): Structure(x, y, w, h) {
	color = QColor(Qt::transparent);
}

// Overrides Structure::playerAbleToMove()
// Determines if the player is allowed to move off of the bench
// Checks the players roll. If less than 3, player remains benched, if greater they can move
// @param p is a pointer to the player we are checking if can move
// @param roll is the value of the dice roll the benched player has made
// @retrun boolean true if the player can move off of the bench, false otherwise
bool Bench::playerAbleToMove(Player *p, int roll) {
	if (!p->getIsBenched()) {
		return true;
	}

	if (roll >= 3) {
		p->setIsBenched(false);
		return true;
	}

	return false;
}

// Constructor overrides Structure's constructor
// @params x,y is the (x,y) position of the Trainer
// @params w,h is the width and heigh of the Trainer
Trainer::Trainer(int x, int y, int w, int h): Structure(x, y, w, h) {
	color = QColor(Qt::transparent);
}

// Overrides Structure::playerAbleToMove()
// Determines if the player is allowed to leave the trainers
// Checks how many rounds the player p has spent injured at the trainers
// A player must sit out two rounds to recover at the trainers
// @param p is a pointer to the player we are checking if can move
// @param roll unused here
// @retrun boolean true if the player can leave the trainers, false otherwise
bool Trainer::playerAbleToMove(Player *p, int roll) {
	if (p->getRoundsInjured() < 2) { // less than not leq since first iteration will have 0 rounds injured, then 1
		p->incrementRoundsInjured();
		return false;
	}

	p->resetRoundsInjured();
	p->setIsInjured(false);
	return true;
}
