PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS Tasks (
    task_id INTEGER PRIMARY KEY AUTOINCREMENT,
    class_id TEXT NOT NULL,
    [set_id] INTEGER NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    due_date TEXT,
    difficulty INTEGER NOT NULL,
    CHECK ( difficulty = 0 OR difficulty = 1 OR difficulty = 2 ),
    FOREIGN KEY([set_id]) REFERENCES CardSet([set_id]),
    FOREIGN KEY(class_id) REFERENCES Classroom(private_code)
)