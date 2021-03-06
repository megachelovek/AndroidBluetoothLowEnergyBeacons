package com.ssau.danilius.diplomablebeacons.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class BeaconMapDbHelper  extends SQLiteOpenHelper {

    final static int DB_VER = 1;
    public static final String LOG_TAG = BeaconMapDbHelper.class.getSimpleName();
    private static final String DB_NAME  = "BeaconMapDatabase.db";
    private static final String TABLE_NAME  = "beaconmap";
    private static  String DATABASE_PATH = "";
    private static final String DEBUG_CONTEXT = "DatabaseContext";
    final String DEBUG_TAG = "BLEBeaconMap";

    public final static String COLUMN_ID = "id";
    public final static String COLUMN_MAC = "mac";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_LASR_RSSI = "last_rssi";
    public final static String COLUMN_BEACON_MAP_X = "beacon_map_x";
    public final static String COLUMN_BEACON_MAP_Y = "beacon_map_y";

    final String DATA_FILE_NAME = "data.txt";
    final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
            "(id integer primary key autoincrement,mac text ,name text,last_rssi int,beacon_map_x int, beacon_map_y int);";
    final String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;
    private boolean mNeedUpdate = false;
    private final Context mContext;

    private static final int DATABASE_VERSION = 1;

    public BeaconMapDbHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;
        this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(DEBUG_TAG,"onCreate() called");
        db.execSQL(CREATE_TABLE);
        //fillData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    private ArrayList<String> getData() {
        InputStream stream = null;
        ArrayList<String> list = new ArrayList<String>();
        try {
            stream = mContext.getAssets().open(DATA_FILE_NAME);
        }
        catch (IOException e) {
            Log.d(DEBUG_TAG,e.getMessage());
        }

        DataInputStream dataStream = new DataInputStream(stream);
        String data = "";
        try {
            while( (data=dataStream.readLine()) != null ) {
                list.add(data);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void fillData(SQLiteDatabase db){
        ArrayList<String> data = getData();
        for(String dt:data) Log.d(DEBUG_TAG,"item="+dt);

        if( db != null ){
            ContentValues values;

            for(String dat:data){
                values = new ContentValues();
                values.put("todo", dat);
                db.insert(TABLE_NAME, null, values);
            }
        }
        else {
            Log.d(DEBUG_TAG,"db null");
        }
    }
    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DATABASE_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }
    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }
    private boolean checkDataBase() {
        File dbFile = new File(DATABASE_PATH + DB_NAME);
        return dbFile.exists();
    }
    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        OutputStream mOutput = new FileOutputStream(DATABASE_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

}
