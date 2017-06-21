package com.amtee.camscanner.utilities.extra_utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.amtee.camscanner.model_classes.MyDocItems;

import java.util.ArrayList;


/**
 * Created by DEVEN SINGH on 1/22/2015.
 */
public class CamScannerDatabase extends SQLiteOpenHelper {

    Context context;
    public static final String DATABASE_NAME = "Amtee_CamScanners.db";
    public static final String DOCUMENTS_TABLE_NAME = "docs";
    public static final String DOCUMENTS_COLUMN_ID = "id";
    public static final String DOCUMENTS_COLUMN_NAME = "docName";
    public static final String DOCUMENTS_COLUMN_PATH = "path";
    public static final String DOCUMENTS_COLUMN_TAG = "tag";
    public static final String DOCUMENTS_COLUMN_DATE_TIME = "dateTime";

    public CamScannerDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table docs " +
                        "(id integer primary key,docName text,path text,dateTime text,tag text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS docs");
        onCreate(db);
    }

    public boolean insertDoc(String name, String path, String dateTime, String tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DOCUMENTS_COLUMN_NAME, name);
        contentValues.put(DOCUMENTS_COLUMN_PATH, path);
        contentValues.put(DOCUMENTS_COLUMN_DATE_TIME, dateTime);
        contentValues.put(DOCUMENTS_COLUMN_TAG, tag);
        db.insert(DOCUMENTS_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from docs where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, DOCUMENTS_TABLE_NAME);
        return numRows;
    }
//
//    public String updateDoc(Integer id, String name, String path) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(DOCUMENTS_COLUMN_NAME, name);
//        contentValues.put(DOCUMENTS_COLUMN_PATH, path);
//        db.update(DOCUMENTS_TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
//        db.close();
//        return name;
//    }

    public String updateDoc(String docName, String updateDocName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DOCUMENTS_COLUMN_NAME, updateDocName);
        db.update(DOCUMENTS_TABLE_NAME, contentValues, DOCUMENTS_COLUMN_NAME + " = ? ", new String[]{docName});
        db.close();
        return docName;
    }


    public void deleteDoc(String docsName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DOCUMENTS_TABLE_NAME,
                "docName = ? ", new String[]{docsName});
        db.close();
    }


    public ArrayList<MyDocItems> getAllDocs() {
        String docPaths = "";
        ArrayList<MyDocItems> listOfDocs = new ArrayList<MyDocItems>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from docs", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            MyDocItems myDocItems = new MyDocItems();
            myDocItems.setMyDocUniqueID(res.getInt(res.getColumnIndex(DOCUMENTS_COLUMN_ID)));
            myDocItems.setMyDocName(res.getString(res
                    .getColumnIndex(DOCUMENTS_COLUMN_NAME)));
            docPaths = res.getString(res
                    .getColumnIndex(DOCUMENTS_COLUMN_PATH));
            if (docPaths.contains(","))
                docPaths = docPaths.substring(0, docPaths.indexOf(","));
            myDocItems.setMyDocImgPathName(docPaths);
            myDocItems.setMyDocTime(res.getString(res
                    .getColumnIndex(DOCUMENTS_COLUMN_DATE_TIME)));
            myDocItems.setMyDocTags(res.getString(res
                    .getColumnIndex(DOCUMENTS_COLUMN_TAG)));
            listOfDocs.add(myDocItems);
            res.moveToNext();
        }
        return listOfDocs;
    }

    public ArrayList<MyDocItems> getAllDocsForDocCreation(String documentName) {
        ArrayList<MyDocItems> listOfDocs = new ArrayList<MyDocItems>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from docs where docName" + "= ?", new String[]{documentName});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
//            MyDocItems myDocItems = new MyDocItems();
            MyDocItems myDocItems = null;
            if (res.getString(res.getColumnIndex(DOCUMENTS_COLUMN_PATH)).contains(",")) {
                String[] path = res.getString(res.getColumnIndex(DOCUMENTS_COLUMN_PATH)).split(",");
                for (String str : path) {
                    int i = 1;
                    myDocItems = new MyDocItems();
                    myDocItems.setMyDocImgPathName(str);
                    myDocItems.setMyDocName("" + i);
                    listOfDocs.add(myDocItems);
                    i++;
                }
            } else {
                myDocItems = new MyDocItems();
                myDocItems.setMyDocName(res.getString(res
                        .getColumnIndex(DOCUMENTS_COLUMN_NAME)));
                myDocItems.setMyDocImgPathName(res.getString(res
                        .getColumnIndex(DOCUMENTS_COLUMN_PATH)));
                listOfDocs.add(myDocItems);
            }
            res.moveToNext();
        }
        return listOfDocs;
    }

    public String[] getDocsForDeletionAndPdfCreation(String documentName) {
        String[] path = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from docs where docName" + "= ?", new String[]{documentName});
        res.moveToFirst();
        while (res.isAfterLast() == false) {

            if (res.getString(res.getColumnIndex(DOCUMENTS_COLUMN_PATH)).contains(",")) {
                path = res.getString(res.getColumnIndex(DOCUMENTS_COLUMN_PATH)).split(",");
            } else {
                String pathName = res.getString(res.getColumnIndex(DOCUMENTS_COLUMN_PATH));
                path = new String[]{pathName};
            }
            res.moveToNext();
        }
        return path;
    }


    public String getDocPathsForMerge(String docName) {
        String pathName = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from docs where docName" + "= ?", new String[]{docName});
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            pathName = res.getString(res.getColumnIndex(DOCUMENTS_COLUMN_PATH));
            res.moveToNext();
        }
        return pathName;
    }

    public String getAllPathsFromAllDocs() {
        String docPaths = "";
        int i = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from docs", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            if (i > 0) {
                docPaths = docPaths + ",";
            }
            docPaths = docPaths + res.getString(res
                    .getColumnIndex(DOCUMENTS_COLUMN_PATH));
            res.moveToNext();
        }
        return docPaths;
    }
}
