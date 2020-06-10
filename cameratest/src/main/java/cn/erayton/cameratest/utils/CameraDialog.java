package cn.erayton.cameratest.utils;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public abstract class CameraDialog extends DialogFragment implements DialogInterface.OnClickListener {
    abstract String getTitle() ;
    abstract String getMessage() ;
    abstract String getOKBottonMsg() ;
    abstract String getNoBottonMsg() ;

    abstract void onButtonClick(int which) ;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()) ;
        builder.setTitle(getTitle()) ;
        builder.setMessage(getMessage()) ;
        builder.setCancelable(false) ;
        if (getOKBottonMsg() != null){
            builder.setPositiveButton(getOKBottonMsg(), this) ;
        }
        if (getNoBottonMsg() != null){
            builder.setNegativeButton(getNoBottonMsg(), this) ;
        }
//        return super.onCreateDialog(savedInstanceState);
        return builder.create() ;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        onButtonClick(which);
    }
}
