package com.apps.koru.star8_video_app.apputils;

public class OfflinePlayList {
//    private Model appModel = Model.getInstance();
//    private SharedPreferences sharedPreferences;
//    private Context context;
//    private String message;
//
//    public OfflinePlayList() {
//        EventBus.getDefault().register(this);
//    }
//    @Subscribe
//    public void onEvent(GetOfflinePlayListEvent event) {
//        context = event.getContext();
//        message = event.getMessage();
//        Log.d("function", "loadThePlayList called");
//
//        final int[] size = new int[1];
//        appModel.uriPlayList.clear();
//        AsyncHandler.post(() -> {
//            sharedPreferences = context.getSharedPreferences("play_list", Context.MODE_PRIVATE);
//            UiHandler.post(() -> {
//                size[0] = sharedPreferences.getInt("size", 0);
//                for(int i=0;i<size[0];i++)
//                {
//                    try {
//                        appModel.dbList.add(i, sharedPreferences.getString("db_"+i,null));
//                        appModel.uriPlayList.add(i, Uri.parse(sharedPreferences.getString("video_"+i, null)));
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//
//                }
//                Log.e("**loading", " is finished");
//                if (message.equals("delete")){
//                    EventBus.getDefault().post(new DeleteVideosEvent(appModel.dbList,"delete"));
//                } else if(message.equals("offline")) {
//                    EventBus.getDefault().post(new DeleteVideosEvent(appModel.dbList,"offline"));
//                }
//            });
//        });
//    }
}

