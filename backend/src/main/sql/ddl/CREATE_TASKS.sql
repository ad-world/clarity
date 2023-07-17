PRAGMA foreign_keys = ON;

DROP TABLE IF EXISTS Tasks;
CREATE TABLE IF NOT EXISTS Tasks (
    task_id INTEGER PRIMARY KEY AUTOINCREMENT,
    class_id TEXT NOT NULL,
    [set_id] INTEGER NOT NULL,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    due_date TEXT,
    FOREIGN KEY([set_id]) REFERENCES CardSet([set_id]),
    FOREIGN KEY(class_id) REFERENCES Classroom(private_code)
)