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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String FRAGMENT_PDF_RENDERER_BASIC = "pdf_renderer_basic";
    WebView webViewEN;
    WebView webViewTR;
    Context mContext;
    Button nextButton;
    Button prevButton;
    Button zoomPlus;
    Button zoomMinus;
    boolean fihristMod = false;
    int currPage;
    int currFihrist;
    ArrayList<Integer> pageSizesWebViewEN;
    ArrayList<Integer> pageSizesWebViewTR;
    ArrayList<Integer> pageTopsWebViewTR;
    ArrayList<Integer> pageTopsWebViewEN;
    boolean engLoaded = false;
    boolean trLoaded = false;

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
            nextButton = (Button) findViewById(R.id.next);
            prevButton = (Button) findViewById(R.id.prev);
            zoomPlus = (Button) findViewById(R.id.zoomPlus);
            zoomMinus = (Button) findViewById(R.id.zoomMinus);
            RadioButton fihristRadio = (RadioButton) findViewById(R.id.fihristRadio);
            RadioGroup group = (RadioGroup) findViewById(R.id.radioGroup);
            group.setOnCheckedChangeListener((group1, checkedId) -> {
                if(checkedId == R.id.fihristRadio)
                    fihristMod = true;
                else
                    fihristMod = false;
            });

            pageSizesWebViewEN = new ArrayList<Integer>();
            pageSizesWebViewTR  = new ArrayList<Integer>();
            pageTopsWebViewEN  = new ArrayList<Integer>();
            pageTopsWebViewTR = new ArrayList<Integer>();

            currPage = 0;
            currFihrist = 0;
            nextButton.setOnClickListener((view) -> {
                if(fihristMod) {
                    currFihrist++;
                    webViewEN.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
                    webViewTR.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
                }
                else {
                    currPage++;
                    webViewEN.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
                    webViewTR.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
                }
            });
            prevButton.setOnClickListener((view) -> {
                if(fihristMod) {
                    currFihrist--;
                    webViewEN.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
                    webViewTR.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
                }
                else {
                    currPage--;
                    webViewEN.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
                    webViewTR.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
                }
            });

            zoomPlus.setOnClickListener( (view) -> {
                    int newZoom = webViewEN.getSettings().getTextZoom() + 20;
                    webViewEN.getSettings().setTextZoom(newZoom);
                    webViewTR.getSettings().setTextZoom(newZoom);

            });


            zoomMinus.setOnClickListener( (view) -> {
                int newZoom = webViewEN.getSettings().getTextZoom() - 20;
                webViewEN.getSettings().setTextZoom(newZoom);
                webViewTR.getSettings().setTextZoom(newZoom);

            });
            webViewEN.loadUrl("file:///android_asset/eng_min.html");
            webViewEN.getSettings().setBuiltInZoomControls(true);
            webViewEN.getSettings().setDisplayZoomControls(true);
            webViewEN.getSettings().setJavaScriptEnabled(true);
            webViewTR.getSettings().setJavaScriptEnabled(true);
            webViewTR.getSettings().setBuiltInZoomControls(true);
            webViewTR.getSettings().setDisplayZoomControls(true);
            webViewTR.loadUrl("file:///android_asset/tr_min.html");
            webViewEN.setWebViewClient(new WebViewClient() {
                @Override
                public void onLoadResource(WebView view, String url) {
                    if(engLoaded)
                        return;

                    webViewEN.evaluateJavascript("// JQUERY highlight separate paragraphs\n" +
                            "var offsets = [];\n" +
                            "$(\".Paragraf-1\").each( function(index) {\n" +
                            "\t//var inText = document.getElementsByClassName('sayfaAralik')[x].setAttribute(\"name\", \"page-\" + x);\n" +
                            "\t//var pageNumHeader = document.createTextNode(\"#\" + x);\n" +
                            "\t\n" +
                            "\t$(this).prepend(\"<h3>#\" + index + \"</h3>\");\n" +
                            "\t$(this).attr(\"name\", \"prg-\" + index);\n" +
                            "\t$('html, body').animate({ scrollTop: $(\".Paragraf-1\").eq(index).offset().top}, 1000);\n" +
                            "\t//offsets.push(document.body.scrollTop + elm.getBoundingClientRect().top);\n" +
                            "} );\n" +
                            "\n", null);
                    engLoaded = true;

//                    webViewEN.evaluateJavascript("//calc page tops\n" +
//                            "var pageTopElements = document.getElementsByClassName(\"sayfaAralik\");\n" +
//                            "var pageTops = [];\n" +
//                            "for( var i = 0; i < pageTopElements.length; i++) {\n" +
//                            "\t\tpageTops.push(pageTopElements[i].offsetTop);\n" +
//                            "}\n" +
//                            "pageTops;", (pageTops) -> {
//                            Log.d("EVALJS", pageTops);
//
//                            for(String str: TextUtils.split(pageTops.substring(1,pageTops.length() - 1), ",")) {
//                                pageTopsWebViewEN.add(Integer.parseInt(str));
//                            }
//                    });
//                    webViewEN.loadUrl("javascript:document.getElementsByClassName('Başlık')[0].style.color='green';");
//                    webViewEN.evaluateJavascript("document.getElementsByClassName('Paragraf-1')[0].style.fontSize='2em';", null);


//                    (new AlertDialog.Builder(mContext)).setTitle("DENEME").setMessage("DENEME")
//                            .setPositiveButton("Page Num Test", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
////                                    webViewEN.findAllAsync("god");
////                                    webViewEN.findNext(true);
////                                        webViewEN.evaluateJavascript("document.getElementsByClassName('pageNumber')", lastPage -> {
////                                            int currPage = 0;
////                                            new Timer().schedule(new TimerTask() {
////                                                @Override
////                                                public void run() {
////                                                    runOnUiThread(new Thread() {
////                                                        if(currPage < Integer.parseInt(lastPage)) {
////                                                            webViewEN.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null)
////                                                            webViewTR.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null)
////                                                        }
////                                                    });
////
////                                                }
////                                            }, 1000);
////                                        });
//                                }
//                            })
//                            .setNegativeButton("Fihrist test", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
////                                    webViewEN.loadUrl("file:///android_asset/onuncusoz.html#sekizinci");
//                                    webViewEN.evaluateJavascript("location.hash = '#sekizinci';", null);
//
//                                }
//                            })
//                            .setCancelable(true);
////                            .create().show();

                }

            });

            webViewTR.setWebViewClient(new WebViewClient() {

                @Override
                public void onLoadResource(WebView view, String url) {
                if(trLoaded)
                    return;

                webViewTR.evaluateJavascript("\n" +
                        "// JQUERY highlight separate paragraphs\n" +
                        "var offsets = [];\n" +
                        "$(\".Paragraf-1\").each( function(index) {\n" +
                        "\t//var inText = document.getElementsByClassName('sayfaAralik')[x].setAttribute(\"name\", \"page-\" + x);\n" +
                        "\t//var pageNumHeader = document.createTextNode(\"#\" + x);\n" +
                        "\t\n" +
                        "\t$(this).prepend(\"<h3>#\" + index + \"</h3>\");\n" +
                        "\t$(this).attr(\"name\", \"prg-\" + index);\n" +
                        "\t$('html, body').animate({ scrollTop: $(\".Paragraf-1\").eq(index).offset().top}, 1000);\n" +
                        "\t//offsets.push(document.body.scrollTop + elm.getBoundingClientRect().top);\n" +
                        "} );",null);
                trLoaded = true;
//                    webViewTR.evaluateJavascript("//calc page tops\n" +
//                            "var pageTopElements = document.getElementsByClassName(\"sayfaAralik\");\n" +
//                            "var pageTops = [];\n" +
//                            "for( var i = 0; i < pageTopElements.length; i++) {\n" +
//                            "\t\tpageTops.push(pageTopElements[i].offsetTop);\n" +
//                            "}\n" +
//                            "pageTops;", (pageTops) -> {
//                        Log.d("EVALJS", pageTops);
//                        for(String str: TextUtils.split(pageTops.substring(1,pageTops.length() - 1), ",")) {
//                            pageTopsWebViewTR.add(Integer.parseInt(str));
//                        }
//
//                    });
//                    webViewEN.loadUrl("javascript:document.getElementsByClassName('Başlık')[0].style.color='green';");
//                    webViewEN.evaluateJavascript("document.getElementsByClassName('Paragraf-1')[0].style.fontSize='2em';", null);


//                    (new AlertDialog.Builder(mContext)).setTitle("DENEME").setMessage("DENEME")
//                            .setPositiveButton("Page Num Test", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
////                                    webViewEN.findAllAsync("god");
////                                    webViewEN.findNext(true);
////                                        webViewEN.evaluateJavascript("document.getElementsByClassName('pageNumber')", lastPage -> {
////                                            int currPage = 0;
////                                            new Timer().schedule(new TimerTask() {
////                                                @Override
////                                                public void run() {
////                                                    runOnUiThread(new Thread() {
////                                                        if(currPage < Integer.parseInt(lastPage)) {
////                                                            webViewEN.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null)
////                                                            webViewTR.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null)
////                                                        }
////                                                    });
////
////                                                }
////                                            }, 1000);
////                                        });
//                                }
//                            })
//                            .setNegativeButton("Fihrist test", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
////                                    webViewEN.loadUrl("file:///android_asset/onuncusoz.html#sekizinci");
//                                    webViewEN.evaluateJavascript("location.hash = '#sekizinci';", null);
//
//                                }
//                            })
//                            .setCancelable(true);
////                            .create().show();

                }

            });


            webViewEN.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                    float positionTopView = webViewEN.getTop();
