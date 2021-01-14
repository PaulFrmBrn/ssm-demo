package com.paulfrmbrn.ssmdemo;


import com.fasterxml.jackson.annotation.JsonCreator;

public class CommentData {
    private final String comment;

    @JsonCreator
    public CommentData(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
