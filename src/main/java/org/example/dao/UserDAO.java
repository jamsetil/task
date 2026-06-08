package org.example.dao;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.example.model.base.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDAO {
    private final EntityManagerFactory emf;

    public User authenticate (String username, String password) {
        try (var em = emf.createEntityManager()) {
            var result = Optional.ofNullable(em.createQuery("SELECT u FROM User u WHERE u.userName = :username AND u.password = " +
                            ":password", User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult());
            if (result.isEmpty()) {
                throw new RuntimeException("Invalid username or password");
            }

            return result.get();
        }
    }

    public User changePassword(String username, String oldPassword, String newPassword) {
        try (var em = emf.createEntityManager()) {

            User user = em.createNamedQuery("User.findByUsername", User.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Invalid username or password"));

            if (!user.getPassword().equals(oldPassword)) {
                throw new RuntimeException("Invalid username or password");
            }

            em.getTransaction().begin();
            user.setPassword(newPassword);
            em.getTransaction().commit();

            return user;
        }
    }
}
