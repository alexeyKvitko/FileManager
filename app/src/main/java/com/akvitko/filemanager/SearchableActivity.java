package com.akvitko.filemanager;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.app.SearchManager;

import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.akvitko.filemanager.adapters.SearchAdapter;
import com.akvitko.filemanager.dao.DAOConstants;
import com.akvitko.filemanager.fragments.FMDialog;
import com.akvitko.filemanager.loader.SearchLoader;
import com.akvitko.filemanager.model.FileEntity;

import java.util.ArrayList;

/**
 * Created by alexey on 30.05.15.
 */
public class SearchableActivity extends ListActivity implements
                                            AdapterView.OnItemClickListener,
                                            LoaderManager.LoaderCallbacks< ArrayList<FileEntity> >{

    private SearchLoader searchLoader;

    private ArrayList< FileEntity > filesList;
    private SearchAdapter searchAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filesList = new ArrayList< FileEntity >();
        searchAdapter = new SearchAdapter( this, R.layout.search_list_view_item, filesList);
        setListAdapter( searchAdapter );
        getListView().setOnItemClickListener( this );
        searchLoader = ( SearchLoader ) getLoaderManager().initLoader( AppConstants.SEARCH_LOADER, null, this );
        parseIntentAndSearch(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntentAndSearch(getIntent());
    }

    /**
     *  Show dialog
     *  @param tag - dialog tag
     *  @param title - Dialog title
     *  @param message - Dialog message
     *  @param finishActivity - boolean - true - finish Activity after dialog close, false - continue
     */
    public void showDialogFragment( String tag, String title, String message, boolean finishActivity ){
        DialogFragment dialogFragment = FMDialog.getInstance(title, message, finishActivity);
        dialogFragment.show(getFragmentManager(), tag);
    }

    /**
     *  Parseing intent and if it has search action start search files
     *  @param intent - intent
     */
    private void parseIntentAndSearch( Intent intent ){
        if ( intent.ACTION_SEARCH.equals( intent.getAction() ) ){

            String arg = intent.getStringExtra( SearchManager.QUERY );

            SharedPreferences prefs = getSharedPreferences( AppConstants.PREFS, Activity.MODE_PRIVATE );
            String searchColumn = prefs.getString( AppConstants.SEARCH_COLUMN, DAOConstants.COLUMN_FILE_NAME );
            String currentRoot =  prefs.getString( AppConstants.CURRENT_ROOT, AppConstants.EXTERNAL_STORAGE_PATH );

            if ( DAOConstants.COLUMN_MODIFY_DATE.equals( searchColumn) && !AppUtils.isDate( arg ) ){
                showDialogFragment( AppConstants.ERROR_DIALOG_TAG,getString( R.string.dialog_title_error ),
                        getString( R.string.dialog_message_error_date), true );
                return;
            }
            String where = searchColumn+ "=?";
            if ( DAOConstants.COLUMN_FILE_NAME.equals( searchColumn ) ){
                where = searchColumn+ " LIKE \""+arg+"%\" ";
                arg = null;
            }
            if ( DAOConstants.COLUMN_MODIFY_DATE.equals( searchColumn ) ){
                where = searchColumn+ " LIKE \"%"+arg+"%\" ";
                arg = null;
            }
            where = DAOConstants.COLUMN_DIRECTORY +" LIKE \""+currentRoot+"%\" and "+ where;
            searchLoader.setWhere( where );
            searchLoader.setArg( arg );
            searchLoader.forceLoad();
        }
    }

    @Override
    public Loader< ArrayList<FileEntity> > onCreateLoader(int id, Bundle args) {
        return new SearchLoader( this );
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<FileEntity>> loader, ArrayList<FileEntity> fileEntities) {
        if ( fileEntities == null || fileEntities.isEmpty() ){
            Toast.makeText(this,getString( R.string.dialog_message_not_found),Toast.LENGTH_LONG).show();
            finish();
        }
        filesList.clear();
        for( FileEntity fileEntity: fileEntities ){
            filesList.add( fileEntity );
        }
        searchAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<FileEntity>> loader) {}

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileEntity fileEntity = searchAdapter.getItem( position );
        Intent intentResponse = new Intent();
        intentResponse.setAction( AppConstants.MAIN_ACTIVITY_RECEIVER);
        intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
        intentResponse.putExtra( AppConstants.RECEIVER_RESULT, AppConstants.SELECT_SEARCHED_ITEM);
        intentResponse.putExtra( AppConstants.RECEIVER_DESC, fileEntity.getPath().replace( fileEntity.getName(),"" ) );
        sendBroadcast( intentResponse );
        finish();
    }
}
