package windsekirun.qrreader.test;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

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
        setSupportActionBar(toolbar);
        generate.setOnClickListener(this);
        capture.setOnClickListener(this);
        license.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.generate) {
            startActivity(new Intent(MainActivity.this, GenerateActivity.class));
            finish();
        } else if (id == R.id.read) {
            Intent i = new Intent("com.google.zxing.client.android.SCAN");
            startActivityForResult(i, 0);
        } else {
            showLicenseDialog();
        }
    }

    public void showLicenseDialog() {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(this).inflate(R.layout.dialog, null, false);
        WebView wv = (WebView) v.findViewById(R.id.wv);
        wv.loadUrl("");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (requestCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Toast.makeText(MainActivity.this, "인식한 내용: " + contents, Toast.LENGTH_LONG).show();
            } else Toast.makeText(MainActivity.this, "인식에 실패하였습니다.", Toast.LENGTH_LONG).show();
        }
    }
}
