package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerCreateRequestDto {
    private int age;
    private String gender;
    private String login;
    private String password;
    private String role;
    private String screenName;

    // Getters
    public int getAge() {
        return age;
    }

    public PlayerCreateRequestDto setAge(int age) {
        this.age = age;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public PlayerCreateRequestDto setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public PlayerCreateRequestDto setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public PlayerCreateRequestDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getRole() {
        return role;
    }

    public PlayerCreateRequestDto setRole(String role) {
        this.role = role;
        return this;
    }

    public String getScreenName() {
        return screenName;
    }

    public PlayerCreateRequestDto setScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    @Override
    public String toString() {
        return "Player{" +
                " age=" + age +
                ", gender='" + gender + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", screenName='" + screenName + '\'' +
                '}';
    }
}