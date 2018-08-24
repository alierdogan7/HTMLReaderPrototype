package com.example.android.pdfrendererbasic;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    public static final String FRAGMENT_PDF_RENDERER_BASIC = "pdf_renderer_basic";
    WebView webViewEN;
    WebView webViewTR;
    Context mContext;
    Button zoomPlus;
    Button zoomMinus;
    boolean fihristMod = false;
    int currPage;
    int currFihrist;
    boolean engLoaded = false;
    boolean trLoaded = false;
    boolean onAnimationTRSide = false;
    boolean onAnimationOtherSide = false;
    int currentSection = 0;
    int currentZoom = 100;
    String currentParagraphId = null;
    String currentPageId = null;
    final int lastSection = 2;
    int sectionPageOffset = 10;
    final String rootUrl = "file:///android_asset/hasir/";


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
            prepareUI();
            initWebViews();
            loadWebViews(currentSection, null);


        } else {
            //do something for restoring webview states
            mContext = this;
//            prepareButtons();
            currentSection = savedInstanceState.getInt("currentSection", 0);
            currentParagraphId = savedInstanceState.getString("currentParagraphId", null);
            currentPageId = savedInstanceState.getString("currentPageId ", null);
            currentZoom = savedInstanceState.getInt("currentZoom ", currentZoom);
            prepareUI();
            initWebViews();
            loadWebViews(currentSection, currentParagraphId);


        }
    }

    public void prepareUI() {
        // Hide the status bar.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Put the action bar there.
        if (getSupportActionBar() != null) {
            if(currentPageId != null)
                getSupportActionBar().setTitle("Page " +  getNumberFromAnchor(currentPageId));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("currentSection", currentSection);
        outState.putString("currentParagraphId", currentParagraphId);
        outState.putString("currentPageId ", currentPageId );
        outState.putInt("currentZoom ", currentZoom);
        super.onSaveInstanceState(outState);
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        currentSection = savedInstanceState.getInt("currentSection", 0);
//     }

    public void initWebViews() {
        webViewEN = (WebView) findViewById(R.id.ingWV);
        webViewTR = (WebView) findViewById(R.id.trVW);
        SwipyRefreshLayout swipyEN = (SwipyRefreshLayout) findViewById(R.id.swipyOther);
        SwipyRefreshLayout swipyTR = (SwipyRefreshLayout) findViewById(R.id.swipyTR);
        swipyEN.setDistanceToTriggerSync(30);
        swipyTR.setDistanceToTriggerSync(30);

        SwipyRefreshLayout.OnRefreshListener refreshListener = new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                swipyEN.setRefreshing(false);
                swipyTR.setRefreshing(false);

                if (direction == SwipyRefreshLayoutDirection.TOP ) {
                    loadWebViews(currentSection - 1, null);
                } else {
                    loadWebViews(currentSection + 1, null);
                }

            }
        };

        swipyEN.setOnRefreshListener(refreshListener);
        swipyTR.setOnRefreshListener(refreshListener);

        if (Build.VERSION.SDK_INT >= 19) {
            webViewEN.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webViewTR.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webViewEN.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            webViewTR.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webViewEN.getSettings().setBuiltInZoomControls(true);
        webViewEN.getSettings().setDisplayZoomControls(true);
        webViewEN.getSettings().setJavaScriptEnabled(true);
        webViewTR.getSettings().setJavaScriptEnabled(true);
        webViewEN.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewTR.getSettings().setAllowFileAccessFromFileURLs(true);
        webViewTR.getSettings().setBuiltInZoomControls(true);
        webViewTR.getSettings().setDisplayZoomControls(true);
        webViewEN.getSettings().setDisplayZoomControls(false);
        webViewTR.getSettings().setDisplayZoomControls(false);
        webViewEN.getSettings().setSupportZoom(false);
        webViewTR.getSettings().setSupportZoom(false);
        webViewEN.getSettings().setTextZoom(currentZoom);
        webViewTR.getSettings().setTextZoom(currentZoom);


        webViewEN.addJavascriptInterface(new MyJavaScriptInterface(this), "androidInterface");
        webViewTR.addJavascriptInterface(new MyJavaScriptInterface(this), "androidInterface");


    }

    public void loadWebViews(int sectionNo, String goToAnchor) {

        if (sectionNo < 0 || sectionNo > lastSection)
            return;

        boolean willScrollBottom;

        // if this func. triggered by prev. button, scroll to bottom after loading
        if(sectionNo == currentSection - 1)
            willScrollBottom = true;
        else
            willScrollBottom = false;

        engLoaded = false;
        trLoaded = false;
        currentSection = sectionNo;

        webViewEN.loadUrl(rootUrl + "hasr-ENG-section-" + sectionNo + ".html");
        webViewTR.loadUrl(rootUrl + "hasr-TR-section-" + sectionNo + ".html");
//        webViewEN.loadUrl("file:///android_asset/hasir/hasr-ENG-tashihli.html");
//        webViewTR.loadUrl("file:///android_asset/hasir/hasr-TR-tashihli.html");
        webViewEN.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (engLoaded)
                    return;

                engLoaded = true;
                onAnimationOtherSide = false;
                if(goToAnchor != null) {
                    scrollToAnchor(webViewEN, goToAnchor);
                    scrollToAnchor(webViewTR, goToAnchor);
                }
                else if (willScrollBottom)
                    webViewEN.scrollTo(0, webViewEN.getContentHeight());

            }

            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
                Log.d("SCALE", "old " + oldScale);
                Log.d("SCALE", "new " + newScale);
            }
        });

        webViewTR.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                if (trLoaded)
                    return;

                trLoaded = true;
                onAnimationTRSide = false;
                if(goToAnchor != null) {
                    scrollToAnchor(webViewEN, goToAnchor);
                    scrollToAnchor(webViewTR, goToAnchor);
                }
                else if (willScrollBottom)
                    webViewTR.scrollTo(0, webViewTR.getContentHeight());
            }

        });

    }

    public void scrollToAnchor(WebView wv, String anchorId) {
        wv.evaluateJavascript("$('html, body').animate({ scrollTop: $(\"[name='" + anchorId + "']\" ).offset().top - 15}, 50);", null);
    }

    public void prepareButtons() {
//        nextButton = (Button) findViewById(R.id.next);
//        prevButton = (Button) findViewById(R.id.prev);
//        zoomPlus = (Button) findViewById(R.id.zoomPlus);
//        zoomMinus = (Button) findViewById(R.id.zoomMinus);
//        RadioButton fihristRadio = (RadioButton) findViewById(R.id.fihristRadio);
//        RadioGroup group = (RadioGroup) findViewById(R.id.radioGroup);
//        group.setOnCheckedChangeListener((group1, checkedId) -> {
//            if (checkedId == R.id.fihristRadio)
//                fihristMod = true;
//            else
//                fihristMod = false;
//        });
//
//        pageSizesWebViewEN = new ArrayList<Integer>();
//        pageSizesWebViewTR = new ArrayList<Integer>();
//        pageTopsWebViewEN = new ArrayList<Integer>();
//        pageTopsWebViewTR = new ArrayList<Integer>();

//        currPage = 0;
//        currFihrist = 0;
//        List<Bookmark> fihrist = getFihrist("hasir_eng");
//        nextButton.setOnClickListener((view) -> {
//            if (fihristMod) {
//                currFihrist = Math.min(++currFihrist, fihrist.size());
//                goToFihrist(fihrist.get(currFihrist), webViewEN);
//                goToFihrist(fihrist.get(currFihrist), webViewTR);
////                webViewEN.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
////                webViewTR.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
//            } else {
//                currPage++;
//                webViewEN.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
//                webViewTR.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
//            }
//        });
//        prevButton.setOnClickListener((view) -> {
//            if (fihristMod) {
//                currFihrist = Math.max(--currFihrist, 0);
//                goToFihrist(fihrist.get(currFihrist), webViewEN);
//                goToFihrist(fihrist.get(currFihrist), webViewTR);
////                webViewEN.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
////                webViewTR.evaluateJavascript("location.hash = '#fihrist-" + new String(currFihrist + "") + "';", null);
//            } else {
//                currPage--;
//                webViewEN.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
//                webViewTR.evaluateJavascript("location.hash = '#page-" + new String(currPage + "") + "';", null);
//            }
//        });
//
//        zoomPlus.setOnClickListener((view) -> {
//            int newZoom = webViewEN.getSettings().getTextZoom() + 20;
//            webViewEN.getSettings().setTextZoom(newZoom);
//            webViewTR.getSettings().setTextZoom(newZoom);
//        });
//
//
//        zoomMinus.setOnClickListener((view) -> {
//            int newZoom = webViewEN.getSettings().getTextZoom() - 20;
//            webViewEN.getSettings().setTextZoom(newZoom);
//            webViewTR.getSettings().setTextZoom(newZoom);
//
//        });

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

    public String normalizeWord(String word, String language) {
        word = word.replace("î", "i").replace("Î", "i").
                replace("Û", "u").replace("û", "u")
                .replace("â", "a").replace("Â", "a")
                .replace("'", "").replace("’", "");

        if (language.equals("ENG"))
            return word.toLowerCase(Locale.ENGLISH);

        return word.toLowerCase();

    }

    public int getNumberFromAnchor(String anchor) {
        int hyphen = anchor.lastIndexOf('-');
        return Integer.parseInt(anchor.substring(hyphen + 1));
    }

    public void showLugatDefinition(String word, String paragraph, String language) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Word> wordList = null;
                final StringBuilder dialogMessage = new StringBuilder();
                String normalizedWord = null;
                String normalizedParagraph = normalizeWord(paragraph, language);

                if (!language.equals("TR") && !language.equals("ENG"))
                    throw new IllegalArgumentException("Language should be TR or ENG");

                normalizedWord = normalizeWord(word.trim(), language);

                ArrayList<String> prefices = new ArrayList<String>();

                // i.e. "risalelerde" generates prefices like "risaleler", "risalel", "risal", "ris"
                for (int i = normalizedWord.length(); i >= 3; i = i - 2) {
                    prefices.add("%" + normalizedWord.substring(0, i) + "%");
                }


                List<Word> candidateWords = null;
                boolean bestSuitableWordFound = false;
                //longest prefix is at the first position in the array

                switch (prefices.size()) {
                    case 6:
                        candidateWords = App.getDatabase().wordDao()
                                .getAllPrefixCandidateLugatMatchesOrderedByLength6Prefix(language,
                                        prefices.get(0), prefices.get(1), prefices.get(2),
                                        prefices.get(3), prefices.get(4), prefices.get(5));
                        break;
                    case 5:
                        candidateWords = App.getDatabase().wordDao()
                                .getAllPrefixCandidateLugatMatchesOrderedByLength5Prefix(language,
                                        prefices.get(0), prefices.get(1), prefices.get(2),
                                        prefices.get(3), prefices.get(4));
                        break;
                    case 4:
                        candidateWords = App.getDatabase().wordDao()
                                .getAllPrefixCandidateLugatMatchesOrderedByLength4Prefix(language,
                                        prefices.get(0), prefices.get(1), prefices.get(2),
                                        prefices.get(3));
                        break;
                    case 3:
                        candidateWords = App.getDatabase().wordDao()
                                .getAllPrefixCandidateLugatMatchesOrderedByLength3Prefix(language,
                                        prefices.get(0), prefices.get(1), prefices.get(2));
                        break;
                    case 2:
                        candidateWords = App.getDatabase().wordDao()
                                .getAllPrefixCandidateLugatMatchesOrderedByLength2Prefix(language,
                                        prefices.get(0), prefices.get(1));
                        break;
                    case 1:
                        candidateWords = App.getDatabase().wordDao()
                                .getAllPrefixCandidateLugatMatchesOrderedByLength1Prefix(language,
                                        prefices.get(0));
                        break;
                    case 0:
                    default:
                        candidateWords = null;
                        break;
                }

                if (candidateWords != null && candidateWords.size() > 0) {

                    // start from the longest matched word, i.e. "risale" (because array received from db is sorted)
                    for (Word word : candidateWords) {
                        if (normalizedParagraph.contains(word.simpleWord)) { // means that the best suiting word found
                            dialogMessage.append(word.fullWord + ": ");

                            if (word.definition.length() > 100)
                                dialogMessage.append(word.definition.substring(0, 100) + " ...");
                            else
                                dialogMessage.append(word.definition);

                            bestSuitableWordFound = true;
                            break;
                        }
                    }
                }

                if (bestSuitableWordFound) {
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
            }
        }).start();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_info:
                DialogFragment fihristFragment = new FihristDialogFragment();
                Bundle args2 = new Bundle();

                fihristFragment.setArguments(args2);
                fihristFragment.show(getSupportFragmentManager(), "fihrist");

