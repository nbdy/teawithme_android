package io.eberlein.smthnspcl.drinkteawithme;

import android.util.Log;

public class FireBaseObject {
    private String id;

    public static void get(String id) {
        Log.w(FireBaseObject.class.getCanonicalName(), "get should be overwritten");
    }

    public void update() {
        Log.w(FireBaseObject.class.getCanonicalName(), "update should be overwritten");
    }
}
