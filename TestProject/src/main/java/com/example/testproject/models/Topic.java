package com.example.testproject.models;

import java.io.Serializable;

public class Topic implements Serializable {
    private String nameTopic;

    public void setNameTopic(String nameTopic) {
        this.nameTopic = nameTopic;
    }
    public String getNameTopic() {
        return this.nameTopic;
    }

    @Override
    public String toString() {
        return "Topic : " + nameTopic;
    }
}
