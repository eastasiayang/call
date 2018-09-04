package com.yang.mydialog;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import com.yang.basic.LunarCalendar;
import com.yang.basic.MyCalendarHelp;

public class MyDateTimeDialog {

    private static final String TAG = "MyDateTimeDialog";
    private ResultHandler handler;
    private Context context;

    private Dialog datePickerDialog;
    public ImageView left, right;
    public TextView title;
    public DatePickerView date_pv, hour_pv, minute_pv;
    public Button button_cancel, button_select;
    public Switch lunar, all_day;
    public LinearLayout ll_lunar;

    private ArrayList<String> date, hour, minute;
    private Calendar selectedCalender, startCalendar, endCalendar;
    private boolean bLunar = false;
    private MyCalendarHelp m_CalHelp;

    public MyDateTimeDialog(Context context, ResultHandler resultHandler, Calendar current) {

        selectedCalender = (Calendar) current.clone();
        this.context = context;
        this.handler = resultHandler;
        m_CalHelp = new MyCalendarHelp(context);
        initDateRange();
        initDialog();
        initView();
        InitDatePicker(true);
    }

    private void initDateRange() {
        startCalendar = (Calendar) selectedCalender.clone();
        endCalendar = (Calendar) selectedCalender.clone();
        startCalendar.add(Calendar.MONTH, -1);
        endCalendar.add(Calendar.MONTH, 1);
    }

    private void initDialog() {
        if (datePickerDialog == null) {
            datePickerDialog = new Dialog(context, R.style.MyDialog);
            datePickerDialog.setCancelable(false);
            datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            datePickerDialog.setContentView(R.layout.dialog_date_time);
            Window window = datePickerDialog.getWindow();
            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels * 95 / 100;
            window.setAttributes(lp);
        }
    }

