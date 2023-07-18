PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS ClassroomAttempts (
     classroom_attempt_id INTEGER PRIMARY KEY AUTOINCREMENT,
     task_id INTEGER NOT NULL,
     user_id INTEGER NOT NULL,
     card_id INTEGER NOT NULL,
     pronunciation DOUBLE NOT NULL,
     accuracy DOUBLE NOT NULL,
     fluency DOUBLE NOT NULL,
     completeness DOUBLE NOT NULL,
     is_complete INT NOT NULL,
     attempt_date TEXT NOT NULL,
     CHECK ( is_complete = 0 OR is_complete = 1 ),
     FOREIGN KEY(user_id) REFERENCES User(user_id),
     FOREIGN KEY(card_id) REFERENCES Card(card_id),
     FOREIGN KEY(task_id) REFERENCES Tasks(task_id)
)