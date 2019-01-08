package com.example.dell.link_game;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.dell.link_game.GameInterface.COLUMN;
import static com.example.dell.link_game.GameInterface.ROW;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> mItemList;
    private Context context;
    public int map[][]=new int[ROW][COLUMN];
    public int firstposition = -1;
    private MediaPlayer sound;
    public Chronometer chronometer;
    int totalTime = 0;
    public String level = "EASY";

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemIamge;
        ImageView itemImage_selected;

        public ViewHolder(View view) {
            super(view);
            itemIamge = (ImageView) view.findViewById(R.id.image);
            itemImage_selected = (ImageView) view.findViewById(R.id.selected);

        }
    }
    public ItemAdapter(List<Item> itemList, Context context) {
        mItemList = itemList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        if (context == null) {
            context = parent.getContext();
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getAssets().open("Image/" + mItemList.get(position).getId() + ".png"));
            holder.itemIamge.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Item item = mItemList.get(position);
        if (item.isSelect()) {
            holder.itemImage_selected.setVisibility(View.VISIBLE);
        }
        else {
            holder.itemImage_selected.setVisibility(View.INVISIBLE);
        }

        if (item.isEliminated()) {
            holder.itemIamge.setVisibility(View.GONE);
            holder.itemImage_selected.setVisibility(View.GONE);
        }
        else {
            holder.itemIamge.setVisibility(View.VISIBLE);
        }


        holder.itemIamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playsound(R.raw.click);
                if (item.isEliminated()) {
                    return;
                }
                mItemList.get(position).setSelect(!mItemList.get(position).isSelect());
                notifyItemChanged(position);
                Log.d("position","position:" + position);

                if (firstposition != -1) {
                    eliminate(firstposition,position);
                }
                else {
                    firstposition = position;
                }
                notifyItemChanged(position);
                if (gameover()) {
                    chronometer.stop();
                    totalTime = Integer.parseInt(chronometer.getText().toString().split(":")[0]) * 60 + Integer.parseInt(chronometer.getText().toString().split(":")[1]);
                    final Bundle bundle = new Bundle();
                    final Intent intent = new Intent(context, Rank.class);
                    final EditText editText = new EditText(context);
                    new AlertDialog.Builder(context).setTitle("Congratulation!").setMessage("游戏结束啦！用时为" + totalTime + "秒！" + "请留下你的大名：")
                            .setView(editText).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bundle.putString("user",editText.getText().toString());
                            bundle.putString("level",level);
                            bundle.putInt("record",totalTime);
                            bundle.putBoolean("write",true);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bundle.putString("user","Anonymous");
                            bundle.putString("level",level);
                            bundle.putInt("record",totalTime);
                            bundle.putBoolean("write",true);
                            intent.putExtras(bundle);
                            context.startActivity(intent);
                        }
                    }).show();
                }
            }
        });
    }

    public void eliminate(int position_1, int position_2) {
        firstposition = -1;

        int firstRow = position_1 / COLUMN;
        int firstColumn = position_1 % COLUMN;

        int secondRow = position_2 / COLUMN;
        int secondColumn = position_2 % COLUMN;

        Pair<Integer,Integer> point1 = new Pair<>(firstRow,firstColumn);
        Pair<Integer,Integer> point2 = new Pair<>(secondRow,secondColumn);

        Log.d("first","first:" + firstRow + "," + firstColumn + "second:" + secondRow + "," + secondColumn);

        if (mItemList.get(position_1).getId() == mItemList.get(position_2).getId() && Util.linkable(map, firstRow, firstColumn, secondRow, secondColumn)) {
            playsound(R.raw.eliminate);
            mItemList.get(position_1).setEliminated(true);
            mItemList.get(position_2).setEliminated(true);
            map[firstRow][firstColumn] = 0;
            map[secondRow][secondColumn] = 0;
        }
        else {
            playsound(R.raw.wrong);
            mItemList.get(position_1).setSelect(false);
            mItemList.get(position_2).setSelect(false);
        }
        notifyItemChanged(position_1);
        notifyItemChanged(position_2);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public void init() {
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                map[i][j] = 1;
            }
        }
    }

    public void setmItemList(List<Item> itemList) {
        this.mItemList = itemList;

    }

    public boolean gameover() {
        boolean gameover = true;
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                if (map[i][j] == 1) {
                    return false;
                }
            }
        }
        return gameover;
    }

    public void shuffle() {
        if (firstposition != -1) {
            mItemList.get(firstposition).setSelect(false);
            firstposition = -1;
        }
        Collections.shuffle(mItemList);
        resetLinkGameMatrix();
        notifyDataSetChanged();

    }

    private void resetLinkGameMatrix() {
        int size = mItemList.size();
        for (int i = 0; i < size; i++) {
            int row = i / COLUMN;
            int column = i % COLUMN;
            if (mItemList.get(i).isEliminated()) {
                map[row][column] = 0;
            } else {
                map[row][column] = 1;
            }
        }
    }

    private void playsound(int id) {
        sound = MediaPlayer.create(context,id);
        sound.start();
        sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("status","播放完毕");
                mp.release();
            }
        });
    }

    public void setLevel(int level1) {
        if (level1 == 1) {
            level = "EASY";
        }
        else if (level1 == 2) {
            level = "MEDIUM";
        }
        else if (level1 == 3) {
            level = "DIFFICULT";
        }
    }
}
