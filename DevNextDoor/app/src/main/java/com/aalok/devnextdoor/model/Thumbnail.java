package com.aalok.devnextdoor.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Thumbnail {

    @JsonProperty("medium_large")
    private Image medium_large;

    public Image getMedium_large ()
    {
        return medium_large;
    }

    public void setMedium_large (Image medium_large)
    {
        this.medium_large = medium_large;
    }
}
