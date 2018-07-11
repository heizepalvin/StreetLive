package com.example.heizepalvin.streetlive.mainFragment.LiveFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;


public class LiveFragmentActivity extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ListView liveList;
    private LiveListAdapter liveListAdapter;
    public static ArrayList<LiveFragmentListItem> liveListItems;
    private FloatingActionButton fab;
    private SwipeRefreshLayout liveListSwipe;

    public LiveFragmentActivity(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        LinearLayout liveFragmentLayout = (LinearLayout) inflater.inflate(R.layout.main_fragment_live,container,false);

        liveListItems = new ArrayList<>();
        liveList = liveFragmentLayout.findViewById(R.id.liveListview);
        liveListSwipe = liveFragmentLayout.findViewById(R.id.liveListSwipe);
        liveListSwipe.setOnRefreshListener(this);
        liveListAdapter = new LiveListAdapter(getContext(),R.layout.live_fragment_list_item,liveListItems);
        liveList.setAdapter(liveListAdapter);
        fab = liveFragmentLayout.findViewById(R.id.liveFragmentBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createLiveRoomIntent = new Intent(getContext(),CreateLiveRoomActivity.class);
                startActivity(createLiveRoomIntent);

            }
        });
        LiveRoomListGetToDB liveRoomListGetToDB = new LiveRoomListGetToDB();
        liveRoomListGetToDB.execute();

        //리스트뷰를 클릭했을때 메소드
        //리스트뷰 클릭하면 스트리밍키를 인텐트로 다음 액티비티에 넘겨줌
        liveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent liveRoomEnterIntent = new Intent(getContext(),LiveRoomActivity.class);
                liveRoomEnterIntent.putExtra("key",liveListItems.get(position).getLiveKey());
                startActivity(liveRoomEnterIntent);
            }
        });

        return liveFragmentLayout;
    }

    //DB에서 생방송 중인 목록 가져오는 AsyncTask
    private class LiveRoomListGetToDB extends AsyncTask<String,Void,String>{
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            liveListAdapter.notifyDataSetChanged();
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
                sql = "select * from live.room_info order by num desc;";

                pgResult = pgStatement.executeQuery(sql);
                while(pgResult.next()){
                    String title = pgResult.getString("title");
                    String nickname = pgResult.getString("nickname");
                    String key = pgResult.getString("key");
                    String thumnail = pgResult.getString("thumnail");

                    LiveFragmentListItem item = new LiveFragmentListItem(title,nickname,key,thumnail);
                    liveListItems.add(item);
                }
                pgStatement.close();
            }catch (Exception e){
                Log.e("LiveRoomListGetToDB",e.toString());
            }


            return null;
        }
    }
    // 새로고침 메소드
    @Override
    public void onRefresh() {
        liveListItems.clear();
        LiveRoomListGetToDB liveRoomListGetToDB = new LiveRoomListGetToDB();
        liveRoomListGetToDB.execute();
        liveListSwipe.setRefreshing(false);
    }
    //리스트뷰 어뎁터
    public class LiveListAdapter extends BaseAdapter{

        private ArrayList<LiveFragmentListItem> items;
        private int layout;
        private LayoutInflater inflater;
        ViewHolder viewholder;

        public LiveListAdapter(Context context, int layout, ArrayList<LiveFragmentListItem> items){
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.layout = layout;
            this.items = items;
        }

        class ViewHolder{
            ImageView liveListItemImg;
            TextView liveListItemTitle;
            TextView liveListItemID;
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

        @SuppressLint("CheckResult")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = inflater.inflate(layout,parent,false);
                viewholder = new ViewHolder();
                viewholder.liveListItemImg =  convertView.findViewById(R.id.liveListItemImg);
                viewholder.liveListItemID =  convertView.findViewById(R.id.liveListItemID);
                viewholder.liveListItemTitle = convertView.findViewById(R.id.liveListItemTitle);
                convertView.setTag(viewholder);
            } else {
                viewholder = (ViewHolder) convertView.getTag();
            }
            Glide.with(convertView.getContext()).load(items.get(position).getLiveThumnail()).into(viewholder.liveListItemImg);
            viewholder.liveListItemTitle.setText(items.get(position).getLiveTitle());
            viewholder.liveListItemID.setText(items.get(position).getLiveNickname());

            return convertView;
        }
    }


}
