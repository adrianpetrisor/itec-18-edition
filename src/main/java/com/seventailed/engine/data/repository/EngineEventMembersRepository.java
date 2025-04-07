package com.seventailed.engine.data.repository;

import com.seventailed.engine.entity.EngineEventMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EngineEventMembersRepository extends JpaRepository<EngineEventMemberEntity, Integer> {
    @Query("SELECT CASE WHEN COUNT(member) > 0 THEN true ELSE false END FROM EngineEventMemberEntity member WHERE member.memberID = :member AND member.eventID = :event")
    boolean isMemberOfEvent(@Param("member") String member, @Param("event") String eventID);
}
