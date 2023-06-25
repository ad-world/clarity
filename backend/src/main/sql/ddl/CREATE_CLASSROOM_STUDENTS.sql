PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS ClassroomStudents (
    class_id TEXT NOT NULL,
    user_id INTEGER NOT NULL,
    PRIMARY KEY (class_id, user_id),
    FOREIGN KEY (class_id) REFERENCES Classroom(private_code),
    FOREIGN KEY (user_id) REFERENCES User(user_id)
)