PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS SetLikes (
    user_id INTEGER NOT NULL,
    [set_id] INTEGER NOT NULL,
    PRIMARY KEY (user_id, [set_id]),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
    FOREIGN KEY ([set_id]) REFERENCES CardSet([set_id])
)