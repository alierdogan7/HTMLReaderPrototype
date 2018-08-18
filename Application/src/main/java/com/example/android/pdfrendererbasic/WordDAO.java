package com.example.android.pdfrendererbasic;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface WordDAO {

    @Insert
    void insert(Word word);

    @Insert
    void insertAll(List<Word> words);

    @Query("SELECT id, simpleWord, fullWord, language FROM word_table WHERE simpleWord LIKE :prefix AND language = 'TR' ORDER BY simpleWord LIMIT 300 ")
    List<Word> getAllTurkishWordsPrefixWithoutDefinition(String prefix);

    @Query("SELECT id, simpleWord, fullWord, language FROM word_table WHERE simpleWord LIKE :prefix AND language = 'ENG'  ORDER BY simpleWord LIMIT 300")
    List<Word> getAllEnglishWordsPrefixWithoutDefinition(String prefix);

    @Query("SELECT id, simpleWord, fullWord, language, definition FROM word_table WHERE simpleWord LIKE :prefix AND language = :lang  ORDER BY length(simpleWord) DESC LIMIT 50")
    List<Word> getAllPrefixCandidateLugatMatchesOrderedByLength(String prefix, String lang);

    @Query("SELECT id, simpleWord, fullWord, language, definition FROM word_table WHERE simpleWord IN(:prefices) AND language = :lang  ORDER BY length(simpleWord) DESC LIMIT 50")
    List<Word> getAllMultiplePrefixCandidateLugatMatchesOrderedByLength(String[] prefices, String lang);

    @Query("SELECT id, simpleWord, fullWord, definition, language FROM word_table WHERE language = 'ENG' ORDER BY simpleWord")
    List<Word> getAllEnglishWords();

    @Query("SELECT definition FROM word_table WHERE id = :wordId")
    String getDefinitionOfWord(int wordId);

    @Query("SELECT * FROM word_table ORDER BY simpleWord ASC LIMIT 100")
    List<Word> getAllWords();


}
