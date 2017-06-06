package com.apps.koru.star8_video_app;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


import com.apps.koru.star8_video_app.sharedutils.AsyncHandler;
import com.apps.koru.star8_video_app.sharedutils.UiHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;


import static android.content.Context.DOWNLOAD_SERVICE;
import static com.apps.koru.star8_video_app.MainActivity.database;
import static com.apps.koru.star8_video_app.MainActivity.mainPlayList;
import static com.apps.koru.star8_video_app.MainActivity.mainVideoView;


public class PlayList extends AppCompatActivity {
    static ArrayList<Uri> uriPlayList = new ArrayList<>();
    String event;
    ArrayList<String> list = new ArrayList<>();
    int onTrack =-1;int dcount = 0; int p = 0; int pstep = 1; boolean flag = false;
    ArrayList<String> playlistFileNames = new ArrayList<>();
    ArrayList<String> videoListphats = new ArrayList<>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef;
    Context context;
    public   ProgressDialog progressDialog;
    File videoDir;
    boolean downloadFinishd = true;
    DatabaseReference playlistNode;
    DataSnapshot listSnapshot;
    private FirebaseAnalytics mFirebaseAnalytics;
    SharedPreferences sharedPreferences;

    public PlayList(Context contex) {
        Log.d("function","PlayList contractor calld");
        context = contex;
        // create a File object for the parent directory
        videoDir = new File(context.getExternalCacheDir().getAbsolutePath()+"/playlist1");
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(contex);

        progressDialog = null;
    }

