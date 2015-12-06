package com.protection.plpt.plpt;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sergey on 8/29/15.
 */
public class BaseActivity extends AppCompatActivity {

    void showProgress(boolean isProgressShown) {
        ViewGroup holder = (ViewGroup) findViewById(R.id.holder);
        if (isProgressShown) {

            for (int i = 0; i < holder.getChildCount(); i++) {
                holder.getChildAt(i).setVisibility(View.GONE);
            }
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < holder.getChildCount(); i++) {
                holder.getChildAt(i).setVisibility(View.VISIBLE);
            }
            findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
