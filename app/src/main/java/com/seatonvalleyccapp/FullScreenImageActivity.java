package com.seatonvalleyccapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

/**
 * Created by julius on 29/03/2018.
 */

public class FullScreenImageActivity extends Activity {
    ImageView mImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_full_screen_image);
        mImage = (ImageView) findViewById(R.id.iv_full_screen_image);

        Bitmap bmp = GlobalVariables.currentBitmap;

        mImage.setImageBitmap(bmp);
        super.onCreate(savedInstanceState);
    }
}
