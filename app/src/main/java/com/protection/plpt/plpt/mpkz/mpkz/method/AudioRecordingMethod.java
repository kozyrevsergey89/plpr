package com.protection.plpt.plpt.mpkz.mpkz.method;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by sergey on 5/27/16.
 */
public class AudioRecordingMethod {

  public static final int DELAY_MILLIS = 30000;
  private static MediaRecorder mediaRecorder;
  private static String fileName;

  public static void recordAudioDoJob(Context context, final String recipientAddress) {
    fileName = null;
    final Handler mainHandler = new Handler(context.getMainLooper());
    //final Handler localHandler = new Handler();
    mainHandler.post(new Runnable() {
      @Override public void run() {
        recordAudio();
      }
    });
    mainHandler.postDelayed(new Runnable() {
      @Override public void run() {
        stopRecording();
        mainHandler.post(new Runnable() {
          @Override public void run() {
            EmailMethod.sendEmail(fileName, recipientAddress);
          }
        });
      }
    }, DELAY_MILLIS);
  }

  private static void recordAudio() {
    File file = new File(Environment.getExternalStorageDirectory() + "/dirr");
    if (!file.isDirectory()) {
      file.mkdir();
    }
    String fileTimeName = DateFormat.getDateInstance().format(new Date()) + "audio.mp4";
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

  private static void stopRecording() {
    mediaRecorder.stop();
    mediaRecorder.release();
  }
}
