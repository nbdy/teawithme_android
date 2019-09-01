package io.eberlein.smthnspcl.drinkteawithme;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class User {
    private Boolean online;
    private String displayName;
    private String lastOnline;
    private String created;
    private String sessionCount;
    private String lastSession;
    private List<String> friends;

    User() {

    }

    User(Context ctx, String displayName) {
        this.displayName = displayName;
        online = true;
        lastOnline = Static.getCurrentTimestamp();
        created = lastOnline;
        sessionCount = "0";
        lastSession = ctx.getResources().getString(R.string.never_documented);
        friends = new ArrayList<>();
    }

    User(String displayName, String lastOnline, String created, String sessionCount, String lastSession, Boolean online, List<String> friends) {
        this.displayName = displayName;
        this.lastOnline = lastOnline;
        this.created = created;
        this.sessionCount = sessionCount;
        this.lastSession = lastSession;
        this.online = online;
        this.friends = friends;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(String lastOnline) {
        this.lastOnline = lastOnline;
    }

    public String getLastSession() {
        return lastSession;
    }

    public void setLastSession(String lastSession) {
        this.lastSession = lastSession;
    }

    public String getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(String sessionCount) {
        this.sessionCount = sessionCount;
    }
}