    private void initView() {
        left = (ImageView) datePickerDialog.findViewById(R.id.imageview_date_time_left_arrow);
        right = (ImageView) datePickerDialog.findViewById(R.id.imageview_date_time_right_arrow);
        title = (TextView) datePickerDialog.findViewById(R.id.textview_date_time_title);
        date_pv = (DatePickerView) datePickerDialog.findViewById(R.id.date_pv);
        hour_pv = (DatePickerView) datePickerDialog.findViewById(R.id.hour_pv);
        minute_pv = (DatePickerView) datePickerDialog.findViewById(R.id.minute_pv);
        lunar = (Switch) datePickerDialog.findViewById(R.id.switch_date_time_lunar);
        all_day = (Switch) datePickerDialog.findViewById(R.id.switch_date_time_all_day);
        ll_lunar = (LinearLayout) datePickerDialog.findViewById(R.id.linearlayout_date_time_lunar);

        button_cancel = (Button) datePickerDialog.findViewById(R.id.button_dialog_date_time_cancel);
        button_select = (Button) datePickerDialog.findViewById(R.id.button_dialog_date_time_OK);

        setTitle(selectedCalender);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCalender.add(Calendar.YEAR, -1);
                startCalendar.add(Calendar.YEAR, -1);
                endCalendar.add(Calendar.YEAR, -1);
                setTitle(selectedCalender);
                InitDatePicker(false);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCalender.add(Calendar.YEAR, 1);
                startCalendar.add(Calendar.YEAR, 1);
                endCalendar.add(Calendar.YEAR, 1);
                setTitle(selectedCalender);
                InitDatePicker(false);
            }
        });

        lunar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bLunar = !bLunar;
                InitDatePicker(false);
            }
        });

        all_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((selectedCalender.get(Calendar.HOUR_OF_DAY) != 0)
                ||(selectedCalender.get(Calendar.MINUTE) != 0)) {
                    selectedCalender.set(Calendar.HOUR_OF_DAY, 0);
                    selectedCalender.set(Calendar.MINUTE, 0);
                }else
                {
                    selectedCalender.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY));
                    selectedCalender.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE));
                }
                InitDatePicker(true);
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.dismiss();
            }
        });

        button_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.handle(selectedCalender);
                datePickerDialog.dismiss();
            }
        });
        addListener();
    }

    public void setTitle(Calendar cal) {
        int iMonth = cal.get(Calendar.MONTH) + 1;
        String sTitle = cal.get(Calendar.YEAR) + context.getString(R.string.year) +
                iMonth + context.getString(R.string.month) +
                cal.get(Calendar.DAY_OF_MONTH) + context.getString(R.string.days) +
                m_CalHelp.getWeekString(cal);
        title.setText(sTitle);
    }

    private void InitDatePicker(boolean bTime) {
        int iIndex = 0;
        if (date == null) {
            date = new ArrayList<>();
        }
        date.clear();
        String sTemp;

        Calendar cal = (Calendar) startCalendar.clone();
        while (endCalendar.compareTo(cal) > 0) {
            int iMonth = cal.get(Calendar.MONTH) + 1;
            if (bLunar) {
                sTemp = new LunarCalendar().GetLunar(cal) + " " + m_CalHelp.getWeekString(cal);
            } else {
                sTemp = iMonth + context.getString(R.string.month) +
                        cal.get(Calendar.DAY_OF_MONTH) + context.getString(R.string.days) +
                        m_CalHelp.getWeekString(cal);
            }
            if (cal.before(selectedCalender)) {
                iIndex++;
            }
            cal.add(Calendar.DATE, 1);
            date.add(sTemp);
        }
        date_pv.setData(date);
        date_pv.setSelected(iIndex);

        if (bTime) {
            if (hour == null) {
                hour = new ArrayList<>();
            }
            if (minute == null) {
                minute = new ArrayList<>();
            }
            hour.clear();
            minute.clear();
            for (int i = 0; i <= 23; i++) {
                hour.add(formatTimeUnit(i));
            }
            hour_pv.setData(hour);
            hour_pv.setSelected(selectedCalender.get(Calendar.HOUR_OF_DAY));

            for (int i = 0; i <= 59; i++) {
                minute.add(formatTimeUnit(i));
            }
            minute_pv.setData(minute);
            minute_pv.setSelected(selectedCalender.get(Calendar.MINUTE));
        }
    }

    private String formatTimeUnit(int unit) {
        return unit < 10 ? "0" + String.valueOf(unit) : String.valueOf(unit);
    }

    private void addListener() {

        date_pv.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                int iWeek = m_CalHelp.getWeekDay(text.substring(text.length() - 3));
                int iYear = selectedCalender.get(Calendar.YEAR);
                if (bLunar) {
                    int iDate[] = new LunarCalendar().getSolar(
                            iYear,
                            text.substring(0, text.length() - 4));
                    selectedCalender.set(iDate[0], iDate[1] - 1, iDate[2]);
                      //for correct year
                    if (selectedCalender.get(Calendar.DAY_OF_WEEK) != iWeek) {
                        iDate = new LunarCalendar().getSolar(
                                iYear-1,
                                text.substring(0, text.length() - 4));
                        selectedCalender.set(iDate[0], iDate[1] - 1, iDate[2]);
                        if (selectedCalender.get(Calendar.DAY_OF_WEEK) != iWeek) {
                            iDate = new LunarCalendar().getSolar(
                                    iYear+1,
                                    text.substring(0, text.length() - 4));
                            selectedCalender.set(iDate[0], iDate[1] - 1, iDate[2]);
                        }
                    }
                } else {
                    String[] dateStr = text.replaceAll("[^0-9]", ",").split(",");
                    selectedCalender.set(Calendar.MONTH, Integer.parseInt(dateStr[0]) - 1);
                    selectedCalender.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateStr[1]));
                    //for correct year
                    if (selectedCalender.get(Calendar.DAY_OF_WEEK) != iWeek) {
                        selectedCalender.add(Calendar.YEAR, 1);
                        if (selectedCalender.get(Calendar.DAY_OF_WEEK) != iWeek) {
                            selectedCalender.add(Calendar.YEAR, -2);
                        }
                    }
                }
                initDateRange();
                InitDatePicker(false);
                setTitle(selectedCalender);
            }
        });

        hour_pv.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(text));
            }
        });

        minute_pv.setOnSelectListener(new DatePickerView.onSelectListener() {
            @Override
            public void onSelect(String text) {
                selectedCalender.set(Calendar.MINUTE, Integer.parseInt(text));
            }
        });
    }

    public void show() {
        datePickerDialog.show();
    }

    public void showDate(boolean show) {
        if (show) {
            date_pv.setVisibility(View.VISIBLE);
        } else {
            date_pv.setVisibility(View.GONE);
        }
    }

    public void showLunar(boolean show) {
        if (show) {
            ll_lunar.setVisibility(View.VISIBLE);
        } else {
            ll_lunar.setVisibility(View.GONE);
        }
    }

    public void setIsLoop(boolean isLoop) {
        this.date_pv.setIsLoop(isLoop);
        this.hour_pv.setIsLoop(isLoop);
        this.minute_pv.setIsLoop(isLoop);
    }

    public interface ResultHandler {
        void handle(Calendar cal);
    }
}
