package org.example.model;

public class PlayerUpdateRequestDto {
    private int age;
    private String gender;
    private String login;
    private String password;
    private String role;
    private String screenName;

    // Getters and Setters
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

    public PlayerUpdateRequestDto setAge(int age) {
        this.age = age;
        return this;
    }

    public PlayerUpdateRequestDto setScreenName(String name) {
        this.screenName = name;
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
