package com.yang.mydialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MyAlertDialog {

    private static final String TAG = "MyAlertDialog";
    private ResultHandler handler;
    private Context context;
    private Dialog myDialog;

    public TextView title;
    public Button button_cancel, button_OK;

    public MyAlertDialog(Context context, ResultHandler resultHandler) {
        this.context = context;
        this.handler = resultHandler;
        initDialog();
        initView();
        initData();
    }

    private void initData() {

    }


    private void initDialog() {
        if (myDialog == null) {
            myDialog = new Dialog(context, R.style.MyDialog);
            myDialog.setCancelable(false);
            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myDialog.setContentView(R.layout.dialog_alert);
            Window window = myDialog.getWindow();
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels * 95 / 100;
            window.setAttributes(lp);
        }
    }

    private void initView() {
        title = (TextView) myDialog.findViewById(R.id.tv_dlg_alert_title);
        button_OK = (Button) myDialog.findViewById(R.id.btn_dlg_alert_OK);
        button_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.handle(true);
                myDialog.dismiss();
            }
        });
        button_cancel = (Button) myDialog.findViewById(R.id.btn_dlg_alert_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
    }

    public void setTitle(String sTitle) {
        title.setText(sTitle);
    }

    public void show() {
        myDialog.show();
    }

    public interface ResultHandler {
        void handle(boolean bOK);
    }
}
