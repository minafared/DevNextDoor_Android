package com.aalok.devnextdoor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;


@JsonIgnoreProperties(ignoreUnknown=true)
public class PostResponse {

    public PostResponse() {
        this.page = 1;
    }

    private int page; //used for Lazy loading

    private String count;

    private String status;

    private String pages;

    private String count_total;

    public ArrayList<Post> posts;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getCount ()
    {
        return count;
    }

    public void setCount (String count)
    {
        this.count = count;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getPages ()
    {
        return pages;
    }

    public void setPages (String pages)
    {
        this.pages = pages;
    }

    public ArrayList<Post> getPosts ()
    {
        return posts;
    }

    public void setPosts (ArrayList<Post> posts)
    {
        this.posts = posts;
    }

    public String getCount_total ()
    {
        return count_total;
    }

    public void setCount_total (String count_total)
    {
        this.count_total = count_total;
    }

}
