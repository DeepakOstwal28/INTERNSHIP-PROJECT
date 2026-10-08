package com.mediastream.repository;

import com.mediastream.entity.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessRequestRepository extends JpaRepository<AccessRequest, Long> {
    List<AccessRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<AccessRequest> findByStatusOrderByCreatedAtDesc(String status);
    List<AccessRequest> findAllByOrderByCreatedAtDesc();
    long countByStatus(String status);
}
