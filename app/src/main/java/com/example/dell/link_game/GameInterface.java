package com.example.dell.link_game;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TimeUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GameInterface extends AppCompatActivity {
    private long startTime = 0, endTime = 0;
    private List<Item> items = new ArrayList<>();
    public static final int ROW = 9;
    public static final int COLUMN = 8;
    public static final int EASY = 1;
    public static final int MEDIUM = 2;
    public static final int DIFFICULT = 3;
    public int level = EASY;
    private ItemAdapter itemAdapter;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        items = loadImage(level); //载入图片内容
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, COLUMN)); //生成网格布局
        itemAdapter = new ItemAdapter(items, this);
        recyclerView.setAdapter(itemAdapter);
        itemAdapter.init();
        itemAdapter.chronometer = (Chronometer)findViewById(R.id.timer);
        itemAdapter.chronometer.start();
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_ranking:
                        Intent intent1 = new Intent(GameInterface.this,Rank.class);
                        startActivity(intent1);
                        return true;
                    case R.id.action_main:
                        Intent intent2 = new Intent(GameInterface.this, MainActivity.class);
                        startActivity(intent2);
                        return true;
                    case R.id.action_about:
                        Intent intent3 = new Intent(GameInterface.this,About.class);
                        startActivity(intent3);
                        return true;
                }
                return false;
            }
        });
    }

    private List<Item> loadImage(int level) {
        List<Item> itemList = new ArrayList<>();
        itemList.clear();
        int total = 10; //默认图片总数
        if (level == EASY) {
            total = 10;
        }
        else if (level == MEDIUM) {
            total = 15;
        }
        else if (level == DIFFICULT) {
            total = 25;
        }
        for (int i = 0; i < (COLUMN * ROW / 2); i++) { //同时添加两张图片
            Item item1 = new Item(i % total + 1, false, false);
            Item item2 = new Item(i % total + 1, false, false);
            itemList.add(item1);
            itemList.add(item2);
        }
        Collections.shuffle(itemList); //打乱图片顺序
        return itemList;
    }

    private Intent initIntent() { //初始化背景音乐服务
        Intent intent = new Intent(GameInterface.this, MusicService.class);
        return intent;
    }

    @Override
    public void onResume() {
        super.onResume();
        startService(initIntent()); //启动背景音乐服务
    }

    @Override
    public void onStop() {
        super.onStop();
        stopService(initIntent()); //关闭背景音乐服务
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shuffle:
                itemAdapter.shuffle();
                break;

            case R.id.easy:
                Toast.makeText(this, "难度为简单", Toast.LENGTH_SHORT).show();
                level = EASY;
                items = loadImage(level);
                itemAdapter.setLevel(EASY);
                itemAdapter.setmItemList(items);
                itemAdapter.notifyDataSetChanged();
                break;

            case R.id.medium:
                Toast.makeText(this, "难度为中等", Toast.LENGTH_SHORT).show();
                level = MEDIUM;
                items = loadImage(level);
                itemAdapter.setLevel(MEDIUM);
                itemAdapter.setmItemList(items);
                itemAdapter.notifyDataSetChanged();
                break;

            case R.id.diffcult:
                Toast.makeText(this, "难度为困难", Toast.LENGTH_SHORT).show();
                level = DIFFICULT;
                items = loadImage(level);
                itemAdapter.setLevel(DIFFICULT);
                itemAdapter.setmItemList(items);
                itemAdapter.notifyDataSetChanged();
                break;

            case R.id.newGame:
                Toast.makeText(this, "开始新游戏", Toast.LENGTH_SHORT).show();
                items = loadImage(level);
                itemAdapter.setmItemList(items);
                itemAdapter.notifyDataSetChanged();
                break;

            case R.id.music:
                item.setChecked(item.isChecked());
                if (!item.isChecked()) {
                    stopService(initIntent());
                    Toast.makeText(this, "关闭音乐", Toast.LENGTH_SHORT).show();
                }
                else {
                    startService(initIntent());
                    Toast.makeText(this, "开启音乐", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

