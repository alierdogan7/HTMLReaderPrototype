package com.example.android.pdfrendererbasic;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by burak on 25.06.2018.
 */

@Entity(tableName = "word_table")
public class Word {

    @PrimaryKey(autoGenerate = true)
    int id;

    String simpleWord;
    String fullWord;
    String definition;
    String language;

    public Word(String simpleWord, String fullWord, String definition, String language) {
        this.simpleWord = simpleWord;
        this.fullWord = fullWord;
        this.definition = definition;

        language = language.toUpperCase();
        if (language.equals("TR") || language.equals("ENG"))
            this.language = language;
        else
            throw new IllegalArgumentException("language should be either TR or ENG");
    }
}
