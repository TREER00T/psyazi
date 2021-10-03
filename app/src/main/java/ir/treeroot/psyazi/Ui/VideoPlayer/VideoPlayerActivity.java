package ir.treeroot.psyazi.Ui.VideoPlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.Utils.Link;

public class VideoPlayerActivity extends AppCompatActivity {

    Bundle bundle;
    VideoView video_layout;
    LinearLayout lin_videoPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        init();

        hideNavigationBar();

        videoPlayer();

    }



    private void videoPlayer() {

        video_layout.setVideoURI(Uri.parse(bundle.getString(Link.Key_i_video_player)));

        video_layout.setVisibility(View.VISIBLE);

        lin_videoPlayer.setOnClickListener(v -> {

            lin_videoPlayer.setVisibility(View.GONE);

            video_layout.start();

        });

        video_layout.setOnClickListener(v -> {

            lin_videoPlayer.setVisibility(View.VISIBLE);

            video_layout.pause();

        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        video_layout.pause();
        lin_videoPlayer.setVisibility(View.VISIBLE);

    }

    public void init() {

        video_layout = findViewById(R.id.video_layout);

        lin_videoPlayer = findViewById(R.id.linearLayout_video_play);

        bundle = getIntent().getExtras();

    }

    @Override
    protected void onResume() {
        super.onResume();

        hideNavigationBar();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        video_layout.pause();
        lin_videoPlayer.setVisibility(View.VISIBLE);

    }

    private void hideNavigationBar() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            this.getWindow().getDecorView().setSystemUiVisibility(

                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            );

        }

    }

}