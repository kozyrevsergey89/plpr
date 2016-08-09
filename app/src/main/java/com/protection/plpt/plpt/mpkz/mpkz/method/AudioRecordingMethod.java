package com.protection.plpt.plpt.mpkz.mpkz.method;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.Stack;

/**
 * Created by sergey on 5/27/16.
 */
public class AudioRecordingMethod {

    public static final int DELAY_MILLIS = 30000;
    private static MediaRecorder mediaRecorder;
    private static String fileName;
    private static MarkedRunnable newRecordingRunnable;
    private static MarkedRunnable finishRecordingRunnable;
    private static Handler mainHandler;


    public static void recordAudioDoJob(Context context, final String recipientAddress) {
        fileName = null;
        if (mainHandler == null) {
            mainHandler = new Handler(context.getMainLooper());
        }

        //final Handler localHandler = new Handler();
        long timeNow = System.currentTimeMillis();
        newRecordingRunnable = new MarkedRunnable(timeNow) {

            @Override
            public void run() {
//                stopRecording(recipientAddress);
                recordAudio();
            }
        };
        finishRecordingRunnable = new MarkedRunnable(timeNow) {
            @Override
            public void run() {
                stopRecording(recipientAddress);

            }
        };
        mainHandler.removeCallbacksAndMessages(null);
//        mainHandler.removeCallbacks(newRecordingRunnable);
//        mainHandler.removeCallbacks(finishRecordingRunnable);
        mainHandler.post(newRecordingRunnable);

        mainHandler.postDelayed(finishRecordingRunnable, DELAY_MILLIS);
    }

    private static void recordAudio() {
        File file = new File(Environment.getExternalStorageDirectory() + "/dirr");
        if (!file.isDirectory()) {
            file.mkdir();
        }
        String fileTimeName = DateFormat.getDateInstance().format(new Date()) + "audio.mp4";

        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        mediaRecorder = new MediaRecorder();
        //ContentValues values = new ContentValues(3);
        //values.put(MediaStore.MediaColumns.TITLE, fileName);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        fileName = Environment.getExternalStorageDirectory() + "/dirr/" +
                fileTimeName;
        mediaRecorder.setOutputFile(fileName);
        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    private static void stopRecording(final String recipientAddress) {
        if (mediaRecorder != null && newRecordingRunnable.millis == finishRecordingRunnable.millis) {
            mediaRecorder.stop();
            mediaRecorder.reset(); 
            mediaRecorder.release();
            mediaRecorder = null;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    EmailMethod.sendEmail(fileName, recipientAddress);
                }
            });
        }
    }

    private static abstract class MarkedRunnable implements Runnable {
        public long millis;

        public MarkedRunnable(long millis) {
            super();
            this.millis = millis;
        }
    }
}
