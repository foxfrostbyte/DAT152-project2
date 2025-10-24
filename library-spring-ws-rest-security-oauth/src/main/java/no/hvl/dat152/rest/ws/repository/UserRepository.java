/**
 * 
 */
package no.hvl.dat152.rest.ws.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import no.hvl.dat152.rest.ws.model.User;

/**
 * 
 */
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

}
