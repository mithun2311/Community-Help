package com.community.backend.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.community.backend.entity.TrustedContact;
import com.community.backend.entity.User;
public interface TrustedContactRepository extends JpaRepository<TrustedContact,Long> {
    List<TrustedContact> findByUser(User user);
}