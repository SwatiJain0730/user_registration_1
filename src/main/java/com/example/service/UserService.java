/**
 * 
 */
package com.example.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.User;
import com.example.repository.UserRepository;

/**
 * @author Princy
 *
 */

@Service("userService")
public class UserService {

	private UserRepository userRepository;

	@Autowired
	public UserService(UserRepository userRepository){
		this.userRepository = userRepository;
	}

	public User findByEmail(String email){
		return this.userRepository.findByEmail(email);
	}

	public User saveUser(User user){
		return this.userRepository.save(user);
	}
	public User findByUserId(String userId){
		return userRepository.findByUserId(userId);
	}
	public User findByPincodeAndUserId(int pincode, String userId){
		return userRepository.findByPincodeAndUserId(pincode, userId);
	}

}
