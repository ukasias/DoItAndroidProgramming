package com.ukasias.android.samplerssfeeder;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
    private String TAG = "SampleRSSFeeder";
    //private static String rssUrl = "http://api.sbs.co.kr/xml/new/rss.jsp?pmDiv=entertainment";
    //private static String rssUrl = "http://rss.cnn.com/rss/edition_world.rss";
    private static String rssUrl = "http://rss.hankyung.com/new/news_economy.xml";
    EditText address;
    Button request;
    ListView listView;

    ArrayList<RSSNewsItem> newsItemList;
    RSSNewsItemAdapter adapter;

    ProgressDialog progressDialog;

    Handler handler;
    UpdateRSSRunnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        address = (EditText) findViewById(R.id.input01);
        request = (Button) findViewById(R.id.requestButton);
        listView = (ListView) findViewById(R.id.listView);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRSS(address.getText().toString());
            }
        });

        newsItemList = new ArrayList<RSSNewsItem>();

        adapter = new RSSNewsItemAdapter(this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                RSSNewsItem item = newsItemList.get(position);
                Toast.makeText(getApplicationContext(),
                        "Selected: " + item.getTitle(),
                        Toast.LENGTH_LONG).show();
            }
        });

        handler = new Handler();
        runnable = new UpdateRSSRunnable();

        address.setText(rssUrl);
    }

    private void requestRSS(String rssUrl) {
        try {
            progressDialog = ProgressDialog.show(this, "RSS Refresh",
                    "RSS 정보 업데이트 중 ...", true, true);

            RefreshThread thread = new RefreshThread(rssUrl);
            thread.start();
        }
        catch (Exception e) {
            Log.e("TAG", "requestRSS() error", e);
        }
    }

    private InputStream getInputStreamUsingHttp(URL url) {
        try {
            int resCode;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            resCode = conn.getResponseCode();

            Log.d("getInputStreamsUsing", "resCode = " + resCode);

            if (resCode == HttpURLConnection.HTTP_OK) {
                return conn.getInputStream();
            }
            else {
                return null;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private int processDocument(Document document) {
        newsItemList.clear();

        Element docEle = document.getDocumentElement();

        NodeList nodeList = docEle.getElementsByTagName("item");
        int count = 0;
        int length = (nodeList != null)? nodeList.getLength() : 0;

        for (int i = 0; i < length; i++) {
            RSSNewsItem item = dissectNode(nodeList, i);
            if (item != null) {
                newsItemList.add(item);
                count++;
            }
        }

        return count;
    }

    private RSSNewsItem dissectNode(NodeList list, int index) {
        RSSNewsItem item = null;
        Resources res = getResources();
        Drawable icon = res.getDrawable(R.drawable.rss_icon);

        Element entry = (Element) list.item(index);

        Element title = (Element) entry.getElementsByTagName("title").item(0);
        NodeList pubDateNode = entry.getElementsByTagName("pubDate");
        if (pubDateNode == null) {
            pubDateNode = entry.getElementsByTagName("dc:date");
        }
        Element description = (Element) entry.getElementsByTagName("description").item(0);
        Element link = (Element) entry.getElementsByTagName("link").item(0);

        String titleValue = null;
        if (title != null) {
            Node firstChild = title.getFirstChild();
            if (firstChild != null) {
                titleValue = firstChild.getNodeValue();
            }
        }

        String dateValue = null;
        Element date = (Element) pubDateNode.item(0);
        if (date != null) {
            Node firstChild = date.getFirstChild();
            if (firstChild != null) {
                dateValue = firstChild.getNodeValue();
            }
        }

        String descriptionValue = null;
        if (description != null) {
            Node firstChild = description.getFirstChild();
            if (firstChild != null) {
                descriptionValue = firstChild.getNodeValue();
            }
        }

        String linkValue = null;
        if (link != null) {
            Node firstChild = link.getFirstChild();
            if (firstChild != null) {
                linkValue = firstChild.getNodeValue();
            }
        }

        item = new RSSNewsItem(icon, titleValue, dateValue, descriptionValue, linkValue);

        return item;
    }

    class UpdateRSSRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Log.d("UpdateRSSRunnable", "size: " + newsItemList.size());
                int size = newsItemList.size();

                Log.d("UpdateRSSRunnable", "size: " + size);
                for (int i = 0; i < size; i++) {
                    RSSNewsItem item = newsItemList.get(i);
                    adapter.addItem(item);
                }

                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    class RefreshThread extends Thread {
        private String TAG = "RefreshThread";

        String urlStr;

        public RefreshThread(String urlStr) {
            this.urlStr = urlStr;
        }

        @Override
        public void run() {
            try {
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();

                URL url = new URL(urlStr);

                InputStream instream = getInputStreamUsingHttp(url);

                int countItems = 0;
                if (instream != null) {
                    Log.d(TAG, "Before PARSE - " + countItems);
                    Document document = builder.parse(instream);
                    Log.d(TAG, "After PARSE - " + countItems);
                    countItems = processDocument(document);
                }

                Log.d(TAG, "new item processed - " + countItems);

                handler.post(runnable);
            }
            catch(ParserConfigurationException e) {
                e.printStackTrace();
            }
            catch(MalformedURLException e) {
                e.printStackTrace();
            }
            catch(SAXException e) {
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
