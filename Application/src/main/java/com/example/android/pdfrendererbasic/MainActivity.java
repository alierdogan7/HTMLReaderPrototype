
package com.example.android.pdfrendererbasic;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

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
    boolean onAnimationTRSide = false;
    boolean onAnimationOtherSide = false;
    ScaleGestureDetector sgd;

    enum Side {Turkish, OtherLang, Neutral}

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
            prepareButtons();
            prepareWebViews();


        } else {
            //do something for restoring webview states
            mContext = this;
//            prepareButtons();
            prepareWebViews();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    public void prepareWebViews() {
        webViewEN = (WebView) findViewById(R.id.ingWV);
        webViewTR = (WebView) findViewById(R.id.trVW);

//        webViewEN.loadUrl("file:///android_asset/eng_min.html");
        webViewEN.loadUrl("file:///android_asset/hasr-ENG-tashihli.html");
        webViewEN.getSettings().setBuiltInZoomControls(true);
        webViewEN.getSettings().setDisplayZoomControls(true);
        webViewEN.getSettings().setJavaScriptEnabled(true);
        webViewTR.getSettings().setJavaScriptEnabled(true);
        webViewTR.getSettings().setBuiltInZoomControls(true);
        webViewTR.getSettings().setDisplayZoomControls(true);
        webViewEN.getSettings().setDisplayZoomControls(false);
        webViewTR.getSettings().setDisplayZoomControls(false);
        webViewEN.getSettings().setSupportZoom(false);
        webViewTR.getSettings().setSupportZoom(false);
//        webViewTR.loadUrl("file:///android_asset/tr_min.html");
        webViewTR.loadUrl("file:///android_asset/hasr-TR-tashihli.html");

        webViewEN.addJavascriptInterface(new MyJavaScriptInterface(this), "androidInterface");
        webViewTR.addJavascriptInterface(new MyJavaScriptInterface(this), "androidInterface");

        webViewEN.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (engLoaded)
                    return;

                engLoaded = true;

            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
                Log.d("SCALE", "old " + oldScale );
                Log.d("SCALE", "new " + newScale );
            }
        });

        webViewTR.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                if (trLoaded)
                    return;

                trLoaded = true;
            }

        });


//        webViewEN.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//
//            }
//        });
    }

    public void prepareButtons() {
        nextButton = (Button) findViewById(R.id.next);
        prevButton = (Button) findViewById(R.id.prev);
        zoomPlus = (Button) findViewById(R.id.zoomPlus);
        zoomMinus = (Button) findViewById(R.id.zoomMinus);
        RadioButton fihristRadio = (RadioButton) findViewById(R.id.fihristRadio);
        RadioGroup group = (RadioGroup) findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener((group1, checkedId) -> {
            if (checkedId == R.id.fihristRadio)
                fihristMod = true;
            else
                fihristMod = false;
        });

        pageSizesWebViewEN = new ArrayList<Integer>();
        pageSizesWebViewTR = new ArrayList<Integer>();
        pageTopsWebViewEN = new ArrayList<Integer>();
        pageTopsWebViewTR = new ArrayList<Integer>();

        currPage = 0;
        currFihrist = 0;
        nextButton.setOnClickListener((view) -> {
            if (fihristMod) {
                currFihrist++;
                webViewEN.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
                webViewTR.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
            } else {
                currPage++;
                webViewEN.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
                webViewTR.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
            }
        });
        prevButton.setOnClickListener((view) -> {
            if (fihristMod) {
                currFihrist--;
                webViewEN.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
                webViewTR.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
            } else {
                currPage--;
                webViewEN.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
                webViewTR.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
            }
        });

        zoomPlus.setOnClickListener((view) -> {
            int newZoom = webViewEN.getSettings().getTextZoom() + 20;
            webViewEN.getSettings().setTextZoom(newZoom);
            webViewTR.getSettings().setTextZoom(newZoom);
            });


        zoomMinus.setOnClickListener((view) -> {
            int newZoom = webViewEN.getSettings().getTextZoom() - 20;
            webViewEN.getSettings().setTextZoom(newZoom);
            webViewTR.getSettings().setTextZoom(newZoom);

        });

    }

    @Override
    public void onActionModeStarted(final android.view.ActionMode mode) {

        mode.getMenu().add("DENEME").setEnabled(true).setVisible(true)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {


                        mode.finish();
                        return true;
                    }
                });
