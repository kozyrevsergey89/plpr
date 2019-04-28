package com.protection.plpt.plpt.mpkz.mpkz.method;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sergey on 5/26/16.
 */
public class CameraMethod {

  static List<String> fileNames = new ArrayList<>();

  @SuppressWarnings("deprecation")
  public static void takePhoto(final Context context, final String recipientAddress) {
    fileNames.clear();
    Handler mainHandler = new Handler(context.getMainLooper());

    Runnable myRunnable = new Runnable() {
      @Override
      public void run() {
        final SurfaceView preview = new SurfaceView(context);
        SurfaceHolder holder = preview.getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
          @Override
          //The preview must happen at or after this point or takePicture fails
          public void surfaceCreated(SurfaceHolder holder) {
            showMessage("Surface created");

            Camera camera = null;
            int cameraCount = 0;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            cameraCount = Camera.getNumberOfCameras();
            //for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            //SystemClock.sleep(1000);
            //  Camera.getCameraInfo(camIdx, cameraInfo);
            try {
              camera = Camera.open(1);
              showMessage("Opened camera");

              try {
                camera.setPreviewDisplay(holder);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }

              camera.startPreview();
              showMessage("Started preview");

              camera.takePicture(null, null, new Camera.PictureCallback() {

                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                  showMessage("Took picture");
                  if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    if (bitmap != null) {

                      File file = new File(Environment.getExternalStorageDirectory() + "/dirr");
                      if (!file.isDirectory()) {
                        file.mkdir();
                      }
                      String fileName = DateFormat.getDateInstance().format(new Date()) + "photo.jpg";
                      file = new File(Environment.getExternalStorageDirectory() + "/dirr",
                          fileName);
                      if (file.exists()) {
                        file.delete();
                      }
                      fileNames.add(Environment.getExternalStorageDirectory() + "/dirr/" +
                          fileName);

                      try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);

                        //rotating picture if needed
                        ExifInterface exif=new ExifInterface(file.toString());
                        Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
                        if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                          bitmap= rotate(bitmap, 90);
                        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                          bitmap= rotate(bitmap, 270);
                        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
                          bitmap= rotate(bitmap, 180);
                        } else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
                          bitmap= rotate(bitmap, 270);
                        }


                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

                        fileOutputStream.flush();
                        fileOutputStream.close();
                        //for (String file :
                        //    fileNames) {
                          EmailMethod.sendEmail(Environment.getExternalStorageDirectory() + "/dirr/" +
                              fileName, recipientAddress);
                        //}
                      } catch (IOException e) {
                        e.printStackTrace();
                      } catch (Exception exception) {
                        exception.printStackTrace();
                      }
                    }
                  }
                  camera.release();
                }
              });
            } catch (Exception e) {
              if (camera != null) {
                camera.release();
              }
              throw new RuntimeException(e);
            }
            //}
          }

          @Override public void surfaceDestroyed(SurfaceHolder holder) {
          }

          @Override
          public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
          }
        });

        WindowManager wm = (WindowManager) context
            .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            1, 1, //Must be at least 1x1
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            0,
            //Don't know if this is a safe default
            PixelFormat.UNKNOWN);

        //Don't set the preview visibility to GONE or INVISIBLE
        wm.addView(preview, params);
      }
    };
    mainHandler.post(myRunnable);

  }

  private static void showMessage(String message) {
    Log.i("Camera", message);
  }

  public static Bitmap rotate(Bitmap bitmap, int degree) {
    int w = bitmap.getWidth();
    int h = bitmap.getHeight();

    Matrix mtx = new Matrix();
    //       mtx.postRotate(degree);
    mtx.setRotate(degree);

    return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
  }
}
