PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS CardSet (
    set_id INTEGER PRIMARY KEY AUTOINCREMENT,
    creator_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    type VARCHAR(10) NOT NULL,
    FOREIGN KEY creator_id REFERENCES User(user_id)
)