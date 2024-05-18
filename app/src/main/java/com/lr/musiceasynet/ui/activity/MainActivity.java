package com.lr.musiceasynet.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lr.musiceasynet.R;
import com.lr.musiceasynet.adapter.NightModeAdapter;
import com.lr.musiceasynet.music.MusicInfo;
import com.lr.musiceasynet.music.MusicPlayerService;
import com.lr.musiceasynet.util.CommonUtil;
import com.lr.musiceasynet.viewmodel.MusicPlayerBarViewModel;

public class MainActivity extends AppCompatActivity {
    public final static int DEFAULT_DELAY_MILLS = 500;
    public final static int PROGRESS_START = 0;

    BottomNavigationView bottomNavigationView;
    NavController navController;
    MotionLayout main;
    ConstraintLayout musicPlayerBar;
    TextView bottomMusicBarTitle,
            bottomMusicBarSubtitle;
    ImageView bottomMusicBarImg,
            bottomMusicBarController,
            bottomMusicBarNext,
            bottomMusicBarPrevious;
    SeekBar bottomMusicBarProgress;
    //ActivityMainBinding activityMainBinding;
    MusicPlayerBarViewModel musicPlayerBarViewModel;
    MusicPlayerService musicPlayerService;
    private Handler handler;
    private Runnable updateseekbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        CommonUtil.edgeToEdge(this,main);
        musicPlayerBarViewModel = new ViewModelProvider(this).get(MusicPlayerBarViewModel.class);
        connectToService();
        //navController = Navigation.findNavController(this,R.id.fragmentContainerView);
        musicPlayerBarViewModel.getMusicInfoLiveData().observe(this, this::onObserveMusicInfoChanged);
        initHandler(bottomMusicBarProgress);
        musicPlayerBarViewModel.getIsPlaying().observe(this, this::onObserveMusicIsPlaying);
        bottomMusicBarNext.setOnClickListener(view->
                musicPlayerBarViewModel.playNextMusic(musicPlayerService));
        bottomMusicBarPrevious.setOnClickListener(view->
                musicPlayerBarViewModel.playPreviousMusic(musicPlayerService));
        progressChanged();
        musicPlayerBar.setOnClickListener(view->{
            if (main.getCurrentState()==R.id.start) main.transitionToEnd();
        });
        bottomMusicBarController.setOnClickListener(view->
                musicPlayerBarViewModel.onBottomMusicBarControllerClick(musicPlayerService));
        bottomNavigationView.setOnItemSelectedListener(menuItem ->{
            onBottomNavigationItemClick(menuItem);
            return true;
         });

    }

    @Override
    protected void onStart() {
        //onStart中加载fragment
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
        }else if (keyCode==KeyEvent.KEYCODE_BACK&&
                event.getRepeatCount() == 0&&
                navController.getCurrentDestination().getId()==R.id.navigationPlaylist){
            navController.navigateUp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp();
        //return super.onSupportNavigateUp();
    }

    private void initView(){
        main = findViewById(R.id.main);
        bottomNavigationView = findViewById(R.id.bottom_navigationView);
        musicPlayerBar = findViewById(R.id.music_player_bar);
        bottomMusicBarTitle = findViewById(R.id.bottom_music_bar_title);
        bottomMusicBarSubtitle = findViewById(R.id.bottom_music_bar_subtitle);
        bottomMusicBarImg = findViewById(R.id.bottom_music_bar_img);
        bottomMusicBarController = findViewById(R.id.bottom_music_bar_controller);
        bottomMusicBarNext = findViewById(R.id.bottom_music_bar_next);
        bottomMusicBarPrevious = findViewById(R.id.bottom_music_bar_previous);
        bottomMusicBarProgress = findViewById(R.id.bottom_music_bar_progress);
    }

    private void onBottomNavigationItemClick(MenuItem menuItem){
        if (menuItem.getItemId()==R.id.nav_to_discovery){
            navController.navigate(R.id.navigationrecommandpage);
        }else if(menuItem.getItemId()==R.id.nav_to_main){
            navController.navigate(R.id.navigationHome);
        }
        else if(menuItem.getItemId()==R.id.nav_to_myaccount){
            navController.navigate(R.id.navigationmyAccount);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void setMusicImg(MusicInfo musicInfo){
        if (musicInfo.getUrl()!=null&& musicInfo.getMusicImg()!=null) {
            bottomMusicBarImg.setImageBitmap (musicInfo.getMusicImg());
        }else if (musicInfo.getUrl()!=null){
                bottomMusicBarImg.setImageDrawable(
                        NightModeAdapter.getImg(R.drawable.tune_dark,R.drawable.tune_light));
        }
    }

    private void progressChanged(){
        bottomMusicBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicPlayerBarViewModel.progressChanged(progress,fromUser,musicPlayerService);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                overrideOnStartTrackingTouch();
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                overrideOnStopTrackingTouch();
            }
        });
    }

    private void onObserveMusicIsPlaying(boolean isPlaying){//ui层
        if (!isPlaying){
            bottomMusicBarController.setImageDrawable(
                    NightModeAdapter.getImg(R.drawable.play_dark,R.drawable.play_light));
        }else {
            bottomMusicBarController.setImageDrawable(
                    NightModeAdapter.getImg(R.drawable.pause_dark, R.drawable.pause_light));
        }
    }
    private void onObserveMusicInfoChanged(MusicInfo musicInfo){//ui层
        bottomMusicBarTitle.setText(musicInfo.getTitle());
        bottomMusicBarSubtitle.setText(musicInfo.getArtisst());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            setMusicImg(musicInfo);
        }//版本适配待做
        bottomMusicBarProgress.setProgress(PROGRESS_START);
        bottomMusicBarProgress.setMax(Math.toIntExact(musicInfo.getDuration()));
    }

    public void connectToService(){
        Intent intent = new Intent(this,MusicPlayerService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicPlayerService = ((MusicPlayerService.MusicPlayerBinder)service).getService();
                musicPlayerService.deliverMusicInfo.observe(MainActivity.this,musicInfo ->
                        musicPlayerBarViewModel.setMusicInfoLiveData(musicInfo));
                musicPlayerService.isPlaying.observe(MainActivity.this,
                        isPlaying-> musicPlayerBarViewModel.setIsPlaying(isPlaying));
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {}
        };
        startService(intent);
        this.bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void overrideOnStartTrackingTouch(){
        handler.removeCallbacks(updateseekbar);
    }
    private void overrideOnStopTrackingTouch() {
        handler.postDelayed(updateseekbar, DEFAULT_DELAY_MILLS);
    }

    private void initHandler(SeekBar bottomMusicBarProgress) {
        handler = new Handler();
        updateseekbar = new Runnable() {
            @Override
            public void run() {
                if(musicPlayerService!=null) {
                    bottomMusicBarProgress
                            .setProgress(musicPlayerService.mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, DEFAULT_DELAY_MILLS);
                }
            }
        };
        handler.postDelayed(updateseekbar, DEFAULT_DELAY_MILLS);
    }

    public MusicPlayerService getBindedService(){
        return musicPlayerService;
    }

}