package com.vetthai.app.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by th.panya.bas on 10/14/2015.
 */
public class Mapping {

    String status;

    int count;

    @SerializedName("count_total")
    int totalCount;

    int pages;

    List<Post> posts;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
