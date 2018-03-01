package nk.mobileapps.weather.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 5/12/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String UID = "UID";
    public static final String DB_NAME = "Weather.db";
    public static final String DATABASE_PATH = "/data/data/nk.mobileapps.weather/databases/"; //Change it
    public static int DB_VERSION = 2;
    Context context;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public void exportDatabase() {
        File f = new File(DATABASE_PATH + DB_NAME);
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(f);
            fos = new FileOutputStream("/mnt/sdcard/" + DB_NAME);
            while (true) {
                int i = fis.read();
                if (i != -1) {
                    fos.write(i);
                } else {
                    break;
                }
            }
            fos.flush();
            Toast.makeText(context, "Database Export Successfully", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "DB dump ERROR", Toast.LENGTH_LONG).show();
        }
    }

    public void copydatabase() throws IOException {
        //Open your local db as the input stream
        InputStream myinput = context.getAssets().open(DB_NAME);
        // Path to the just created empty db
        String outfilename = DATABASE_PATH + DB_NAME;
        //Open the empty db as the output stream
        OutputStream myoutput = new FileOutputStream(DATABASE_PATH + DB_NAME);
        // transfer byte to inputfile to outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer)) > 0) {
            myoutput.write(buffer, 0, length);
        }
        //Close the streams
        myoutput.flush();
        myoutput.close();
        myinput.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTableStringByID(Location.TABLE_NAME, UID, Location.cols));


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            default:
                break;
        }

    }

    public long insertintoTable(String tableName, String[] colNames,
                                String[] colVals) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < colNames.length; i++) {
            cv.put(colNames[i], colVals[i]);
        }
        long result = db.insert(tableName, null, cv);
        db.close();
        return result;
    }

    public boolean deleteAll(String tablename) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean flag = db.delete(tablename, null, null) > 0;
        db.close();
        return flag;
    }

    public boolean deleteByValues(String tablename, String[] wherecolumnNames,
                                  String[] wherecolumnValues) {
        SQLiteDatabase db = this.getReadableDatabase();
        String cond = "";
        for (int i = 0; i < wherecolumnNames.length; i++) {
            cond = cond + wherecolumnNames[i] + " = '" + wherecolumnValues[i]
                    + "' AND ";
        }
        cond = cond.substring(0, cond.length() - 5);
        boolean flag = db.delete(tablename, cond, null) > 0;
        // System.out.println("deleted?" + flag);
        db.close();
        return flag;
    }

    public List<List<String>> getTableData(String TableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TableName, null);
        List<List<String>> list = new ArrayList<List<String>>();
        if (cur.getCount() > 0)
            list = cursorToListArr(cur);
        cur.close();
        db.close();
        return list;
    }

    public List<List<String>> cursorToListArr(Cursor c) {
        List<List<String>> rowList = new ArrayList<List<String>>();
        while (c.moveToNext()) {
            List<String> arr = new ArrayList<String>();
            for (int i = 0; i < c.getColumnCount(); i++) {
                arr.add(c.getString(i));
            }
            rowList.add(arr);
        }
        c.close();
        return rowList;
    }

    public boolean updateByValues(String tablename, String[] columnNames,
                                  String[] columnValues, String whereColumn[], String whereValue[]) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < columnNames.length; i++) {
            cv.put(columnNames[i], columnValues[i]);
        }
        String query = "";
        for (int i = 0; i < whereColumn.length; i++)
            query = query + whereColumn[i] + "='" + whereValue[i] + "' AND ";
        query = query.substring(0, query.length() - 5);
        //  System.out.println("query:" + query);
        boolean flag = db.update(tablename, cv, query, null) > 0;
        db.close();

        return flag;
    }

    public int getCount(String tableName) {

        SQLiteDatabase db = this.getReadableDatabase();
        int cnt = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName, null);
        cursor.moveToFirst();
        cnt = Integer.parseInt(cursor.getString(0));
        cursor.close();
        db.close();
        return cnt;
    }

    public int getCountByValues(String tableName, String colName[],
                                String colValue[]) {

        SQLiteDatabase db = this.getReadableDatabase();
        int cnt = 0;
        String countQuery = "SELECT  COUNT(*) FROM " + tableName + " WHERE ";
        for (int k = 0; k < colName.length; k++)
            countQuery = countQuery + colName[k] + "='" + colValue[k]
                    + "' AND ";
        countQuery = countQuery.substring(0, countQuery.length() - 5);
        // System.out.println("countQuery:" + countQuery);
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        cnt = Integer.parseInt(cursor.getString(0));
        cursor.close();
        db.close();
        return cnt;
    }

    public String CreateTableStringByID(String tablename, String primarycolumn,
                                        String[] restcolumns) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + tablename + " ("
                + primarycolumn + " INTEGER PRIMARY KEY AUTOINCREMENT,";

        for (String data : restcolumns) {
            CREATE_TABLE = CREATE_TABLE + data + " TEXT,";
        }
        CREATE_TABLE = CREATE_TABLE.substring(0, CREATE_TABLE.length() - 1)
                + ")";
        return CREATE_TABLE;
    }

    public static class Location {
        public static final String TABLE_NAME = "Location";

        public static final String gps = "gps";
        public static final String place = "place";
        public static final String last_updateTime = "last_updateTime";
        public static final String weather_obj = "weather_obj";

        public static final String[] cols = new String[]{gps, place, last_updateTime, weather_obj};

    }
}
