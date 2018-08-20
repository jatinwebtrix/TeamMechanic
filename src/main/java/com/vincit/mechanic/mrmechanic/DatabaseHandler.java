package com.vincit.mechanic.mrmechanic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by R on 9/9/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";
    //Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "dataManager";
    // Users Valid table name
    private static final String KEY_ID = "id";

    private static final String TABLE_STATE = "tbl_state";
    private static final String KEY_SID = "sid";
    private static final String KEY_SNAME = "sname";
    private static final String KEY_SCODE = "scode";
    // Users Valid table name
    private static final String TABLE_CITY = "tbl_city";
    private static final String KEY_CID = "cid";
    private static final String KEY_CCNAME = "ccname";


    private static final String TABLE_CAT = "tbl_cat";
    private static final String KEY_CAT_ID = "cat_id";
    private static final String KEY_CAT_NAME = "cat_name";

    private static final String TABLE_SUBCAT = "tbl_subcat";
    private static final String KEY_SUBCAT_ID = "subcat_id";
    private static final String KEY_SUBCAT_NAME = "subcat_name";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_STATE = "CREATE TABLE " + TABLE_STATE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_SID + " TEXT,"
                + KEY_SNAME + " TEXT,"
                + KEY_SCODE + " TEXT"
                + ")";


        String CREATE_TABLE_CITY = "CREATE TABLE " + TABLE_CITY + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CID + " TEXT,"
                + KEY_SID + " TEXT,"
                + KEY_CCNAME + " TEXT"
                + ")";


        String CREATE_TABLE_CAT = "CREATE TABLE " + TABLE_CAT + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_CAT_ID + " TEXT,"
                + KEY_CAT_NAME + " TEXT"
                + ")";

        String CREATE_TABLE_SUBCAT = "CREATE TABLE " + TABLE_SUBCAT + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_SUBCAT_ID + " TEXT,"
                + KEY_SUBCAT_NAME + " TEXT,"
                + KEY_CAT_ID + " TEXT"
                + ")";

        db.execSQL(CREATE_TABLE_STATE);
        db.execSQL(CREATE_TABLE_CITY);
        db.execSQL(CREATE_TABLE_CAT);
        db.execSQL(CREATE_TABLE_SUBCAT);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBCAT);
        // Create tables again
        onCreate(db);
    }



    public void SaveStates(ArrayList<State> stateList){

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try
        {
            ContentValues values = new ContentValues();
            for (State state : stateList) {
                values.put(KEY_SID, state.sid);
                values.put(KEY_SNAME, state.sname);
                values.put(KEY_SCODE, state.scode);
                db.insert(TABLE_STATE, null, values);
                Log.e(TAG,"OK");
            }
            db.setTransactionSuccessful();
        }catch (Exception e){

        }finally {
            db.endTransaction();
        }
        db.close();

    }
    public ArrayList<State> GetStateList(){
        ArrayList<State> stateList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_STATE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                State state = new State();
                state.sid = cursor.getString(1);
                state.sname = cursor.getString(2);
                state.scode = cursor.getString(3);
                stateList.add(state);
            } while (cursor.moveToNext());
        }
        return stateList;
    }


    public void SaveCities(ArrayList<City> cityList){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try
        {
            ContentValues values = new ContentValues();
            for (City city : cityList) {
                values.put(KEY_CID, city.cid);
                values.put(KEY_SID, city.sid);
                values.put(KEY_CCNAME, city.ccname);
                db.insert(TABLE_CITY, null, values);
                Log.e(TAG,"OK");
            }
            db.setTransactionSuccessful();
        }catch (Exception e){

        }finally {
            db.endTransaction();
        }
        db.close();
    }
    public ArrayList<City> GetCityList(){
        ArrayList<City> cityList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CITY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.cid = cursor.getString(1);
                city.sid = cursor.getString(2);
                city.ccname = cursor.getString(3);
                cityList.add(city);
            } while (cursor.moveToNext());
        }
        return cityList;
    }


    public void SaveCat(ArrayList<Cat> catList){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try
        {
            ContentValues values = new ContentValues();
            for (Cat cat : catList) {
                values.put(KEY_CAT_ID, cat.cat_id);
                values.put(KEY_CAT_NAME, cat.cat_name);
                db.insert(TABLE_CAT, null, values);
                Log.e(TAG,"OK");
            }
            db.setTransactionSuccessful();
        }catch (Exception e){

        }finally {
            db.endTransaction();
        }
        db.close();
    }
    public ArrayList<Cat> GetCatList(){
        ArrayList<Cat> catList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CAT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Cat cat = new Cat();
                cat.cat_id = cursor.getString(1);
                cat.cat_name = cursor.getString(2);
                catList.add(cat);
            } while (cursor.moveToNext());
        }
        return catList;
    }


    public void SaveSubCat(ArrayList<SubCat> subcatList){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try
        {
            ContentValues values = new ContentValues();
            for (SubCat subCat : subcatList) {
                values.put(KEY_SUBCAT_ID, subCat.subcat_id);
                values.put(KEY_SUBCAT_NAME, subCat.subcat_name);
                values.put(KEY_CAT_ID, subCat.cat_id);
                db.insert(TABLE_SUBCAT, null, values);
                Log.e(TAG,"OK");
            }
            db.setTransactionSuccessful();
        }catch (Exception e){

        }finally {
            db.endTransaction();
        }
        db.close();
    }
    public ArrayList<SubCat> GetSubCatList(){
        ArrayList<SubCat> subcatList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_SUBCAT;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                SubCat subCat = new SubCat();
                subCat.subcat_id = cursor.getString(1);
                subCat.subcat_name = cursor.getString(2);
                subCat.cat_id = cursor.getString(3);
                subcatList.add(subCat);
            } while (cursor.moveToNext());
        }
        return subcatList;
    }


}
