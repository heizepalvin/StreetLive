package com.example.heizepalvin.streetlive.mainFragment.LiveFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraManager;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heizepalvin.streetlive.R;
import com.github.faucamp.simplertmp.RtmpPublisher;
import com.github.faucamp.simplertmp.io.RtmpConnection;
import com.pedro.encoder.input.video.Camera1ApiManager;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.encoder.input.video.Frame;
import com.pedro.encoder.input.video.GetCameraData;
import com.pedro.encoder.video.VideoEncoder;
import com.pedro.rtplibrary.base.Camera1Base;
import com.pedro.rtplibrary.rtmp.RtmpCamera1;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.pedro.rtplibrary.rtmp.RtmpDisplay;
import com.tsengvn.typekit.TypekitContextWrapper;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.Policy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.heizepalvin.streetlive.mainFragment.LiveFragment.LiveFragmentActivity.liveListItems;

public class CreateLiveRoomActivity extends AppCompatActivity implements ConnectCheckerRtmp {

    private static CameraPreview surfaceView;
    private SurfaceHolder createRoomHolder;
    private static Button camera_preview_btn;
    private static android.hardware.Camera camera;
    private int RESULT_PERMISSIONS = 100;
    public static CreateLiveRoomActivity getCreatRoomActivityInstance;

    @BindView(R.id.previewFlashBtn)
    ImageButton previewFlashBtn;
    @BindView(R.id.previewBackBtn)
    ImageButton previewBackBtn;
    @BindView(R.id.previewCameraSwitchBtn)
    ImageButton previewCameraSwitchBtn;
    @BindView(R.id.previewEditBtn)
    ImageButton previewEditBtn;
    @BindView(R.id.previewMicBtn)
    ImageButton previewMicBtn;
    @BindView(R.id.previewRotationBtn)
    ImageButton previewRotationBtn;
    @BindView(R.id.previewStartBtn)
    ImageButton previewStartBtn;
    @BindView(R.id.previewTitleText)
    TextView previewTitleText;
    @BindView(R.id.previewLayout)
    RelativeLayout previewLayout;

    // 플래시가 켜져있는지 안켜져있는지 확인하는 Boolean
    private boolean flashOnOff = false;
    // 전면 후면 카메라 상태 저장
    public static int cameraFacing;
    // 가로 세로 상태
    private int previewOrientation;
    //기본 방제목
    private String previewTitle;
    //rtmp 라이브러리
    private RtmpCamera1 rtmpCamera1;
    private Camera1ApiManager apiManager;
    private boolean micOnOff = true;

    //방 생성하는 유저 정보
    private String userNickname;
    private String userGender;
    private String userStreamingKey;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //18/05/29
        getCreatRoomActivityInstance = this;
        requestPermissionCamera();

    }
    private void setInit(){

        //18/05/29
        Log.e("onCreate",cameraFacing+"?");
        surfaceView = new CameraPreview(getCreatRoomActivityInstance,cameraFacing);
        setContentView(surfaceView);
        addContentView(LayoutInflater.from(this).inflate(R.layout.create_live_room_activity,null), new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ButterKnife.bind(this);
        SharedPreferences createLiveRoomTitle = getSharedPreferences("liveRoomTitle",MODE_PRIVATE);
        SharedPreferences getLoginUserInfo = getSharedPreferences("userLoginInfo",MODE_PRIVATE);
        previewTitle = createLiveRoomTitle.getString("title",getLoginUserInfo.getString("nickname","")+"님의 방송입니다.");
        previewTitleText.setText(previewTitle);
        userNickname = getLoginUserInfo.getString("nickname","null");
        Random streamingKey = new Random();
        StringBuffer buf = new StringBuffer();
        for(int i=0;i<10;i++){
            if(streamingKey.nextBoolean()){
                buf.append((char)((int)(streamingKey.nextInt(26))+97));
            }else {
                buf.append((streamingKey.nextInt(10)));
            }
        }
        Log.e("랜덤",buf.toString());
        userStreamingKey = buf.toString();
        //18/06/04

        //18/06/11
        rtmpCamera1 = new RtmpCamera1(surfaceView,getCreatRoomActivityInstance);
        apiManager = new Camera1ApiManager(surfaceView,rtmpCamera1);
        previewCameraSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(apiManager.isRunning()){
                    apiManager.switchCamera();
                } else {
                    rtmpCamera1.switchCamera();
                }
            }
        });



