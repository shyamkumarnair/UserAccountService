package com.handson.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.handson.user.persistence.entity.User;
import com.handson.user.publish.UserChangePublisher;
import com.handson.user.response.Response;
import com.handson.user.search.SearchParam;
import com.handson.user.service.UserAccountManagementService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "User Account Management Service endpoints")
public class UserController {
	@Autowired
	UserAccountManagementService userAccountService;

	@Autowired
	UserChangePublisher publisher;

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@ApiOperation(value = "get all users")
	@GetMapping("/users")
	public Response getUsers() {
		log.info("Search all users");
		return userAccountService.findAll();
	}

	@ApiOperation(value = "get user by id")
	@GetMapping("/users/{id}")
	public Response getUserById(@PathVariable Integer id) {
		return userAccountService.findById(id);
	}

	@ApiOperation(value = "search users")
	@GetMapping("/users/search")
	public Response search(@RequestParam(value = "firstName", defaultValue = "") String firstName,
			@RequestParam(value = "lastName", defaultValue = "") String lastName,
			@RequestParam(value = "nickName", defaultValue = "") String nickName,
			@RequestParam(value = "email", defaultValue = "") String email,
			@RequestParam(value = "country", defaultValue = "") String country) {

		SearchParam param = new SearchParam(firstName, lastName, nickName, email, country);
		log.info(String.format("Search all users based on search params [%s]", param));
		return userAccountService.search(param);
	}

	@ApiOperation(value = "add user")
	@PostMapping("/users")
	public Response addUser(@RequestBody User user) {
		log.info(String.format("Add new user - [%s]", user));
		return userAccountService.save(user);
	}

	@ApiOperation(value = "modify user")
	@PutMapping("/users")
	public Response modifyUser(@RequestBody User user) {
		log.info(String.format("Modify user - [%s]", user));
		return userAccountService.update(user.getId(), user);
	}

	@ApiOperation(value = "delete user")
	@DeleteMapping("/users/{userId}")
	public Response deleteUser(@PathVariable int userId) {
		log.info(String.format("Delete user - [%d]", userId));
		return userAccountService.delete(userId);
	}
}
