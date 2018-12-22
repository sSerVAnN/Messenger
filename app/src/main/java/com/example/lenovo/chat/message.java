package com.example.lenovo.chat;

import java.util.Date;


public class message {

    private String text;
    private String user;
    private String authorUID;
    private long time;


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public message() {

    }


    public message(String text, String user, String authorUID) {
        this.text = text;
        this.user = user;
        this.authorUID = authorUID;
        time = new Date().getTime();
    }

    public String getAuthorUID() {
        return authorUID;
    }

    public void setAuthorUID(String authorUID) {
        this.authorUID = authorUID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
