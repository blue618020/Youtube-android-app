package com.blue.youtubeapi.model;

public class Post {

    public String title;
    public String body;
    public String thumbnailUrl;
    public String highUrl;
    public String videoId;

    public Post(String title, String body, String thumbnailUrl, String highUrl, String videoId) {
        this.title = title;
        this.body = body;
        this.thumbnailUrl = thumbnailUrl;
        this.highUrl = highUrl;
        this.videoId = videoId;
    }
}
