package com.example.heizepalvin.streetlive.mainFragment.VodFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.heizepalvin.streetlive.R;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class VodRoomActivity extends AppCompatActivity {

    @BindView(R.id.vodRoomPlayerView)
    PlayerView vodRoomPlayerView;

    //VodFragmentActivity에서 넘겨받은 VOD URL
    private String vodURL;
    private String vodTitle;
    private DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
    private DefaultTrackSelector trackSelector;
    private SimpleExoPlayer vodPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vod_room_activity);
        ButterKnife.bind(this);
        initPlayer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vodPlayer.stop();
        vodPlayer.release();
        vodPlayer = null;
    }

    private void initPlayer(){
        if(vodPlayer == null){
            Intent getVodUrlIntent = getIntent();
            vodURL = getVodUrlIntent.getStringExtra("url");
            vodTitle = getVodUrlIntent.getStringExtra("title");
            Log.e("vodURL?",vodURL);
            Log.e("vodTitle?",vodTitle);

            TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(defaultBandwidthMeter);
            int extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this,extensionRendererMode);
            trackSelector = new DefaultTrackSelector(trackSelectionFactory);

            vodPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory,trackSelector);
            vodPlayer.setPlayWhenReady(true);
            vodPlayer.addAnalyticsListener(new EventLogger(trackSelector));
//            vodPlayer.addListener(new PlayerEventListener());
            vodRoomPlayerView.setPlayer(vodPlayer);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this,"StreetLive"));
            Uri vodUri = Uri.parse(vodURL);
            ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource(vodUri,dataSourceFactory,new DefaultExtractorsFactory(),null,null);
            vodPlayer.prepare(extractorMediaSource);

            redisPostDataSend redisPostDataSend = new redisPostDataSend();
            redisPostDataSend.execute("VodRoomActivity","WatchingVod:"+vodTitle);
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
