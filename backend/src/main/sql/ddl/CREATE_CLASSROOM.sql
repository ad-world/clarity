PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Classroom (
    private_code TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    teacher INTEGER NOT NULL,
    FOREIGN KEY (teacher) REFERENCES User(user_id)
)