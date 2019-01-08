package com.example.dell.link_game;

public class gameUser {
    public String user;
    public int record;
    public String level;

    public void setLevel(String level) {
        this.level = level;
    }

    public void setRecord(int record) {
        this.record = record;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getRecord() {
        return record;
    }

    public String getLevel() {
        return level;
    }

    public String getUser() {
        return user;
    }
}
