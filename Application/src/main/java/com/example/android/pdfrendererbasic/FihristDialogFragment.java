package com.example.android.pdfrendererbasic;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by burak on 26.06.2018.
 */

public class FihristDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogBookmarks = inflater.inflate(R.layout.dialog_fihrist, null);

        ListView bookmarkLV = (ListView) dialogBookmarks.findViewById(R.id.fihristLV);

        final String bookId = this.getArguments().getString("bookId");


//        List<PdfDocument.Bookmark> fihrist = pdfView.getTableOfContents();
//        List<Bookmark> bookmarks = new ArrayList<Bookmark>();
//        Log.d("fihrist", bookId);

//        for(PdfDocument.Bookmark bookmark : fihrist) {
//            Bookmark b = new Bookmark(bookId, null, bookmark.getTitle(), (int) bookmark.getPageIdx());
//            //TODO: Add children support to bookmarks
//            bookmarks.add(b);
//
//            Log.d("fihrist", b.name + "," + b.pageNumber);
//        }

        WebView webViewEN = (WebView) getActivity().findViewById(R.id.ingWV);
        WebView webViewTR = (WebView) getActivity().findViewById(R.id.trVW);
        List<Bookmark> fihrist = MainActivity.getFihrist(bookId);
        final FihristArrayAdapter bAdapter = new FihristArrayAdapter(getContext(), fihrist, webViewEN, webViewTR ,this);
        // we passed bookId through Bundle in arguments of Fragment
        bookmarkLV.setAdapter(bAdapter);


        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogBookmarks);
        builder.setTitle("Fihrist");
        return builder.create();
    }
}
