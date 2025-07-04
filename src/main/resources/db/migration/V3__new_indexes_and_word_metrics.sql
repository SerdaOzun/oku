CREATE INDEX OkuWordEntity_lang_word_idx on OkuWordEntity (language_id, lower(word));

ALTER TABLE OkuTextEntity
    ADD current_page INT DEFAULT 1;
ALTER TABLE OkuTextEntity
    ADD percentage_known INT DEFAULT -1;
ALTER TABLE OkuTextEntity
    ADD unique_words_count INT DEFAULT -1;
ALTER TABLE OkuTextEntity
    ADD total_words_count INT DEFAULT -1;
ALTER TABLE OkuTextEntity
    ADD update_metrics Boolean DEFAULT TRUE;
