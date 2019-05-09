package com.ukasias.android.sampleweb;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    Button loadButton;
    EditText urlInput;
    private WebView webView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadButton = (Button) findViewById(R.id.loadButton);
        urlInput = (EditText) findViewById(R.id.urlInput);
        webView = (WebView) findViewById(R.id.webView);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        webView.loadUrl("file:///android_asset/www/sample.html");
        webView.addJavascriptInterface(new JavaScriptMethods(), "sample");
        webView.setWebChromeClient(new WebBrowserClient());
        webView.setWebViewClient(new WebViewClient());

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl(urlInput.getText().toString());
            }
        });
    }

    final class JavaScriptMethods {
        JavaScriptMethods() {

        }

        @android.webkit.JavascriptInterface
        public void clickOnFace() {
            handler.post(new Runnable() {
                public void run() {
                    loadButton.setText("클릭 후 열기");
                    webView.loadUrl("javascript:changeFace()");
                }
            });
        }
    }

    final class WebBrowserClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            result.confirm();

            return true;
        }
    }
}
