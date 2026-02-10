package model.entity.DTO;

import java.io.Serializable;

public class UserProfile implements Serializable {
    private final int id;
    private final String name;

    public UserProfile(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "\nUserProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