//        //flash 버튼 이벤트
        previewFlashBtn = findViewById(R.id.previewFlashBtn);
        previewFlashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiManager.prepareCamera(1920,1080,60,ImageFormat.NV21);
                apiManager.start();
                if(apiManager.isLanternEnable()){
                    apiManager.disableLantern();
                    previewFlashBtn.setImageResource(R.drawable.flashoff);
                } else {
                    apiManager.enableLantern();
                    previewFlashBtn.setImageResource(R.drawable.flashon);
                }
            }
        });

        //18/05/30
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        previewOrientation  = display.getOrientation();

        //가로,세로 화면 전환 버튼 이벤트
        previewRotationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previewOrientation == 0){
                    //세로 화면일때 가로화면으로 전환
                    getCreatRoomActivityInstance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    previewOrientation = 1;
                    Log.e("previewRotationBtn",cameraFacing+": 세로");
                } else {
                    // 가로 화면일때 세로화면으로 전환
                    getCreatRoomActivityInstance.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    previewOrientation = 0;
                    Log.e("previewRotationBtn",cameraFacing+": 가로");
                }
            }
        });
        //뒤로가기 버튼 이벤트
        previewBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rtmpCamera1.isStreaming()){
                    Toast.makeText(CreateLiveRoomActivity.this, "뒤로가시려면 방송을 먼저 종료해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                }
            }
        });
        //방제목 변경 버튼 이벤트
        previewEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = new EditText(CreateLiveRoomActivity.this);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                editText.setTextColor(Color.BLACK);
                SharedPreferences getCreateLiveRoomTitle = getSharedPreferences("liveRoomTitle",MODE_PRIVATE);
                editText.setText(previewTitle);
                editText.setSelection(editText.getText().length());
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateLiveRoomActivity.this);
                builder.setTitle("StreetLive");
                builder.setMessage("변경할 방제목을 입력해주세요!(30자 이내)");
                builder.setView(editText);
                builder.setNegativeButton("입력",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(editText.getText().toString().equals("") || editText.getText().toString().replace(" ","").equals("")){
                                    Toast.makeText(CreateLiveRoomActivity.this, "기본 방제목으로 설정됩니다.", Toast.LENGTH_SHORT).show();
                                } else {
                                    previewTitle = editText.getText().toString();
                                    previewTitleText.setText(editText.getText().toString());
                                    SharedPreferences.Editor createLiveRoomTitleEdit = getCreateLiveRoomTitle.edit();
                                    createLiveRoomTitleEdit.clear();
                                    createLiveRoomTitleEdit.putString("title",editText.getText().toString());
                                    createLiveRoomTitleEdit.commit();
                                }

                            }
                        });
                builder.setPositiveButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
            }
        });

        // 오디오 음소거 버튼 이벤트

        previewMicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(micOnOff){
                    micOnOff = false;
                    previewMicBtn.setImageResource(R.drawable.micoff);
                } else {
                    micOnOff = true;
                    previewMicBtn.setImageResource(R.drawable.micon);
                }
                if(rtmpCamera1.isStreaming()){
                    //스트리밍 중일때
                    if(micOnOff){
                        rtmpCamera1.enableAudio();
                    } else {
                        rtmpCamera1.disableAudio();
                    }
                }
            }
        });

        // 촬영 시작 버튼 이벤트

        previewStartBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            @Override
            public void onClick(View v) {
                if(rtmpCamera1.isStreaming()){
                    rtmpCamera1.stopStream();
                    Toast.makeText(CreateLiveRoomActivity.this, "방송을 종료합니다.", Toast.LENGTH_SHORT).show();
                    previewRotationBtn.setVisibility(View.VISIBLE);
                    previewBackBtn.setVisibility(View.VISIBLE);
                    LiveRoomRemoveToDB liveRoomRemoveToDB = new LiveRoomRemoveToDB();
                    liveRoomRemoveToDB.execute(userStreamingKey);
                } else {
                    // 스트리밍 시작

                    rtmpCamera1.prepareVideo(1920,1080,60,5000,true,0,0);
                    rtmpCamera1.prepareAudio(5000,48000,true,true,true);
//                    rtmpCamera1.prepareVideo();
//                    rtmpCamera1.prepareAudio();
                    rtmpCamera1.startStream("rtmp://210.89.190.131:1935/src/streetlive"+"-"+userStreamingKey);
                    previewRotationBtn.setVisibility(View.GONE);
                    previewBackBtn.setVisibility(View.GONE);
                    apiManager.stop();
                    apiManager.prepareCamera(1920,1080,60,ImageFormat.NV21);
                    if(apiManager.isLanternEnable()){
//                        Toast.makeText(CreateLiveRoomActivity.this, "랜턴켜져있음", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(CreateLiveRoomActivity.this, apiManager.isRunning()+"", Toast.LENGTH_SHORT).show();
                        apiManager.start();
                        apiManager.enableLantern();
                    }else{
//                        Toast.makeText(CreateLiveRoomActivity.this, "랜턴꺼져있음", Toast.LENGTH_SHORT).show();
//                        Toast.makeText(CreateLiveRoomActivity.this, apiManager.isRunning()+"", Toast.LENGTH_SHORT).show();
//                        apiManager.start();
                        Log.e("dddddd",apiManager.isRunning()+"");
                        Log.e("dddddd",rtmpCamera1.isOnPreview()+"");
                        apiManager.disableLantern();
                    }

                    // 리스트뷰에 생방송 리스트 추가

                    String liveThumnail = "http://210.89.190.131/dash/streetlive-"+userStreamingKey+".png";
                    LiveRoomAddToDB liveRoomSaveDB = new LiveRoomAddToDB();
                    liveRoomSaveDB.execute(previewTitleText.getText().toString(),userNickname,userStreamingKey,liveThumnail);
                }
            }
        });
    }

    private class LiveRoomRemoveToDB extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            //postgres 데이터 삭제
            Connection pgConnection;
            Statement pgStatement;
            int pgResult;

            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;

            String liveRoomkey = strings[0];


            // 서버 썸네일 삭제

            String serverURL = "http://210.89.190.131/rmImage.php";
            String postParameters = "key="+liveRoomkey;


            try{
                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
                pgStatement = pgConnection.createStatement();
                sql = "delete from live.room_info where key = '"+liveRoomkey+"';";
                pgResult = pgStatement.executeUpdate(sql);
                if(pgResult!=0){
                    Log.e("LiveRoomRemoveToDB","SuccessRemove");
                    pgStatement.close();
                }

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                InputStream inputStream;
                if(responseStatusCode==HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line=bufferedReader.readLine())!= null){
                    sb.append(line);
                }

                bufferedReader.close();

            }catch (Exception e){
                Log.e("LiveRoomRemoveToDB",e.toString());
            }
            return null;
        }
    }

    private class LiveRoomAddToDB extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            Connection pgConnection;
            Statement pgStatement;
            int pgResult;

            String pgJDBCurl = "jdbc:postgresql://210.89.190.131/streetlive";
            String pgUser = "postgres";
            String pgPassword = "rmstnek123";
            String sql;

            String liveRoomTitle = strings[0];
            String liveRoomNickname = strings[1];
            String liveRoomkey = strings[2];
            String liveRoomThumnail = strings[3];

            try{
                pgConnection = DriverManager.getConnection(pgJDBCurl,pgUser,pgPassword);
                pgStatement = pgConnection.createStatement();
                sql = "insert into live.room_info (title,nickname,key,thumnail) values('"
                        +liveRoomTitle+"','"+liveRoomNickname+"','"+liveRoomkey+"','"+liveRoomThumnail+"');";
                pgResult = pgStatement.executeUpdate(sql);
                if(pgResult!=0){
                    Log.e("liveRoomAddToDB","Success!");
                    pgStatement.close();
                }
            }catch (Exception e){
                Log.e("liveRoomAddToDB",e.toString());
            }

            return null;
        }
    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getCreatRoomActivityInstance, "방송을 시작합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateLiveRoomActivity.this, "Failed" + s, Toast.LENGTH_SHORT).show();
                Log.e("onConnectionFailed",s);
