package com.example.groupsend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by yx on 2017/3/22.
 */
public class HintDialogFragmentContracts extends DialogFragment{
    public static final String TITLE="title";
    public static final String MESSAGE="message";
    public static final String REQUEST_CODE="request_code";

    public static HintDialogFragmentContracts newInstance(int title,int message,int requestCode) {

        Bundle args = new Bundle();
        args.putInt(TITLE,title);
        args.putInt(MESSAGE,message);
        args.putInt(REQUEST_CODE,requestCode);
        HintDialogFragmentContracts fragment = new HintDialogFragmentContracts();
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
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((DialogFragmentCallbackCONTRACTS)getActivity()).doPositiveContractsClick(requestCode);
                    }
                })
                .create();
    }

    public interface DialogFragmentCallbackCONTRACTS {

        void doPositiveContractsClick(int requestCode);

//        void doNegativeClick(int requestCode);
    }
}
