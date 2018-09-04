package com.yang.basic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLHelp extends SQLiteOpenHelper{
    private final String TAG = MySQLHelp.class.getSimpleName();

    String[] CreateTableSQL;

    public MySQLHelp(Context context, String database_name, String[] CreateTableSQL) {
        super(context, database_name, null, 12);
        this.CreateTableSQL = CreateTableSQL;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        int i;
        for(i=0; i<CreateTableSQL.length; i++){
            db.execSQL(CreateTableSQL[i]);
        }
        String insertProducts = "insert into records values(?,?,?,?,?,?,?,?,?,?);";
        final String insertValue2[][] = new String[][]{
                new String[] { "0", "杨亚东生日", "", "2017_10_23 00:00", "2017_10_23 23:59", "0", "", "农历每年", "无", ""},
                new String[] { "1", "李春梅生日", "", "2017_04_19 00:00", "2017_04_19 23:59", "0", "", "农历每年", "无", ""},
                new String[] { "2", "招商银行还款", "", "2017_10_24 00:00", "2017_10_26 23:59", "0", "", "每月", "无", ""},
                new String[] { "3", "中信银行还款", "", "2017_10_22 00:00", "2017_10_24 23:59", "0", "", "每月", "无", ""},
                new String[] { "4", "花呗还款", "", "2017_11_08 00:00", "2017_11_10 23:59", "0", "", "每月", "无", ""},
                new String[] { "5", "京东白条还款", "", "2017_11_16 00:00", "2017_11_18 23:59", "0", "", "每月", "无", ""},
                new String[] { "6", "房贷还款", "", "2017_11_15 00:00", "2017_11_17 23:59", "0", "", "每月", "无", ""},
                new String[] { "7", "杨睿涵生日", "", "2017_07_08 00:00", "2017_07_08 23:59", "0", "", "农历每年", "无", ""},
                new String[] { "8", "建设银行还款", "", "2017_10_28 00:00", "2017_10_30 23:59", "0", "", "每月", "无", ""},
                new String[] { "9", "交房租", "", "2017_11_01 00:00", "2017_11_01 23:59", "0", "", "每月", "无", ""},
                new String[] { "10", "收房租", "", "2017_11_01 00:00", "2017_11_01 23:59", "0", "", "每月", "无", ""}
        };
        for (i = 0; i < 11; i++) {
            db.execSQL(insertProducts, insertValue2[i]);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }
}
