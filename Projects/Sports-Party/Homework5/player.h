#ifndef PLAYER_H
#define PLAYER_H

#include <QObject>
#include <QGraphicsItem>
#include <QLabel>
#include "boardsquare.h"

class Player : public QObject, public QGraphicsItem {
	Q_OBJECT

	public:
		Player(int n = 0, BoardSquare *start = nullptr);	// constructor

		// Getters
		int getNumber() const { return number; }
		BoardSquare* getPos() const { return tilePos; }
		QColor getColor() const { return color; }
		std::map<PowerUp, int> getPowerUps() const { return power_ups; }

		bool hasWon();	// returns whether the this player has reached end of game
		void setPos(BoardSquare *s);	// updates the position of the player
		void moveToStruct(int x, int y) { x_ = x; y_ = y; prepareGeometryChange(); }
		void addPowerUp(PowerUp p) { power_ups.at(p)++; }
		void subPowerUp(PowerUp p) { power_ups.at(p)--; }

		QRectF boundingRect() const override;
		QPainterPath shape() const override;
		void paint(QPainter *painter, const QStyleOptionGraphicsItem *item, QWidget *widget) override;

		static int getSize() { return size_; }

		// Getters / Setters for bench and injury
		bool getIsBenched() const { return isBenched; }
		bool getIsInjured() const { return isInjured; }
		int getRoundsInjured() const { return roundsInjured; }
		void setIsBenched(const bool b) { isBenched = b; }
		void setIsInjured(const bool i) { isInjured = i; }
		void incrementRoundsInjured() { roundsInjured++; }
		void resetRoundsInjured() { roundsInjured = 0; }

	signals:
		void MakeMove(Player *p);	// signal to be sent to move player

		void MoveBackwards(Player *p);

	protected:
		void mousePressEvent(QGraphicsSceneMouseEvent *event) override;

	private:
		int number;
		BoardSquare *tilePos;	// current position on board
		QColor color;
		QString image;

		bool isBenched;
		bool isInjured;
		std::map<PowerUp, int> power_ups;	// mapping power up type to quantity
		int roundsInjured;

		int x_;
		int y_;

		static const int size_ = 20;
};

#endif // PLAYER_H
