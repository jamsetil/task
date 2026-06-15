package org.example.model.base;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users")
@NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.userName = :username")
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    String userId;
    @Column(nullable = false)
    String firstName;
    @Column(nullable = false)
    String lastName;
    @Column(nullable = false)
    String userName;
    @Column(nullable = false)
    @ToString.Exclude
    String password;

    @Builder.Default
    @Column(nullable = false)
    Boolean isActive = true;
}
