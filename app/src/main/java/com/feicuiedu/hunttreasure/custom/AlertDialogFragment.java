package com.feicuiedu.hunttreasure.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.feicuiedu.hunttreasure.R;

/**
 * Created by Administrator on 2017/1/3 0003.
 */

public class AlertDialogFragment extends DialogFragment {

    private static final String KEY_TITLE = "key_title";
    private static final String KEY_MESSAGE = "key_message";

    public static AlertDialogFragment getInstances(String title, String message){
        AlertDialogFragment dialogFragment=new AlertDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_MESSAGE,message);
        //fragment获取数据的方法
        dialogFragment.setArguments(bundle);
        return dialogFragment;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString(KEY_TITLE);
        String message = getArguments().getString(KEY_MESSAGE);

        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
    }

}
