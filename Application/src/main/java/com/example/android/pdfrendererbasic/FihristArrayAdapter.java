package com.example.android.pdfrendererbasic;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by burak on 26.06.2018.
 */

public class FihristArrayAdapter extends ArrayAdapter<Bookmark> {

    private Context context;
    private List<Bookmark> bookmarks;
    private DialogFragment dialog;
    private WebView webView1;
    private WebView webView2;

    public FihristArrayAdapter(@NonNull Context context, @NonNull List<Bookmark> bookmarks, DialogFragment dialog) {
        super(context, 0, bookmarks);
        this.context = context;
        this.bookmarks = bookmarks;
        this.dialog = dialog;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.row_bookmark,parent,false);

        final Bookmark b = bookmarks.get(position);

        View.OnClickListener jumpPageListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calc. the page is on which section
                int page = b.pageNumber;
                int offset = ((MainActivity2) context).sectionPageOffset;
                int section = Math.max(0, page - 1) / offset;
                ((MainActivity2) context).loadWebViews(section, b.anchorId);
                dialog.dismiss();

            }
        };

        TextView name = (TextView) listItem.findViewById(R.id.bookmarkTV);
        name.setText(b.name);
        name.setOnClickListener(jumpPageListener);

        ImageButton goButton = (ImageButton) listItem.findViewById(R.id.goBookmarkButton);
        goButton.setOnClickListener(jumpPageListener);


        ImageButton removeButton = (ImageButton) listItem.findViewById(R.id.removeBookmarkButton);
        removeButton.setVisibility(View.GONE);
        return listItem;
    }
}

