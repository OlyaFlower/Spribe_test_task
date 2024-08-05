package org.example.model;

import java.util.Objects;

public class PlayerItemDto {
    private Long id;
    private int age;
    private String gender;
    private String screenName;
    private String role;

    // Getters
    public Long getId() { return id;
    }

    public int getAge() {
        return age;
    }

    public String getRole() {
        return role;
    }

    public String getGender() {
        return gender;
    }

    public String getScreenName() {
        return screenName;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", role='" + role + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }

    public boolean matchesPlayer(PlayerCreateRequestDto player) {
        return this.age == player.getAge() &&
                Objects.equals(this.gender, player.getGender()) &&
                Objects.equals(this.role, player.getRole()) &&
                Objects.nonNull(this.getId()) &&
                Objects.equals(this.screenName, player.getScreenName());
    }
}
