package cc.mudev.bca_android.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;

/* I was too lazy to type all of those codes on main code. */
public class AlertDialogGenerator {
    public static void gen(Context context, String title, String body) {
        runAlertDialogInHandler(
                context, title, body, false,
                "확인", null, (DialogInterface dialog, int i) -> {},
                null, null, null,
                null, null, null
        );
    }

    public static void gen(
            Context context,
            String title, String body,
            boolean swapPositiveNegativeButtonOrder,
            String positiveButtonTitle, DialogInterface.OnClickListener onPositiveButtonClicked
    ) {
        runAlertDialogInHandler(
                context,
                title, body,
                swapPositiveNegativeButtonOrder,
                positiveButtonTitle, null, onPositiveButtonClicked,
                null, null, null,
                null, null, null
        );
    }

    public static void gen(
            Context context,
            String title, String body,
            boolean swapPositiveNegativeButtonOrder,
            String positiveButtonTitle, DialogInterface.OnClickListener onPositiveButtonClicked,
            String negativeButtonTitle, DialogInterface.OnClickListener onNegativeButtonClicked
    ) {
        runAlertDialogInHandler(
                context,
                title, body,
                swapPositiveNegativeButtonOrder,
                positiveButtonTitle, null, onPositiveButtonClicked,
                negativeButtonTitle, null, onNegativeButtonClicked,
                null, null, null
        );
    }

    public static void gen(
            Context context,
            String title, String body,
            boolean swapPositiveNegativeButtonOrder,
            String positiveButtonTitle, DialogInterface.OnClickListener onPositiveButtonClicked,
            String negativeButtonTitle, DialogInterface.OnClickListener onNegativeButtonClicked,
            String neutralButtonTitle, DialogInterface.OnClickListener onNeutralButtonClicked
    ) {
        runAlertDialogInHandler(
                context,
                title, body,
                swapPositiveNegativeButtonOrder,
                positiveButtonTitle, null, onPositiveButtonClicked,
                negativeButtonTitle, null, onNegativeButtonClicked,
                neutralButtonTitle, null, onNeutralButtonClicked
        );
    }

    public static void gen(
            Context context,
            String title, String body,
            boolean swapPositiveNegativeButtonOrder,
            String positiveButtonTitle, Drawable positiveButtonIcon, DialogInterface.OnClickListener onPositiveButtonClicked,
            String negativeButtonTitle, Drawable negativeButtonIcon, DialogInterface.OnClickListener onNegativeButtonClicked,
            String neutralButtonTitle, Drawable neutralButtonIcon, DialogInterface.OnClickListener onNeutralButtonClicked
    ) {
        runAlertDialogInHandler(
                context,
                title, body,
                swapPositiveNegativeButtonOrder,
                positiveButtonTitle, positiveButtonIcon, onPositiveButtonClicked,
                negativeButtonTitle, negativeButtonIcon, onNegativeButtonClicked,
                neutralButtonTitle, neutralButtonIcon, onNeutralButtonClicked
        );
    }

    public static void runAlertDialogInHandler(
            Context context,
            String title, String body,
            boolean swapPositiveNegativeButtonOrder,
            String positiveButtonTitle, Drawable positiveButtonIcon, DialogInterface.OnClickListener onPositiveButtonClicked,
            String negativeButtonTitle, Drawable negativeButtonIcon, DialogInterface.OnClickListener onNegativeButtonClicked,
            String neutralButtonTitle, Drawable neutralButtonIcon, DialogInterface.OnClickListener onNeutralButtonClicked
    ) {
        (new Handler(Looper.getMainLooper())).postDelayed(() -> generateAlertDialog(
                context,
                title, body,
                swapPositiveNegativeButtonOrder,
                positiveButtonTitle, positiveButtonIcon, onPositiveButtonClicked,
                negativeButtonTitle, negativeButtonIcon, onNegativeButtonClicked,
                neutralButtonTitle, neutralButtonIcon, onNeutralButtonClicked
        ).create().show(), 0);
    }

    public static AlertDialog.Builder generateAlertDialog(
            Context context,
            String title, String body,
            boolean swapPositiveNegativeButtonOrder,
            String positiveButtonTitle, Drawable positiveButtonIcon, DialogInterface.OnClickListener onPositiveButtonClicked,
            String negativeButtonTitle, Drawable negativeButtonIcon, DialogInterface.OnClickListener onNegativeButtonClicked,
            String neutralButtonTitle, Drawable neutralButtonIcon, DialogInterface.OnClickListener onNeutralButtonClicked
    ) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        if (title != null && !"".equals(title))
            alertDialog = alertDialog.setTitle(title);

        if (body != null && !"".equals(body))
            alertDialog = alertDialog.setTitle(body);

        if (positiveButtonTitle != null && !"".equals(positiveButtonTitle) && onPositiveButtonClicked != null)
            alertDialog = (swapPositiveNegativeButtonOrder)
                    ? alertDialog.setNegativeButton(positiveButtonTitle, onPositiveButtonClicked)
                    : alertDialog.setPositiveButton(positiveButtonTitle, onPositiveButtonClicked);
        if (positiveButtonIcon != null)
            alertDialog = alertDialog.setPositiveButtonIcon(positiveButtonIcon);

        if (negativeButtonTitle != null && !"".equals(negativeButtonTitle) && onPositiveButtonClicked != null)
            alertDialog = (swapPositiveNegativeButtonOrder)
                    ? alertDialog.setPositiveButton(negativeButtonTitle, onNegativeButtonClicked)
                    : alertDialog.setNegativeButton(negativeButtonTitle, onNegativeButtonClicked);
        if (negativeButtonIcon != null)
            alertDialog = alertDialog.setNegativeButtonIcon(negativeButtonIcon);

        if (neutralButtonTitle != null && !"".equals(neutralButtonTitle) && onNeutralButtonClicked != null)
            alertDialog = alertDialog.setNeutralButton(neutralButtonTitle, onNeutralButtonClicked);
        if (negativeButtonIcon != null)
            alertDialog = alertDialog.setNeutralButtonIcon(neutralButtonIcon);

        return alertDialog;
    }
}
