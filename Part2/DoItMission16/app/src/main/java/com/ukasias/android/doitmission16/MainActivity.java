package com.ukasias.android.doitmission16;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
    private String address = "http://rss.hankyung.com/new/news_economy.xml";

    GridView gridView;
    Button requestButton;
    WebView webView;

    RssItemAdapter adapter;
    Handler handler;

    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView);
        requestButton = (Button) findViewById(R.id.requestButton);
        webView = (WebView) findViewById(R.id.webView);

        adapter = new RssItemAdapter(this, webView);
        gridView.setAdapter(adapter);
        gridView.setNumColumns(2);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RssFeedThread thread = new RssFeedThread();
                thread.start();

                Toast.makeText(getApplicationContext(),
                        "Database: " + databaseList().length,
                        Toast.LENGTH_SHORT).show();
            }
        });

        webView.setWebViewClient(new WebViewClient());

        handler = new Handler();
        drawable = getResources().getDrawable(R.drawable.rss_icon);
    }

    class RssFeedThread extends Thread {
        @Override
        public void run() {
            try {
                URL url = new URL(address);
                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(10000);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("GET");

                final int resCode = conn.getResponseCode();

                /*
                postResCode(resCode);

                String webData = getWebData(conn.getInputStream());
                postString(webData);
                */
                Document doc = getDocument(conn.getInputStream());
                Element document = doc.getDocumentElement();
                postDessectDocument(document);

                /* dissect 문서 */
                conn.disconnect();
            }
            catch(MalformedURLException e) {
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void postResCode(final int code) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "resCode: " + code + ", " +
                                "HTTP_OK: " + HttpURLConnection.HTTP_OK,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void postString(final String str) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        "String: " + str,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void postWebView(final String contents) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                webView.loadData(contents, "text/html", "utf-8");
            }
        });
    }

    public String getWebData(InputStream in) {
        StringBuilder sBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        try {
            do {
                line = reader.readLine();
                sBuilder.append(line + "\n");
            }
            while (line != null);

            reader.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        return sBuilder.toString();
    }

    public Document getDocument(InputStream in) {
        try {
            DocumentBuilderFactory builderFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document doc = builder.parse(in);

            return doc;
        }
        catch(ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch(SAXException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void postDessectDocument(final Element document) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                dissectDocument(document);
            }
        });
    }
    public void dissectDocument(Element document) {
        NodeList nodeList = document.getElementsByTagName("item");
        int count = nodeList.getLength();

        for (int i = 0; i < count; i++) {
            Element node = (Element) nodeList.item(i);
            Element title = (Element) node.getElementsByTagName("title").item(0);
            Element description =
                    (Element) node.getElementsByTagName("description").item(0);
            Element link = (Element) node.getElementsByTagName("link").item(0);
            RssItem item = new RssItem(drawable,
                    title.getFirstChild().getNodeValue(),
                    description.getFirstChild().getNodeValue(),
                    link.getFirstChild().getNodeValue());
            adapter.addItem(item);
        }
        adapter.notifyDataSetChanged();
    }
}
