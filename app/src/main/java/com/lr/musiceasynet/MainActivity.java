package com.lr.musiceasynet;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lr.musiceasynet.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    NavController navController;
    ActivityMainBinding activityMainBinding;
    MusicPlayerBannerViewModel musicPlayerBannerViewModel;
    static MusicPlayerService musicPlayerService;
    Handler handler;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        //setContentView(R.layout.activity_main);
        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        musicPlayerBannerViewModel = new ViewModelProvider(this).get(MusicPlayerBannerViewModel.class);
        activityMainBinding.setMainActivityData(musicPlayerBannerViewModel);
        activityMainBinding.setLifecycleOwner(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

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

        bottomNavigationView = findViewById(R.id.bottom_navigationView);
        //navController = Navigation.findNavController(this,R.id.fragmentContainerView);

        musicPlayerBannerViewModel.getMusicInfoLiveData().observe(this, musicInfo -> {
            activityMainBinding.bottomMusicBannerTitle.setText(musicInfo.getTitle());
            activityMainBinding.bottomMusicBannerSubtitle.setText(musicInfo.getArtisst());
            activityMainBinding.bottomMusicBannerProgress.setProgress(0);
            activityMainBinding.bottomMusicBannerProgress.setMax(Math.toIntExact(musicInfo.getDuration()));
        });

        handler = new Handler();
        Runnable updateseekbar = new Runnable() {
            @Override
            public void run() {
                    activityMainBinding.bottomMusicBannerProgress.setProgress(musicPlayerService.mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 500);
            }
        };

        handler.postDelayed(updateseekbar,500);


        musicPlayerBannerViewModel.getIsPlaying().observe(this,aBoolean -> {
            if (aBoolean){
                if (CommonUtil.isDarkMode()) {
                    activityMainBinding.bottomMusicBannerController.setImageDrawable(getDrawable(R.mipmap.pause_light));
                }else {
                    activityMainBinding.bottomMusicBannerController.setImageDrawable(getDrawable(R.mipmap.pause_dark));
                }
            }else {
                if (CommonUtil.isDarkMode()) {
                    activityMainBinding.bottomMusicBannerController.setImageDrawable(getDrawable(R.mipmap.play_light));
                }else {
                    activityMainBinding.bottomMusicBannerController.setImageDrawable(getDrawable(R.mipmap.play_dark));
                }
            }
        });

        activityMainBinding.musicPlayerBanner.setOnClickListener(view->{

        });



        activityMainBinding.bottomMusicBannerProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (musicPlayerBannerViewModel.musicinfolivedata.getValue().getUrl()!=null&&fromUser) musicPlayerService.mediaPlayer.seekTo(progress);
                //播放完自动显示暂停
                if (musicPlayerBannerViewModel.musicinfolivedata.getValue().getUrl()!=null&&
                        !musicPlayerService.mediaPlayer.isPlaying()) musicPlayerBannerViewModel.isPlaying.setValue(false);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (musicPlayerBannerViewModel.musicinfolivedata.getValue().getUrl()!=null) {
                    handler.removeCallbacks(updateseekbar);
                }
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicPlayerBannerViewModel.musicinfolivedata.getValue().getUrl()!=null) {
                    handler.postDelayed(updateseekbar, 500);
                }
            }
        });

        activityMainBinding.bottomMusicBannerController.setOnClickListener(view->{
            if (musicPlayerBannerViewModel.musicinfolivedata.getValue().getUrl()!=null){
                if (musicPlayerService.mediaPlayer.isPlaying()) {
                    musicPlayerService.mediaPlayer.pause();
                    musicPlayerBannerViewModel.isPlaying.setValue(false);
                } else {
                    musicPlayerService.mediaPlayer.start();
                    musicPlayerBannerViewModel.isPlaying.setValue(true);
                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(menuItem ->{
            if (menuItem.getItemId()==R.id.navtorec){
                navController.navigate(R.id.navigationrecommandpage );
            }else if(menuItem.getItemId()==R.id.navtomain){
                navController.navigate(R.id.navigationHome);
            }
            else if(menuItem.getItemId()==R.id.navtomyaccount){
                navController.navigate(R.id.navigationmyAccount);
            }
                return true;
         });


    }

    @Override
    protected void onStart() {
        super.onStart();
        navController = Navigation.findNavController(this,R.id.fragmentContainerView);
    }
}