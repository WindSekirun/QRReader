package windsekirun.qrreader.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * QRReader
 * Class: TempResultAdapter
 * Created by WindSekirun on 2015. 4. 7..
 */
@SuppressWarnings("ALL")
public class TempResultAdapter {
    public static final String TABLE_NAME = "temp_result";
    public static final String KEY_TEXT = "text";

    public SQLiteDatabase mSQLiteDatabase;
    public TempHelper mDBHelper;
    public final Context c;

    public class TempHelper extends SQLiteOpenHelper {
        private static final String DBNAME = "tempresult.db";
        private static final int DATABASE_VERSION = 2;

        public TempHelper(Context c) {
            super(c, DBNAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(getCreateDBQuery());
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public TempResultAdapter(Context c) {
        this.c = c;
    }

    public void open() {
        mDBHelper = new TempHelper(c);
        mSQLiteDatabase = mDBHelper.getWritableDatabase();
    }

    public void close() {
        mDBHelper.close();
    }

    public void insertText(String text) {
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TEXT, text);
            mSQLiteDatabase.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean deleteText(String text) {
        try {
            mSQLiteDatabase.delete(TABLE_NAME, String.format(" %s = ? ", KEY_TEXT), new String[]{text});
            return true;
        } catch (SQLiteException e) {
            return false;
        }
    }

    public void clear() {
        mSQLiteDatabase.execSQL("delete from " + TABLE_NAME);
    }

    public ArrayList<String> getText() {
        ArrayList<String> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = mSQLiteDatabase.query(TABLE_NAME, new String[]{KEY_TEXT}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int index_text = cursor.getColumnIndex(KEY_TEXT);
            do {
                String text = cursor.getString(index_text);
                list.add(text);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public String getCreateDBQuery() {
        return "create table " + TABLE_NAME + "(_id integer primary key autoincrement, " + KEY_TEXT + " text not null" + ");";
    }

}
