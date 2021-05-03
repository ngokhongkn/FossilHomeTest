package com.dunghx.fossil;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;

public class DialogManager {
    public static final int MIN_MAX = 60;
    public static final int SEC_MAX = 59;
    public static final int MINIMUM_VALUE = 0;

    public interface SetTimerListener {
        void onFinish(int min, int sec);
    }

    public static void createDialogTimer(Context context, SetTimerListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_set_timer);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView btnSet = (TextView) dialog.findViewById(R.id.dialog_button_set);
        NumberPicker min = dialog.findViewById(R.id.dialog_min);
        NumberPicker sec = dialog.findViewById(R.id.dialog_sec);

        min.setMaxValue(MIN_MAX);
        min.setMinValue(MINIMUM_VALUE);
        sec.setMinValue(MINIMUM_VALUE);
        sec.setMaxValue(SEC_MAX);
        dialog.setCancelable(false);
        dialog.show();
        btnSet.setOnClickListener(v -> {
            listener.onFinish(min.getValue(), sec.getValue());
            dialog.cancel();
        });
    }

    public static void createSimpleDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder((new ContextThemeWrapper(context, R.style.DialogSimpleMessage))).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }
}
