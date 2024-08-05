package org.example.model;

import java.util.Objects;

public class PlayerCreateResponseDto {
    private Long id;
    private int age;
    private String gender;
    private String login;
    private String password;
    private String role;
    private String screenName;

    // Getters
    public Long getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getScreenName() {
        return screenName;
    }

    public PlayerCreateResponseDto setScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }

    public boolean matchesPlayer(PlayerCreateRequestDto player) {
        return this.age == player.getAge() &&
                Objects.equals(this.gender, player.getGender()) &&
                Objects.equals(this.login, player.getLogin()) &&
                Objects.equals(this.role, player.getRole()) &&
                Objects.equals(this.password, player.getPassword()) &&
                Objects.equals(this.screenName, player.getScreenName());
    }
}