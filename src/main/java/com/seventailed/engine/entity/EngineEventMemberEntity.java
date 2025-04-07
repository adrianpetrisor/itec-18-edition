package com.seventailed.engine.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "members")
public class EngineEventMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "eventID")
    private String eventID;

    @Column(name = "memberID")
    private String memberID;

    public EngineEventMemberEntity() {}
    public EngineEventMemberEntity(String eventID, String memberID) {
        this.eventID = eventID;
        this.memberID = memberID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }
}
