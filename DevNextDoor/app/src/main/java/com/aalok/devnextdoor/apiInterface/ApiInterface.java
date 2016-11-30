package com.aalok.devnextdoor.apiInterface;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import com.aalok.devnextdoor.model.PostResponse;

public interface ApiInterface {

    @GET("get_posts")
    Call<PostResponse> getAllPosts(@Query("page") int page);

}