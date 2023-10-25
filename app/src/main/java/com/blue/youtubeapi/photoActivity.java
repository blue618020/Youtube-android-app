package com.blue.youtubeapi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.blue.youtubeapi.model.Post;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class photoActivity extends AppCompatActivity {

    ImageView imgPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        Post post = (Post) getIntent().getSerializableExtra("post");
        imgPhoto = findViewById(R.id.imgPhoto);

        Glide.with(photoActivity.this).load(post.highUrl).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                // scaleType -> centerInside
                // 글라이드 통해서 이미지를 네트워크로부터 받아오면 이미지를 centerCrop 하기 또는 fitXY 하기

                // 네트워크를 통해 뭔가 하고싶으면 여기서 작성하기
                // 스낵바나 토스트를 띄우는것도 여기

                imgPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return false;
            }
        }).into(imgPhoto);
    }
}