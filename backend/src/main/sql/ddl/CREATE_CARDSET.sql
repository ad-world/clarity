PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS CardSet (
    -- set_id is a reserved keyword, so using [] so SQL interprets it as a column name.
    [set_id] INTEGER PRIMARY KEY AUTOINCREMENT,
    creator_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    type VARCHAR(10) NOT NULL,
    is_public_ind INTEGER NOT NULL,
    likes INTEGER NOT NULL,
    CHECK ( is_public_ind = 0 OR is_public_ind = 1 ),
    CHECK ( likes >= 0),
    FOREIGN KEY (creator_id) REFERENCES User(user_id)
)