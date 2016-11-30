package com.aalok.devnextdoor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown=true)
public class Post {

    private String comment_count;

    @JsonProperty("tags")
    private Tag[] tags;

    private String status;

    private String excerpt;

    private String comment_status;

    private String type;

    private String date;

    private String url;

    private String modified;

    private String id;

    private String content;

    private Author author;

    private String title;

    private String thumbnail_size;

    @JsonProperty("thumbnail_images")
    private Thumbnail thumbnail;

    private String slug;

    @JsonProperty("categories")
    private Category[] categories;

    //@JsonProperty("attachments")
    //private Attachment[] attachments;

    private String[] comments;

    private String title_plain;

    public String getComment_count ()
    {
        return comment_count;
    }

    public void setComment_count (String comment_count)
    {
        this.comment_count = comment_count;
    }

    public Tag[] getTags ()
    {
        return tags;
    }

    public void setTags (Tag[] tags)
    {
        this.tags = tags;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getExcerpt ()
    {
        return excerpt;
    }

    public void setExcerpt (String excerpt)
    {
        this.excerpt = excerpt;
    }

    public String getComment_status ()
    {
        return comment_status;
    }

    public void setComment_status (String comment_status)
    {
        this.comment_status = comment_status;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    public String getModified ()
    {
        return modified;
    }

    public void setModified (String modified)
    {
        this.modified = modified;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getContent ()
    {
        return content;
    }

    public void setContent (String content)
    {
        this.content = content;
    }

    public Author getAuthor ()
    {
        return author;
    }

    public void setAuthor (Author author)
    {
        this.author = author;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getThumbnail_size ()
    {
        return thumbnail_size;
    }

    public void setThumbnail_size (String thumbnail_size)
    {
        this.thumbnail_size = thumbnail_size;
    }

    public Thumbnail getThumbnail ()
    {
        return thumbnail;
    }

    public void setThumbnail (Thumbnail thumbnail)
    {
        this.thumbnail = thumbnail;
    }

    public String getSlug ()
    {
        return slug;
    }

    public void setSlug (String slug)
    {
        this.slug = slug;
    }

    public Category[] getCategories ()
    {
        return categories;
    }

    public void setCategories (Category[] categories)
    {
        this.categories = categories;
    }

    //public Attachment[] getAttachments ()
//    {
//        return attachments;
//    }

//    public void setAttachments (Attachment[] attachments)
//    {
//        this.attachments = attachments;
//    }

    public String[] getComments ()
    {
        return comments;
    }

    public void setComments (String[] comments)
    {
        this.comments = comments;
    }

    public String getTitle_plain ()
    {
        return title_plain;
    }

    public void setTitle_plain (String title_plain)
    {
        this.title_plain = title_plain;
    }

}
