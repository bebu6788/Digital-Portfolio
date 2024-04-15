#include "player.h"
#include <QtWidgets>

/* Constructor for Player objects
 * @params n Player number, start Pointer to start square
*/
Player::Player(int n, BoardSquare *start) {
	number = n;
	tilePos = start;
	isBenched = false;
	isInjured = false;
	roundsInjured = 0;

	power_ups.insert({PowerUp::Double_Team, 0});
	power_ups.insert({PowerUp::Halftime, 0});
	power_ups.insert({PowerUp::Pick_6, 0});

	// Sets the players color, pawn emoji, and position on the tile
	switch(n) {
		case 1:
			color = QColor(200, 0, 0);
			image = QString("⚽");
			x_ = 3 + tilePos->getX();
			y_ = 3 + tilePos->getY();
			break;
		case 2:
			color = QColor(60, 220, 0);
			image = QString("🏈");
			x_ = (tilePos->getWidth() / 2) + 3 + tilePos->getX();
			y_ = 3 + tilePos->getY();
			break;
		case 3:
			color = QColor(0, 30, 230);
			image = QString("🏀");
			x_ = 3 + tilePos->getX();
			y_ = (tilePos->getWidth() / 2) + 3 + tilePos->getY();
			break;
		case 4:
			color = QColor(230, 220, 20);
			image = QString("🥎");
			x_ = (tilePos->getWidth() / 2) + 3 + tilePos->getX();
			y_ = (tilePos->getWidth() / 2) + 3 + tilePos->getY();
			break;
		default:
			color = QColor(255, 255, 255);
			image = QString("");
			x_ = 0;
			y_ = 0;
			break;
	}

	std::string name = "Player " + std::to_string(number);
	this->setObjectName(name.c_str());
}

// Function that returns if the player has won the game
// @return If player's position is the final square
bool Player::hasWon() {
	return (tilePos->getNumber() == 36);
}

/* Updates the position of the player to the new provided BoardSquare
 * @param s New BoardSquare Position
*/
void Player::setPos(BoardSquare *s) {
	tilePos = s;
	// based on the player number - update the player's position
	switch(number) {
		case 1:	// top left
			x_ = 3 + tilePos->getX();
			y_ = 3 + tilePos->getY();
			break;
		case 2:	// top right
			x_ = (tilePos->getWidth() / 2) + 3 + tilePos->getX();
			y_ = 3 + tilePos->getY();
			break;
		case 3:	// bottom left
			x_ = 3 + tilePos->getX();
			y_ = (tilePos->getWidth() / 2) + 3 + tilePos->getY();
			break;
		case 4:	// bottom right
			x_ = (tilePos->getWidth() / 2) + 3 + tilePos->getX();
			y_ = (tilePos->getWidth() / 2) + 3 + tilePos->getY();
			break;
		default:
			x_ = 0;
			y_ = 0;
			break;
	}
}

// Override the boundingRect method of QGraphicsItem
// allows us to click on this object
QRectF Player::boundingRect() const {
	return QRectF(x_, y_, size_, size_);
}

// Override shape method to allow for easy object access
QPainterPath Player::shape() const {
	QPainterPath path;
	path.addEllipse(x_, y_, size_, size_);
	return path;
}


// Draws the shape of this object on its QGraphicsScene
void Player::paint(QPainter *painter, const QStyleOptionGraphicsItem *item, QWidget *widget) {
	Q_UNUSED(widget);

	QBrush b = painter->brush();
	painter->setBrush(QBrush(color));
	painter->drawEllipse(QRect(x_, y_, size_, size_)); // draw the player circle

	QFont font;
	font.setPixelSize(15);
	painter->setFont(font);
	painter->drawText(x_, y_+16, image);
	painter->setBrush(b);
}

// Sends signal when player object is clicked on
// @param event is a pointer to the event performed on an object
void Player::mousePressEvent(QGraphicsSceneMouseEvent *event) {
	Q_UNUSED(event);

	if (event->button() == Qt::RightButton) {
		emit MoveBackwards(this);
	} else {
		emit MakeMove(this);
	}

	update(); // call update()
}