//                    float contentHeight = webViewEN.getContentHeight();
//                    float currentScrollPosition = webViewEN.getScrollY();
//                    float percentWebview = (currentScrollPosition - positionTopView) / contentHeight;
//
//                    float webviewsize = webViewTR.getContentHeight() - webViewTR.getTop();
//                    float positionInWV = webviewsize * percentWebview;
//                    int positionY = Math.round(webViewTR.getTop() + positionInWV);
//                    webViewTR.scrollTo(0, scrollY);

//                    float dy = scrollY - oldScrollY;
//
//                    int currentPage = 0;
//                    for(int i = 0; i < pageTopsWebViewEN.size(); i++) {
//                        if(scrollY > pageTopsWebViewEN.get(i))
//                            currentPage = i;
//                        else
//                            break;
//                    }
//
//                    int currentPageSize = pageTopsWebViewEN.get(currentPage + 1) - pageTopsWebViewEN.get(currentPage);
//                    int correspondingPageSize = pageTopsWebViewTR.get(currentPage + 1) - pageTopsWebViewTR.get(currentPage);
//                    float scrollRatio = dy / currentPageSize;
//                    webViewTR.scrollTo(scrollX - oldScrollX, webViewTR.getScrollY() + (int) (scrollRatio * correspondingPageSize));
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
