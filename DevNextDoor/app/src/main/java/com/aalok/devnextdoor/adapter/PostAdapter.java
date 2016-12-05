package com.aalok.devnextdoor.adapter;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.CardView;

import com.aalok.devnextdoor.App;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.AnimationUtils;

import com.aalok.devnextdoor.RecyclerItemClickListener;
import com.aalok.devnextdoor.model.Post;
import com.aalok.devnextdoor.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private ArrayList<Post> posts;
    private int rowLayout;
    private Context context;
    private int lastPosition = -1;
    ImageLoader imageLoader;


    public static class PostViewHolder extends RecyclerView.ViewHolder{

        TextView PostTitleView;
        TextView ExcerptView;
        ImageView PostImageView;
        CardView cardView;


        public PostViewHolder(View v) {
            super(v);
            PostTitleView = (TextView) v.findViewById(R.id.PostTitleView);
            PostImageView = (ImageView) v.findViewById(R.id.PostImageView);
            ExcerptView = (TextView) v.findViewById(R.id.ExcerptView);
            cardView = (CardView) v.findViewById(R.id.card_view);

        }

    }

    public PostAdapter(ArrayList<Post> posts, int rowLayout, Context context) {
        this.posts = posts;
        this.rowLayout = rowLayout;
        this.context = context;

        imageLoader = ((App)context.getApplicationContext()).getImageLoader();

        notifyItemRangeInserted(0, posts.size());
    }


    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final PostViewHolder holder, final int position) {


        if (Build.VERSION.SDK_INT >= 24)
        {
            holder.PostTitleView.setText(Html.fromHtml(posts.get(position).getTitle(),Html.FROM_HTML_MODE_LEGACY));
            holder.ExcerptView.setText(Html.fromHtml(posts.get(position).getExcerpt(),Html.FROM_HTML_MODE_LEGACY));

        }
        else
        {
            holder.PostTitleView.setText(Html.fromHtml(posts.get(position).getTitle()));
            holder.ExcerptView.setText(Html.fromHtml(posts.get(position).getExcerpt()));
        }

        if(posts.get(position).getThumbnail() != null)
        {
            imageLoader.displayImage(posts.get(position).getThumbnail().getMedium_large().getUrl(),holder.PostImageView);

        }
        // Here you apply the animation when the view is bound
        setAnimation(holder.cardView, position);

    }

    @Override
    public int getItemCount() {

        return posts.size();
    }

    @Override
    public void onViewDetachedFromWindow(PostViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        holder.cardView.clearAnimation();
    }


    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