//
//        if (webViewEN != null) {
//            webViewEN.evaluateJavascript("window.getSelection().toString()", (value) -> {
//                if (value != null && value.length() > 0) {
//                    // do something about user select
//                    Toast.makeText(getApplicationContext(), "SELECTED: " + value, Toast.LENGTH_SHORT).show();
//                    showLugatDefinition(value.replace("\"", ""));
//                }
//            });
//        }

//        (new AlertDialog.Builder(this)).setTitle("DENEME").setMessage("DENEME").create().show();

//        Menu menus = mode.getMenu();
//        mode.getMenuInflater().inflate(R.menu.highlight,menus);
        super.onActionModeStarted(mode);
    }

    public String normalizeWord(String word) {
        return word.replace("î", "i").replace("Î", "i").
                replace("Û", "u").replace("û", "u")
                .replace("â", "a").replace("Â", "a")
                .replace("'", "").replace("’", "");
    }

    public void showLugatDefinition(String word, String paragraph, String language) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Word> wordList = null;
                final StringBuilder dialogMessage = new StringBuilder();
                String normalizedWord = null;
                String normalizedParagraph = normalizeWord(paragraph).toLowerCase();

                if(!language.equals("TR") && !language.equals("ENG"))
                    throw new IllegalArgumentException("Language should be TR or ENG");

                normalizedWord = normalizeWord(word.trim()).toLowerCase();

                String[] prefices = new String[30];

                // i.e. "risalelerde" generates prefices like "risaleler", "risalel", "risal", "ris"
                for(int i = normalizedWord.length(), j = 0; i > 3; i = i - 2) {
                    prefices[j] = normalizedWord.substring(0, i);
                    j++;
                }

                List<Word> candidateWords = null;
                boolean bestSuitableWordFound = false;
                //longest prefix is at the first position in the array
                for(String prefix : prefices) {
                    candidateWords = App.getDatabase().wordDao()
                            .getAllPrefixCandidateLugatMatchesOrderedByLength("%" + prefix + "%", language);


                    if(candidateWords.size() > 0)
                        // start from the longest matched word, i.e. "risale" (because array received from db is sorted)
                        for(Word word : candidateWords) {
                            if (normalizedParagraph.contains(word.simpleWord)) { // means that the best suiting word found
                                dialogMessage.append(word.fullWord + ": ");

                                if(word.definition.length() > 100)
                                    dialogMessage.append(word.definition.substring(0,100) + " ...");
                                else
                                    dialogMessage.append(word.definition);

                                bestSuitableWordFound = true;
                                break;
                            }
                        }

                    if(bestSuitableWordFound)
                        break; // no need to search with the rest of the prefices
                }




                runOnUiThread(() -> {
                    AlertDialog dialog = new AlertDialog.Builder(mContext).setMessage(dialogMessage).create();
                    dialog.getWindow().setDimAmount(0);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                    wmlp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
//                    wmlp.x = 100;   //x position
                    wmlp.y = 20;   //y position
                    dialog.show();
                });
            }
        }).start();

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

    class MyJavaScriptInterface {

        private Context ctx;
        private String lastActiveParagraphId;
        private Side currentScroller;


        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
            this.lastActiveParagraphId = "";
            this.currentScroller = Side.Neutral;
        }

        @android.webkit.JavascriptInterface
        public void receiveFihrist(String[] fihristIds, String[] fihristNames) {
            if (fihristIds.length != fihristNames.length)
                return;

            for(int i = 0; i < fihristIds.length; i++) {
                Log.d("JSINT", fihristIds[i] + " = " + fihristNames[i]);
            }
        }

        @android.webkit.JavascriptInterface
        public void scrollFinished(String activeParagraphId, String side, String activeParagraphDistanceToWindow) {
//            if(currentScroller != Side.Neutral)
//                return;

            if (side.equals("SIDE_OTHER")) {
//                currentScroller = Side.OtherLang;
                runOnUiThread(() -> {
//                    if (trLoaded) {
                    if (trLoaded && !onAnimationOtherSide) {
//                        if (trLoaded && !lastActiveParagraphId.equals(activeParagraphId)) {
//                        if (trLoaded && lastScroller != Side.Turkish
//                                        && !lastActiveParagraphId.equals(activeParagraphId)) {
//                            lastActiveParagraphId = activeParagraphId  + "";
                        onAnimationTRSide = true;
                        webViewTR.evaluateJavascript("$('html, body').animate({ scrollTop: $(\"p[name='" +
//                                        activeParagraphId + "']\" ).offset().top - 15}, 500); " +
                                        activeParagraphId + "']\" ).offset().top - " + activeParagraphDistanceToWindow + "}, 500, " + "function() { setTimeout(function() { window.androidInterface.animationFinished(\"SIDE_TR\"); },  500)});" +
//                                        activeParagraphId + "']\" ).offset().top - 15}, 500, " + "function() { window.androidInterface.animationFinished(\"SIDE_TR\") });" +
//                                        activeParagraphId + "']\" ).offset().top - 15}, 500, " + "function() { window.androidInterface.scrollFinished("
//                                            + activeParagraphId + "\", \"SIDE_TR\"); }"  + ");\n" +
//                                        + "getElementByTopOffset(\".Paragraf-1\", $(window).scrollTop()).attr(\"name\"), \"SIDE_TR\"); }" + ");\n" +

                                        "$(\"p\").removeClass(\"highlighted\");\n" +
                                        "var currentPrg = $(\"p[name='" + activeParagraphId + "']\"); \n" +
                                        "currentPrg.addClass(\"highlighted\");" +
                                        "currentPrg.nextUntil(currentPrg.nextAll(\".Paragraf-1\").first(), \".Paragraf-2\").addClass(\"highlighted\");\n"
                                , (val) -> {
                                    //callback function
                                    Log.d("JSINTERFACE", "currScrol" + currentScroller);
                                    Log.d("JSINTERFACE", "lastActivePrg" + lastActiveParagraphId);
                                    Log.d("JSINTERFACE", "activePrg" + activeParagraphId);
                                });
//                            currentScroller = Side.Neutral;

                    } else {
                        Log.d("JSINTERFACE", "CANNOT SEND ANIMATE REQ onAnimationTRSide = true;");
                    }

//                    }
                });
            } else if (side.equals("SIDE_TR")) {
//                currentScroller = Side.Turkish;
                runOnUiThread(() -> {
//                    if (engLoaded) {
                    if (engLoaded && !onAnimationTRSide) {
//                        if (engLoaded && !lastActiveParagraphId.equals(activeParagraphId)) {
//                        if (engLoaded && lastScroller != Side.OtherLang
//                                && !lastActiveParagraphId.equals(activeParagraphId)) {
//                            lastActiveParagraphId = activeParagraphId + "";
                        onAnimationOtherSide = true;
                        webViewEN.evaluateJavascript("$('html, body').animate({ scrollTop: $(\"p[name='" +
//                                        activeParagraphId + "']\" ).offset().top - 15}, 500);" +
                                        activeParagraphId + "']\" ).offset().top - " + activeParagraphDistanceToWindow + "}, 500, " + "function() { setTimeout(function() { window.androidInterface.animationFinished(\"SIDE_OTHER\"); },  500)});" +
//                                            + activeParagraphId + "\", \"SIDE_OTHER\"); }"  + ");\n" +
//                                        + "getElementByTopOffset(\".Paragraf-1\", $(window).scrollTop()).attr(\"name\"), \"SIDE_OTHER\"); }" + ");\n" +

                                        "$(\"p\").removeClass(\"highlighted\");\n" +
                                        "var currentPrg = $(\"p[name='" + activeParagraphId + "']\"); \n" +
                                        "currentPrg.addClass(\"highlighted\");" +
                                        "currentPrg.nextUntil(currentPrg.nextAll(\".Paragraf-1\").first(), \".Paragraf-2\").addClass(\"highlighted\");\n"

                                , (val) -> {
                                    //callback function
                                    Log.d("JSINTERFACE", "currScrol" + currentScroller);
                                    Log.d("JSINTERFACE", "lastActivePrg" + lastActiveParagraphId);
                                    Log.d("JSINTERFACE", "activePrg" + activeParagraphId);

                                });
//                            currentScroller = Side.Neutral;

//                        }

                    } else {
                        Log.d("JSINTERFACE", "CANNOT SEND ANIMATE REQ onAnimationOtherSide = true;");
                    }
                });
            } else {
                return;
            }
        }

        @android.webkit.JavascriptInterface
        public void animationFinished(String side) {
            Log.d("JSINTERFACE", "animationFinished(" + side + ") => onAnimation = false;");
            if (side.equals("SIDE_TR")) {
                onAnimationTRSide = false;
            }
            else if (side.equals("SIDE_OTHER")) {
                onAnimationOtherSide = false;
            }
        }

        @android.webkit.JavascriptInterface
        public void receiveSelectedText(String word, String paragraph, String language) {
            showLugatDefinition(word.replace("\"", ""), paragraph, language);
            Toast.makeText(getApplicationContext(), "SELECTED: " + paragraph, Toast.LENGTH_SHORT).show();
        }
    }
}
