package com.allinconnect.allinconnectback2.dto;


public class RatingRequest {
    private Long ratedId;
    private Integer score;
    private String comment;

    public RatingRequest() {}

    public RatingRequest(Long ratedId, Integer score, String comment) {
        this.ratedId = ratedId;
        this.score = score;
        this.comment = comment;
    }

    public Long getRatedId() { return ratedId; }
    public void setRatedId(Long ratedId) { this.ratedId = ratedId; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
