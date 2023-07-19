PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Attempts (
    attempt_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    [set_id] INTEGER NOT NULL,
    card_id INTEGER NOT NULL,
    pronunciation DOUBLE NOT NULL,
    accuracy DOUBLE NOT NULL,
    fluency DOUBLE NOT NULL,
    completeness DOUBLE NOT NULL,
    attempt_date TEXT NOT NULL,
    FOREIGN KEY(user_id) REFERENCES User(user_id),
    FOREIGN KEY([set_id]) REFERENCES CardSet([set_id]),
    FOREIGN KEY(card_id) REFERENCES Card(card_id)
)