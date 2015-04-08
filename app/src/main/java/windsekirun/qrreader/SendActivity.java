package windsekirun.qrreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
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
import windsekirun.qrreader.util.SendListItem;

/**
 * QRReader
 * Class: SendActivity
 * Created by WindSekirun on 15. 4. 8..
 */
@SuppressWarnings("ALL")
public class SendActivity extends ActionBarActivity implements View.OnClickListener {
    public TempResultAdapter adapter;
    public ListView list;
    public Button send;
    public SendListAdapter listAdapter;
    public final ArrayList<SendListItem> sli = new ArrayList<>();
    public final ArrayList<String> receivedList = new ArrayList<>();
    public final ArrayList<String> receivedNameList = new ArrayList<>();
    public final HashMap<String, Integer> map = new HashMap<>();
    public boolean isZeroContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("출석 현황 보기");
        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter = new TempResultAdapter(this);
        send = (Button) findViewById(R.id.send);
        list = (ListView) findViewById(R.id.list);
        send.setOnClickListener(this);
        listAdapter = new SendListAdapter(this, sli);
        new NaraeAsync(false, new LoadListData(), 8, "SET_LIST_DATA").execute();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.send) {
            if (isZeroContent)
                Toast.makeText(this, "보낼 데이터가 존재하지 않습니다", Toast.LENGTH_SHORT).show();
            else
                new NaraeAsync(false, new SendData(), 8, "SEND_DATA_TO_SERVER").execute();
        }
    }

    public class LoadListData implements Runnable {
        ArrayList<String> qrdata = new ArrayList<>();
        final ArrayList<String> putName = new ArrayList<>();
        final ArrayList<String> putNumber = new ArrayList<>();
        final ArrayList<Integer> putCheck = new ArrayList<>();

        @Override
        public void run() {
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
                        String receivedName = c.getString("studentName");
                        receivedList.add(receivedData);
                        receivedNameList.add(receivedName);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            adapter.open();
            qrdata = adapter.getText();
            adapter.close();
            // put datas into Hashmap
            for (int i = 0; i < receivedList.size(); i++) map.put(receivedList.get(i), 0);

            // contract map <-> data
            for (int i = 0; i < qrdata.size(); i++) map.put(qrdata.get(i), 1);

            for (int i = 0; i < map.size(); i++) {
                String number = receivedList.get(i);
                String name = receivedNameList.get(i);
                int check = map.get(receivedList.get(i));
                putName.add(name);
                putNumber.add(number);
                putCheck.add(check);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < putName.size(); i++)
                        listAdapter.add(new SendListItem(putName.get(i), putNumber.get(i), putCheck.get(i)));
                    list.setAdapter(listAdapter);
                }
            });
        }
    }

    public class SendData implements Runnable {
        ArrayList<String> qrlist;
        ProgressDialog mpd;

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mpd = new ProgressDialog(SendActivity.this);
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

            // put datas into Hashmap
            for (int i = 0; i < receivedList.size(); i++) map.put(receivedList.get(i), 0);

            // contract map <-> qrlist
            for (int i = 0; i < qrlist.size(); i++) map.put(qrlist.get(i), 1);

            // Create Table follows Date
            try {
                sendDataforCreateTable("checked" + getTime());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // put hashmap to server
            for (int i = 0; i < map.size(); i++) {
                try {
                    String tableName = "checked" + getTime();
                    String studentNum = receivedList.get(i);
                    String studentName = receivedNameList.get(i);
                    String check = String.valueOf(map.get(receivedList.get(i)));
                    sendDataforInsertData(tableName, studentNum, studentName, check);
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
                    Toast.makeText(SendActivity.this, "서버로 데이터를 보냈습니다!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SendActivity.this, SendActivity.class));
                    finish();
                }
            });
        }
    }

    public void sendDataforCreateTable(String date) throws IOException {
        HttpPost request = new HttpPost("http://windsekirun.cafe24.com/php/create_table.php");
        Vector<NameValuePair> nameValue = new Vector<>();
        nameValue.add(new BasicNameValuePair("name", date));
        request.setEntity(makeEntity(nameValue));
        HttpClient client = new DefaultHttpClient();
        ResponseHandler<String> reshandler = new BasicResponseHandler();
        client.execute(request, reshandler);
    }

    public void sendDataforInsertData(String tableName, String studentNum, String studentName, String checked) throws IOException {
        HttpPost request = new HttpPost("http://windsekirun.cafe24.com/php/put_value.php");
        Vector<NameValuePair> nameValue = new Vector<>();
        nameValue.add(new BasicNameValuePair("tableName", tableName));
        nameValue.add(new BasicNameValuePair("studentNum", studentNum));
        nameValue.add(new BasicNameValuePair("studentName", studentName));
        nameValue.add(new BasicNameValuePair("checked", checked));
        request.setEntity(makeEntity(nameValue));
        HttpClient client = new DefaultHttpClient();
        ResponseHandler<String> reshandler = new BasicResponseHandler();
        client.execute(request, reshandler);
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


    @Override
    public void onBackPressed() {
        startActivity(new Intent(SendActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
