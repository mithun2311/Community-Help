package com.community.backend.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.community.backend.entity.HelperProfile;
import com.community.backend.entity.User;
public interface HelperProfileRepository extends JpaRepository<HelperProfile,Long> {
    Optional<HelperProfile> findByUser(User user);
}
