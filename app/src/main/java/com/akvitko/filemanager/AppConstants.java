package com.akvitko.filemanager;

import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

/**
 * Created by alexey on 27.05.15.
 */
public class AppConstants {

    public static final String MAIN_ACTIVITY_RECEIVER ="com.akvitko.filemanager.MAIN_ACTIVITY_RECEIVER";
    public static final String RECEIVER_RESULT = "indexing_result";
    public static final String RECEIVER_DESC = "indexing_result_description";
    public static final String INDEXING_FILES = "Indexing files: ";
    public static final String SELECT_SEARCHED_ITEM = "select_searched_item";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";
    public static final String FAIL_TAILMER = "fail_timer";
    public static final String PREFS = "preferences";
    public static final String SEARCH_COLUMN = "serach_column";
    public static final String CURRENT_ROOT = "current_root";
    public static final String START_SEARCH = "start_search";
    public static final String SERVICE_ARGS ="intent_service_args";
    public static final String SDCARD_MOUNTED = "mounted";
    public static final String ERROR_DIALOG_TAG = "error_dialog";
    public static final String FILE_INFO_DIALOG_TAG = "fileinfo_dialog";
    public static final String PREV_DIR = "..";
    public static final String EXTERNAL_STORAGE_PATH =
            Environment.getExternalStorageDirectory().getPath();

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_ONLY_DATE = new SimpleDateFormat("dd/MM/yyyy");

    public static final int FILE_LOADER = 0;
    public static final int SEARCH_LOADER = 1;

}
