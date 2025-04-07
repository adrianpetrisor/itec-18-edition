package com.seventailed.engine.data.repository;

import com.seventailed.engine.entity.EngineEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EngineEventRepository extends JpaRepository<EngineEventEntity, String> {
    Optional<EngineEventEntity> findByEventID(String eventID);
    Optional<EngineEventEntity> findByEventName(String eventName);

    boolean existsByEventName(String eventName);
    boolean existsByCreatorID(String creatorID);
    boolean existsByEventID(String eventID);
    int countAllByCreatorID(String creatorID);
    List<EngineEventEntity> findAllByCreatorID(String creatorID);

    @Query("""
SELECT e FROM EngineEventEntity e, EngineEventMemberEntity m WHERE e.eventID = m.eventID AND m.memberID = :member""")
    List<EngineEventEntity> listWithMember(@Param("member") String member);
}
