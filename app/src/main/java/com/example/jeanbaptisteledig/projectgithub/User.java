package com.example.jeanbaptisteledig.projectgithub;

/**
 * Created by jeanbaptiste.ledig on 13/05/2017.
 */

public class User {

    private String username;
    private String password;
    private String avatar_url;
    private String bio;
    private String name;
    private String followers;
    private String following;
    private String nbRepos;
    private String nbGists;

    public User(){
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getBio() {
        return bio;
    }

    public String getName() {
        return name;
    }

    public String getFollowers() {
        return followers;
    }

    public String getFollowing() {
        return following;
    }

    public String getNbRepos() {
        return nbRepos;
    }

    public String getNbGists() {
        return nbGists;
    }


    public void setUsername(String username){
        this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public void setNbRepos(String nbRepos) {
        this.nbRepos = nbRepos;
    }

    public void setNbGists(String nbGists) {
        this.nbGists = nbGists;
    }
}
