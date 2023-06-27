PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Card (
    card_id INTEGER PRIMARY KEY AUTOINCREMENT,
    -- set_id is a reserved keyword, so using [] so SQL interprets it as a column name.
    [set_id] INTEGER NOT NULL,
    phrase TEXT NOT NULL,
    FOREIGN KEY ([set_id]) REFERENCES CardSet(set_id)
)