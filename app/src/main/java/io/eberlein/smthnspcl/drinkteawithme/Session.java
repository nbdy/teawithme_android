package io.eberlein.smthnspcl.drinkteawithme;

import java.util.HashMap;

public class Session {
    private String timestamp;
    private Double liter;
    private String teaName;

    Session(String timestamp, String teaName, Double liter) {
        this.timestamp = timestamp;
        this.teaName = teaName;
        this.liter = liter;
    }

    Session(HashMap<String, String> hm) {
        timestamp = hm.get("timestamp");
        liter = Double.valueOf(hm.get("liter"));
        teaName = hm.get("teaName");
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Double getLiter() {
        return liter;
    }

    public String getTeaName() {
        return teaName;
    }
}
