package com.aalok.devnextdoor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown=true)
public class PostResponse {

    private String count;

    private String status;

    private String pages;

    private String count_total;

    public Post[] posts;

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

    public Post[] getPosts ()
    {
        return posts;
    }

    public void setPosts (Post[] posts)
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
