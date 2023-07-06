PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Following (
    following_id INTEGER NOT NULL,
    follower_id INTEGER NOT NULL,
    PRIMARY KEY (following_id, follower_id),
    FOREIGN KEY(following_id) REFERENCES User(user_id),
    FOREIGN KEY(follower_id) REFERENCES User(user_id)
)