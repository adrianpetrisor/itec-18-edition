package com.seventailed.engine.data.repository;

import com.seventailed.engine.entity.EngineMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;

@Repository
public interface EngineMessageRepository extends JpaRepository<EngineMessageEntity, Integer> {
    List<EngineMessageEntity> findByAuthorID(String authorID);
    List<EngineMessageEntity> findByEventID(String eventID);
    List<EngineMessageEntity> findByAuthorIDAndEventID(String authorID, String eventID);
    LinkedList<EngineMessageEntity> findTop50ByEventIDOrderByTimeAsc(String eventID);
}
