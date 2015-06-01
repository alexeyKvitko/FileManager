package com.akvitko.filemanager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by alexey on 28.05.15.
 */
public class AppUtils {

    /**
     *  Get path to with out root directory
     *  @param rootDir - root directory
     *  @param path - full path
     *  @return string to show current path
     */
    public static final String getLabelPath( String rootDir,String path ){
        String result = "";
        if ( path == null ){
            result = File.separator;
        } else {
            result = path.replace( rootDir,"") ;
        }
        return  result;
    }
    /**
     *  Get root direcories
     *  @return  root direcories
     */
    public static ArrayList< String > getRoots(){
        String state = Environment.getExternalStorageState();
        if ( !AppConstants.SDCARD_MOUNTED.equals( state ) ){
            return null;
        }
        String exPath = AppConstants.EXTERNAL_STORAGE_PATH;
        String[] separated = exPath.split( File.separator );
        File mnt = new File( File.separator+separated[1] );
        if ( !mnt.exists() ){
            return null;
        }
        File[] roots = mnt.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() && pathname.exists()
                        && pathname.canWrite() && !pathname.isHidden();
            }
        });
        ArrayList< String > rootList = new ArrayList< String >();
        for( File dir :roots ){
            rootList.add( dir.getPath() );
        }
        return rootList;
    }

    /**
     *  Format date to string
     *  @param date - date to format
     *  @return string to show current path
     */
    public static String formatDate( Date date ){
        return AppConstants.DATE_FORMAT.format( date );
    }

    /**
     *  Check is possible convert String to Date
     *  @param dateStr - checked string date
     *  @return is possible or not
     */
    public static boolean isDate( String dateStr ){
        boolean isDate = true;
        try {
            AppConstants.DATE_FORMAT_ONLY_DATE.parse( dateStr );
        } catch ( ParseException e){
            isDate = false;
        }
        return isDate;
    }

    /**
     *  Parse String to Date
     *  @param dateStr - string date to parse
     *  @return parsed date
     */
    public static Date parseDate( String dateStr ){
        try {
            return AppConstants.DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

}
