package com.akvitko.filemanager;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.akvitko.filemanager.dao.DAOConstants;
import com.akvitko.filemanager.fragments.FMDialog;
import com.akvitko.filemanager.fragments.FolderContainerFragment;
import com.akvitko.filemanager.services.FMIntentService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener{

    private int currentPos;
    private HashMap<Integer,String> history;
    private String currentRoot;

    private IntentFilter indexingFilter;
    private MainActivityReceiver activityReceiver;
    private Timer failTimer;

    private SearchView searchView;
    private Spinner spinner;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        indexingFilter = new IntentFilter( AppConstants.MAIN_ACTIVITY_RECEIVER );
        indexingFilter.addCategory( Intent.CATEGORY_DEFAULT );
        activityReceiver = new MainActivityReceiver();

        setPreferences( AppConstants.SEARCH_COLUMN,DAOConstants.COLUMN_FILE_NAME );

        currentPos = 0;
        history = new HashMap<Integer,String>();
        history.put( currentPos, null );
        initViews();
        updatePath();
        explictStartService();
    }

    /**
     *  Initializing widgets in Activity
     */
    private void initViews(){
        ArrayList< String > rootList = AppUtils.getRoots();

        if ( rootList == null || rootList.isEmpty() ){
            showDialogFragment( AppConstants.ERROR_DIALOG_TAG,getString( R.string.dialog_title_error ),
                                getString( R.string.dialog_message_error_sdcard), true );
            return;
        }

        spinner = ( Spinner ) findViewById( R.id.spinnerRootFolder );
        ArrayAdapter arrayAdapter = new ArrayAdapter( this,
                                                      R.layout.spinner_item,
                                                      rootList );
        spinner.setAdapter( arrayAdapter );

        spinner.setOnItemSelectedListener( this );

        SearchManager searchManager = ( SearchManager ) getSystemService(Context.SEARCH_SERVICE);

        SearchableInfo searchableInfo = searchManager.getSearchableInfo( getComponentName() );

        searchView = ( SearchView ) findViewById( R.id.searchView );
        searchView.setSearchableInfo( searchableInfo );
        searchView.setOnSearchClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent().putExtra( AppConstants.START_SEARCH, true );
            }
        });
    }

    @Override
    public void onBackPressed() {
        if ( history.get( currentPos ) == null ){
            finish();
            } else {
                currentPos--;
                refreshContainerFragment();
            }
    }

    /**
     *  Refresh container in fragment
     */
    private void refreshContainerFragment(){
        FolderContainerFragment fragment =
                (FolderContainerFragment) getFragmentManager().findFragmentById(R.id.fcFragment);
        fragment.refreshContainer();
    }

    /**
     *  Update label which display path at the top of Activity
     */
    public void updatePath(){
        TextView labelPath = ( TextView ) findViewById( R.id.labelPath );
        String label = AppUtils.getLabelPath( currentRoot,history.get( currentPos ) );
        labelPath.setText(label);
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
     *  Starting service to index files in storages
     */
    public void explictStartService(){
        ArrayList< String > rootList = AppUtils.getRoots();
        if ( rootList != null && !rootList.isEmpty() ){
            Intent intent = new Intent( this, FMIntentService.class );
            intent.putExtra(AppConstants.SERVICE_ARGS, rootList );
            startService(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(activityReceiver, indexingFilter );
    }

    @Override
    protected void onPause() {
        boolean startSearch = getIntent().getBooleanExtra(AppConstants.START_SEARCH, false);
        if ( !startSearch ){
            unregisterReceiver( activityReceiver );
        }
        if ( failTimer != null ){
            failTimer.cancel();
        }
        super.onPause();
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
    }

    public HashMap<Integer, String> getHistory() {
        return history;
    }

    public String getCurrentRoot() {
        return currentRoot;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ){
            case R.id.action_by_name : {
                item.setChecked( true );
                searchView.setInputType( InputType.TYPE_CLASS_TEXT );
                searchView.setQueryHint( getString( R.string.action_search_by_name ) );
                setPreferences( AppConstants.SEARCH_COLUMN,DAOConstants.COLUMN_FILE_NAME);
                break;
            }
            case R.id.action_by_size : {
                item.setChecked( true );
                searchView.setInputType( InputType.TYPE_CLASS_NUMBER );
                searchView.setQueryHint( getString( R.string.action_search_by_size ) );
                setPreferences( AppConstants.SEARCH_COLUMN,DAOConstants.COLUMN_SIZE );
                break;
            }
            case R.id.action_by_date : {
                item.setChecked( true );
                searchView.setInputType( InputType.TYPE_CLASS_DATETIME );
                searchView.setQueryHint( getString( R.string.action_search_by_date ) );
                setPreferences( AppConstants.SEARCH_COLUMN,DAOConstants.COLUMN_MODIFY_DATE );
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  Set Application Preferences
     *  @param key - key
     *  @param value - value
     */
    private void setPreferences( String key,String value ){
        SharedPreferences prefs = getSharedPreferences( AppConstants.PREFS, Activity.MODE_PRIVATE );
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString( key, value );
        editor.apply();
    }

    /**
    *  Redirect to folder which selected in Searchable Activity
    *  @param folder - folder to redirect
    */
    public void redirectToSelectedFolder( String folder ){
        String[] parced = folder.split( File.separator );
        String root = File.separator+parced[1]+File.separator+parced[2];
        history.clear();
        currentPos = 0;
        history.put( currentPos,null );
        for ( int i = 3; i< parced.length; i++ ){
            root = root + File.separator + parced[i];
            history.put( i-2, root );
            currentPos = i-2;
        }
        refreshContainerFragment();
        refreshContainerFragment();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        currentRoot = (String) parent.getAdapter().getItem(position);
        setPreferences( AppConstants.CURRENT_ROOT, currentRoot );
        currentPos = 0;
        history.clear();
        history.put( currentPos, null );
        refreshContainerFragment();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    private class MainActivityReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
           String result = intent.getStringExtra( AppConstants.RECEIVER_RESULT );
           String description = intent.getStringExtra( AppConstants.RECEIVER_DESC );
           switch ( result ){
               case AppConstants.SUCCESS: {
                   if ( failTimer != null ){
                       failTimer.cancel();
                       failTimer = null;
                   }
                   if ( description != null ){
                       Toast.makeText( context,
                               getString( R.string.indexing_success )+description,
                               Toast.LENGTH_SHORT ).show();
                   }
                   break;
               }
               case AppConstants.FAIL: {
                   showDialogFragment( AppConstants.ERROR_DIALOG_TAG,getString( R.string.dialog_title_error ),
                           getString( R.string.indexing_fail)+" "+description, false );
                   if ( failTimer == null ){
                       failTimer = new Timer( AppConstants.FAIL_TAILMER );
                       failTimer.scheduleAtFixedRate( new TimerTask() {
                           @Override
                           public void run() {
                               explictStartService();
                           } },0, 60000);
                   }
                  break;
               }
               case AppConstants.SELECT_SEARCHED_ITEM:{
                   getIntent().putExtra( AppConstants.START_SEARCH, false );
                   redirectToSelectedFolder(description);
                   searchView.setQuery(null, false);
                   searchView.clearFocus();
                   searchView.setIconified( true );
                   break;
               }
           }
        }
    }
}
