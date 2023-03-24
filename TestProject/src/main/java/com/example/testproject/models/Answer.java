package com.example.testproject.models;

import java.io.Serializable;

public class Answer implements Serializable {
    private String nameAnswer;
    private Integer countVotes;

    public Answer() {
        this.nameAnswer = "";
        this.countVotes = 0;
    }
    public void setNameAnswer(String nameAnswer) {
        this.nameAnswer = nameAnswer;
    }

    public void setCountVotes(Integer countVotes) {
        this.countVotes = countVotes;
    }

    public String getNameAnswer() {
        return this.nameAnswer;
    }

    public Integer getCountVotes() {
        return this.countVotes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        Answer guest = (Answer) obj;
        return  nameAnswer != null && nameAnswer.equals(guest.nameAnswer);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((nameAnswer == null) ? 0 : nameAnswer.hashCode());
        result = prime * result + countVotes;
        return result;
    }

    @Override
    public String toString() {
        return "Answer: " + nameAnswer +
                ", Count Votes: " + countVotes.toString();
    }
}
