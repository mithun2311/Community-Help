package com.community.backend.repository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.community.backend.entity.HelpRequest;
import com.community.backend.entity.User;
import com.community.backend.entity.Rating;
public interface RatingRepository extends JpaRepository<Rating,Long> {
    Optional<Rating> findByHelpRequestAndRater(HelpRequest helpRequest,User rater);
    List<Rating> findByRatedUserOrderByCreatedAtDesc(User ratedUser);
}