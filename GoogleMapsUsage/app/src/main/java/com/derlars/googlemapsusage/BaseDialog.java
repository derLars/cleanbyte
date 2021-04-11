package com.derlars.whosin.Templates.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.derlars.whosin.Exceptions.NoContextException;
import com.derlars.whosin.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;

public abstract class BaseDialog extends AppCompatDialogFragment {
    public final static String TAG = "DEBUGPRINT";

    public interface DoneBtn {
        void onDoneBtnClick();
    }

    public interface CancelBtn {
        void onCancelBtnClick();
    }

    FloatingActionButton doneBtn;
    FloatingActionButton cancelBtn;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(setLayout(), null);

        try {
            doneBtn = view.findViewById(R.id.done_btn);
            doneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDoneBtnClick();
                }
            });
        }catch (Exception ex) {}
        try {
            cancelBtn = view.findViewById(R.id.cancel_btn);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCancelBtnClick();
                }
            });
        }catch (Exception ex) {}

        builder.setView(view);

        try {
            onDialogCreated(view);
        } catch (NoContextException e) {
            Log.e(TAG,e.toString());
        }

        return builder.create();
    }

    protected abstract int setLayout();

    protected abstract void onDialogCreated(View view) throws NoContextException;

    protected void onDoneBtnClick() {}
    protected void onCancelBtnClick() {}
}
