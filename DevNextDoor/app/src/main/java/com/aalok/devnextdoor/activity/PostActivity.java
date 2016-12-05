package com.aalok.devnextdoor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aalok.devnextdoor.App;
import com.aalok.devnextdoor.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PostActivity extends AppCompatActivity
        implements AppBarLayout.OnOffsetChangedListener  {

    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    ImageLoader imageLoader;
    String postUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mFab = findViewById(R.id.fab);
        ImageView featureImageView = (ImageView) findViewById(R.id.featureImageView);
        TextView categoryTextView = (TextView) findViewById(R.id.categoryTextView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.flexible_example_toolbar);
        TextView contentTextView = (TextView) findViewById(R.id.contentTextView);
        CollapsingToolbarLayout titleToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.flexibleAppbar);
        appbar.addOnOffsetChangedListener(this);

        imageLoader = ((App)getApplicationContext()).getImageLoader();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            String postTitle = extras.getString("POST_TITLE");
            String postCategory = extras.getString("POST_CATEGORY");
            String postThumbnailUrl = extras.getString("POST_THUMBNAIL_URL");
            String postContent = extras.getString("POST_CONTENT");
            postUrl = extras.getString("POST_URL");

            imageLoader.displayImage(postThumbnailUrl,featureImageView);

            if (Build.VERSION.SDK_INT >= 24)
            {
                titleToolbar.setTitle(Html.fromHtml(postTitle,Html.FROM_HTML_MODE_LEGACY));
                categoryTextView.setText(Html.fromHtml("POSTED IN "+postCategory,Html.FROM_HTML_MODE_LEGACY));
                contentTextView.setText(Html.fromHtml(postContent,Html.FROM_HTML_MODE_LEGACY));

            }
            else
            {
                titleToolbar.setTitle(Html.fromHtml(postTitle));
                categoryTextView.setText(Html.fromHtml("POSTED IN "+postCategory));
                contentTextView.setText(Html.fromHtml(postContent));
            }
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(postUrl));
                startActivity(i);

            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(i)) * 100
                / mMaxScrollSize;

        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;

                ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
            }
        }

        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
            }
        }
    }
}
