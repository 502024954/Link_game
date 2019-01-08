package com.example.dell.link_game;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class SqlHelper extends SQLiteOpenHelper {
    private Context context;
    public SqlHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,name,factory,version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table gameuser (id integer primary key autoincrement," +
                "name text not null," +
                "record integer not null," +
                "level text not null" +
                ")");
        Toast.makeText(context,"表创建成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase,int i, int i1) {
        onCreate(sqLiteDatabase);
    }
}
