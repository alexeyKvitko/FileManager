package com.akvitko.filemanager.services;

import android.app.IntentService;
import android.content.Intent;

import com.akvitko.filemanager.AppConstants;
import com.akvitko.filemanager.dao.DAOManager;
import com.akvitko.filemanager.exception.DAOException;
import com.akvitko.filemanager.model.FileEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexey on 28.05.15.
 */
public class FMIntentService extends IntentService {

    public FMIntentService() {
        super("FMIntentService");
    }

    public FMIntentService(String name) {
        super(name);
    }

    private int upserted;

    @Override
    protected void onHandleIntent(Intent intent) {

        ArrayList<String> roots = intent.getStringArrayListExtra( AppConstants.SERVICE_ARGS );
        Intent intentResponse = new Intent();
        intentResponse.setAction( AppConstants.MAIN_ACTIVITY_RECEIVER );
        intentResponse.addCategory( Intent.CATEGORY_DEFAULT );

        DAOManager daoManager = new DAOManager( getApplicationContext() );
        int sum = 0;
        long start = ( new Date() ).getTime();
        for( String root: roots){
            try {
                upserted = 0;
                indexingDirectory(daoManager, root);
                daoManager.updateLog( root, AppConstants.SUCCESS, AppConstants.INDEXING_FILES+Integer.toString( upserted ) );
                sum = sum+upserted;
            } catch (DAOException e) {
                String message = e.getMessage();
                try {
                    daoManager.updateLog( root, AppConstants.FAIL, message );
                } catch (DAOException e1) {
                    message += " [ "+e1.getMessage()+" ] ";
                }
                intentResponse.putExtra( AppConstants.RECEIVER_RESULT, AppConstants.FAIL );
                intentResponse.putExtra( AppConstants.RECEIVER_DESC, message );
                sendBroadcast(intentResponse);
                return;
            }
        }
        long stop = ( new Date() ).getTime();
        String description = null;
        intentResponse.putExtra( AppConstants.RECEIVER_RESULT, AppConstants.SUCCESS );
        if ( sum > 0){
            description = " [ "+sum+" files at " + (stop-start)/1000 + " sec ]";
        }
        intentResponse.putExtra( AppConstants.RECEIVER_DESC, description );
        sendBroadcast(intentResponse);
    }

    /**
     *  Recursive indexed directory
     *  @param daoManager - manager to wok with DAO
     *  @param dir - directory to indexing
     * @throws DAOException
     */
    private void indexingDirectory(DAOManager daoManager, String dir) throws DAOException {

        File sourceDir = new File( dir );
        if( sourceDir == null || sourceDir.listFiles() == null ){
            return;
        }
        File[] files = sourceDir.listFiles();
        for ( int i = 0; i < files.length; i++ ){
            File file = files[i];
            FileEntity fileEntity = new FileEntity();
            fileEntity.setName( file.getName() );
            fileEntity.setPath(file.getPath());
            fileEntity.setSize(file.length());
            fileEntity.setModifyDate( new Date( file.lastModified()) );

            int upsert = daoManager.upsertIndex( fileEntity );
            upserted = upserted + upsert;

            if ( file.isDirectory() ){
                indexingDirectory(daoManager, file.getPath());
            }
        }
    }

}

