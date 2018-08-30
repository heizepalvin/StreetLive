package com.example.heizepalvin.streetlive.mainFragment.LiveFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heizepalvin.streetlive.R;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LiveRoomActivity extends AppCompatActivity{

    @BindView(R.id.liveRoomPlayerView)
    PlayerView livePlayerView;
//    @BindView(R.id.liveRoomControlRoot)
//    LinearLayout liveControlRoot;
//    @BindView(R.id.liveRoomRoot)
//    FrameLayout liveRoomRoot;
    @BindView(R.id.liveRoomDebugText)
    TextView liveDebugText;
    @BindView(R.id.liveRoomChatInputText)
    EditText liveRoomChatInputText;
    @BindView(R.id.liveRoomChatSendBtn)
    Button liveRoomChatSendBtn;
    @BindView(R.id.liveRoomChatListView)
    ListView liveRoomChatListView;
    @BindView(R.id.liveRoomChatBtn)
    ImageButton liveRoomChatBtn;
    @BindView(R.id.liveRoomChatLayout)
    LinearLayout liveRoomChatLayout;


    //LiveFragmentActivity에서 넘겨받은 스트리밍 키
    private String streamingKey;
    private String streamingTitle;

    private DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
    private DefaultTrackSelector trackSelector;
    private SimpleExoPlayer livePlayer;
    private DebugTextViewHelper debugTextViewHelper;

    //Netty

    private Handler chatHandler;
    private java.nio.channels.SocketChannel socketChannel;
    private String chatMsg;
    private String chatData;

    private String userNickname;
    private ArrayList<ChattingItem> chatItems;
    private ChattingAdapter chattingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_room_activity);
        ButterKnife.bind(this);
        initPlayer();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        chatItems = new ArrayList<>();
        chattingAdapter = new ChattingAdapter(this,R.layout.chatting_item,chatItems);
        liveRoomChatListView.setAdapter(chattingAdapter);

        //채팅 버튼 이벤트 리스너
        liveRoomChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(liveRoomChatLayout.getVisibility() == View.VISIBLE){
                    liveRoomChatLayout.setVisibility(View.GONE);
                } else {
                    liveRoomChatLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        //유저 닉네임 가져오기
        SharedPreferences getLoginUserInfo = getSharedPreferences("userLoginInfo",MODE_PRIVATE);
        userNickname = getLoginUserInfo.getString("nickname","null");
        //채팅 서버 접속

        chatHandler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socketChannel = java.nio.channels.SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress("210.89.190.131",5001));
                    checkUpdate.start();
                    ChattingItem startChat = new ChattingItem("[StreetLive]","채팅서버에 연결되었습니다.");
                    chatItems.add(startChat);
                    chattingAdapter.notifyDataSetChanged();
                    //입장메시지 서버로 보내기
                    String returnMsg = "[StreetLive]/"+userNickname+"님이 입장하셨습니다.";
                    if(!TextUtils.isEmpty(returnMsg)){
                        new SendServerMsgTask().execute(returnMsg);
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



        liveRoomChatSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (liveRoomChatInputText.getText().toString().equals("") || liveRoomChatInputText.getText().toString().replace(" ","").equals("")){
                    Toast.makeText(LiveRoomActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        final String returnMsg = userNickname+"/"+liveRoomChatInputText.getText().toString();
                        if(!TextUtils.isEmpty(returnMsg)){
                            new SendmsgTask().execute(returnMsg);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }
    private class SendmsgTask extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            try{
                socketChannel.socket().getOutputStream().write(strings[0].getBytes("UTF-8"));

            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    ChattingItem userChatData = new ChattingItem(userNickname,liveRoomChatInputText.getText().toString());
                    chatItems.add(userChatData);
                    chattingAdapter.notifyDataSetChanged();
                    liveRoomChatInputText.setText("");
                }
            });
        }
    }
    private class SendServerMsgTask extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            try{
                socketChannel.socket().getOutputStream().write(strings[0].getBytes("UTF-8"));
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }
    }
    void receive(){
        while(true){
            try{
                ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                int readByteCount = socketChannel.read(byteBuffer);
                if(readByteCount == -1){
                    throw new IOException();
                }
                byteBuffer.flip();
                Charset charset = Charset.forName("UTF-8");
                chatData = charset.decode(byteBuffer).toString();
                chatHandler.post(showUpdate);
            }catch (IOException e){
                try{
                    socketChannel.close();
                    break;
                }catch (IOException ee){
                    ee.printStackTrace();
                }
            }
        }
    }

    private Thread checkUpdate = new Thread(){
        @Override
        public void run() {
            super.run();
            try{
                String line;
                receive();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private Runnable showUpdate = new Runnable() {
        @Override
        public void run() {
            String receive = chatData;
            Log.e("받는메시지",receive);
            String[] splitReceiveMsg = receive.split("/");
            String receiveNickname = splitReceiveMsg[0];
            String receiveMsg = splitReceiveMsg[1];
            ChattingItem receiveItem = new ChattingItem(receiveNickname,receiveMsg);
            chatItems.add(receiveItem);
            chattingAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String returnMsg = "[StreetLive]/"+userNickname+"님이 퇴장하셨습니다.";
        if(!TextUtils.isEmpty(returnMsg)){
            new SendServerMsgTask().execute(returnMsg);
        }
        livePlayer.stop();
        livePlayer.release();
        livePlayer = null;
        Log.e("쓰레드",checkUpdate.isInterrupted()+"");
        try {
            checkUpdate.sleep(1000);
            checkUpdate.interrupt();
            Log.e("쓰레드",checkUpdate.isInterrupted()+"");
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initPlayer(){
        if(livePlayer == null){
            Intent getStreamingKeyIntent = getIntent();
            streamingKey = getStreamingKeyIntent.getStringExtra("key");
            streamingTitle = getStreamingKeyIntent.getStringExtra("title");
            Log.e("streamingkey?",streamingKey);

            TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(defaultBandwidthMeter);
            int extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this,extensionRendererMode);
            trackSelector =  new DefaultTrackSelector(trackSelectionFactory);

            livePlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory,trackSelector);
            livePlayer.setPlayWhenReady(true);
            livePlayer.addAnalyticsListener(new EventLogger(trackSelector));
            livePlayer.addListener(new PlayerEventListener());
            livePlayerView.setPlayer(livePlayer);
            livePlayerView.hideController();
            debugTextViewHelper = new DebugTextViewHelper(livePlayer,liveDebugText);
            debugTextViewHelper.start();
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,Util.getUserAgent(this,"StreetLive"));
            Uri liveUri = Uri.parse("http://210.89.190.131/dash/streetlive-"+streamingKey+".mpd");
            DashMediaSource dashMediaSource = new DashMediaSource(liveUri,dataSourceFactory,
                    new DefaultDashChunkSource.Factory(dataSourceFactory),null,null);
            livePlayer.prepare(dashMediaSource);
//            Uri liveUri = Uri.parse("http://210.89.190.131/dash/streetlive-9f190q82vu.flv");
//            ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource(liveUri,dataSourceFactory,new DefaultExtractorsFactory(),null,null);
//            livePlayer.prepare(extractorMediaSource);
            redisPostDataSend redisPostDataSend = new redisPostDataSend();
            redisPostDataSend.execute("LiveRoomActivity","WatchLiveStreaming:"+streamingTitle);
        }

    }

    private class PlayerEventListener extends Player.DefaultEventListener{

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            super.onPlayerError(error);
            switch (error.type){
                case ExoPlaybackException.TYPE_SOURCE:
                    if(error.getSourceException().getMessage().equals("Response code: 404")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(LiveRoomActivity.this);
                        builder.setTitle("StreetLive");
                        builder.setMessage("방송이 종료되었습니다.");
                        builder.setNegativeButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                        builder.show();
                    }
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    Log.e("PlayerError","TYPE_RENDERER: "+ error.getRendererException().getMessage());
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                    Log.e("PlayerError","TYPE_UNEXPECTED: "+error.getUnexpectedException().getMessage());
                    break;
            }
        }
    }
    //okhttp3
    private final OkHttpClient okHttpClient = new OkHttpClient();
    public class redisPostDataSend extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            String viewSendMsg = strings[0];
            String actionSendMsg = strings[1];

            //POST 요청 시
            SharedPreferences userLoginInfo = getSharedPreferences("userLoginInfo",MODE_PRIVATE);
            String userNickname = userLoginInfo.getString("nickname","null");
            Log.e("닉네임가져오기",userNickname);
            RequestBody formbody = new FormBody.Builder()
                    .add("ID",userNickname)
                    .add("View",viewSendMsg)
                    .add("Action",actionSendMsg)
                    .build();

            Request request = new Request.Builder()
                    .url("http://106.10.43.183:80")
                    .post(formbody)
                    .build();

            okHttpClient.newCall(request).enqueue(redisResponseCallback);

            return null;
        }
    }
    private Callback redisResponseCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("MainActivityRedis","error Message : "+ e.getMessage());
        }


        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String responseData = response.body().string();
            Log.e("MainActivityRedis","responseData : " + responseData);
        }
    };
}

