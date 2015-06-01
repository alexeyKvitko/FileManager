package com.akvitko.filemanager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akvitko.filemanager.AppUtils;
import com.akvitko.filemanager.R;
import com.akvitko.filemanager.model.FileEntity;

import java.util.List;

/**
 * Created by alexey on 31.05.15.
 */
public class SearchAdapter  extends ArrayAdapter<FileEntity> {

    private int res;
    private Context context;

    public SearchAdapter(Context context, int resource, List<FileEntity> files) {
        super(context, resource, files);
        this.context = context;
        this.res = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout searchItemView;

        FileEntity fileEntity = getItem( position );
        if ( convertView == null ){
            searchItemView = new LinearLayout( getContext() );
            LayoutInflater layoutInflater = ( LayoutInflater ) getContext()
                    .getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            layoutInflater.inflate( res, searchItemView, true );
        } else {
            searchItemView = ( LinearLayout ) convertView;
        }

        TextView pathView = ( TextView ) searchItemView.findViewById( R.id.searchPath );
        TextView fileView = ( TextView ) searchItemView.findViewById( R.id.searchFile );
        TextView sizeView = ( TextView ) searchItemView.findViewById( R.id.searchSize );
        TextView dateView = ( TextView ) searchItemView.findViewById( R.id.searchDate );

        pathView.setText( context.getString( R.string.path )+" "+
                                    fileEntity.getPath().replace( fileEntity.getName(),"") );
        fileView.setText( context.getString( R.string.file_name)+" "+
                                    fileEntity.getName() );
        sizeView.setText( context.getString( R.string.file_size )+" "+
                                    Long.toString( fileEntity.getSize() ) );
        dateView.setText( context.getString( R.string.mod_date )+" "+
                                    AppUtils.formatDate( fileEntity.getModifyDate() ) );

        return  searchItemView;
    }
}
