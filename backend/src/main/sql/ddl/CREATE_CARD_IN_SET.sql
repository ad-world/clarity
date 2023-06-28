PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS CardInSet (
    [set_id] INTEGER NOT NULL,
    card_id INTEGER NOT NULL,
    PRIMARY KEY (card_id, [set_id]),
    FOREIGN KEY (card_id) REFERENCES Card(card_id),
    FOREIGN KEY ([set_id]) REFERENCES CardSet([set_id])
)