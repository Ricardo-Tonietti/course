package com.ead.course.services;

import com.ead.course.models.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    UserModel save(UserModel userModel);

    Page<UserModel> findAll(Specification<UserModel> spec, Pageable pageable);

    void delete(UUID userId);

    Optional<UserModel> findById(UUID userInstructor);
}
