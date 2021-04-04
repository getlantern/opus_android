package top.oply.opusplayer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.oply.opuslib.OpusRecorder;
import top.oply.opuslib.OpusTool;


public class oplayer extends Activity {
    private static final String TAG = oplayer.class.getName();

    private Runnable stopRecording = null;
    private OpusTool oTool = new OpusTool();

    private ListView lvFiles;
    private List<String> lstFiles = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private String path;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_oplayer);

        //initial listView
        lvFiles = (ListView) findViewById(R.id.lvFile);
        lvFiles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        initData();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, lstFiles);
        lvFiles.setAdapter(adapter);
        lvFiles.setItemChecked(lstFiles.size() - 1, true);
    }

    private List<String> initData() {
        lstFiles = new ArrayList<String>();
        String SDPATH = Environment.getExternalStorageDirectory().getPath();
        path = SDPATH + "/OpusPlayer/";
        File fp = new File(path);
        if (!fp.exists())
            fp.mkdir();

        File[] files = fp.listFiles();
        if (files != null) {
            for (File f : files) {
                lstFiles.add(f.getName());
            }
        }
        return lstFiles;
    }

    private void updateList(String str) {
        if (lstFiles.contains(str))
            return;
        else {
            lstFiles.add(str);
            adapter.notifyDataSetChanged();
            lvFiles.setItemChecked(lstFiles.size() - 1, true);
        }

    }

    private void print(String str) {
        TextView tv;
        tv = (TextView) findViewById(R.id.mainLog);
        tv.setText(Utils.CurTime() + ": " + str + "\n" + tv.getText());
    }

    public void btnDecClick(View view) {

        String selectName = adapter.getItem(lvFiles.getCheckedItemPosition());
        String fileName = path + selectName;

        File f = new File(fileName);
        if (!f.exists()) {

            print(fileName + " is not exist, please put it there");
        }
        String fileNameOut = fileName + ".wav";
        print("Start decoding...");
        Log.d("encode:", oTool.nativeGetString());
        int result = oTool.decode(fileName, fileNameOut, null);
        if (result == 0) {
            String str = "Decode is complete. Output file is: " + fileNameOut;
            updateList(selectName + ".wav");
            print(str);
        } else {
            String str = "Decode failed.";
            print(str);
        }
    }

    public void btnEncClick(View view) {
        String selectName = adapter.getItem(lvFiles.getCheckedItemPosition());
        String fileName = path + selectName;
        File f = new File(fileName);
        if (!f.exists()) {
            String str = fileName + " is not exist, please put it there.";
            print(str);
        }
        String fileNameOut = fileName + ".opus";
        print("Start encoding...");
        Log.d("encode:", oTool.nativeGetString());
        int result = oTool.encode(fileName, fileNameOut, null);
        if (result == 0) {
            print("Encode is complete. Output file is: " + fileNameOut);
            updateList(selectName + ".opus");
        } else {
            print("Encode failed");
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void btnStopRClick(View v) {
        if (stopRecording == null) return;
        stopRecording.run();
        stopRecording = null;
        print("Stopped Recording");
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        try {
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Throwable t) {
            print(t.getMessage());
        }
    }

    public void btnRecordClick(View v) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            startRecording();
        } else if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 123);
        } else {
            startRecording();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Don't have enough permissions", Toast.LENGTH_LONG).show();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        if (stopRecording != null) {
            print("already recording");
            return;
        }

        String base = "record";
        String name = "record";
        int i = 0;
        for (i = 1; i < 100; i++) {
            name = base + i + ".opus";
            if (!lstFiles.contains(name))
                break;
        }
        fileName = path + name;
        OpusRecorder.OpusApplication application = OpusRecorder.OpusApplication.VOIP; // set this to AUDIO for something more optimized for music
        int sampleRate = 16000; // sample rate in Hz
        int bitRate = 24000; // Opus encoding bitrate in bits per second
        boolean stereo = false; // record mono
        long recordLimitMillis = 15000; // 15 second recording limit
        stopRecording = OpusRecorder.startRecording(fileName, application, sampleRate, bitRate, stereo, recordLimitMillis, new OpusRecorder.EffectsInitializer() {
            @Override
            public void init(int audioSessionId) {
                if (NoiseSuppressor.isAvailable()) {
                    try {
                        NoiseSuppressor noiseSuppressor = NoiseSuppressor.create(audioSessionId);
                        if (noiseSuppressor != null) noiseSuppressor.setEnabled(true);
                    } catch (Exception e) {
                        Log.e(TAG, "unable to init noise suppressor: " + e);
                    }
                }

                if (AutomaticGainControl.isAvailable()) {
                    try {
                        AutomaticGainControl automaticGainControl = AutomaticGainControl.create(audioSessionId);
                        if (automaticGainControl != null) automaticGainControl.setEnabled(true);
                    } catch (Exception e) {
                        Log.e(TAG, "unable to init automatic gain control: " + e);
                    }
                }
            }
        });
        print("Start Recording.. Save file to: " + fileName);

        updateList(name);
    }

}
