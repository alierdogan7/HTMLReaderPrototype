
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

            webViewEN.addJavascriptInterface(new MyJavaScriptInterface(this), "androidInterface" );
            webViewTR.addJavascriptInterface(new MyJavaScriptInterface(this), "androidInterface" );

            webViewEN.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    if(engLoaded)
                        return;

                    engLoaded = true;

                }

            });

            webViewTR.setWebViewClient(new WebViewClient() {

                @Override
                public void onPageFinished(WebView view, String url) {
                if(trLoaded)
                    return;

                trLoaded = true;

                }

            });


            webViewEN.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
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

    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @android.webkit.JavascriptInterface
        public void scrollFinished(String activeParagraphId) {
            runOnUiThread(() -> {
                webViewTR.evaluateJavascript("$('html, body').animate({ scrollTop: $(\"p[name='" +
                                activeParagraphId + "']\" ).offset().top - 15}, 1000);\n" +
                                "$(\"p\").removeClass(\"highlighted\");\n" +
                                "var currentPrg = $(\"p[name='" + activeParagraphId + "']\"); \n" +
                                "currentPrg.addClass(\"highlighted\");" +
                                "currentPrg.nextUntil(currentPrg.nextAll(\".Paragraf-1\").first(), \".Paragraf-2\").addClass(\"highlighted\");\n",
                        null);
            });
        }

    }
}
