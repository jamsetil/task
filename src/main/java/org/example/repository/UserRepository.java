package org.example.repository;

import org.example.model.base.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Profile("!docker")

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserName(String userName);

    Optional<User> findByUserNameAndPassword(String userName, String password);
}
