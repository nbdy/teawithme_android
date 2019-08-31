package io.eberlein.smthnspcl.drinkteawithme;

public class User {
    private String username;
    private String password;

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    User(String username) {
        this.username = username;
        this.password = "";
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
