CREATE TABLE IF NOT EXISTS drinks (
        drinkID VARCHAR(20) NOT NULL,
        drinkName VARCHAR(50) NOT NULL,
        drinkAlc VARCHAR(50) NOT NULL,
        drinkCat VARCHAR(50) NOT NULL,
        drinkImg VARCHAR(500) NOT NULL,
        PRIMARY KEY(drinkID)
);