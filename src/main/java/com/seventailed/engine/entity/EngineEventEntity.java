package com.seventailed.engine.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class EngineEventEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "eventID", updatable = false, nullable = false)
    private String eventID;

    @Column(name = "creatorID")
    private String creatorID;

    @Column(name = "eventName")
    private String eventName;

    @Column(name = "startsIn")
    private LocalDateTime startsIn;

    @Column(name = "topic")
    private String topic;

    public EngineEventEntity() {}

    public EngineEventEntity(String creatorID, String eventName, LocalDateTime startsIn, String topic) {
        this.creatorID = creatorID;
        this.eventName = eventName;
        this.startsIn = startsIn;
        this.topic = topic;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public LocalDateTime getStartsIn() {
        return startsIn;
    }

    public void setStartsIn(LocalDateTime startsIn) {
        this.startsIn = startsIn;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
