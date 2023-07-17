PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Announcements (
    announcement_id INTEGER PRIMARY KEY AUTOINCREMENT,
    class_id TEXT NOT NULL,
    text TEXT NOT NULL,
    description TEXT NOT NULL,
    dateCreated TEXT NOT NULL,
    FOREIGN KEY (class_id) REFERENCES Classroom(private_code)
)