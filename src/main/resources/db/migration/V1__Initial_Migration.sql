CREATE TABLE IF NOT EXISTS LanguageEntity
(
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    language VARCHAR(25) NOT NULL
);

CREATE TABLE IF NOT EXISTS OkuTextEntity
(
    id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    title              VARCHAR(200) NOT NULL,
    body               TEXT         NOT NULL,
    wordList           TEXT         NOT NULL,
    okuWordSet         TEXT,
    timestamp_created  TEXT         NOT NULL,
    timestamp_finished TEXT         NULL,
    language_id        INT          NOT NULL,
    frequency_analysis BOOLEAN      NOT NULL, -- Whether a text is from frequency analysis or normal reading
    CONSTRAINT fk_OkuTextEntity_language_id__id FOREIGN KEY (language_id) REFERENCES LanguageEntity (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS OkuSentenceEntity
(
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    sentence TEXT   NOT NULL,
    text_id  BIGINT NULL,
    CONSTRAINT fk_OkuSentenceEntity_text_id__id FOREIGN KEY (text_id) REFERENCES OkuTextEntity (id) ON DELETE SET NULL ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS OkuWordEntity
(
    id                INTEGER PRIMARY KEY AUTOINCREMENT,
    word              TEXT NOT NULL,
    wordstatus        INT  NOT NULL,
    learning_start    TEXT NULL,
    learning_finished TEXT NULL,
    language_id       INT  NOT NULL,
    occurrence_count  INT  NOT NULL, -- Increase by one for each occurrence of word in Texts
    CONSTRAINT fk_OkuWordEntity_language_id__id FOREIGN KEY (language_id) REFERENCES LanguageEntity (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS OkuWord_Sentence
(
    okuWord     BIGINT NOT NULL,
    okuSentence BIGINT NOT NULL,
    CONSTRAINT fk_OkuWord_Sentence_okuWord__id FOREIGN KEY (okuWord) REFERENCES OkuWordEntity (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_OkuWord_Sentence_okuSentence__id FOREIGN KEY (okuSentence) REFERENCES OkuSentenceEntity (id) ON DELETE RESTRICT ON UPDATE RESTRICT
);

CREATE TABLE IF NOT EXISTS SettingsEntity
(
    key   TEXT PRIMARY KEY NOT NULL,
    value TEXT NOT NULL
);

CREATE UNIQUE INDEX OkuWordEntity_word_idx on OkuWordEntity (word, language_id);

INSERT INTO LanguageEntity (language)
values ('Albanian'),
       ('Arabic'),
       ('Azerbaijani'),
       ('Basque'),
       ('Belarusian'),
       ('Bosnian'),
       ('Bulgarian'),
       ('Catalan'),
       ('Cantonese'),
       ('Chinese'),
       ('Croatian'),
       ('Czech'),
       ('Danish'),
       ('Dutch'),
       ('English'),
       ('Esperanto'),
       ('Estonian'),
       ('Finnish'),
       ('French'),
       ('Galician'),
       ('German'),
       ('Hungarian'),
       ('Icelandic'),
       ('Irish'),
       ('Italian'),
       ('Korean'),
       ('Kurmanji'),
       ('Sorani'),
       ('Latin'),
       ('Latvian'),
       ('Lithuanian'),
       ('Luxembourgish'),
       ('Maltese'),
       ('Moldovan'),
       ('Montenegrin'),
       ('Norwegian'),
       ('Persian'),
       ('Polish'),
       ('Portuguese'),
       ('Romani'),
       ('Romanian'),
       ('Russian'),
       ('Scottish Gaelic'),
       ('Serbian'),
       ('Slovak'),
       ('Slovene'),
       ('Spanish'),
       ('Swedish'),
       ('Tajik'),
       ('Tatar'),
       ('Turkish'),
       ('Turkmen'),
       ('Ukrainian'),
       ('Urdu'),
       ('Uzbek'),
       ('Welsh');
