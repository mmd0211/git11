package ir.sigmarobotic.classplanneronline;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String PREFS = "sigma_class_prefs";
    private static final String KEY_URL = "server_url";
    private SharedPreferences prefs;
    private WebView webView;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String url = prefs.getString(KEY_URL, "");
        if (url == null || url.trim().isEmpty()) showSetup();
        else showWeb(url);
    }

    private TextView text(String value, int sp, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(sp);
        t.setTextColor(Color.rgb(39,50,74));
        if (bold) t.setTypeface(null, android.graphics.Typeface.BOLD);
        t.setGravity(Gravity.RIGHT);
        return t;
    }

    private void showSetup() {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setPadding(42, 60, 42, 42);
        screen.setGravity(Gravity.CENTER_VERTICAL);
        screen.setBackgroundColor(Color.rgb(245,247,251));

        TextView logo = text("Σ", 44, true);
        logo.setGravity(Gravity.CENTER);
        screen.addView(logo, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView title = text("Sigma Class Planner", 24, true);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tp.setMargins(0, 18, 0, 10);
        screen.addView(title, tp);

        TextView desc = text("لینک Web App مربوط به Sigma Class Planner را یک‌بار وارد کن. بعد از آن همه کاربران به اطلاعات مشترک سرور وصل می‌شوند.", 14, false);
        desc.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dp.setMargins(0, 0, 0, 24);
        screen.addView(desc, dp);

        EditText input = new EditText(this);
        input.setHint("https://script.google.com/macros/s/.../exec");
        input.setTextDirection(View.TEXT_DIRECTION_LTR);
        input.setSingleLine(true);
        input.setPadding(20, 16, 20, 16);
        screen.addView(input, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button save = new Button(this);
        save.setText("ذخیره و ورود");
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bp.setMargins(0, 18, 0, 0);
        screen.addView(save, bp);

        save.setOnClickListener(v -> {
            String url = input.getText().toString().trim();
            if (!url.startsWith("https://script.google.com/") || !url.contains("/exec")) {
                Toast.makeText(this, "لینک Web App معتبر نیست.", Toast.LENGTH_LONG).show();
                return;
            }
            prefs.edit().putString(KEY_URL, url).apply();
            showWeb(url);
        });

        setContentView(screen);
    }

    private void showWeb(String url) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(12, 8, 12, 8);
        bar.setBackgroundColor(Color.rgb(245,247,251));

        Button settingsButton = new Button(this);
        settingsButton.setText("⚙");
        settingsButton.setTextSize(18);
        settingsButton.setMinWidth(0);
        settingsButton.setMinimumWidth(0);
        settingsButton.setPadding(8,0,8,0);

        TextView title = text("Sigma Class Planner", 15, true);
        title.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        bar.addView(settingsButton, new LinearLayout.LayoutParams(64, ViewGroup.LayoutParams.WRAP_CONTENT));
        bar.addView(title, titleParams);

        webView = new WebView(this);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setDefaultTextEncodingName("UTF-8");
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(false);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String host = uri.getHost() == null ? "" : uri.getHost();
                if (host.contains("script.google.com") || host.contains("googleusercontent.com")) return false;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                } catch (Exception ignored) {
                    return false;
                }
            }
        });

        settingsButton.setOnClickListener(v -> {
            prefs.edit().remove(KEY_URL).apply();
            if (webView != null) webView.destroy();
            showSetup();
        });

        root.addView(bar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        root.addView(webView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
