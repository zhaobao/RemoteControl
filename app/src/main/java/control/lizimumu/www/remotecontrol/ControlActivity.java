package control.lizimumu.www.remotecontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;

public class ControlActivity extends AppCompatActivity {

    static String CHOSEN_ADDRESS = "chosen";
    private int[] mSongs = {R.raw.s1, R.raw.s2, R.raw.s3, R.raw.s4, R.raw.s5, R.raw.s6, R.raw.s7, R.raw.s8};
    private int mSongIndex = 0;
    private MediaPlayer mMediaPlayer;

    private void play() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.release();
        }
        mMediaPlayer = MediaPlayer.create(this, mSongs[mSongIndex++ % mSongs.length]);
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.release();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        final String address = intent.getStringExtra(CHOSEN_ADDRESS);
        TextView ip = findViewById(R.id.device_ip);
        ip.setText(address.split("\\|")[1]);

        findViewById(R.id.action_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionApi.doAction("left", address.split("\\|")[0], getApplicationContext());
                play();
            }
        });

        findViewById(R.id.action_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionApi.doAction("right", address.split("\\|")[0], getApplicationContext());
                play();
            }
        });

        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new AlertDialog.Builder(ControlActivity.this)
                        .setTitle("Stop Control")
                        .setMessage("Are you sure to stop control?")
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(0);
                                ControlActivity.this.finish();
                            }
                        })
                        .create();
                dialog.show();
//                DisplayMetrics displayMetrics = new DisplayMetrics();
//                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                int displayWidth = displayMetrics.widthPixels;
//                int displayHeight = displayMetrics.heightPixels;
//                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//                layoutParams.copyFrom(dialog.getWindow().getAttributes());
//                layoutParams.width = (int) (displayWidth * 1f);
//                layoutParams.height = (int) (displayHeight * 0.7f);
//                dialog.getWindow().setAttributes(layoutParams);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            }
        });
    }
}
