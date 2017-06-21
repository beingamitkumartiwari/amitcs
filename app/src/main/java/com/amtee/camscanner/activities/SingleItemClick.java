package com.amtee.camscanner.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.amtee.camscanner.R;
import com.amtee.camscanner.model_classes.MyDocItems;

/**
 * Created by Ankita on 02-11-2015.
 */
public class SingleItemClick extends Activity {
    ImageView pdfimage;
    Bundle extras;
    String pos;
    MyDocItems myDocItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_activity);
        myDocItems = new MyDocItems();
        pdfimage = (ImageView) findViewById(R.id.pdfimage);
        extras = getIntent().getExtras();
        if (extras != null) {
            pos = extras.getString("keyPosition");
            System.out.println("position" + pos);
            Bitmap bmImg = BitmapFactory.decodeFile(pos);
            pdfimage.setImageBitmap(bmImg);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
