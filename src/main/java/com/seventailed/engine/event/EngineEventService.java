package com.seventailed.engine.event;

import com.seventailed.engine.data.repository.EngineEventMembersRepository;
import com.seventailed.engine.data.repository.EngineEventRepository;
import com.seventailed.engine.email.EngineEmailService;
import com.seventailed.engine.entity.EngineEventEntity;
import com.seventailed.engine.entity.EngineEventMemberEntity;
import com.seventailed.engine.entity.EngineUserEntity;
import com.seventailed.engine.utils.EngineUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Properties;

@Component
public class EngineEventService {
    private Logger eventLogger = LoggerFactory.getLogger("event");

    @Autowired
    private EngineEventRepository eventRepository;

    @Autowired
    private EngineEmailService engineEmailService;

    @Autowired
    private EngineEventMembersRepository engineEventMembersRepository;

    public boolean checkStartsIn(LocalDateTime startsIn) {
        if(LocalDateTime.now().isAfter(startsIn)) {
            return false;
        }

        Duration duration = Duration.between(LocalDateTime.now(), startsIn).abs();
        return duration.toHours() > 1;
    }

    public boolean createEvent(EngineUserEntity engineUserEntity, String eventName, LocalDateTime startsIn, String topic, Properties properties) {
        if (checkStartsIn(startsIn)) {
            EngineEventEntity engineEventEntity = new EngineEventEntity(engineUserEntity.getUniqueID(), eventName, startsIn, topic);
            eventRepository.save(engineEventEntity);
            engineEmailService.sendEmail("redirect", engineUserEntity.getEmail(), "Event notification", "Event created", "Your event named " + eventName + " has been and will start on " + EngineUtils.getLocalTimeDate(startsIn) + ".", "https://localhost:8080/event/" + engineEventEntity.getEventID(), "Click here to go to event.");

            properties.setProperty("eventID", engineEventEntity.getEventID());

            eventLogger.info("User " + engineUserEntity.getUsername() + " has created event " + engineEventEntity.getEventName() + " that will start in " + EngineUtils.getLocalTimeDate(startsIn) + ".");

            return true;
        } else {
            return false;
        }
    }

    public void addMember(EngineUserEntity engineUserEntity, String eventID) {
        engineEventMembersRepository.save(new EngineEventMemberEntity(eventID, engineUserEntity.getUniqueID()));
    }

    public EngineEventRepository getEventRepository() {
        return eventRepository;
    }
}
