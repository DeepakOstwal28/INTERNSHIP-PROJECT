package com.mediastream.repository;

import com.mediastream.entity.MediaContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaContentRepository extends JpaRepository<MediaContent, Long> {
    List<MediaContent> findByCategoryIgnoreCase(String category);
    List<MediaContent> findByRequiredPermission(String requiredPermission);
}