//                new AlertDialog.Builder(this)
//                        .setMessage(R.string.intro_message)
//                        .setPositiveButton(android.R.string.ok, null)
//                        .show();
                return true;
            case R.id.zoomPlus:
                int newZoom = webViewEN.getSettings().getTextZoom() + 20;
                webViewEN.getSettings().setTextZoom(newZoom);
                webViewTR.getSettings().setTextZoom(newZoom);
                currentZoom = newZoom;
                return true;
            case R.id.zoomMinus:
                newZoom = webViewEN.getSettings().getTextZoom() - 20;
                webViewEN.getSettings().setTextZoom(newZoom);
                webViewTR.getSettings().setTextZoom(newZoom);
                currentZoom = newZoom;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static List<Bookmark> getFihrist(String bookId) {
        List<Bookmark> fihrist = new ArrayList<Bookmark>();
        BufferedReader br;

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(App.getContext().getAssets().open("hasir/hasir_fihrist.txt"), "UTF-8"));
//            line = br.readLine();
//            if(!line.trim().equals(bookId)) // file first line should match bookId
//                throw new FileNotFoundException();
            bookId = "hasir_eng";

            while ((line = br.readLine()) != null) {
                String[] strings = TextUtils.split(line, "::");
                if (strings.length < 3 || strings[0].equals("") || strings[1].equals(""))
                    continue;
                else {
                    int page = Integer.parseInt(strings[0].trim().substring(1));
                    String label = strings[1].trim();
                    String fihristName = strings[2].trim();
                    fihrist.add(new Bookmark(label, bookId, null, fihristName, page));
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fihrist;
    }

    public static void goToFihrist(Bookmark bookmark, WebView vw) {
        String section = ((bookmark.pageNumber - 1) / 10) + ""; //calculate section index (offset is 10)

//        vw.evaluateJavascript("changeToSection(" + section + ", function() {"
//                                + "location.hash = '#" + bookmark.bookmarkId + "';" + "});", (val) -> {
//                            Toast.makeText(this, bookmark.name, Toast.LENGTH_SHORT).show();
//        vw.evaluateJavascript("changeToSection(" + section + ");", (val) -> {
//                            vw.evaluateJavascript("location.hash = '#" + bookmark.bookmarkId + "';", null);
//                            Toast.makeText(this, bookmark.name, Toast.LENGTH_SHORT).show();
//        vw.evaluateJavascript("changeToSection(" + section + ", '" + bookmark.bookmarkId + "');", null);// (val) -> {
//                            Toast.makeText(, bookmark.name, Toast.LENGTH_SHORT).show();
//        });


    }

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @android.webkit.JavascriptInterface
        public void receiveFihrist(String[] fihristIds, String[] fihristNames, String lang) {
            if (fihristIds.length != fihristNames.length)
                return;

            for (int i = 0; i < fihristIds.length; i++) {
                Log.d("JSINT", fihristIds[i] + " = " + fihristNames[i]);
            }
        }

        @android.webkit.JavascriptInterface
        public void scrollFinished(String activeParagraphId, String side,
                                   String activeParagraphDistanceToWindow,
                                   String activePageId) {
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
                        currentParagraphId = activeParagraphId + "";
                        currentPageId = activePageId + "";
                        if (getSupportActionBar() != null) {
                            if(currentPageId != null)
                                getSupportActionBar().setTitle("Page " +  getNumberFromAnchor(currentPageId));
                        }

                        Log.d("JSINT", "currPrgId: " + currentParagraphId + " / currPage: " + currentPageId);


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
                        currentParagraphId = activeParagraphId + "";
                        currentPageId = activePageId + "";
                        if (getSupportActionBar() != null) {
                            if(currentPageId != null)
                                getSupportActionBar().setTitle("Page " +  getNumberFromAnchor(currentPageId));
                        }


                        Log.d("JSINT", "currPrgId: " + currentParagraphId + " / currPage: " + currentPageId);
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
            } else if (side.equals("SIDE_OTHER")) {
                onAnimationOtherSide = false;
            }
        }

//        @android.webkit.JavascriptInterface
//        public void doSectionChange(int newSectionNo) {
//            runOnUiThread(() -> {
//                loadWebViews(newSectionNo, null);
//            });
//        }

        @android.webkit.JavascriptInterface
        public void receiveSelectedText(String word, String paragraph, String language) {
            showLugatDefinition(word.replace("\"", ""), paragraph, language);
//            runOnUiThread(() -> {
//                Toast.makeText(getApplicationContext(), "SELECTED: " + paragraph, Toast.LENGTH_SHORT).show();
//            });
        }

    }
}

   /* Calculates the similarity (a number within 0 and 1) between two strings.

    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ //}
    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
//        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

//    }
/*
    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    } */

