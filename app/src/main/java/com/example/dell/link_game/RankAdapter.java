package com.example.dell.link_game;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {
    private Context context;
    private List<gameUser> rankList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView user;
        TextView record;
        TextView level;
        TextView id;

        public ViewHolder(View view) {
            super(view);
            user = (TextView) view.findViewById(R.id.user);
            record = (TextView) view.findViewById(R.id.record);
            level = (TextView) view.findViewById(R.id.level);
            id = (TextView) view.findViewById(R.id.id);
        }
    }

    public RankAdapter(List<gameUser> rankList1) {
        rankList = rankList1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        gameUser user1 = rankList.get(position);
        holder.user.setText(user1.getUser());
        holder.record.setText(user1.getRecord() + "s");
        holder.level.setText(user1.getLevel());
    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }
}
