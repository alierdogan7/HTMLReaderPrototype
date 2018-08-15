package com.example.android.pdfrendererbasic;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.huma.room_for_asset.RoomAsset;

@Database(entities = {Word.class}, version = 2)
public abstract class WordRoomDatabase extends RoomDatabase {

    public abstract WordDAO wordDao();

    private static WordRoomDatabase INSTANCE;


    static WordRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordRoomDatabase.class) {
                if (INSTANCE == null) {
                    // POPULATING ON THE DEVICE ON FIRST LAUNCH
//                                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                            WordRoomDatabase.class, "word_database")
//                            .addCallback(sRoomDatabaseCallback)
//                            .build();

                    // SHIPPING PRE-POPULATED DATABASE
                    INSTANCE = RoomAsset
                            .databaseBuilder(context.getApplicationContext(), WordRoomDatabase.class, "lugat_database.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

//    private static RoomDatabase.Callback sRoomDatabaseCallback =
//            new RoomDatabase.Callback(){
//
//                @Override
//                public void onCreate (@NonNull SupportSQLiteDatabase db){
//                    super.onCreate(db);
//                    new PopulateDbAsync(INSTANCE).execute();
//                }
//
//            };
//
//    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
//
//        private final WordDAO mDao;
//
//        PopulateDbAsync(WordRoomDatabase db) {
//            mDao = db.wordDao();
//        }
//
//        @Override
//        protected Void doInBackground(final Void... params) {
////            mDao.deleteAll();
////            Word word = new Word("Hello");
////            mDao.insert(word);
////            word = new Word("World");
////            mDao.insert(word);
//
//            // TURKISH LUGAT INSERT ///
//            Log.d("LUGATDB", "Turkish words pre-population started.");
//            List<Word> wordList = new ArrayList<Word>();
//            String line;
//            String[] lugatFilenames = {"a", "b", "c", "c1", "d", "e", "f", "g", "h", "i", "i1", "j",
//                    "k", "l", "m", "n", "o", "o1", "p", "r", "s", "s1", "t", "u", "u1", "v", "y", "z"};
//
//            for(String lugatFile : lugatFilenames) {
//                int lugatRef = App.getContext().getResources().getIdentifier("lugat_" + lugatFile, "raw", App.getContext().getPackageName());
//                InputStream in = App.getContext().getResources().openRawResource(lugatRef);
//                BufferedReader br = new BufferedReader(new InputStreamReader(in));
//
//                try {
//                    while ((line = br.readLine()) != null) {
//                        String[] strings = TextUtils.split(line, ":::");
//                        if (strings.length < 3 || strings[0].equals("") || strings[1].equals("") || strings[2].equals(""))
//                            continue;
//
//                        String originalWord = strings[0];
//                        String fullWord = strings[1];
//                        String definition = strings[2];
//                        wordList.add(new Word(originalWord, fullWord, definition, "TR"));
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                mDao.insertAll(wordList);
//                wordList.clear(); //finish inserting the words starting with the letter and clear the list.
//            }
//
//            Log.d("LUGATDB", "Turkish words pre-population done.");
//            wordList.clear();
//            // TURKISH LUGAT INSERT ///
//
//
//            // ENGLISH LUGAT INSERT ///
//            Log.d("LUGATDB", "English words pre-population started.");
//            int lugatRef = App.getContext().getResources().getIdentifier("english_revised", "raw", App.getContext().getPackageName());
//            InputStream in = App.getContext().getResources().openRawResource(lugatRef);
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//
//            try {
//                while ((line = br.readLine()) != null) {
//                    String[] strings = TextUtils.split(line, ":");
//                    if (strings.length < 2 || strings[0].equals("") || strings[1].equals("") )
//                        continue;
//
//                    String[] originalWords = TextUtils.split(strings[0], ",");
//                    String[] islamicWords = TextUtils.split(strings[1], ",");
//
//                    for(int i = 0; i < islamicWords.length; i++)
//                        islamicWords[i] = islamicWords[i].trim();
//
//                    for( String originalWord : originalWords)
//                    {
//                        originalWord = originalWord.trim().toLowerCase();
//                        if (originalWord.length() == 0)
//                            continue;
//
//                        // search for this word on the list to check if added before
//                        int wordFound = -1;
//                        for(int i = 0; i < wordList.size(); i++) {
//                            if(wordList.get(i).simpleWord.equals(originalWord)) {
//                                wordFound = i;
//                            }
//                        }
//
//                        // if this word added before, just append the new meaning to the prev. meaning of this word
//                        if(wordFound != -1) {
//                            wordList.get(wordFound).definition = wordList.get(wordFound).definition +  ", " + strings[1];
//                        }
//                        // if not added before, add this word to the dict
//                        else {
//                            wordList.add(new Word(originalWord, originalWord, strings[1], "ENG"));
//                        }
//
//                    }
//
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            mDao.insertAll(wordList);
//
//            Log.d("LUGATDB", "English words pre-population done.");
//            // ENGLISH LUGAT INSERT ///
//
//
//            return null;
//        }
//    }
}
