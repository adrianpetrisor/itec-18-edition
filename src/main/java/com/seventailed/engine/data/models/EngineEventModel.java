package com.seventailed.engine.data.models;

import java.time.LocalDateTime;

public class EngineEventModel {
    private String name;
    private LocalDateTime starts;
    private String topic;

    public EngineEventModel() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStarts() {
        return starts;
    }

    public void setStarts(LocalDateTime starts) {
        this.starts = starts;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
