package com.example.heizepalvin.streetlive.mainFragment.LiveFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.heizepalvin.streetlive.R;

import java.util.ArrayList;

public class ChattingAdapter extends BaseAdapter {

    private ArrayList<ChattingItem> items;
    private LayoutInflater inflater;
    private int layout;

    public ChattingAdapter(Context context, int layout, ArrayList<ChattingItem> items){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.layout = layout;
    }

    private class ViewHolder{
        private TextView nicknameView;
        private TextView chattingText;
    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(layout,parent,false);
            holder = new ViewHolder();
            holder.nicknameView = convertView.findViewById(R.id.ChatItemNickname);
            holder.chattingText = convertView.findViewById(R.id.ChatItemText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        holder.nicknameView.setText(items.get(position).getNickname());
        holder.chattingText.setText(items.get(position).getChattingData());

        return convertView;
    }
}
