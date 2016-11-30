package com.aalok.devnextdoor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Content
{

    private String rendered;


    public String getRendered ()
    {
        return rendered;
    }

    public void setRendered (String rendered)
    {
        this.rendered = rendered;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [ rendered = "+rendered+"]";
    }
}
