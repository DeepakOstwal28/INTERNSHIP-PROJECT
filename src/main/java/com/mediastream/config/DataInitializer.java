package com.mediastream.config;

import com.mediastream.entity.*;
import com.mediastream.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final UserPermissionGrantRepository grantRepository;
    private final MediaContentRepository mediaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           PermissionRepository permissionRepository,
                           UserRepository userRepository,
                           UserPermissionGrantRepository grantRepository,
                           MediaContentRepository mediaRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.grantRepository = grantRepository;
        this.mediaRepository = mediaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Initialize Roles
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
        Role roleUser = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        // 2. Initialize Permissions
        Permission permMovies = getOrCreatePermission("STREAM_MOVIES", "Access to Movie Streaming Catalog", 9.99);
        Permission permSeries = getOrCreatePermission("STREAM_SERIES", "Access to TV Series Catalog", 12.99);
        Permission permAnime = getOrCreatePermission("STREAM_ANIME", "Access to Anime Catalog", 7.99);
        Permission permMusic = getOrCreatePermission("STREAM_MUSIC", "Lossless Audio and Music Streaming", 4.99);
        Permission permVip = getOrCreatePermission("VIP_ACCESS", "Universal All-Access Pass", 24.99);

        // 3. Initialize Admin User
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", "admin@example.com", passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(roleAdmin, roleUser));
            admin = userRepository.save(admin);

            grantRepository.save(new UserPermissionGrant(admin, permVip, LocalDateTime.now(), null, "ADMIN_PERMANENT_GRANT"));
            log.info("Initialized default administrator account: admin@example.com");
        }

        // 4. Initialize Standard Demo User
        if (userRepository.findByUsername("user").isEmpty()) {
            User regularUser = new User("user", "user@example.com", passwordEncoder.encode("user123"));
            regularUser.setRoles(Set.of(roleUser));
            regularUser = userRepository.save(regularUser);

            grantRepository.save(new UserPermissionGrant(regularUser, permMovies, LocalDateTime.now(), LocalDateTime.now().plusDays(30), "PURCHASE"));
            log.info("Initialized default user account: user@example.com (STREAM_MOVIES pass)");
        }

        // 5. Initialize Sample Media Content
        if (mediaRepository.count() == 0) {
            mediaRepository.saveAll(List.of(
                    new MediaContent("Inception", "A thief who steals corporate secrets through dream-sharing technology.", "MOVIE",
                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                            "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500", "STREAM_MOVIES", 148, 2010),
                    new MediaContent("Interstellar", "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.", "MOVIE",
                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                            "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=500", "STREAM_MOVIES", 169, 2014),
                    new MediaContent("Attack on Titan", "Humanity fights for survival against gigantic human-eating humanoids known as Titans.", "ANIME",
                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                            "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=500", "STREAM_ANIME", 24, 2013),
                    new MediaContent("Demon Slayer", "A young man searches for a cure for his sister who was turned into a demon.", "ANIME",
                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                            "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=500", "STREAM_ANIME", 23, 2019),
                    new MediaContent("Stranger Things", "When a young boy vanishes, a small town uncovers a mystery involving secret experiments.", "SERIES",
                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                            "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=500", "STREAM_SERIES", 50, 2016),
                    new MediaContent("Cyberpunk Synthwave Beats", "Hi-Fi Chillhop and Synthwave streaming track for focus and chill.", "MUSIC",
                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                            "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500", "STREAM_MUSIC", 180, 2023)
            ));
            log.info("Populated media catalog with initial seed content");
        }
    }

    private Permission getOrCreatePermission(String name, String description, double price) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(new Permission(name, description, price)));
    }
}
