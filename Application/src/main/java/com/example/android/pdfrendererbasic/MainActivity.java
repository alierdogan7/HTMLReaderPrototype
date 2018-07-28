/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.pdfrendererbasic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String FRAGMENT_PDF_RENDERER_BASIC = "pdf_renderer_basic";
    WebView webViewEN;
    WebView webViewTR;
    Context mContext;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_real);
        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PdfRendererBasicFragment(),
//                            FRAGMENT_PDF_RENDERER_BASIC)
//                    .commit();
            mContext = this;
            webViewEN = (WebView)findViewById(R.id.ingWV);
            webViewTR = (WebView)findViewById(R.id.trVW);
            webViewEN.loadUrl("file:///android_asset/onuncusoz.html");
            webViewEN.getSettings().setBuiltInZoomControls(true);
            webViewEN.getSettings().setDisplayZoomControls(true);
            webViewEN.getSettings().setJavaScriptEnabled(true);
            webViewTR.getSettings().setBuiltInZoomControls(true);
            webViewTR.getSettings().setDisplayZoomControls(true);
            webViewTR.loadUrl("file:///android_asset/turkce.html");
            webViewEN.setWebViewClient(new WebViewClient() {
                @Override
                public void onLoadResource(WebView view, String url) {
//                    webViewEN.loadUrl("javascript:document.getElementsByClassName('Başlık')[0].style.color='green';");
//                    webViewEN.evaluateJavascript("document.getElementsByClassName('Paragraf-1')[0].style.fontSize='2em';", null);
                    webViewEN.getSettings().setTextZoom(200);

                    (new AlertDialog.Builder(mContext)).setTitle("DENEME").setMessage("DENEME")
                            .setPositiveButton("GOD BUL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    webViewEN.findAllAsync("god");
                                    webViewEN.findNext(true);
                                    webViewEN.findNext(true);
                                    webViewEN.findNext(true);
                                    webViewEN.findNext(true);
                                }
                            })
                            .setNegativeButton("8.isarete git", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    webViewEN.loadUrl("file:///android_asset/onuncusoz.html#sekizinci");
                                    webViewEN.evaluateJavascript("location.hash = '#sekizinci';", null);

                                }
                            })
                            .setCancelable(true)
                            .create().show();

                }

            });


            webViewEN.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    webViewTR.scrollTo(scrollX, scrollY);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onActionModeStarted(final android.view.ActionMode mode) {
        mode.getMenu().add("DENEME").setEnabled(true).setVisible(true)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (webViewEN != null) {
                            webViewEN.evaluateJavascript("window.getSelection().toString()", new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                        if (value != null && value.length() > 0) {
                                            // do something about user select
                                            Toast.makeText(getApplicationContext(), "SELECTED: " + value, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            );
                        }
                        mode.finish();
                        return true;
                    }
                });

//        (new AlertDialog.Builder(this)).setTitle("DENEME").setMessage("DENEME").create().show();

//        Menu menus = mode.getMenu();
//        mode.getMenuInflater().inflate(R.menu.highlight,menus);
        super.onActionModeStarted(mode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                new AlertDialog.Builder(this)
                        .setMessage(R.string.intro_message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
