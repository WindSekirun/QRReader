package windsekirun.qrreader;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import windsekirun.qrreader.async.NaraeAsync;
import windsekirun.qrreader.db.TempResultAdapter;
import windsekirun.qrreader.util.JsonReceiver;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    TempResultAdapter adapter;

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
        setSupportActionBar(toolbar);
        generate.setOnClickListener(this);
        capture.setOnClickListener(this);
        license.setOnClickListener(this);
        send.setOnClickListener(this);
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
        } else {
            showSendAlert();
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
    }

    public class SendData implements Runnable {
        ArrayList<String> qrlist;
        ArrayList<String> receivedList = new ArrayList<>();
        HashMap<String, Integer> map = new HashMap<>();

        @Override
        public void run() {
            // get SQLite list
            try {
                adapter.open();
                qrlist = adapter.getText();
                adapter.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Receive Server Form data.
            // TODO: implement Receive PHP
            JsonReceiver sh = new JsonReceiver();
            String jsonStr = sh.makeJsonCall("", JsonReceiver.GET);
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
            makeDateTable(getTime());

            // put hashmap to server
            // TODO: implement put hashmap to server

            // SQLite list clear
            try {
                adapter.open();
                adapter.clear();
                adapter.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "서버로 데이터를 보냈습니다!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public HttpPost makeDateTable(String dateTableName) {
        // TODO: implement Create Table PHP
        HttpPost request = new HttpPost("");
        ArrayList<NameValuePair> nameValue = new ArrayList<>();
        nameValue.add(new BasicNameValuePair("name", dateTableName));
        request.setEntity(makeEntity(nameValue));
        return request;
    }

    public HttpEntity makeEntity(ArrayList<NameValuePair> $nameValue){
        HttpEntity result = null;
        try{
            result = new UrlEncodedFormEntity($nameValue,"UTF-8");
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return result;
    }

    protected String getTime() {
        return DateFormat.format("yyyyMMdd", Calendar.getInstance().getTime()).toString();
    }

}
