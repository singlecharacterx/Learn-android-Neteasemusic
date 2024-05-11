package com.lr.musiceasynet.music;

import androidx.annotation.NonNull;

public class PlayList {
    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    Creator creator;
    String coverImgUrl;
    String name;
    long id;
    String description;
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public PlayList(String coverImgUrl, String name, long id) {
        this.coverImgUrl = coverImgUrl;
        this.name = name;
        this.id = id;
    }

    public PlayList(Creator creator, String coverImgUrl, String name, long id) {
        this.creator = creator;
        this.coverImgUrl = coverImgUrl;
        this.name = name;
        this.id = id;
    }

    public class Creator{
        long userId;
        String nickname;

        public Creator(long userId, String nickName) {
            this.userId = userId;
            this.nickname = nickName;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return creator.userId+creator.nickname+coverImgUrl+name+id;
    }
}
