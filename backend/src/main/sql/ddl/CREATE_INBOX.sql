PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Inbox (
    notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    message TEXT NOT NULL,
    notification_date TEXT NOT NULL,
    message_read INTEGER NOT NULL,
    CHECK (message_read == 0 OR message_read == 1),
    FOREIGN KEY(user_id) REFERENCES User(user_id)
)