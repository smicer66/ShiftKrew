package com.syncstate.go.cardinal.inside.ShiftKrew.repositories;


import com.syncstate.go.cardinal.inside.ShiftKrew.models.User;
import com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    @Query("SELECT new com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserDTO(u.username, u.firstName, u.lastName) from " +
            "User u " +
            "WHERE u.deletedAt IS NULL")
    Collection<UserDTO> getAllUsers();


    @Query("SELECT new com.syncstate.go.cardinal.inside.ShiftKrew.models.dto.UserDTO(u.username, u.firstName, u.lastName) from " +
            "User u " +
            "WHERE u.deletedAt IS NULL AND u.userId = :userId")
    UserDTO getUserById(Long userId);


    @Query("SELECT u from User u WHERE u.deletedAt IS NULL AND " +
            "u.username = :username")
    Optional<User> getUserByUsername(String username);

    @Query("SELECT u from User u WHERE u.deletedAt IS NULL AND u.username = :username")
    Optional<User> getPrimaryUserByUsername(String username);

    @Query("SELECT u from User u WHERE u.deletedAt IS NULL " +
            "AND u.username = :username")
    Optional<User> getAnyserByUsername(String username);
}
