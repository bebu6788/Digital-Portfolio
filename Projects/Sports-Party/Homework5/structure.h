#ifndef STRUCTURE_H
#define STRUCTURE_H

#include <QObject>
#include <QGraphicsItem>
#include "player.h"

// Two different structures - Bench and Trainer
// this meets the inheritance requirement
class Structure : public QObject, public QGraphicsItem {
	Q_OBJECT

	public:
		Structure(int x, int y, int w, int h);	// constructor
		virtual ~Structure() = default;	// destructor

		// Getters
		int getX() const { return x_; }
		int getY() const { return y_; }
		int getWidth() const { return width_; }
		int getHeight() const { return height_; }

		QRectF boundingRect() const override;
		QPainterPath shape() const override;
		void paint(QPainter *painter, const QStyleOptionGraphicsItem *item, QWidget *widget) override;

		virtual bool playerAbleToMove(Player *p, int roll);	// returns whether player can move from structure

	protected:
		QColor color;

		int x_;
		int y_;

		int width_;
		int height_;
};

// Bench inherits from Structure
class Bench : public Structure {
	Q_OBJECT

	public:
		Bench(int x, int y, int w, int h);	// constructor

		bool playerAbleToMove(Player *p, int roll) override;	// returns whether player can move from structure
};

// Trainer inherits from Structure
class Trainer : public Structure {
	Q_OBJECT

	public:
		Trainer(int x, int y, int w, int h);	// constructor

		bool playerAbleToMove(Player *p, int roll) override;	// returns whether player can move from structure
};

#endif // STRUCTURE_H
