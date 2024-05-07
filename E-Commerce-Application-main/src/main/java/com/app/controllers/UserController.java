package com.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payloads.UserDTO;

import com.app.services.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/admin/users/{userId}")
	public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
		UserDTO user = userService.getUserById(userId);

		return ResponseEntity.ok(user);
	}

	@PutMapping("/public/users/{userId}")
	public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO,
			@PathVariable Long userId) {
		UserDTO updatedUser = userService.updateUser(userId, userDTO);

		return new ResponseEntity<UserDTO>(updatedUser, HttpStatus.OK);
	}

	@DeleteMapping("/admin/users/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
		String status = userService.deleteUser(userId);

		return new ResponseEntity<String>(status, HttpStatus.OK);
	}

}
