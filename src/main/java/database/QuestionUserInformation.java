package database;

import java.time.LocalDateTime;

public class QuestionUserInformation {
    private int quiId;
    private LocalDateTime dateAccessed;
    private int questionId;
    private int userId;
    private Boolean answeredCorrectly; // use Boolean so null can be handled

    public int getQuiId() { return quiId; }
    public void setQuiId(int quiId) { this.quiId = quiId; }

    public LocalDateTime getDateAccessed() { return dateAccessed; }
    public void setDateAccessed(LocalDateTime dateAccessed) { this.dateAccessed = dateAccessed; }

    public int getQuestionId() { return questionId; }
    public void setQuestionId(int questionId) { this.questionId = questionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Boolean getAnsweredCorrectly() { return answeredCorrectly; }
    public void setAnsweredCorrectly(Boolean answeredCorrectly) { this.answeredCorrectly = answeredCorrectly; }
}

