package com.app.services;

import com.app.payloads.UserDTO;

public interface UserService {
	UserDTO registerUser(UserDTO userDTO);

	UserDTO getUserById(Long userId);

	UserDTO updateUser(Long userId, UserDTO userDTO);

	String deleteUser(Long userId);
}
