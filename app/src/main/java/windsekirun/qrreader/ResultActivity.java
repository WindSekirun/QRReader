package windsekirun.qrreader;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.sql.SQLException;

import windsekirun.qrreader.async.NaraeAsync;
import windsekirun.qrreader.db.TempResultAdapter;

/**
 * QRReader
 * Class: ResultActivity
 * Created by WindSekirun on 2015. 4. 7..
 */
public class ResultActivity extends ActionBarActivity implements View.OnClickListener {
    String studentNum;
    TextView statusText;
    TempResultAdapter adapter;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        if (Build.VERSION.SDK_INT >= 20) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setStatusBarColor(getResources().getColor(R.color.light_blue_800));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("QR코드 인식 결과");
        toolbar.setTitleTextColor(0xffffffff);
        TextView studentNumText = (TextView) findViewById(R.id.studentNum);
        statusText = (TextView) findViewById(R.id.statusText);
        Button anotherQR = (Button) findViewById(R.id.anotherQR);
        anotherQR.setOnClickListener(this);
        Intent i = getIntent();
        Bundle data = i.getExtras();
        studentNum = data.getString("studentNum");
        studentNumText.setText(studentNum);
        setSupportActionBar(toolbar);
        adapter = new TempResultAdapter(this);
        new NaraeAsync(false, new ResultSave(), 8, "RESULT_SAVE").execute();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.anotherQR) {
            startActivity(new Intent(ResultActivity.this, CaptureActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ResultActivity.this, MainActivity.class));
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

    public class ResultSave implements Runnable {

        @Override
        public void run() {
            try {
                adapter.open();
                adapter.insertText(studentNum);
                adapter.close();
                statusText.setText("체크 완료!");
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
