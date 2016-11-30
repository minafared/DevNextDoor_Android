package com.aalok.devnextdoor.activity;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.aalok.devnextdoor.R;
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

    List<Post> posts;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    int page = 1, totalPages; //Used in incremental loading
    int pastVisibleItems, visibleItemCount, totalItemCount;
    private boolean isIncrementalLoading; //Used to check if  incremental loading is in progress
    LinearLayoutManager mLayoutManager;
    ProgressBar progressBar, incrementalProgressBar;
    LinearLayout loadErrorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        incrementalProgressBar = (ProgressBar) findViewById(R.id.incrementalProgressBar);
        loadErrorLayout = (LinearLayout) findViewById(R.id.load_error_layout);

        recyclerView = (RecyclerView)findViewById(R.id.PostRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setVisibility(View.GONE);

        loadAllPosts(page, true); // First Load of all posts

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#303F9F"), Color.parseColor("#8BC34A"), Color.GREEN, Color.parseColor("#3F51B5"));
        swipeRefreshLayout.setOnRefreshListener((new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadAllPosts(1, true); // Refresh posts, same call as First Load
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
                        if(page < totalPages && !isIncrementalLoading) // check if there are Pages available to load & are not already loading
                        {
                            page++;
                            loadAllPosts(page, false); // Incremental Loading call
                        }

                    }
                }
            }
        });

        Button refreshButton = (Button) findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadErrorLayout.setVisibility(View.GONE);
                loadAllPosts(1,true);

            }
        });
    }

    void onItemsLoadComplete() {

        swipeRefreshLayout.setRefreshing(false); //Disable SwipeRefresh animation
        page = 1; //Reset Page count
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
            isIncrementalLoading =true;
            incrementalProgressBar.setVisibility(View.VISIBLE);
        }

        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse>call, Response<PostResponse> response) {

                final PostResponse postResponse = response.body();


                if(isFirstLoad)
                {
                    totalPages = Integer.parseInt(postResponse.getPages());

                    //Creating modifiable List fom array
                    posts = new ArrayList(Arrays.asList(postResponse.getPosts()));

                }
                else
                {
                    List<Post> temp = Arrays.asList(postResponse.getPosts());

                    //Adding newly loaded posts to existing List
                    posts.addAll(temp);
                    isIncrementalLoading = false;

                }

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(new PostAdapter(posts, R.layout.post_list_row, getBaseContext()));

                //Prevent from scrolling to Top when Incremental Loading occurs
                if(!isFirstLoad)
                    recyclerView.scrollToPosition(pastVisibleItems + 1);

                progressBar.setVisibility(View.GONE);
                incrementalProgressBar.setVisibility(View.GONE);

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

            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
