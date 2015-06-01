package com.akvitko.filemanager.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akvitko.filemanager.AppConstants;
import com.akvitko.filemanager.R;
import com.akvitko.filemanager.model.FileEntity;

import java.util.List;

/**
 * Created by alexey on 27.05.15.
 */
public class FilesAdapter extends ArrayAdapter<FileEntity> {

    private int res;
    private Context context;

    public FilesAdapter(Context context, int resource, List<FileEntity> files) {
        super(context, resource, files);
        this.context = context;
        this.res = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout fileView;

        FileEntity fileEntity = getItem( position );
        if ( convertView == null ){
            fileView = new LinearLayout( getContext() );
            LayoutInflater layoutInflater = ( LayoutInflater ) getContext()
                                                .getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            layoutInflater.inflate( res, fileView, true );
        } else {
            fileView = ( LinearLayout ) convertView;
        }

        ImageView imageView = ( ImageView ) fileView.findViewById( R.id.fileImage );
        TextView textView = ( TextView ) fileView.findViewById( R.id.fileName );
        int drawableId = R.drawable.file;
        if ( AppConstants.PREV_DIR.equals( fileEntity.getName() ) ){
                drawableId = R.drawable.back;
            } else if ( fileEntity.isFolder() ){
                drawableId = R.drawable.folder;
               } else if ( !fileEntity.isHasChild() ){
                        drawableId = R.drawable.empty;
                    }
        imageView.setBackgroundResource( drawableId );

        textView.setText( fileEntity.getName() );
        textView.setTypeface(null, Typeface.NORMAL);
        textView.setGravity( Gravity.LEFT );
        if ( fileEntity.isFolder() ){
            textView.setTypeface(null, Typeface.BOLD);
        }
        if ( !fileEntity.isHasChild() ){
            textView.setGravity( Gravity.CENTER_HORIZONTAL );
        }
        return  fileView;
    }
}
