package com.akvitko.filemanager.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.akvitko.filemanager.dao.DAOConstants.*;

/**
 * Created by alexey on 29.05.15.
 */
public class FMDBOpenHelper extends SQLiteOpenHelper {

    public FMDBOpenHelper(Context context) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( CREATE_INDEXES_TABLE );
        db.execSQL( CREATE_INDEXING_LOG_TABLE );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( DROP_INDEXES_TABLE );
        db.execSQL( DROP_INDEXING_LOG_TABLE );
        onCreate( db );
    }
}
