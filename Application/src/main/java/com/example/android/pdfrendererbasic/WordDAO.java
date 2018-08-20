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

    @Query("SELECT id, simpleWord FROM word_table " +
            "WHERE simpleWord LIKE :prefix AND language = :lang  ORDER BY length(simpleWord) DESC LIMIT 50")
    List<Word> getAllPrefixCandidateLugatMatchesOrderedByLength1Prefix(String lang, String prefix);

    @Query("SELECT id, simpleWord FROM word_table " +
            "WHERE simpleWord LIKE :prefix1 OR simpleWord LIKE :prefix2 " +
            "AND language = :lang  ORDER BY length(simpleWord) DESC LIMIT 100")
    List<Word> getAllPrefixCandidateLugatMatchesOrderedByLength2Prefix(String lang, String prefix1, String prefix2);

    @Query("SELECT id, simpleWord FROM word_table " +
            "WHERE simpleWord LIKE :prefix1 OR simpleWord LIKE :prefix2 OR simpleWord LIKE :prefix3 " +
            "AND language = :lang  ORDER BY length(simpleWord) DESC LIMIT 100")
    List<Word> getAllPrefixCandidateLugatMatchesOrderedByLength3Prefix(String lang, String prefix1, String prefix2, String prefix3);

    @Query("SELECT id, simpleWord FROM word_table " +
            "WHERE simpleWord LIKE :prefix1 OR simpleWord LIKE :prefix2 OR simpleWord LIKE :prefix3 " +
            "OR simpleWord LIKE :prefix4 " +
            "AND language = :lang  ORDER BY length(simpleWord) DESC LIMIT 100")
    List<Word> getAllPrefixCandidateLugatMatchesOrderedByLength4Prefix(String lang, String prefix1, String prefix2, String prefix3, String prefix4);

    @Query("SELECT id, simpleWord FROM word_table " +
            "WHERE simpleWord LIKE :prefix1 OR simpleWord LIKE :prefix2 OR simpleWord LIKE :prefix3 " +
            "OR simpleWord LIKE :prefix4 OR simpleWord LIKE :prefix5 " +
            "AND language = :lang  ORDER BY length(simpleWord) DESC LIMIT 100")
    List<Word> getAllPrefixCandidateLugatMatchesOrderedByLength5Prefix(String lang, String prefix1, String prefix2, String prefix3, String prefix4, String prefix5);

    @Query("SELECT id, simpleWord FROM word_table " +
            "WHERE simpleWord LIKE :prefix1 OR simpleWord LIKE :prefix2 OR simpleWord LIKE :prefix3 " +
            "OR simpleWord LIKE :prefix4 OR simpleWord LIKE :prefix5 OR simpleWord LIKE :prefix6 " +
            "AND language = :lang  ORDER BY length(simpleWord) DESC LIMIT 100")
    List<Word> getAllPrefixCandidateLugatMatchesOrderedByLength6Prefix(String lang, String prefix1, String prefix2, String prefix3, String prefix4, String prefix5, String prefix6);

    @Query("SELECT id, simpleWord, fullWord, language, definition FROM word_table WHERE simpleWord IN(:prefices) AND language = :lang  ORDER BY length(simpleWord) DESC LIMIT 50")
    List<Word> getAllMultiplePrefixCandidateLugatMatchesOrderedByLength(String[] prefices, String lang);

    @Query("SELECT id, simpleWord, fullWord, definition, language FROM word_table WHERE language = 'ENG' ORDER BY simpleWord")
    List<Word> getAllEnglishWords();

    @Query("SELECT definition FROM word_table WHERE id = :wordId")
    String getDefinitionOfWord(int wordId);

    @Query("SELECT * FROM word_table ORDER BY simpleWord ASC LIMIT 100")
    List<Word> getAllWords();


}
