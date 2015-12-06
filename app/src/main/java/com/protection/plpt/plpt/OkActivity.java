package com.protection.plpt.plpt;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.protection.plpt.plpt.mpkz.mpkz.utils.SharedUtils;

/**
 * Created by sergey on 9/2/15.
 */
public class OkActivity extends BaseActivity {

    public static final int DONE_BACKUP = 1;
    public static final int DONE_RESTORE = 2;
    public static final int DONE_FOUND = 3;
    private static final String DONE_EXTRA_KEY = "done_extra_key";


    private Ringtone ringtone;
    private String soundUriString;
    private Uri notification;

    public static void startNewTask(Context context, int doneExtraKey) {
        Intent intent = new Intent(context, OkActivity.class);
        intent.putExtra(DONE_EXTRA_KEY, doneExtraKey);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void start(Context context, int doneExtraKey) {
        Intent intent = new Intent(context, OkActivity.class);
        intent.putExtra(DONE_EXTRA_KEY, doneExtraKey);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ok_activity_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView doneText = (TextView) findViewById(R.id.done_text);
        switch (getIntent().getIntExtra(DONE_EXTRA_KEY, 0)) {
            case DONE_BACKUP:
                doneText.setText(getString(R.string.done_backup_label));
                break;
            case DONE_RESTORE:
                doneText.setText(getString(R.string.done_restore_label));
                break;
            case DONE_FOUND:
                doneText.setText(getString(R.string.done_found_label));

                soundUriString = SharedUtils.getFromShared(this, "SOUND_URI_STRING");
                if (soundUriString == null) {
                    notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                } else {
                    notification = Uri.parse(soundUriString);
                }

                ringtone = RingtoneManager.getRingtone(this, notification);

                AudioManager audioManager =
                        (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_PLAY_SOUND);

                ringtone.setStreamType(AudioManager.STREAM_RING);
//                Button discad = (Button) findViewById(R.id.discad_button);
//                if (ringtone != null && ringtone.isPlaying()) {
//                    ringtone.stop();
//                    ringtone = null;
//                }
//                finish();


                break;
        }

        findViewById(R.id.done_holder).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (ringtone != null && ringtone.isPlaying()) {
                    ringtone.stop();
                    ringtone = null;
                }
                finish();
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ringtone != null && !ringtone.isPlaying()) {
            ringtone.play();
        }
    }
}
