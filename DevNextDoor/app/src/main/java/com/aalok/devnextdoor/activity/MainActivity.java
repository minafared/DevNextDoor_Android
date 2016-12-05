package com.aalok.devnextdoor.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aalok.devnextdoor.R;
import com.aalok.devnextdoor.RecyclerItemClickListener;
import com.aalok.devnextdoor.adapter.PostAdapter;
import com.aalok.devnextdoor.apiClient.ApiClient;
import com.aalok.devnextdoor.apiInterface.ApiInterface;
import com.aalok.devnextdoor.model.Post;
import com.aalok.devnextdoor.model.PostResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Different List variables so they can store posts for different categories
    PostResponse allPosts, thoughtPosts, tutorialPosts, experiencePosts, reviewPosts;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    int pastVisibleItems, visibleItemCount, totalItemCount; //Used for Lazy loading in RecyclerView
    private boolean isIncrementalLoading; //Used to check if Lazy loading is in progress
    LinearLayoutManager mLayoutManager;
    ProgressBar progressBar, incrementalProgressBar;
    LinearLayout loadErrorLayout;

    //Indicates the current category of posts being displayed
    //This string will be used throughout the Activity to check the current selected category
    String selectedCategory;
    TextView categoryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_recent); //Recent Post is selected in Navigation Drawer

        //Default selected category is Recent Posts when the App starts
        selectedCategory = "RECENT POSTS";

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        incrementalProgressBar = (ProgressBar) findViewById(R.id.incrementalProgressBar);
        loadErrorLayout = (LinearLayout) findViewById(R.id.load_error_layout);
        recyclerView = (RecyclerView)findViewById(R.id.PostRecyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        Button refreshButton = (Button) findViewById(R.id.refreshButton);
        categoryTextView = (TextView) findViewById(R.id.categoryTextView);

        //Set the TextView above the RecyclerView to reflect the current category being displayed
        categoryTextView.setText(selectedCategory);

        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setVisibility(View.GONE);

        // First Load of all posts
        loadAllPosts(1, true);

        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#303F9F"), Color.parseColor("#8BC34A"), Color.GREEN, Color.parseColor("#3F51B5"));
        swipeRefreshLayout.setOnRefreshListener((new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

          switch (selectedCategory)
          {
              case "RECENT POSTS":
                  loadAllPosts(1, true); // Refresh posts, same call as First Load

                  break;
              case "TUTORIALS":
                  loadPostsForCategory(1,true, getString(R.string.slug_tutorials));

                  break;
              case "THOUGHTS":
                  loadPostsForCategory(1,true, getString(R.string.slug_thoughts));

                  break;
              case "EXPERIENCES":
                  loadPostsForCategory(1,true, getString(R.string.slug_experiences));

                  break;
              case "REVIEWS":
                  loadPostsForCategory(1,true, getString(R.string.slug_reviews));

                  break;
          }

                onItemsLoadComplete();

            }
        }));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {

                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if ( (visibleItemCount + pastVisibleItems) >= totalItemCount)
                    {
                        PostResponse res = getPostResponseForCurrentCategory();
                        int page = res.getPage();

                        if(res.getPage() < Integer.parseInt(res.getPages()) && !isIncrementalLoading) // check if there are Pages available to load & are not already loading
                        {
                            page++;
                            res.setPage(page);

                            switch (selectedCategory)
                            {
                                case "RECENT POSTS":
                                    loadAllPosts(page, false);

                                    break;
                                case "TUTORIALS":
                                    loadPostsForCategory(page,false, getString(R.string.slug_tutorials));

                                    break;
                                case "THOUGHTS":
                                    loadPostsForCategory(page,false, getString(R.string.slug_thoughts));

                                    break;
                                case "EXPERIENCES":
                                    loadPostsForCategory(page,false, getString(R.string.slug_experiences));

                                    break;
                                case "REVIEWS":
                                    loadPostsForCategory(page,false, getString(R.string.slug_reviews));

                                    break;
                            }

                        }

                    }
                }
            }
        });


        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadErrorLayout.setVisibility(View.GONE);
                loadAllPosts(1,true);

            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getBaseContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        Intent intent = new Intent(getBaseContext(), PostActivity.class);
                        intent.putExtra("POST_TITLE", getPostResponseForCurrentCategory().getPosts().get(position).getTitle());
                        intent.putExtra("POST_CATEGORY", getPostResponseForCurrentCategory().getPosts().get(position).getCategories()[0].getTitle());
                        intent.putExtra("POST_THUMBNAIL_URL", getPostResponseForCurrentCategory().getPosts().get(position).getThumbnail().getMedium_large().getUrl());
                        intent.putExtra("POST_CONTENT", getPostResponseForCurrentCategory().getPosts().get(position).getContent());
                        intent.putExtra("POST_URL", getPostResponseForCurrentCategory().getPosts().get(position).getUrl());

                        startActivity(intent);
                    }

                    @Override public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }

    void onItemsLoadComplete() {

        swipeRefreshLayout.setRefreshing(false); //Disable SwipeRefresh animation
        PostResponse res = getPostResponseForCurrentCategory();
        res.setPage(1);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_recent) {

            selectedCategory = "RECENT POSTS";
            categoryTextView.setText(selectedCategory);
            if(allPosts!=null)
            {
                recyclerView.setAdapter(new PostAdapter(allPosts.getPosts(), R.layout.post_list_row, getBaseContext()));
            }
            else
            {
                loadAllPosts(1,true);
            }

        } else if (id == R.id.nav_tutorial) {

            selectedCategory = getString(R.string.slug_tutorials).toUpperCase();
            categoryTextView.setText(selectedCategory);
            if(tutorialPosts!=null)
            {
                recyclerView.setAdapter(new PostAdapter(tutorialPosts.getPosts(), R.layout.post_list_row, getBaseContext()));
            }
            else
            {
                loadPostsForCategory(1,true, getString(R.string.slug_tutorials));
            }


        } else if (id == R.id.nav_thoughts) {

            selectedCategory = getString(R.string.slug_thoughts).toUpperCase();
            categoryTextView.setText(selectedCategory);
            if(thoughtPosts!=null)
            {
                recyclerView.setAdapter(new PostAdapter(thoughtPosts.getPosts(), R.layout.post_list_row, getBaseContext()));

            }
            else
            {
                loadPostsForCategory(1,true, getString(R.string.slug_thoughts));
            }


        } else if (id == R.id.nav_experiences) {

            selectedCategory =  getString(R.string.slug_experiences).toUpperCase();
            categoryTextView.setText(selectedCategory);
            if(experiencePosts!=null)
            {
                recyclerView.setAdapter(new PostAdapter(experiencePosts.getPosts(), R.layout.post_list_row, getBaseContext()));
            }
            else
            {
                loadPostsForCategory(1,true, getString(R.string.slug_experiences));
            }


        } else if (id == R.id.nav_reviews) {

            selectedCategory = getString(R.string.slug_reviews).toUpperCase();
            categoryTextView.setText(selectedCategory);
            if(reviewPosts!=null)
            {
                recyclerView.setAdapter(new PostAdapter(reviewPosts.getPosts(), R.layout.post_list_row, getBaseContext()));
            }
            else
            {
                loadPostsForCategory(1,true, getString(R.string.slug_reviews));
            }
       }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadPostsForCategory(int pageNumber,final boolean isFirstLoad, final String slug)
    {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<PostResponse> call = apiService.getPostsForCategory(slug,pageNumber);


        if(isFirstLoad)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            isIncrementalLoading =true;
            incrementalProgressBar.setVisibility(View.VISIBLE);
        }

        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse>call, Response<PostResponse> response) {

                if(slug.toUpperCase().equals(selectedCategory))
                {
                    PostResponse postsRes = getPostResponseForCurrentCategory();

                    if(isFirstLoad)
                    {
                        postsRes = response.body();
                        setPostResponseForCategory(postsRes);
                    }
                    else
                    {
                        ArrayList<Post> temp = response.body().getPosts();
                        postsRes.posts.addAll(temp);
                        isIncrementalLoading = false;
                    }

                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new PostAdapter(postsRes.getPosts(), R.layout.post_list_row, getBaseContext()));

                    //Prevent from scrolling to Top when Incremental Loading occurs
                    if(!isFirstLoad)
                        recyclerView.scrollToPosition(pastVisibleItems + 1);

                    progressBar.setVisibility(View.GONE);
                    incrementalProgressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<PostResponse>call, Throwable t) {

                // Log error here since request failed
                Log.e("API Call Failed", t.toString());
                progressBar.setVisibility(View.GONE);

                if(isFirstLoad)
                {
                    //Show Error Layout since no posts to display

                    loadErrorLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else
                {
                    isIncrementalLoading = false;
                    incrementalProgressBar.setVisibility(View.GONE);
                }

            }
        });
    }

    private void loadAllPosts(int pageNumber,final boolean isFirstLoad)
    {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<PostResponse> call = apiService.getAllPosts(pageNumber);


        if(isFirstLoad)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            isIncrementalLoading = true;
            incrementalProgressBar.setVisibility(View.VISIBLE);
        }

        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse>call, Response<PostResponse> response) {

                if(selectedCategory.equals(getString(R.string.category_recent).toUpperCase()))
                {
                    final PostResponse postResponse = response.body();

                    if(isFirstLoad)
                    {
                        allPosts = postResponse;
                    }
                    else
                    {
                        ArrayList<Post> temp = postResponse.getPosts();
                        allPosts.posts.addAll(temp);
                        isIncrementalLoading = false;
                    }

                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new PostAdapter(allPosts.getPosts(), R.layout.post_list_row, getBaseContext()));

                    //Prevent from scrolling to Top when Incremental Loading occurs
                    if(!isFirstLoad)
                        recyclerView.scrollToPosition(pastVisibleItems + 1);

                    progressBar.setVisibility(View.GONE);
                    incrementalProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PostResponse>call, Throwable t) {

                // Log error here since request failed
                Log.e("API Call Failed", t.toString());
                progressBar.setVisibility(View.GONE);

                if(isFirstLoad)
                {
                    //Show Error Layout since no posts to display

                    loadErrorLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
                else
                {
                    isIncrementalLoading = false;
                    incrementalProgressBar.setVisibility(View.GONE);
                }

            }
        });
    }

    private PostResponse getPostResponseForCurrentCategory()
    {
        if(selectedCategory != null)
        {
            switch (selectedCategory)
            {
                case "RECENT POSTS":
                    return allPosts;

                case "TUTORIALS":
                    return tutorialPosts;

                case "THOUGHTS":
                    return thoughtPosts;

                case "EXPERIENCES":
                    return experiencePosts;

                case "REVIEWS":
                    return reviewPosts;

            }
        }

        return  allPosts;
    }

    private void setPostResponseForCategory(PostResponse res)
    {
        if(selectedCategory != null)
        {
            switch (selectedCategory)
            {
                case "RECENT POSTS":
                    allPosts = res;

                    break;
                case "TUTORIALS":
                    tutorialPosts = res;

                    break;
                case "THOUGHTS":
                    thoughtPosts = res;

                    break;
                case "EXPERIENCES":
                    experiencePosts = res;

                    break;
                case "REVIEWS":
                    reviewPosts = res;
                    break;
            }
        }
    }
}

