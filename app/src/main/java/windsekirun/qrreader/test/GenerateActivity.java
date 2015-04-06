package windsekirun.qrreader.test;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import windsekirun.qrreader.test.async.NaraeAsync;
import windsekirun.qrreader.test.encode.Contents;
import windsekirun.qrreader.test.encode.QRCodeEncoder;

/**
 * QRReader
 * Class: GenerateActivity
 * Created by WindSekirun on 2015. 4. 6..
 */

/**
 * Using Library:
 * https://github.com/zxing/zxing
 */
public class GenerateActivity extends ActionBarActivity implements View.OnClickListener {
    EditText studentNum;
    Button generate;
    Button save;
    ImageView display;
    Drawable generatedCode = null;
    Bitmap generatedBitmap = null;
    String path;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        if (Build.VERSION.SDK_INT >= 20) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.setStatusBarColor(getResources().getColor(R.color.light_blue_800));
        }
        studentNum = (EditText) findViewById(R.id.studentNum);
        generate = (Button) findViewById(R.id.generate);
        save = (Button) findViewById(R.id.save);
        display = (ImageView) findViewById(R.id.display);
        generate.setOnClickListener(this);
        save.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("학번으로 QR코드 생성");
        toolbar.setTitleTextColor(0xffffffff);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        path = Environment.getExternalStorageDirectory().getPath() + "/QRReader/";
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.generate) {
            if (studentNum.getText().toString().isEmpty())
                Toast.makeText(this, "학번을 입력해주세요!", Toast.LENGTH_LONG).show();
            else new NaraeAsync(false, new generateQRCode(), 8, "GENERATE_QRCODE").execute();
        } else {
            if (generatedCode == null)
                Toast.makeText(this, "코드를 먼저 생성해주세요!", Toast.LENGTH_LONG).show();
            else save();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GenerateActivity.this, MainActivity.class));
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

    public class generateQRCode implements Runnable {

        @Override
        public void run() {
            String text = studentNum.getText().toString();
            QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(text, null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString());
            try {
                generatedBitmap = qrCodeEncoder.encodeAsBitmap();
                generatedCode = new BitmapDrawable(GenerateActivity.this.getResources(), generatedBitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    display.setImageDrawable(generatedCode);
                    Toast.makeText(GenerateActivity.this, "생성 완료!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void save() {
        if (isAccessibleSDCard()) {
            if (isPathExist()) {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(path + studentNum.getText().toString() + ".png");
                    generatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(GenerateActivity.this, "저장 완료!", Toast.LENGTH_LONG).show();
            }
        }

    }

    public boolean isAccessibleSDCard() {
        return Environment.isExternalStorageEmulated();
    }

    public boolean isPathExist() {
        File f = new File(path);
        return f.exists() || f.mkdir();
    }
}
