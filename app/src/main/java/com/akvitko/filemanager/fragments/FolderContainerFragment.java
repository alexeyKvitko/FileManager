package com.akvitko.filemanager.fragments;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.akvitko.filemanager.AppConstants;
import com.akvitko.filemanager.AppUtils;
import com.akvitko.filemanager.MainActivity;
import com.akvitko.filemanager.R;
import com.akvitko.filemanager.adapters.FilesAdapter;
import com.akvitko.filemanager.loader.FilesLoader;
import com.akvitko.filemanager.model.FileEntity;

import java.util.ArrayList;

/**
 * Created by alexey on 27.05.15.
 */
public class FolderContainerFragment extends ListFragment implements AdapterView.OnItemClickListener,
                                                                     AdapterView.OnItemLongClickListener,
                                                                     LoaderManager.LoaderCallbacks< ArrayList<FileEntity> >  {

    private ArrayList< FileEntity > filesList;
    private FilesAdapter filesAdapter;
    private FilesLoader filesLoader;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        filesList = new ArrayList< FileEntity >();
        filesAdapter = new FilesAdapter( getActivity(), R.layout.fm_list_view_item, filesList);
        setListAdapter( filesAdapter );
        getListView().setDivider(null);
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener( this );
        filesLoader = ( FilesLoader ) getActivity().getLoaderManager().initLoader( AppConstants.FILE_LOADER, null, this );
    }

    /**
     *  Refresh container-ListView which contains files in current dir
     */
    public void refreshContainer(){
        MainActivity activity = ( (MainActivity) getActivity() );
        String path = activity.getHistory().get( activity.getCurrentPos() );
        boolean isRoot = false;
        if( path == null)  {
            path = activity.getCurrentRoot();
            isRoot = true;
        }
        activity.updatePath();
        filesLoader.setPath( path );
        filesLoader.setRoot( isRoot );
        filesLoader.forceLoad();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FileEntity fileEntity = filesAdapter.getItem( position );
        if ( !fileEntity.isFolder() ){
            return;
        }
        MainActivity activity = ( ( MainActivity ) getActivity() );

        if ( AppConstants.PREV_DIR.equals( fileEntity.getName() ) ){
            activity.onBackPressed();
            return;
        }

        int nextPos = activity.getCurrentPos()+1;
        activity.setCurrentPos( nextPos );
        activity.getHistory().put( nextPos, fileEntity.getPath() );
        refreshContainer();
    }

    @Override
    public Loader<ArrayList<FileEntity>> onCreateLoader(int id, Bundle args) {
        return new FilesLoader( getActivity() );
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<FileEntity>> loader, ArrayList<FileEntity> fileEntities ) {
        if ( fileEntities == null ){
            showErrorEndFinishActivity();
        }
        filesList.clear();
        for( FileEntity fileEntity: fileEntities ){
            filesList.add( fileEntity );
        }
        filesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<FileEntity>> loader) {}

    private void showErrorEndFinishActivity(){
        Toast.makeText( getActivity(),getString( R.string.error_read_sdcard),
                Toast.LENGTH_LONG ).show();
        getActivity().finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        FileEntity fileEntity = filesAdapter.getItem( position );
        StringBuilder fileInfo = new StringBuilder();
        fileInfo.append( getString( R.string.path )+" " ).append( fileEntity.getPath() ).append(", \n")
                .append( getString( R.string.file_size )+" " ).append( fileEntity.getSize() ).append(", \n")
                .append( getString( R.string.mod_date )+" " ).append( AppUtils.formatDate(fileEntity.getModifyDate() ) )
                .append(" \n");
        (( MainActivity ) getActivity()).showDialogFragment( AppConstants.FILE_INFO_DIALOG_TAG,
                                         getString( R.string.dialog_title_file_info),
                                         fileInfo.toString(), false);
        return true;
    }
}
