package com.seventailed.engine.data.repository;

import com.seventailed.engine.entity.EngineUserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EngineUserRepository extends JpaRepository<EngineUserEntity, String> {
    Optional<EngineUserEntity> findByEmail(String email);
    Optional<EngineUserEntity> findByUniqueID(String uniqueID);
    Optional<EngineUserEntity> findByUsername(String username);

    @Query("SELECT engineUser.password FROM EngineUserEntity engineUser WHERE engineUser.username = :username")
    Optional<String> findPasswordByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE EngineUserEntity engineUser SET engineUser.lastLogin = :time, engineUser.ips = :ipHistory WHERE engineUser.username = :username")
    void updateLastLoginIPAndTime(@Param("username") String username, @Param("time") LocalDateTime time, @Param("ipHistory") String ipHistory);

    @Transactional
    @Modifying
    @Query("UPDATE EngineUserEntity engineUser SET  engineUser.googleAuthKey = :secretKey WHERE engineUser.username = :username")
    void updateGoogleAuthenticatorSecretKey(@Param("username") String username, @Param("secretKey") String secretKey);

    @Query("SELECT engineUser FROM EngineUserEntity engineUser, EngineEventMemberEntity member WHERE engineUser.uniqueID = member.memberID AND member.eventID = :event AND engineUser.role = :role")
    List<EngineUserEntity> listAllMembersOfEventByRole(@Param("event") String eventID, @Param("role") String role);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByUniqueID(String uniqueID);
}
