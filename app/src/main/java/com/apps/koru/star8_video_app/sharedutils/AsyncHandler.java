/*
 * Copyright (C) 2016 MindTheApps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apps.koru.star8_video_app.sharedutils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Helper class for managing the background thread used to perform io operations
 * and handle async broadcasts.
 * <p>
 * This class has a collection of helper methods useful in any app, you can take it as-is or change whatever you want.
 * I use a similar Util class in my production apps, and publish it here for an Android Course I give at Shenkar Engineering College
 *
 * @author amir uval
 */
public final class AsyncHandler {

    private static final String TAG = AsyncHandler.class.getSimpleName();
    private static final Handler sHandler;
    private static final HandlerThread sHandlerThread = new HandlerThread(AsyncHandler.class.getSimpleName());

    static final int EXAMPLE_MESSAGE_1 = 1;
    static final int EXAMPLE_MESSAGE_2 = 2;

    static {
        sHandlerThread.start();
        sHandler = new Handler(sHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                @AsyncMessage int what = msg.what;
                switch (what) {
                    case EXAMPLE_MESSAGE_1:
                        Log.i(TAG, "Hey, got message no. 1!! (I could do something useful with it)");
                        break;
                    case EXAMPLE_MESSAGE_2:
                        Log.i(TAG, "Hey, got message no. 2!! (I could do something else)");
                        break;
                }

            }
        };
    }


    /**
     * We don't need to instantiate this class. Everything is static here.
     */
    private AsyncHandler() {
    }

    /**
     * run anything safely on the thread looper
     *
     * @param r
     */
    public static void post(Runnable r) {
        sHandler.post(r);
    }

    /**
     * beware:
     * it will not fire while in deep sleep. (as handler depends on
     * uptimeMillis = not advancing while off).
     * deep sleep will postpone post.
     *
     * @param r
     * @param timeStamp
     * @param removeOlder
     */
    public static void postAtTime(Runnable r, long timeStamp, boolean removeOlder) {
        if (removeOlder) {
            sHandler.removeCallbacks(r);
        }
        sHandler.postDelayed(r, timeStamp - System.currentTimeMillis());
    }

    /**
     * beware:
     * it will not fire while in deep sleep. (as handler depends on
     * uptimeMillis = not advancing while off).
     * deep sleep will postpone post.
     *
     * @param r
     * @param delayMillis
     * @param removeOlder
     */
    public static void postDelayed(Runnable r, long delayMillis,
                                   boolean removeOlder) {
        if (removeOlder) {
            sHandler.removeCallbacks(r);
        }
        sHandler.postDelayed(r, delayMillis);
    }

    /**
     * Quick way to run common things asynchronously
     *
     * @param what
     * @param obj
     */
    public static void postMessage(@AsyncMessage int what, Object obj, String comment) {
        Message message = sHandler.obtainMessage(what);
        message.obj = obj;
        message.getData().putString("c", comment);
        sHandler.sendMessage(message);

    }

    public static void removeAllCallbacks() {
        sHandler.removeCallbacksAndMessages(null);
    }

    public static void removeCallbacks(Runnable r) {
        sHandler.removeCallbacks(r);

    }

    @Retention(CLASS)
    @IntDef({
            EXAMPLE_MESSAGE_1,
            EXAMPLE_MESSAGE_2,
            // you can add more
    })
    @interface AsyncMessage {
    }

}
