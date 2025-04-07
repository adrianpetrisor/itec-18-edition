package com.seventailed.engine.security;

import com.seventailed.engine.data.repository.EngineMessageRepository;
import com.seventailed.engine.entity.EngineMessageEntity;
import com.seventailed.engine.entity.EngineUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class EngineMessagesService {
    @Autowired
    private EngineMessageRepository engineMessageRepository;

    public void saveMessage(EngineUserEntity engineUserEntity, String content, int messageType, String eventID) {
        EngineMessageEntity engineMessageEntity = new EngineMessageEntity(engineUserEntity.getUniqueID(), eventID, content, messageType, LocalDateTime.now());
        engineMessageRepository.save(engineMessageEntity);
    }

    public EngineMessageRepository getEngineMessageRepository() {
        return engineMessageRepository;
    }
}
