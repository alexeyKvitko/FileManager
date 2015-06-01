package com.akvitko.filemanager.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static com.akvitko.filemanager.dao.DAOConstants.*;

import com.akvitko.filemanager.AppUtils;
import com.akvitko.filemanager.exception.DAOException;
import com.akvitko.filemanager.model.FileEntity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by alexey on 29.05.15.
 */
public class DAOManager {

    private FMDBOpenHelper helper;

    public DAOManager(Context context) {
        helper = new FMDBOpenHelper( context );
    }

    /**
    *  Insert new record in indexing table
    *  @param fileEntity - model of entity which  will be inserted
    * @return primary key
    * @throws DAOException
    */
    public long insert( FileEntity fileEntity ) throws DAOException{
        long result=-1;
        try {
            ContentValues values = mapModelToContentValues( fileEntity );
            result = getDB().insert(TABLE_FM_INDEXES, null, values);
        } catch ( Exception e ){
            throw new DAOException( e.getMessage() );
        }
       return result;
    }

    /**
     *  Update record in indexing table
     *  @param fileEntity - model of entity which  will be updated
     * @return primary key
     * @throws DAOException
     */
    public int update( FileEntity fileEntity ) throws DAOException{
        int result = -1;
        try {
            ContentValues values = mapModelToContentValues(fileEntity );
            String where = COLUMN_PATH_HASH + "=?";
            String hc = Integer.toString(fileEntity.getPath().hashCode()) ;
            String[] whereArgs = new String[]{ hc };
            result = getDB().update(TABLE_FM_INDEXES, values, where, whereArgs);
        } catch ( Exception e ){
            throw  new DAOException( e.getMessage() );
        }
        return result;
    }
    /**
     *  Delete record from indexing table
     *  @param fileEntity - model of entity which  will be deleted
     * @throws DAOException
     */
    public int delete( FileEntity fileEntity ) throws DAOException {
        int result = -1;
        try{
            String where = COLUMN_PATH_HASH + "=?";
            String hc = Integer.toString( fileEntity.getPath().hashCode() ) ;
            String[] whereArgs = new String[]{ hc };
            result = getDB().delete(TABLE_FM_INDEXES, where, whereArgs);
        } catch ( Exception e ){
           throw new DAOException( e.getMessage() );
        }

        return result;
    }

    /**
     *  Insert or  update record in case exist one or not
     *  @param fileEntity - model of entity which  will be updated
     * @return count of upserted record
     * @throws DAOException
     */
    public int upsertIndex(FileEntity fileEntity ) throws DAOException {
        int upsert = 0;
        try {
            String where = COLUMN_PATH_HASH + "=?";
            String hc = Integer.toString( fileEntity.getPath().hashCode() ) ;
            String[] whereArgs = new String[]{ hc };
            Cursor cursor = getDB().query(TABLE_FM_INDEXES, RESULT_FULL_HASH_COLUMN,  where,whereArgs,
                    null, null, null );
            if ( cursor.moveToFirst() ){
                int columnIndexFullHash = cursor.getColumnIndexOrThrow( COLUMN_FULL_HASH );
                int fullHash = cursor.getInt( columnIndexFullHash );
                String modifyDate = AppUtils.formatDate( fileEntity.getModifyDate() );
                String fullInfo = fileEntity.getPath()+fileEntity.getSize()+modifyDate;
                if( fullHash != fullInfo.hashCode() ){
                    update( fileEntity );
                    upsert = 1;
                }
            } else {
                insert( fileEntity );
                upsert = 1;
            }
        } catch ( Exception e ){
            throw new DAOException( e.getMessage() );
        }
        return upsert;
    }

    /**
     *  Update record in  log table
     * @param root - root directory
     * @param status - status for updated root
     * @param description - description to finished operation
     * @throws DAOException
     */
    public void updateLog(String root,String status, String description ) throws DAOException {
        try {
            ContentValues values = new ContentValues();
            values.put( COLUMN_ROOT_DIR, root );
            values.put( COLUMN_MODIFY_DATE, AppUtils.formatDate( new Date() ) );
            values.put( COLUMN_STATUS, status );
            values.put( COLUMN_DESCRIPTION, description );
            getDB().insert(TABLE_INDEXING_LOG, null, values);
        } catch ( Exception e ){
            throw new DAOException( e.getMessage() );
        }
    }

    /**
     *  Search files in indexing table
     *  @param where - in which column will be searching
     *  @param args - arguments
     * @return list of files
     * @throws DAOException
     */
    public ArrayList< FileEntity > searchFiles( String where, String[] args ) throws DAOException{
        ArrayList< FileEntity > fileEntities = new ArrayList< FileEntity >();

        try {
            Cursor cursor = getDB().query(TABLE_FM_INDEXES, RESULT_ALL_COLUMN,  where, args,
                                          null, null, null );
            while ( cursor.moveToNext() ){
                FileEntity fileEntity = mapCursorToModel( cursor );
                fileEntities.add( fileEntity );
            }
        } catch (Exception e) {
            throw new DAOException( e.getMessage() );
        }
        return  fileEntities;
    }

    /**
     *  Map values from Cursor to Model
     *  @param cursor - Cursor which contains values from table
     * @return model of FileEntity
     * @throws Exception
     */
    private FileEntity mapCursorToModel( Cursor cursor ) throws Exception{
        FileEntity fileEntity = new FileEntity();
        int columnIndexDir = cursor.getColumnIndexOrThrow( COLUMN_DIRECTORY );
        int columnIndexFileName = cursor.getColumnIndexOrThrow( COLUMN_FILE_NAME );
        int columnIndexSize = cursor.getColumnIndexOrThrow( COLUMN_SIZE);
        int columnIndexDate = cursor.getColumnIndexOrThrow( COLUMN_MODIFY_DATE );
        fileEntity.setPath( cursor.getString( columnIndexDir )+cursor.getString( columnIndexFileName ) );
        fileEntity.setName(cursor.getString(columnIndexFileName));
        fileEntity.setSize(cursor.getLong(columnIndexSize));
        fileEntity.setModifyDate( AppUtils.parseDate( cursor.getString( columnIndexDate ) ) );
        return fileEntity;
    }

    /**
     *  Map from File Entity Model to Cursor values
     *  @param fileEntity - FileEntity model
     * @return Cursor with values to insert or update in db
     */
    private ContentValues mapModelToContentValues( FileEntity fileEntity ){
        ContentValues values = new ContentValues();
        String modifyDate = AppUtils.formatDate( fileEntity.getModifyDate() );
        String fullInfo = fileEntity.getPath()+fileEntity.getSize()+modifyDate;
        values.put( COLUMN_PATH_HASH, fileEntity.getPath().hashCode() );
        values.put( COLUMN_FULL_HASH, fullInfo.hashCode() );
        values.put( COLUMN_DIRECTORY, fileEntity.getPath().replace( fileEntity.getName(),"") );
        values.put( COLUMN_FILE_NAME, fileEntity.getName() );
        values.put( COLUMN_SIZE, fileEntity.getSize() );
        values.put( COLUMN_MODIFY_DATE, AppUtils.formatDate( fileEntity.getModifyDate() ) );

        return values;
    }

    /**
     *  Return dtabase instance
     *
     * @return database instance
     * @throws Exception
     */
    private SQLiteDatabase getDB() throws Exception{
        return helper.getWritableDatabase();
    }
}
