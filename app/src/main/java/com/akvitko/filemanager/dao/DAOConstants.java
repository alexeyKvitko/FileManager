package com.akvitko.filemanager.dao;

/**
 * Created by alexey on 29.05.15.
 */
public class DAOConstants {

    public static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "fmindexes.db";
    public static final String TABLE_FM_INDEXES = "indexes";
    public static final String TABLE_INDEXING_LOG = "indexing_log";

    public static final String COLUMN_PR_KEY = "_id";
    public static final String COLUMN_MODIFY_DATE = "modify_date";

    //INDEXES TABLE
    public static final String COLUMN_PATH_HASH = "path_hash";
    public static final String COLUMN_FULL_HASH = "full_hash";
    public static final String COLUMN_DIRECTORY = "directory";
    public static final String COLUMN_FILE_NAME = "file_name";
    public static final String COLUMN_SIZE = "size";

    public static final String CREATE_INDEXES_TABLE = "create table "
            + TABLE_FM_INDEXES + " ("
            + COLUMN_PR_KEY + " integer primary key autoincrement, "
            + COLUMN_PATH_HASH + " integer not null, "
            + COLUMN_FULL_HASH + " integer not null, "
            + COLUMN_DIRECTORY + " text not null, "
            + COLUMN_FILE_NAME + " text not null, "
            + COLUMN_SIZE + " integer not null, "
            + COLUMN_MODIFY_DATE + " text not null); ";

    public static final String DROP_INDEXES_TABLE = " drop table if exists "+TABLE_FM_INDEXES;

    //INDEXES LOG TABLE
    public static final String COLUMN_ROOT_DIR = "root_dir";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DESCRIPTION = "description";

    public static final String CREATE_INDEXING_LOG_TABLE = "create table "
            + TABLE_INDEXING_LOG + " ("
            + COLUMN_PR_KEY + " integer primary key autoincrement, "
            + COLUMN_ROOT_DIR + " text not null, "
            + COLUMN_MODIFY_DATE + " text not null, "
            + COLUMN_STATUS + " text not null, "
            + COLUMN_DESCRIPTION + " text not null); ";

    public static final String DROP_INDEXING_LOG_TABLE = " drop table if exists "+TABLE_INDEXING_LOG;

    //SQLs
    public static final String[] RESULT_FULL_HASH_COLUMN = new String[]{ COLUMN_FULL_HASH };
    public static final String[] RESULT_ALL_COLUMN = new String[]{ COLUMN_DIRECTORY,
                                                                   COLUMN_FILE_NAME,
                                                                   COLUMN_SIZE,
                                                                   COLUMN_MODIFY_DATE };

}
