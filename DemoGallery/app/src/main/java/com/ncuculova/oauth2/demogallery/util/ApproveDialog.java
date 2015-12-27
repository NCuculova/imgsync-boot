package com.ncuculova.oauth2.demogallery.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.ncuculova.oauth2.demogallery.R;


public class ApproveDialog extends DialogFragment {

    public DialogInterface.OnClickListener mOnClickListener;

    public void setOnDialogClickListener(DialogInterface.OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.alert)
                .setMessage(getString(R.string.alert_message))
                .setPositiveButton(R.string.action_continue, mOnClickListener)
                .setNegativeButton(R.string.action_cancel, mOnClickListener);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
