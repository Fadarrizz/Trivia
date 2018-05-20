package com.example.fadarrizz.trivianew.Model;

public class QuestionScore {
    private String questionScore;
    private String user;
    private String score;

    public QuestionScore() {
    }

    public QuestionScore(String user, String score) {
        this.user = user;
        this.score = score;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
