package com.example.runningapp;

//import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.PropertyName;

import java.util.Date;
import java.util.List;

public class Run {
    @PropertyName("runId")
    private String runId;
    @PropertyName("userId")
    private String userId;
    @PropertyName("date")
    private String date;
    @PropertyName("title")
    private String title;
    @PropertyName("moreInfo")
    private String moreInfo;
    @PropertyName("visibleEveryone")
    private boolean isVisibleEveryone;
    @PropertyName("distance")
    private String distance;
    @PropertyName("time")
    private String time;
    @PropertyName("route")
    private List<LatLng> route;

    @Override
    public String toString() {
        return "Run{" +
                "runId='" + runId + '\'' +
                ", userId='" + userId + '\'' +
                ", date=" + date +
                ", title='" + title + '\'' +
                ", moreInfo='" + moreInfo + '\'' +
                ", isVisibleEveryone=" + isVisibleEveryone +
                ", distance='" + distance + '\'' +
                ", time='" + time + '\'' +
                ", route=" + route +
                '}';
    }

    public Run() {
    }

    public String getRunId() {
        return runId;
    }

    public Run setRunId(String runId) {
        this.runId = runId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Run setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Run setDate(String date) {
        this.date = date;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Run setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public Run setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
        return this;
    }

    public boolean isVisibleEveryone() {
        return isVisibleEveryone;
    }

    public Run setVisibleEveryone(boolean visibleEveryone) {
        isVisibleEveryone = visibleEveryone;
        return this;
    }

    public String getDistance() {
        return distance;
    }

    public Run setDistance(String distance) {
        this.distance = distance;
        return this;
    }

    public String getTime() {
        return time;
    }

    public Run setTime(String time) {
        this.time = time;
        return this;
    }

    public List<LatLng> getRoute() {
        return route;
    }

    public Run setRoute(List<LatLng> route) {
        this.route = route;
        return this;
    }
}
