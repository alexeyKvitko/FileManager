package com.akvitko.filemanager.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.akvitko.filemanager.R;

/**
 * Created by alexey on 28.05.15.
 */
public class FMDialog extends DialogFragment {

    private static final String TITLE = "dialog_title";
    private static final String MESSAGE = "dialog_message";
    private static final String ACTION = "dialog_action";


    /**
     *  Get instance of FragmentDialog
     *  @param title - Dialog title
     *  @param message - Dialog message
     *  @param finishActivity - boolean - true - finish Activity after dialog close, false - continue
     * @return instance of FMDialog
     */
    public static FMDialog getInstance( String title, String message, boolean finishActivity){

        FMDialog fmDialog = new FMDialog();
        Bundle args = new Bundle();
        args.putString( TITLE, title );
        args.putString( MESSAGE, message );
        args.putBoolean( ACTION, finishActivity );
        fmDialog.setArguments( args );

        return fmDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( getActivity() );
        alertDialog.setTitle( getArguments().getString( TITLE ) );
        alertDialog.setMessage( getArguments().getString( MESSAGE ) );
        final boolean finishActivity =  getArguments().getBoolean( ACTION );
        alertDialog.setPositiveButton( getString( R.string.btn_close ),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         if ( finishActivity ){
                             getActivity().finish();
                         }
                    }
                });
        return alertDialog.create();
    }
}
