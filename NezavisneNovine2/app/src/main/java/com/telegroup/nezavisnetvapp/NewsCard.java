package com.telegroup.nezavisnetvapp;

import java.io.Serializable;

/**
 * Created by Macbook on 3/28/2018.
 */

public class NewsCard implements Serializable {
    private String title;
    private String newsId;
    private String lid;
    private String imageUrl;

    private String date;
    private  String author;
    private String menuId;

    public NewsCard(String title, String newsId, String lid, String imageUrl, String date, String author, String menuId) {
        this.title = title;
        this.newsId = newsId;
        this.lid = lid;
        this.imageUrl = imageUrl;
        this.date = date;
        this.author = author;
        this.menuId = menuId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
