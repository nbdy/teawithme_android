package io.eberlein.smthnspcl.drinkteawithme;

import com.google.android.gms.common.util.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Static {
    static final String USERS = "users";
    static final String DISPLAY_NAME = "displayName";
    static final String ONLINE = "online";
    static final String FRIENDS = "friends";
    static final String SESSION_COUNT = "sessionCount";
    static final String LAST_SESSION = "lastSession";
    static final String DATETIMEFORMAT = "EEE, d MMM yyyy HH:mm";

    static String hash(String in) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(in.getBytes());
            return Hex.bytesToStringLowercase(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
