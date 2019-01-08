package com.example.dell.link_game;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rank extends AppCompatActivity {
    public String userName;
    public int record;
    public String level;
    SQLiteDatabase sqLiteDatabase;
    Bundle bundle;
    SqlHelper sqlHelper;
    private List<gameUser> users = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sqlHelper = new SqlHelper(this,"gameuser.db",null,2);
        setContentView(R.layout.ranklist);
        bundle = getIntent().getExtras();
        if (bundle.getBoolean("write")) {
            writedata();
        }
        users = readdata();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        RankAdapter adapter = new RankAdapter(users);
        recyclerView.setAdapter(adapter);
    }

    public void writedata() {
        sqLiteDatabase = sqlHelper.getWritableDatabase();
        userName = bundle.getString("user");
        level = bundle.getString("level");
        record = bundle.getInt("record");
        ContentValues values = new ContentValues();
        values.put("name",userName);
        values.put("record",record);
        values.put("level",level);
        sqLiteDatabase.insert("gameuser",null,values);
    }

    public List<gameUser> readdata() {
        sqLiteDatabase = sqlHelper.getReadableDatabase();
        List<gameUser> userList = new ArrayList<>();
        if (isTableExist("gameuser")) {
            Cursor cursor = sqLiteDatabase.rawQuery("select * from gameuser order by record", null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    gameUser user1 = new gameUser();
                    user1.setUser(cursor.getString(cursor.getColumnIndex("name")));
                    user1.setRecord(cursor.getInt(cursor.getColumnIndex("record")));
                    user1.setLevel(cursor.getString(cursor.getColumnIndex("level")));
                    userList.add(user1);
                }
                cursor.close();
                sqLiteDatabase.close();
            }
            cursor.close();
            sqLiteDatabase.close();
        }
        return userList;
    }

    public boolean isTableExist(String table) {
        Cursor cursor = sqLiteDatabase.rawQuery("select count(*) from sqlite_master where type='table' and name='" + table + "'",null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    cursor.close();
                    return true;
                }

            }
        }
        else {
            cursor.close();
        }
        return false;
    }
}
