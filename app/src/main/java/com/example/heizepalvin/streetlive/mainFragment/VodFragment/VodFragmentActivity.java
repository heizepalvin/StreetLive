package com.example.heizepalvin.streetlive.mainFragment.VodFragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.heizepalvin.streetlive.R;
import com.example.heizepalvin.streetlive.mainFragment.VodFragment.vodFragmentListItem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class VodFragmentActivity extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView vodList;
    public static ArrayList<vodFragmentListItem> vodListItems;
    private SwipeRefreshLayout vodListSwipe;
    private VodListAdapter VodListAdapter;


    public VodFragmentActivity(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout vodFragmentLayout = (LinearLayout) inflater.inflate(R.layout.main_fragment_vod,container,false);
        vodListItems = new ArrayList<>();
        vodList = vodFragmentLayout.findViewById(R.id.vodListview);
        vodListSwipe = vodFragmentLayout.findViewById(R.id.vodListSwipe);
        vodListSwipe.setOnRefreshListener(this);
        VodListAdapter = new VodListAdapter(getContext(),R.layout.vod_fragment_list_item,vodListItems);
        vodList.setAdapter(VodListAdapter);

        VODListGetToDB vodListGetToDB = new VODListGetToDB();
        vodListGetToDB.execute();

        vodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent vodRoomEnterIntent = new Intent(getContext(),VodRoomActivity.class);
                vodRoomEnterIntent.putExtra("url",vodListItems.get(position).getVodUrl());
                vodRoomEnterIntent.putExtra("title",vodListItems.get(position).getVodTitle());
                startActivity(vodRoomEnterIntent);
            }
        });


        return vodFragmentLayout;
//        return inflater.inflate(R.layout.main_fragment_vod,container,false);
    }



    //DB에서 VOD 목록 가져오는 AsyncTask
    private class VODListGetToDB extends AsyncTask<String,Void,String>{

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            VodListAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... strings) {

            Connection pgConnection;
            Statement pgStatement;
            ResultSet pgResult;

            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;

            try{
                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
                pgStatement = pgConnection.createStatement();
                sql = "select * from vod.list order by num desc;";

                pgResult = pgStatement.executeQuery(sql);
                while(pgResult.next()){
                    String title = pgResult.getString("title");
                    String nickname = pgResult.getString("nickname");
                    String url = pgResult.getString("url");
                    String thumnail = pgResult.getString("thumnail");

                    vodFragmentListItem item = new vodFragmentListItem(title,nickname,thumnail,url);
                    vodListItems.add(item);
                }
                pgStatement.close();
            }catch (Exception e){
                Log.e("LiveRoomListGetToDB",e.toString());
            }

            return null;
        }
    }

    private class VodListAdapter extends BaseAdapter{

        private ArrayList<vodFragmentListItem> items;
        private int layout;
        private LayoutInflater inflater;
        ViewHolder viewHolder;

        public VodListAdapter(Context context, int layout, ArrayList<vodFragmentListItem> items){
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.layout = layout;
            this.items = items;
        }
        class ViewHolder {
            ImageView vodListItemImg;
            TextView vodListItemTitle;
            TextView vodListItemID;

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

            if(convertView == null){
                convertView = inflater.inflate(layout,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.vodListItemImg = convertView.findViewById(R.id.vodListItemImg);
                viewHolder.vodListItemID = convertView.findViewById(R.id.vodListItemID);
                viewHolder.vodListItemTitle = convertView.findViewById(R.id.vodListItemTitle);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Glide.with(convertView.getContext()).load(items.get(position).getVodThumnail()).into(viewHolder.vodListItemImg);
            viewHolder.vodListItemTitle.setText(items.get(position).getVodTitle());
            viewHolder.vodListItemID.setText(items.get(position).getVodNickname());

            return convertView;
        }
    }

    @Override
    public void onRefresh() {
        vodListItems.clear();
        VODListGetToDB vodListGetToDB = new VODListGetToDB();
        vodListGetToDB.execute();
        vodListSwipe.setRefreshing(false);
    }

}
