package model.entity.DTO;

public class UserPrincipal {
    private int id;
    private String username;

    public UserPrincipal(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