//                rtmpCamera1.stopStream();
            }
        });

    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateLiveRoomActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onAuthErrorRtmp() {
    runOnUiThread(new Runnable() {
        @Override
        public void run() {
            Toast.makeText(CreateLiveRoomActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
        }
    });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CreateLiveRoomActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

        public CameraPreview(Context context, int cameraFacings) {
            super(context);
            createRoomHolder = getHolder();
            createRoomHolder.addCallback(this);
            createRoomHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            cameraFacing = cameraFacings;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e("surfaceCreated","come in");
//            //18/06/09
//            try{
//                camera = android.hardware.Camera.open(cameraFacing);
//                android.hardware.Camera.Parameters parameters = camera.getParameters();
//                listPreviewSizes = parameters.getSupportedPreviewSizes();
//                int rotation = CreateLiveRoomActivity.getCreatRoomActivityInstance.getWindowManager().getDefaultDisplay().getRotation();
//                if (rotation == Surface.ROTATION_0) {
//                    camera .setDisplayOrientation(90);
//                    parameters.setRotation(90);
//                }else if(rotation == Surface.ROTATION_90){
//                    camera .setDisplayOrientation(0);
//                    parameters.setRotation(0);
//                }else if(rotation == Surface.ROTATION_180){
//                    camera .setDisplayOrientation(270);
//                    parameters.setRotation(270);
//                }else{
//                    camera .setDisplayOrientation(180);
//                    parameters.setRotation(180);
//                }
//                parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//                if(cameraFacing== android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK){
//                    camera.setParameters(parameters);
//                }
//                camera.setPreviewDisplay(holder);
//                camera.setParameters(parameters);
//                camera.startPreview();
//
//            }catch (IOException e){
//                e.printStackTrace();
//                Log.e("surfaceCreated",e.toString());
//            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.e("surfaceChanged",cameraFacing+"");
//            camera.startPreview();
            rtmpCamera1.startPreview(1920,1080);

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e("surfaceDestroy","come in ");
//            camera.stopPreview();
//            camera.release();
//            camera = null;
//            listPreviewSizes = null;
//            previewSize = null;
            if(apiManager.isRunning()){
                apiManager.stop();
            } else {
                rtmpCamera1.stopStream();
                rtmpCamera1.stopPreview();
            }
        }

        public android.hardware.Camera.Size getPreviewSize(List<android.hardware.Camera.Size> sizes, int w, int h) {

            final double ASPECT_TOLERANCE = 0.1;
            double targetRatio = (double) h / w;

            if (sizes == null)
                return null;

            android.hardware.Camera.Size optimalSize = null;
            double minDiff = Double.MAX_VALUE;

            int targetHeight = h;

            for (android.hardware.Camera.Size size : sizes) {
                double ratio = (double) size.width / size.height;
                if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                    continue;

                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }

            if (optimalSize == null) {
                minDiff = Double.MAX_VALUE;
                for (android.hardware.Camera.Size size : sizes) {
                    if (Math.abs(size.height - targetHeight) < minDiff) {
                        optimalSize = size;
                        minDiff = Math.abs(size.height - targetHeight);
                    }
                }
            }

            return optimalSize;
        }

    }



    public boolean requestPermissionCamera(){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(CreateLiveRoomActivity.this,new String[]{Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},RESULT_PERMISSIONS);
            } else {
                setInit();
                return true;
            }
        } else {
            //version 6 이하 일때,
            setInit();
            return true;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(RESULT_PERMISSIONS == requestCode){
            if(grantResults.length > 0){
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] != PackageManager.PERMISSION_GRANTED ||
                        grantResults[2] != PackageManager.PERMISSION_GRANTED){
                    //권한 거부시
                    Toast.makeText(getCreatRoomActivityInstance, "앱 설정에서 권한을 허용해주세요.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    //권한 허가시
                    setInit();
                }
            }
//            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences getCreateLiveRoomTitle = getSharedPreferences("liveRoomTitle",MODE_PRIVATE);
        SharedPreferences.Editor remove = getCreateLiveRoomTitle.edit();
        remove.clear();
        remove.commit();
        if(rtmpCamera1.isStreaming()){
            rtmpCamera1.stopStream();
            rtmpCamera1.stopPreview();
        }
    }

    // Typekit 라이브러리 (폰트 적용)를 사용하기 위해 만든 메소드
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
