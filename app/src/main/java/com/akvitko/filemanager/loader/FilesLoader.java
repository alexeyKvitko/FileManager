package com.akvitko.filemanager.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.akvitko.filemanager.AppConstants;
import com.akvitko.filemanager.R;
import com.akvitko.filemanager.model.FileEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by alexey on 29.05.15.
 */
public class FilesLoader extends AsyncTaskLoader< ArrayList<FileEntity> > {

    String path;
    Context context;
    boolean root;

    public FilesLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public ArrayList<FileEntity> loadInBackground() {
        ArrayList< FileEntity> results = new ArrayList<FileEntity>();

        if ( !isRoot() ){
            FileEntity fileEntity = new FileEntity();
            fileEntity.setName( AppConstants.PREV_DIR );
            fileEntity.setHasChild( true );
            fileEntity.setFolder( true );

            results.add( fileEntity );
        }
        try{
            File sourceDir = new File( path );
            File[] files = sourceDir.listFiles();
            if ( files.length == 0 ){
                FileEntity fileEntity = new FileEntity();
                fileEntity.setName( context.getString(R.string.empty_folder) );
                fileEntity.setFolder( false );
                fileEntity.setHasChild( false );

                results.add( fileEntity );
            }
            for ( int i = 0; i < files.length; i++ ){
                File file = files[i];

                FileEntity fileEntity = new FileEntity();
                fileEntity.setName( file.getName() );
                fileEntity.setPath(file.getPath());
                fileEntity.setSize(file.length());
                fileEntity.setModifyDate( new Date( file.lastModified()) );
                fileEntity.setFolder( file.isDirectory() );
                fileEntity.setHasChild( true );

                results.add( fileEntity );
            }
            Collections.sort( results );
        } catch ( Exception e ){
            results = null;
        }
        return results;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }
}
