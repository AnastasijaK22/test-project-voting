package com.example.testproject.models;

import java.io.Serializable;
import java.util.List;

public class Vote implements Serializable {
    private Topic topic;
    private String nameVote;
    private String descriptionVote;
    private Integer countAnswers;
    private List<Answer> answers;

    public Vote() {
        this.topic = new Topic();
        this.nameVote = "";
        this.descriptionVote = "";
        this.countAnswers = 0;
    }
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public void setNameVote(String nameVote) {
        this.nameVote = nameVote;
    }

    public void setDescriptionVote(String descriptionVote) {
        this.descriptionVote = descriptionVote;
    }

    public void setCountAnswers(Integer countAnswers) {
        this.countAnswers = countAnswers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public Topic getTopic() {
        return this.topic;
    }
    public String getNameVote() {
        return this.nameVote;
    }

    public String getDescriptionVote() {
        return this.descriptionVote;
    }

    public Integer getCountAnswers() {
        return this.countAnswers;
    }

    public List<Answer> getAnswers() {
        return this.answers;
    }

    @Override
    public String toString() {
        return topic.toString() +
                "\nName Vote: " + nameVote +
                "\nDescription: " + descriptionVote +
                "\nCount Answers: " + countAnswers.toString() +
                "\nAnswers: " + answers.toString();
     }
}
