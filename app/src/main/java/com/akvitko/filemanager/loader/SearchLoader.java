package com.akvitko.filemanager.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.akvitko.filemanager.dao.DAOManager;
import com.akvitko.filemanager.exception.DAOException;
import com.akvitko.filemanager.model.FileEntity;

import java.util.ArrayList;

/**
 * Created by alexey on 31.05.15.
 */
public class SearchLoader extends AsyncTaskLoader< ArrayList<FileEntity> > {

    private Context context;
    private String where;
    private String arg;

    public SearchLoader( Context context ) {
        super(context);
        this.context = context;
    }

    @Override
    public ArrayList< FileEntity > loadInBackground() {
        ArrayList< FileEntity > result = null;
        DAOManager daoManager = new DAOManager( context );
        String[] args = arg == null ? null : new String[] { arg };
        try {
            result = daoManager.searchFiles( where, args );
        } catch (DAOException e) {
            Log.e( this.getClass().getName(), e.getMessage() );
        }

        return result;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public void setArg(String arg) {
        this.arg = arg;
    }
}
