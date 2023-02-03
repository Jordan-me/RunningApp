package com.example.runningapp;

import android.net.Uri;

public class Runner {
    private String firstName;
    private String lastName;
    private String profileImage;
    private String city;
    private String state;
    private String bio;
    private String birthdate;
    private String gender;
    private String weight;

    public Runner() {
    }

    public String getFirstName() {
        return firstName;
    }

    public Runner setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Runner setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public Runner setProfileImage(String profileImage) {
        this.profileImage = profileImage;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Runner setCity(String city) {
        this.city = city;
        return this;
    }

    public String getState() {
        return state;
    }

    public Runner setState(String state) {
        this.state = state;
        return this;
    }

    public String getBio() {
        return bio;
    }

    public Runner setBio(String bio) {
        this.bio = bio;
        return this;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public Runner setBirthdate(String birthdate) {
        this.birthdate = birthdate;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public Runner setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getWeight() {
        return weight;
    }

    public Runner setWeight(String weight) {
        this.weight = weight;
        return this;
    }
}
