package com.example.android.pdfrendererbasic;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    private static Context context;
    private static WordRoomDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        database = WordRoomDatabase.getDatabase(this); // for triggering first database pre-population
    }

    public static Context getContext() {
        return context;
    }

    public static WordRoomDatabase getDatabase() {
        return database;
    }
}