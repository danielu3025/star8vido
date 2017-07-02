package com.apps.koru.star8_video_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.apps.koru.star8_video_app.events.DownloadCompleteEvent;
import com.apps.koru.star8_video_app.sharedutils.AsyncHandler;
import com.apps.koru.star8_video_app.sharedutils.UiHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import static com.apps.koru.star8_video_app.MainActivity.videoView;

public class VideoPlayer {
    private SharedPreferences sharedPreferences;
    private Context context;
    private Model appModel = Model.getInstance();
    private int onTrack =0;

    public VideoPlayer(Context context) {

        EventBus.getDefault().register(this);
        this.context=context;
    }
    @Subscribe
    public void onEvent(DownloadCompleteEvent event) {
        System.out.println("lets playyy!!!!!");
        MainActivity.infoBt.setText("");
        MainActivity.infoBt.setVisibility(View.INVISIBLE);
        MainActivity.videoView.stopPlayback();
        //get Uri play List
        File lf[] = appModel.videoDir.listFiles();
        ArrayList <String >folderList = new ArrayList<>();
        for (File file : lf){
            folderList.add(file.getAbsolutePath());
        }

        appModel.uriPlayList.clear();

        for (String path :appModel.dbList){
            appModel.uriPlayList.add(Uri.parse(path));
        }
        appModel.dbList.clear();
            onTrack = 0;
            MainActivity.videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
            MainActivity.videoView.start();
        saveThePlayList();
        MainActivity.videoView.setOnErrorListener((mp, what, extra) -> {
            Log.d("Error"," - playing video error");
            if (onTrack > 0){
                if (onTrack != appModel.uriPlayList.size()){
                    MainActivity.videoView.setVideoURI(appModel.uriPlayList.get(onTrack + 1));
                }
                else {
                    MainActivity.videoView.setVideoURI(appModel.uriPlayList.get(0));
                }
            }
            else {
                MainActivity.videoView.setVideoURI(appModel.uriPlayList.get(0));
            }
            MainActivity.videoView.start();
            return true;
        });
        MainActivity.videoView.setOnCompletionListener(mp -> {
                 if (onTrack<appModel.uriPlayList.size()){
                     onTrack++;
                 }
                 if (onTrack==appModel.uriPlayList.size()){
                     onTrack = 0;
                 }
                MainActivity.videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
                MainActivity.videoView.start();
            });
    }

    private void saveThePlayList() {
        AsyncHandler.post(() -> {
            sharedPreferences = context.getSharedPreferences("play_list", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("size", appModel.uriPlayList.size());

            for(int i=0;i<appModel.uriPlayList.size();i++)
            {
                editor.putString("video_" + i, String.valueOf(appModel.uriPlayList.get(i)));
            }

            editor.apply();
        });
    }

    public void loadThePlayList(){
        Log.d("function", "loadThePlayList called");

        final int[] size = new int[1];
        appModel.uriPlayList.clear();
        AsyncHandler.post(() -> {
            sharedPreferences = context.getSharedPreferences("play_list", Context.MODE_PRIVATE);
            UiHandler.post(() -> {
                size[0] = sharedPreferences.getInt("size", 0);
                for(int i=0;i<size[0];i++)
                {
                    appModel.uriPlayList.add(i,Uri.parse(sharedPreferences.getString("video_"+i, null)));

                }
                Log.e("function", "isFinishLoading");
                playOffline();
            });
        });
    }

    public void playOffline(){
        Log.d("function", "PlayOffline called");
        onTrack = 0;
        videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
        videoView.start();
        MainActivity.videoView.setOnCompletionListener(mp -> {
            if(MainActivity.isConnection){
                Log.d("function", "isConnected");
            }
            if (onTrack < appModel.uriPlayList.size()-1) {
                onTrack++;
            } else {
                onTrack = 0;
            }
            videoView.setVideoURI(appModel.uriPlayList.get(onTrack));
            videoView.start();
        });
    }
}
