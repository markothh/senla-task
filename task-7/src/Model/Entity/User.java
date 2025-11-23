package Model.Entity;

import Model.Enum.UserRole;

import java.io.Serializable;

public class User implements Serializable {
    private static int nextId = 1;

    private int id;
    private final String name;
    private final String password;
    UserRole role;

    public User(String name, String password) {
        this.id = User.nextId++;
        this.name = name;
        this.password = password;
        this.role = UserRole.USER;
    }

    public User(int id, String name, String password, UserRole role) {
        this.id = id;
        if (id >= User.nextId)
            User.nextId = id + 1;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    @Override
    public String toString() {
        return "\nUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }
}
