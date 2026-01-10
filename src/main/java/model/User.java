package model;

public class User {
    private String username;
    private String email;
    private int userId;

    public User(String username, String email, int userId) {
        this.username = username;
        this.email = email;
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public int getUserId() {
        return userId;
    }
}