    public void downloadPlaylist(String playlistName){
        Log.d("function", "downloadPlaylist calld");
        //get the playlist files name

        playlistNode = MainActivity.database.getReference(playlistName);

        playlistNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mainVideoView.stopPlayback();
                //get playlist files names
                listSnapshot = snapshot;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    playlistFileNames.add((String) postSnapshot.getValue() + ".mp4");
                    videoListphats.add(videoDir.getAbsolutePath() + "/" + (String) postSnapshot.getValue() + ".mp4");
                }
                playlistFileNames = new ArrayList<String>(new LinkedHashSet<String>(playlistFileNames));
                videoListphats = new ArrayList<String>(new LinkedHashSet<String>(videoListphats));
                //check if playlist Folder is exists
                if (checkFolderExists(videoDir)) {
                    //all videos are in storage ?
                    switch (allVideosOnDevice(videoDir, playlistFileNames)) {
                        case 1: // all videos is in storage
                            mainPlayList.list.removeAll(list);
                            for (int i = 0; i < playlistFileNames.size(); i++) {
                                mainPlayList.list.add(videoDir.getAbsolutePath() + "/" + playlistFileNames.get(i));
                            }
                            playTheplayList();
                            break;
                        case 2:// not all videos are in the storage
                            downloadMissVideos(videoDir, videoListphats);
                            mainPlayList.list.clear();
                            for (int i = 0; i < playlistFileNames.size(); i++) {
                                mainPlayList.list.add(videoDir.getAbsolutePath() + "/" + playlistFileNames.get(i));
                            }
                            break;
                        default:
                    }
                    ArrayList<String> temp = new ArrayList<String>(playlistFileNames);
                    playlistFileNames.clear();
                    videoListphats.clear();

                    //Collections.sort(mainPlayList.list);
                    //mainPlayList.list = new ArrayList<String>(new LinkedHashSet<String>(mainPlayList.list));

                    File[] folder = videoDir.listFiles();
                    ArrayList<String> tempString = new ArrayList<>();
                    for (File path : folder) {
                        tempString.add(path.getAbsolutePath());
                    }
                    //playTheplayList(temp);
                } else {
                    try {
                        videoDir.mkdirs();
                        downloadPlaylist("testPlaylist");
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
             @Override
             public void onCancelled(DatabaseError databaseError) {
                 Log.e("The read failed: ", databaseError.getMessage());
             }
        });
    }

    private void saveThePlayList() {
        AsyncHandler.post(() -> {
            sharedPreferences = context.getSharedPreferences("play_list", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("size", uriPlayList.size());

            for(int i=0;i<uriPlayList.size();i++)
            {
                editor.putString("video_" + i, String.valueOf(uriPlayList.get(i)));
            }

            editor.apply();
        });
    }

    public void loadThePlayList(){
        final int[] size = new int[1];
        uriPlayList.clear();
        AsyncHandler.post(() -> {
            sharedPreferences = context.getSharedPreferences("play_list", Context.MODE_PRIVATE);
            UiHandler.post(() -> {
                size[0] = sharedPreferences.getInt("size", 0);
                for(int i=0;i<size[0];i++)
                {
                    uriPlayList.add(i,Uri.parse(sharedPreferences.getString("video_"+i, null)));

                }
                Log.e("function", "isfinishloading");
                playOffline();
            });
        });
    }

    public void playOffline(){
        if(uriPlayList.size()==0){
            Toast.makeText(context, "no internet and no playlist played before",
                    Toast.LENGTH_LONG).show();
        } else {
            onTrack = 0;
            //mainVideoView.setVideoPath(mainPlayList.list.get(mainPlayList.onTrack));
            mainVideoView.setVideoURI(uriPlayList.get(onTrack));
            mainVideoView.start();
            MainActivity.mainVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    if (onTrack < uriPlayList.size()) {
                        onTrack++;
                    } else {
                        onTrack = 0;
                    }
                    //  mainVideoView.setVideoPath(mainPlayList.list.get(onTrack));
                    mainVideoView.setVideoURI(uriPlayList.get(onTrack));
                    mainVideoView.start();
                }
            });
        }
    }
    public void playTheplayList(){
        uriPlayList.clear();
        int counter = 0;
        Log.d("function","playTheplayList calld");
        Iterable<DataSnapshot> list =  listSnapshot.getChildren();
        mainPlayList.list.clear();
        for (DataSnapshot data: list){
            mainPlayList.list.add( videoDir.getAbsolutePath()+"/"+data.getValue().toString() + ".mp4");
            counter++;
        }
        File[] lf = videoDir.listFiles();
        ArrayList<String> temp = new ArrayList();
        for (File file :lf){
            temp.add(file.getAbsolutePath());
        }
        for (String path :mainPlayList.list){
            uriPlayList.add(Uri.parse(path));
        }
        if (progressDialog != null){
            if (progressDialog.isShowing()){
                dismissProgressDialog();
                progressDialog.dismiss();
            }
        }

       // mainVideoView.setVideoPath(mainPlayList.list.get(0));
        if (temp.containsAll(mainPlayList.list)){
            final Bundle bundle = new Bundle();
            //mainVideoView.setVideoPath(mainPlayList.list.get(0));
            mainPlayList.onTrack =0;
            //mainVideoView.setVideoPath(mainPlayList.list.get(mainPlayList.onTrack));

            mainVideoView.setVideoURI(uriPlayList.get(onTrack));
            mainVideoView.start();
            MainActivity.mainVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Log.d("function","playTheplayList - on complate calld");

                    if (mainPlayList.onTrack < MainActivity.mainPlayList.list.size()){
                        mainPlayList.onTrack++;
                    }
                    if (onTrack == MainActivity.mainPlayList.list.size()){
                        mainPlayList.onTrack=0;
                    }

                  //  mainVideoView.setVideoPath(mainPlayList.list.get(onTrack));
                    mainVideoView.setVideoURI(uriPlayList.get(onTrack));

                    saveThePlayList();

                    mainVideoView.start();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mainPlayList.list.get(onTrack));
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mainPlayList.list.get(onTrack));
                    bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "video");
                    try{
                        event = uriPlayList.get(onTrack-1).toString();
                        event = event.substring(event.lastIndexOf("/") + 1);

                        mFirebaseAnalytics.logEvent(event,bundle);

                    }catch (Exception e){
                        e.getCause();
                    }
                }
            });
        }
    }

    private boolean checkFolderExists(File dir){
        Log.d("function","checkFolderExists calld");

        if (dir.exists()){
            return true;
        }
        return false;
    }

    private void downloadMissVideos(File dir, ArrayList<String> playlistPaths) {
        Log.d("function","downloadMissVideos calld");

        File[] lf = dir.listFiles();
        ArrayList<String> onDevice = new ArrayList<>();
        ArrayList<String> toDownloadList = new ArrayList<>();
        if (lf != null) {
            for (int i =0 ;i<lf.length;i++){
                onDevice.add(lf[i].getAbsolutePath());
            }
            if (playlistPaths.size() == onDevice.size() || playlistPaths.size() < onDevice.size() ){
                for(String path : playlistPaths){
                    if (!onDevice.contains(path)){
                        File temp =new File(path);
                        toDownloadList.add(temp.getName());
                    }
                }
            }
            else if (playlistPaths.size() > onDevice.size()){
                for(String devicePath : onDevice){
                    if (playlistPaths.contains(devicePath)){
                        playlistPaths.remove(devicePath);
                    }
                }
                for (String path : playlistPaths){
                    File temp =new File(path);
                    toDownloadList.add(temp.getName());
                }
            }
            fetchFilesFromFireBaseStorage(toDownloadList);

        }

    }

    private void fetchFilesFromFireBaseStorage(final ArrayList<String> toDownloadList){
        Log.d("function","fetchFilesFromFireBaseStorage calld");

        try {
            downloadFinishd = false;
            pstep = 100/toDownloadList.size();
            showProgressDialog("Downloading Playlist","complete: "+ p  + "%");
            for (String fileName : toDownloadList) {
                storageRef = storage.getReferenceFromUrl("gs://star8videoapp.appspot.com").child(fileName);
                final File videoFile = new File(videoDir, fileName);
                storageRef.getFile(videoFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d("function","fetchFilesFromFireBaseStorage - onSucsees calld");

                        System.out.println("make file: " + videoFile.getPath());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("function","fetchFilesFromFireBaseStorage - onFail calld");

                        dismissProgressDialog();
                        showProgressDialog("connection Error",exception.getMessage());
                        exception.getCause();
                    }
                }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        Log.d("function","fetchFilesFromFireBaseStorage - on complate calld");

                        dcount++;
                        p=p+pstep;
                        progressDialog.setMessage("complete: "+ p  + "%");
                        if (dcount == toDownloadList.size()){
                            Log.d("status:","complete");
                            dcount = 0;
                            p = 0;
                            downloadFinishd = true;
                            dismissProgressDialog();
                            playTheplayList();
                        }
                    }
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
            showProgressDialog("Downloading  Error",e.getMessage());
            Log.e("Main", "IOE Exception");
        }
    }

    private int allVideosOnDevice(File dir, ArrayList<String> filesName) {
        Log.d("function","allVideosOnDevice calld");

        int fleg = 2;
        File[] lf = dir.listFiles();
        if (lf != null) {
            ArrayList<String> onDevice = new ArrayList<>();
            ArrayList<String> needToBeOnDevice = new ArrayList<>();
            for (File filePath : lf) {
                onDevice.add(filePath.getAbsolutePath());
            }
            for (String name : filesName) {
                needToBeOnDevice.add(dir.getAbsolutePath() + "/" + name);
            }
            if (onDevice.containsAll(needToBeOnDevice)) {
                fleg =  1;
            }
            else {
                fleg =2;
            }
        }
        return  fleg;
    }

    protected void showProgressDialog(String title, String msg) {
        Log.d("function","showProgressDialog calld");

        progressDialog = ProgressDialog.show(context, title, msg, true);
    }

    protected void dismissProgressDialog() {
        Log.d("function","dismissProgressDialog calld");
        progressDialog.dismiss();
    }
}
