package model.service;

import model.entity.DTO.UserProfile;
import model.entity.User;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class UserContext implements Serializable {
    private UserProfile currentUser;

    private UserContext() { }

    public void setCurrentUser(User currentUser) {
        this.currentUser = new UserProfile(
                currentUser.getId(),
                currentUser.getName()
        );
    }

    public UserProfile getCurrentUser() {
        return currentUser;
    }
}
