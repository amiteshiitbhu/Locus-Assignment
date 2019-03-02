package com.locus.locusassignment.model;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

public class Item {

    private String type;
    private String id;
    private String title;
    private JsonObject dataMap;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public JsonObject getDataMap() {
        return dataMap;
    }

    public int getItemCount() {
        return 1;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n Id = " + id + "\n");
        sb.append("Type = " + type + "\n");
        sb.append("Input = " + dataMap.toString());

        return sb.toString();
    }
}
