package com.mediastream.repository;

import com.mediastream.entity.UserPermissionGrant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionGrantRepository extends JpaRepository<UserPermissionGrant, Long> {
    List<UserPermissionGrant> findByUserId(Long userId);
    Optional<UserPermissionGrant> findByUserIdAndPermissionId(Long userId, Long permissionId);

    @Modifying
    @Query("DELETE FROM UserPermissionGrant g WHERE g.user.id = :userId AND g.permission.id = :permissionId")
    void deleteByUserIdAndPermissionId(@Param("userId") Long userId, @Param("permissionId") Long permissionId);
}
