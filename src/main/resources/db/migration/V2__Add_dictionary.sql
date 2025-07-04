CREATE TABLE IF NOT EXISTS InstalledDictionaries
(
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    language_id INT  NOT NULL,
    updated_at  TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS Dictionary
(
    word        TEXT NOT NULL,
    language_id INT  NOT NULL,
    json_data   TEXT NOT NULL,
    dialect     TEXT NOT NULL
);

CREATE INDEX Dictionary_lang_word_idx on Dictionary (language_id, lower(word));