/**
 * 
 */
package com.example.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.model.User;

/**
 * @author Swati
 *
 */

@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long> {
	
	User findByEmail(String email);
	User findByUserId(String userId);
	User findByPincodeAndUserId(int pincode, String userId);	
	
}
