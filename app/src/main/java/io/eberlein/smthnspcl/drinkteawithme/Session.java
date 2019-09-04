package io.eberlein.smthnspcl.drinkteawithme;

public class Session {
    private String timestamp;
    private Double liter;
    private String teaName;

    Session(String timestamp, String teaName, Double liter) {
        this.timestamp = timestamp;
        this.teaName = teaName;
        this.liter = liter;
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
