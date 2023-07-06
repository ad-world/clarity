CREATE TABLE IF NOT EXISTS User (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE ,
    password TEXT NOT NULL,
    phone_number TEXT NOT NULL,
    last_logged_in TEXT NOT NULL,
    login_streak INTEGER NOT NULL
)