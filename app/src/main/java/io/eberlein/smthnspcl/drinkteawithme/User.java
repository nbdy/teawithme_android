package io.eberlein.smthnspcl.drinkteawithme;

import android.content.Context;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import static io.eberlein.smthnspcl.drinkteawithme.Static.CREATED;
import static io.eberlein.smthnspcl.drinkteawithme.Static.DISPLAY_NAME;
import static io.eberlein.smthnspcl.drinkteawithme.Static.FRIENDS;
import static io.eberlein.smthnspcl.drinkteawithme.Static.LAST_ONLINE;
import static io.eberlein.smthnspcl.drinkteawithme.Static.LAST_SESSION;
import static io.eberlein.smthnspcl.drinkteawithme.Static.ONLINE;
import static io.eberlein.smthnspcl.drinkteawithme.Static.SESSION_COUNT;

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

    User(DocumentSnapshot snapshot) {
        online = snapshot.get(ONLINE, Boolean.class);
        displayName = snapshot.get(DISPLAY_NAME, String.class);
        lastOnline = snapshot.get(LAST_ONLINE, String.class);
        created = snapshot.get(CREATED, String.class);
        sessionCount = snapshot.get(SESSION_COUNT, String.class);
        lastSession = snapshot.get(LAST_SESSION, String.class);
        friends = (ArrayList<String>) snapshot.get(FRIENDS);
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

    public void addFriend(String friend) {
        if (!friends.contains(friend)) friends.add(friend);
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
