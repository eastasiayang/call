package com.yang.mydialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yang.basic.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRadioDialog {

    private static final String TAG = "MyRadioDialog";
    private ResultHandler handler;
    private Context context;
    private Dialog myDialog;

    public TextView title;
    public ListView listview;
    public Button button_cancel;

    private String[] m_Items;
    private String m_SelectItem;


    public MyRadioDialog(Context context, ResultHandler resultHandler,
                         String[] items, String checkedItem) {
        this.context = context;
        this.handler = resultHandler;
        m_Items = items;
        m_SelectItem = checkedItem;
        initDialog();
        initView();
        initData();
    }

    private void initData() {

        List<Map<String, Object>> slist = new ArrayList<Map<String, Object>>();

        if (m_Items.length >= 7) {
            ViewGroup.LayoutParams params = listview.getLayoutParams();
            params.height = 800;
            listview.setLayoutParams(params);
        }
        for (int i = 0; i < m_Items.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text", m_Items[i]);
            if (m_SelectItem.equals(m_Items[i])) {
                map.put("radio", true);
            } else {
                map.put("radio", false);
            }
            slist.add(map);
        }
        SimpleAdapter simple = new SimpleAdapter(context, slist,
                R.layout.item_radio, new String[]{"text", "radio"}, new int[]{
                R.id.textView, R.id.radioButton}) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = View.inflate(context, R.layout.item_radio, null);
                RadioButton radio = (RadioButton) convertView.findViewById(R.id.radioButton);
                radio.setClickable(false);
                return super.getView(position, convertView, parent);
            }
        };
        listview.setAdapter(simple);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                LogUtils.d(TAG, "position = " + position);
                TextView c = (TextView) view.findViewById(R.id.textView);
                String playerChanged = c.getText().toString();
                handler.handle(playerChanged);
                myDialog.dismiss();
            }
        });
    }


    private void initDialog() {
        if (myDialog == null) {
            myDialog = new Dialog(context, R.style.MyDialog);
            myDialog.setCancelable(false);
            myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            myDialog.setContentView(R.layout.dialog_radio);
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
        title = (TextView) myDialog.findViewById(R.id.tv_dlg_single_title);
        listview = (ListView)myDialog.findViewById(R.id.listview_dialog_radio);
        button_cancel = (Button) myDialog.findViewById(R.id.btn_dlg_single_cancel);
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
        void handle(String str);
    }
}
