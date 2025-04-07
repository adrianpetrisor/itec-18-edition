package com.seventailed.engine.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class EngineMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "messageID")
    private int messageID;

    @Column(name = "authorID")
    private String authorID;

    @Column(name = "eventID")
    private String eventID;

    @Column(name = "content")
    private String content;

    @Column(name = "type")
    private int type;

    @Column(name = "time")
    private LocalDateTime time;

    public EngineMessageEntity() {}

    public EngineMessageEntity(String authorID, String eventID, String content, int type, LocalDateTime time) {
        this.authorID = authorID;
        this.eventID = eventID;
        this.content = content;
        this.type = type;
        this.time = time;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
