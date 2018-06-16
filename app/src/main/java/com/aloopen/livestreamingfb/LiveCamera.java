package com.aloopen.livestreamingfb;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import net.ossrs.rtmp.ConnectCheckerRtmp;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LiveCamera extends AppCompatActivity
        implements ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback {


    private RtmpCamera2 rtmpCamera2;
    private Button button;
    private Button bRecord;
    private EditText etUrl;

    private String currentDateAndTime = "";
    private File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/rtmp-rtsp-stream-client-java");

    private Socket socket;
    {
        try {
            socket = IO.socket("http://192.168.0.161:3000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    Adapter adapter;
    private List<Message> mMessages = new ArrayList<Message>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_example);
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        button = findViewById(R.id.b_start_stop);
        button.setOnClickListener(this);
//        bRecord = findViewById(R.id.b_record);
//        bRecord.setOnClickListener(this);
        Button switchCamera = findViewById(R.id.switch_camera);
        switchCamera.setOnClickListener(this);
        etUrl = findViewById(R.id.et_rtp_url);
        etUrl.setHint("rtmp://server-url");
        rtmpCamera2 = new RtmpCamera2(surfaceView, this);
        surfaceView.getHolder().addCallback(this);

        socket.connect();


        socket.on("server_gui_message",newMess);


        //ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(getApplicationContext(),android.R.layout.simple_list_item_2,mMessages);


         adapter = new Adapter(this,mMessages);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectionSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveCamera.this, "Connection success", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void onConnectionFailedRtmp(final String reason) {
        runOnUiThread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                Toast.makeText(LiveCamera.this, "Connection failed. " + reason,
                        Toast.LENGTH_SHORT).show();
                rtmpCamera2.stopStream();
                button.setText("start");
            }
        });
    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveCamera.this, "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveCamera.this, "Auth error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LiveCamera.this, "Auth success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_start_stop:
                if (!rtmpCamera2.isStreaming()) {
                    if (rtmpCamera2.isRecording()
                            || rtmpCamera2.prepareAudio() && rtmpCamera2.prepareVideo()) {
                        button.setText("stop");
                        rtmpCamera2.startStream(etUrl.getText().toString());
                    } else {
                        Toast.makeText(this, "Error preparing stream, This device cant do it",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    button.setText("start");
                    rtmpCamera2.stopStream();
                }
                break;
            case R.id.switch_camera:
                try {
                    rtmpCamera2.switchCamera();
                } catch (CameraOpenException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
           /* case R.id.b_record:
                if (!rtmpCamera2.isRecording()) {
                    try {
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                        currentDateAndTime = sdf.format(new Date());
                        if (!rtmpCamera2.isStreaming()) {
                            if (rtmpCamera2.prepareAudio() && rtmpCamera2.prepareVideo()) {
                                rtmpCamera2.startRecord(
                                        folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                             //   bRecord.setText("stop");
                                Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Error preparing stream, This device cant do it",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            rtmpCamera2.startRecord(
                                    folder.getAbsolutePath() + "/" + currentDateAndTime + ".mp4");
                            //bRecord.setText("start");
                            Toast.makeText(this, "Recording... ", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        rtmpCamera2.stopRecord();
                       // bRecord.setText("start recoder");
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    rtmpCamera2.stopRecord();
                   // bRecord.setText("start recoder");
                    Toast.makeText(this,
                            "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                            Toast.LENGTH_SHORT).show();
                    currentDateAndTime = "";
                }
                break;*/
            default:
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @SuppressLint("NewApi")
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        rtmpCamera2.startPreview();
    }

    @SuppressLint("NewApi")
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (rtmpCamera2.isRecording()) {
            rtmpCamera2.stopRecord();
            //bRecord .setText("start recoder");
            Toast.makeText(this,
                    "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
                    Toast.LENGTH_SHORT).show();
            currentDateAndTime = "";
        }
        if (rtmpCamera2.isStreaming()) {
            rtmpCamera2.stopStream();
            button.setText("start");
        }
        rtmpCamera2.stopPreview();
    }

    private void addMess(String message){
        mMessages.add(new Message.Builder().message(message).build());
        adapter.notifyDataSetChanged();
    }

    private Emitter.Listener newMess = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject data = (JSONObject) args[0];
                String message;
                try {
                    message = data.getString("msg");
                } catch (JSONException e) {
                    Log.e("TAG", e.getMessage());
                    return;
                }
                addMess(message);
            }
        });
        }
    };

}
