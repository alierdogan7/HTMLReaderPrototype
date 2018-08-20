package com.example.android.pdfrendererbasic;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by burak on 26.06.2018.
 */

public class Bookmark {
    String bookId;
    Date date;
    String name;
    int pageNumber;
    float position;
    String bookmarkId;

    public Bookmark(String bookmarkId, String bookId, Date date, String name, int pageNumber) {
        this.bookmarkId = bookmarkId;
        this.bookId = bookId;
        this.date = date;
        this.name = name;
        this.pageNumber = pageNumber;
    }

    public String getFormattedDate() {
        SimpleDateFormat dt = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dt.format(this.date);

    }
}
