package com.example.android.goodslist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ShowImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        ImageView imageView = (ImageView) findViewById(R.id.show_image);
        Intent intent = getIntent();
        if (intent != null) {

            byte[] buff = intent.getByteArrayExtra("image");
            Bitmap bitmap = BitmapFactory.decodeByteArray(buff, 0, buff.length);
            imageView.setImageBitmap(bitmap);
        } else {
            finish();
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
