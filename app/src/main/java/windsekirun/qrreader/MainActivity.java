package windsekirun.qrreader;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import windsekirun.qrreader.async.NaraeAsync;
import windsekirun.qrreader.db.TempResultAdapter;
import windsekirun.qrreader.util.JsonReceiver;

@SuppressWarnings("ALL")
public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    public TempResultAdapter adapter;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 20) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setStatusBarColor(getResources().getColor(R.color.light_blue_800));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("QR Reader Test");
        toolbar.setTitleTextColor(0xffffffff);
        Button generate = (Button) findViewById(R.id.generate);
        Button capture = (Button) findViewById(R.id.read);
        Button license = (Button) findViewById(R.id.license);
        Button send = (Button) findViewById(R.id.serverSend);
        Button github = (Button) findViewById(R.id.github);
        setSupportActionBar(toolbar);
        generate.setOnClickListener(this);
        capture.setOnClickListener(this);
        license.setOnClickListener(this);
        send.setOnClickListener(this);
        github.setOnClickListener(this);
        adapter = new TempResultAdapter(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.generate) {
            startActivity(new Intent(MainActivity.this, GenerateActivity.class));
            finish();
        } else if (id == R.id.read) {
            startActivity(new Intent(MainActivity.this, CaptureActivity.class));
            finish();
        } else if (id == R.id.license) {
            showLicenseDialog();
        } else if (id == R.id.serverSend) {
            showSendAlert();
        } else if (id == R.id.github) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/windsekirun/qrreader"));
            startActivity(i);
        }
    }

    public void showLicenseDialog() {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.dialog, null, false);
        WebView wv = (WebView) v.findViewById(R.id.wv);
        wv.loadUrl("file:///android_asset/license.html");
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setView(v);
        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ab.show();
    }

    public void showSendAlert() {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("서버로 데이터를 정말 보내시겠습니까?");
        ab.setPositiveButton("보내기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new NaraeAsync(false, new SendData(), 8, "SEND_DATA").execute();
            }
        });
        ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ab.show();
    }

    public class SendData implements Runnable {
        ArrayList<String> qrlist;
        ArrayList<String> receivedList = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap<>();
        ProgressDialog mpd;

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mpd = new ProgressDialog(MainActivity.this);
                    mpd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mpd.setMessage("서버와 데이터 통신중입니다.");
                    mpd.setIndeterminate(false);
                    mpd.setCancelable(false);
                    mpd.show();
                }
            });

            // get SQLite list
            adapter.open();
            qrlist = adapter.getText();
            adapter.close();

            // Receive Default Data from server
            JsonReceiver sh = new JsonReceiver();
            String jsonStr = sh.makeJsonCall("http://windsekirun.cafe24.com/php/get_formdata.php", JsonReceiver.GET);
            if (jsonStr != null) {
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(jsonStr);
                    JSONArray data = jsonObj.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        String receivedData = c.getString("studentNum");
                        receivedList.add(receivedData);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // put datas into Hashmap
            for (int i = 0; i < receivedList.size(); i++) map.put(receivedList.get(i), 0);

            // contract map <-> qrlist
            for (int i = 0; i < qrlist.size(); i++) map.put(qrlist.get(i), 1);

            // Create Table follows Date
            try {
                sendDataforCreateTable("http://windsekirun.cafe24.com/php/create_table.php", "checked" + getTime());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // put hashmap to server
            for (int i = 0; i < map.size(); i++) {
                try {
                    String tableName = "checked" + getTime();
                    String studentNum = receivedList.get(i);
                    String check = String.valueOf(map.get(receivedList.get(i)));
                    sendDataforInsertData("http://windsekirun.cafe24.com/php/put_value.php", tableName, studentNum, check);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // SQLite list clear
            adapter.open();
            adapter.clear();
            adapter.close();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mpd != null && mpd.isShowing()) mpd.dismiss();
                    Toast.makeText(MainActivity.this, "서버로 데이터를 보냈습니다!", Toast.LENGTH_LONG).show();

                }
            });
        }
    }

    public String sendDataforCreateTable(String url, String date) throws ClientProtocolException, IOException {
        HttpPost request = new HttpPost(url);
        Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
        nameValue.add(new BasicNameValuePair("name", date));
        request.setEntity(makeEntity(nameValue));
        HttpClient client = new DefaultHttpClient();
        ResponseHandler<String> reshandler = new BasicResponseHandler();
        String result = client.execute(request, reshandler);
        return result;
    }

    public String sendDataforInsertData(String url, String tableName, String studentNum, String checked) throws ClientProtocolException, IOException {
        HttpPost request = new HttpPost(url);
        Vector<NameValuePair> nameValue = new Vector<NameValuePair>();
        nameValue.add(new BasicNameValuePair("tableName", tableName));
        nameValue.add(new BasicNameValuePair("studentNum", studentNum));
        nameValue.add(new BasicNameValuePair("checked", checked));
        request.setEntity(makeEntity(nameValue));
        HttpClient client = new DefaultHttpClient();
        ResponseHandler<String> reshandler = new BasicResponseHandler();
        String result = client.execute(request, reshandler);
        return result;
    }

    public HttpEntity makeEntity(Vector<NameValuePair> nameValue) {
        HttpEntity result = null;
        try {
            result = new UrlEncodedFormEntity(nameValue);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getTime() {
        return DateFormat.format("yyyyMMdd", Calendar.getInstance().getTime()).toString();
    }
}
