package com.mediastream.service;

import com.mediastream.dto.MediaDto;
import com.mediastream.entity.MediaContent;
import com.mediastream.entity.User;
import com.mediastream.exception.ApiException;
import com.mediastream.repository.MediaContentRepository;
import com.mediastream.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MediaService {

    private final MediaContentRepository mediaRepository;
    private final UserRepository userRepository;

    public MediaService(MediaContentRepository mediaRepository, UserRepository userRepository) {
        this.mediaRepository = mediaRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<MediaDto> getAllMedia(String username) {
        User user = (username != null) ? userRepository.findByUsername(username).orElse(null) : null;
        List<MediaContent> list = mediaRepository.findAll();

        return list.stream().map(m -> toDto(m, user)).toList();
    }

    @Transactional(readOnly = true)
    public List<MediaDto> getMediaByCategory(String category, String username) {
        User user = (username != null) ? userRepository.findByUsername(username).orElse(null) : null;
        List<MediaContent> list = mediaRepository.findByCategoryIgnoreCase(category);

        return list.stream().map(m -> toDto(m, user)).toList();
    }

    @Transactional(readOnly = true)
    public MediaDto getMediaById(Long id, String username) {
        User user = (username != null) ? userRepository.findByUsername(username).orElse(null) : null;
        MediaContent media = mediaRepository.findById(id)
                .orElseThrow(() -> new ApiException("Media not found with id: " + id, HttpStatus.NOT_FOUND));

        return toDto(media, user);
    }

    @Transactional(readOnly = true)
    public MediaDto streamMedia(Long id, String username) {
        if (username == null) {
            throw new ApiException("Authentication required to stream media", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        MediaContent media = mediaRepository.findById(id)
                .orElseThrow(() -> new ApiException("Media not found with id: " + id, HttpStatus.NOT_FOUND));

        boolean hasAccess = user.hasActivePermission(media.getRequiredPermission());

        if (!hasAccess) {
            throw new ApiException("Access Denied! You need the permission '" + media.getRequiredPermission()
                    + "' to stream this media. You can purchase access or request temporary access from the Admin.", HttpStatus.FORBIDDEN);
        }

        MediaDto dto = toDto(media, user);
        dto.setAccessible(true);
        return dto;
    }

    @Transactional
    public MediaDto createMedia(MediaDto dto) {
        MediaContent media = new MediaContent(
                dto.getTitle(),
                dto.getDescription(),
                dto.getCategory().toUpperCase(),
                dto.getVideoUrl(),
                dto.getThumbnailUrl(),
                dto.getRequiredPermission(),
                dto.getDurationMinutes(),
                dto.getReleaseYear()
        );

        MediaContent saved = mediaRepository.save(media);
        return toDto(saved, null);
    }

    @Transactional
    public void deleteMedia(Long id) {
        if (!mediaRepository.existsById(id)) {
            throw new ApiException("Media not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        mediaRepository.deleteById(id);
    }

    private MediaDto toDto(MediaContent m, User user) {
        MediaDto dto = new MediaDto();
        dto.setId(m.getId());
        dto.setTitle(m.getTitle());
        dto.setDescription(m.getDescription());
        dto.setCategory(m.getCategory());
        dto.setVideoUrl(m.getVideoUrl());
        dto.setThumbnailUrl(m.getThumbnailUrl());
        dto.setRequiredPermission(m.getRequiredPermission());
        dto.setDurationMinutes(m.getDurationMinutes());
        dto.setReleaseYear(m.getReleaseYear());

        boolean isAccessible = false;
        if (user != null) {
            isAccessible = user.hasActivePermission(m.getRequiredPermission());
        }
        dto.setAccessible(isAccessible);
        return dto;
    }
}
