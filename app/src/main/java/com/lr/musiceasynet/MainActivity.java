package com.lr.musiceasynet;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lr.musiceasynet.Util.CommonUtil;
import com.lr.musiceasynet.Util.MusicUtil;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavController navController;
    MotionLayout main;
    ConstraintLayout musicPlayerBanner;
    TextView bottomMusicBannerTitle,
            bottomMusicBannerSubtitle;
    ImageView bottomMusicBannerImg,
            bottomMusicBannerController,
            bottomMusicBannerNext,
            bottomMusicBannerPrevious;
    SeekBar bottomMusicBannerProgress;
    //ActivityMainBinding activityMainBinding;
    MusicPlayerBannerViewModel musicPlayerBannerViewModel;
    static MusicPlayerService musicPlayerService;
    Handler handler;
    Runnable updateseekbar;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        initView();
        musicPlayerBannerViewModel = new ViewModelProvider(this).get(MusicPlayerBannerViewModel.class);
        ViewCompat.setOnApplyWindowInsetsListener(main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        connectToService();
        //navController = Navigation.findNavController(this,R.id.fragmentContainerView);
        musicPlayerBannerViewModel.getMusicInfoLiveData().observe(this, this::onObserveMusicInfoChanged);
        initHandler();
        musicPlayerBannerViewModel.getIsPlaying().observe(this, this::onObserveMusicIsPlaying);
        bottomMusicBannerNext.setOnClickListener(view-> playNextMusic());
        bottomMusicBannerPrevious.setOnClickListener(view-> playPreviousMusic());
        progressChanged();
        musicPlayerBanner.setOnClickListener(view->{
            if (main.getCurrentState()==R.id.start) main.transitionToEnd();
        });
        bottomMusicBannerController.setOnClickListener(view-> onBottomMusicBannerControllerClick());
        bottomNavigationView.setOnItemSelectedListener(menuItem ->{
            onBottomNavigationItemClick(menuItem);
            return true;
         });


    }

    private void initHandler() {
        handler = new Handler();
        updateseekbar = new Runnable() {
            @Override
            public void run() {
                if(musicPlayerService!=null) {
                    bottomMusicBannerProgress.setProgress(musicPlayerService.mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.postDelayed(updateseekbar,500);
    }

    @Override
    protected void onStart() {
        //onStart中加载fragment
        //initView();
        super.onStart();
        navController = Navigation.findNavController(this,R.id.fragmentContainerView);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK&&
                event.getRepeatCount() == 0&&
                main.getCurrentState()==R.id.end){
            main.transitionToStart();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void connectToService(){
        Intent intent = new Intent(this,MusicPlayerService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicPlayerService = ((MusicPlayerService.MusicPlayerBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initView(){
        main = findViewById(R.id.main);
        bottomNavigationView = findViewById(R.id.bottom_navigationView);
        musicPlayerBanner = findViewById(R.id.music_player_banner);
        bottomMusicBannerTitle = findViewById(R.id.bottom_music_banner_title);
        bottomMusicBannerSubtitle = findViewById(R.id.bottom_music_banner_subtitle);
        bottomMusicBannerImg = findViewById(R.id.bottom_music_banner_img);
        bottomMusicBannerController = findViewById(R.id.bottom_music_banner_controller);
        bottomMusicBannerNext = findViewById(R.id.bottom_music_banner_next);
        bottomMusicBannerPrevious = findViewById(R.id.bottom_music_banner_previous);
        bottomMusicBannerProgress = findViewById(R.id.bottom_music_banner_progress);
    }

    private void onBottomNavigationItemClick(MenuItem menuItem){
        if (menuItem.getItemId()==R.id.navtorec){
            navController.navigate(R.id.navigationrecommandpage );
        }else if(menuItem.getItemId()==R.id.navtomain){
            navController.navigate(R.id.navigationHome);
        }
        else if(menuItem.getItemId()==R.id.navtomyaccount){
            navController.navigate(R.id.navigationmyAccount);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void setMusicImg(MusicInfo musicInfo){
        if (musicInfo.getUrl()!=null&&MusicUtil.getMusicImg(musicInfo)!=null) {
            bottomMusicBannerImg.setImageBitmap (MusicUtil.getMusicImg(musicInfo));
        }else if (musicInfo.getUrl()!=null){
            if (CommonUtil.isDarkMode())
                bottomMusicBannerImg.setImageDrawable(getDrawable(R.drawable.tune_light));
            else
                bottomMusicBannerImg.setImageDrawable(getDrawable(R.drawable.tune_dark));
        }
    }

    private void progressChanged(){
        bottomMusicBannerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicPlayerBannerViewModel.musicInfoLiveData.getValue().getUrl()!=null&&fromUser){
                    musicPlayerService.mediaPlayer.seekTo(progress);
                    musicPlayerService.playbackStateCompat
                            .setState(PlaybackStateCompat.STATE_PLAYING,progress,1);
                    musicPlayerService.mediaSessionCompat
                            .setPlaybackState(musicPlayerService.playbackStateCompat.build());
                }
                //播放完自动显示暂停
               /* if (musicPlayerBannerViewModel.musicinfolivedata.getValue().getUrl()!=null&&
                        !musicPlayerService.mediaPlayer.isPlaying()) musicPlayerBannerViewModel.isPlaying.setValue(false);*/
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (musicPlayerBannerViewModel.musicInfoLiveData.getValue().getUrl()!=null) {
                    handler.removeCallbacks(updateseekbar);
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicPlayerBannerViewModel.musicInfoLiveData.getValue().getUrl()!=null) {
                    handler.postDelayed(updateseekbar, 500);
                }
            }
        });
    }

    private void onObserveMusicIsPlaying(boolean isPlaying){
        if (isPlaying){
            if (CommonUtil.isDarkMode()) {
                bottomMusicBannerController.setImageDrawable(getDrawable(R.drawable.pause_light));
            }else {
                bottomMusicBannerController.setImageDrawable(getDrawable(R.drawable.pause_dark));
            }
        }else {
            if (CommonUtil.isDarkMode()) {
                bottomMusicBannerController.setImageDrawable(getDrawable(R.drawable.play_light));
            }else {
                bottomMusicBannerController.setImageDrawable(getDrawable(R.drawable.play_dark));
            }
        }
    }
    private void onObserveMusicInfoChanged(MusicInfo musicInfo){
        bottomMusicBannerTitle.setText(musicInfo.getTitle());
        bottomMusicBannerSubtitle.setText(musicInfo.getArtisst());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setMusicImg(musicInfo);
        }
        bottomMusicBannerProgress.setProgress(0);
        bottomMusicBannerProgress.setMax(Math.toIntExact(musicInfo.getDuration()));
    }
    private void onBottomMusicBannerControllerClick(){
        if (musicPlayerBannerViewModel.musicInfoLiveData.getValue().getUrl()!=null){
            if (musicPlayerService.mediaPlayer.isPlaying()) {
                musicPlayerService.pauseMusic();
                musicPlayerBannerViewModel.isPlaying.setValue(false);
            } else {
                musicPlayerService.resumeMusic();
                musicPlayerBannerViewModel.isPlaying.setValue(true);
            }
        }
    }
    private void playNextMusic(){
        musicPlayerService.playNextMusic();
        musicPlayerBannerViewModel.isPlaying.setValue(musicPlayerService.mediaPlayer.isPlaying());
    }

    private void playPreviousMusic(){
        musicPlayerService.playPreviousMusic();
        musicPlayerBannerViewModel.isPlaying.setValue(musicPlayerService.mediaPlayer.isPlaying());
    }
}