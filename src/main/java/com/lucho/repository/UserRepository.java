package com.lucho.repository;

import com.lucho.domain.User;
import org.springframework.data.neo4j.repository.GraphRepository;

/**
 * Repository that handles User objects.
 *
 * @author Luciano.Leggieri
 */
public interface UserRepository extends GraphRepository<User>,
        UserRepositoryCustom {

    /**
     * Find a user by its username.
     *
     * @param username the name of the user to find.
     * @return a User object that holds the username information or null
     *         if no user with that name exists.
     */
    User findByUsername(String username);

}
