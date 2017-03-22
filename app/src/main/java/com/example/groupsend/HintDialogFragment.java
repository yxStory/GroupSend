package com.example.groupsend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by yx on 2017/3/22.
 */
public class HintDialogFragment extends DialogFragment{
    public static final String TITLE="title";
    public static final String MESSAGE="message";
    public static final String REQUEST_CODE="request_code";

    public static HintDialogFragment newInstance(int title,int message,int requestCode) {
        
        Bundle args = new Bundle();
        args.putInt(TITLE,title);
        args.putInt(MESSAGE,message);
        args.putInt(REQUEST_CODE,requestCode);
        HintDialogFragment fragment = new HintDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title=getArguments().getInt(TITLE);
        int message=getArguments().getInt(MESSAGE);
        final int requestCode=getArguments().getInt(REQUEST_CODE);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        ((DialogFragmentCallback)getActivity()).doPositiveClick(requestCode);
                    }
                })
                .create();
    }

    public interface DialogFragmentCallback {

        void doPositiveClick(int requestCode);

//        void doNegativeClick(int requestCode);
    }
}
