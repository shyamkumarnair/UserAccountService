package com.handson.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.handson.user.exception.UserNotFoundException;
import com.handson.user.persistence.entity.User;
import com.handson.user.persistence.repository.UserRepository;
import com.handson.user.publish.UserAccountEvent;
import com.handson.user.publish.UserAccountEventType;
import com.handson.user.publish.UserChangePublisher;
import com.handson.user.response.Response;
import com.handson.user.search.SearchParam;

@Service
public class UserAccountManagementService {

	private static final Logger log = LoggerFactory.getLogger(UserAccountManagementService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	UserChangePublisher publisher;

	public Response findAll() {
		Response response = new Response();
		try {
			response.setStatus(HttpStatus.OK.name());
			response.setMessage("success");
			response.setResult(userRepository.findAll());
		} catch (Exception e) {
			log.error("Error happened - " + e.getCause());
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
			response.setMessage("failure : " + e.getMessage());
			response.setResult(new ArrayList<>());
		}
		return response;
	}

	public Response findById(Integer id) {
		Response response = new Response();
		try {
			response.setStatus(HttpStatus.OK.name());
			response.setMessage("success");
			List<User> user = new ArrayList<>();
			user.add(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
			response.setResult(user);
		} catch (UserNotFoundException e) {
			log.error("Error happened - " + e.getMessage());
			response.setStatus(HttpStatus.BAD_REQUEST.name());
			response.setMessage("failure : " + e.getMessage());
			response.setResult(new ArrayList<>());
		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
			response.setMessage("failure : " + e.getMessage());
			response.setResult(new ArrayList<>());
		}
		return response;
	}

	public Response search(SearchParam param) {
		Response response = new Response();
		try {
			response.setStatus(HttpStatus.OK.name());
			response.setMessage("success");
			response.setResult(searchUser(param).orElseThrow(() -> new UserNotFoundException()));
		} catch (UserNotFoundException e) {
			log.error("Error happened - " + e.getMessage());
			response.setStatus(HttpStatus.BAD_REQUEST.name());
			response.setMessage("failure : " + e.getMessage());
			response.setResult(new ArrayList<>());
		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
			response.setMessage("failure : " + e.getMessage());
			response.setResult(new ArrayList<>());
		}
		return response;
	}

	private Optional<List<User>> searchUser(SearchParam param) {
		Predicate<User> lastNameSearch = user -> user.getLastName().toLowerCase()
				.startsWith(param.getLastName().toLowerCase());
		Predicate<User> countrySearch = user -> user.getCountry().toLowerCase()
				.startsWith(param.getCountry().toLowerCase());
		Predicate<User> nickNameSearch = user -> user.getNickName().toLowerCase()
				.startsWith(param.getNickName().toLowerCase());
		Predicate<User> emailSearch = user -> user.getEmail().toLowerCase().startsWith(param.getEmail().toLowerCase());

		return Optional.of(this.userRepository.findByFirstName(param.getFirstName()).stream()
				.filter(lastNameSearch.and(countrySearch).and(nickNameSearch).and(emailSearch))
				.collect(Collectors.toList()));
	}

	public Response save(User user) {
		Response response = new Response();
		try {
			response.setStatus(HttpStatus.OK.name());
			response.setMessage("successfully created");
			List<User> newUser = new ArrayList<>();
			newUser.add(userRepository.save(user));
			response.setResult(newUser);
			publisher.publish(new UserAccountEvent(UserAccountEventType.NEW_USER_CREATED, "New user has been created"));
		} catch (DataIntegrityViolationException e) {
			log.error("Error happened - " + e.getCause());
			response.setStatus(HttpStatus.BAD_REQUEST.name());
			response.setMessage("failure : " + e.getCause().getMessage());
			response.setResult(new ArrayList<>());
		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
			response.setMessage("failure : " + e.getCause().getMessage());
			response.setResult(new ArrayList<>());
		}
		return response;
	}

	public Response update(Integer id, User user) {
		Response response = new Response();
		try {
			response.setStatus(HttpStatus.OK.name());
			response.setMessage("successfully modified");
			List<User> result = new ArrayList<>();
			result.add(updateUser(user.getId(), user));
			response.setResult(result);
			publisher.publish(new UserAccountEvent(UserAccountEventType.USER_MODIFIED, "User details modified"));
		} catch (UserNotFoundException e) {
			log.error("Error happened - " + e.getCause());
			response.setStatus(HttpStatus.BAD_REQUEST.name());
			response.setMessage("failure : " + e.getCause().getMessage());
			response.setResult(new ArrayList<>());
		} catch (DataIntegrityViolationException e) {
			log.error("Error happened - " + e.getCause());
			response.setStatus(HttpStatus.BAD_REQUEST.name());
			response.setMessage("failure : " + e.getCause().getMessage());
			response.setResult(new ArrayList<>());
		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
			response.setMessage("failure : " + e.getMessage());
			response.setResult(new ArrayList<>());
		}

		return response;

	}

	private User updateUser(int id, User modifiedUser) {
		return this.userRepository.findById(id).map(user -> {
			user.setFirstName(modifiedUser.getFirstName());
			user.setLastName(modifiedUser.getLastName());
			user.setNickName(modifiedUser.getNickName());
			user.setEmail(modifiedUser.getEmail());
			user.setPassword(modifiedUser.getPassword());
			user.setCountry(modifiedUser.getCountry());
			return userRepository.save(user);
		}).orElseThrow(() -> new UserNotFoundException(id));
	}

	public Response delete(int id) {
		Response response = new Response();
		try {
			response.setStatus(HttpStatus.OK.name());
			response.setMessage("successfully deleted");
			List<User> deletedUser = new ArrayList<>();
			User user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
			this.userRepository.deleteById(user.getId());
			deletedUser.add(user);
			response.setResult(deletedUser);
			publisher.publish(new UserAccountEvent(UserAccountEventType.USER_DELETED, "User details removed"));
		} catch (DataIntegrityViolationException e) {
			log.error("Error happened - " + e.getCause());
			response.setStatus(HttpStatus.BAD_REQUEST.name());
			response.setMessage("failure : " + e.getCause().getMessage());
			response.setResult(new ArrayList<>());
		} catch (UserNotFoundException e) {
			log.error("Error happened - " + e.getMessage());
			response.setStatus(HttpStatus.BAD_REQUEST.name());
			response.setMessage("failure : " + e.getMessage());
			response.setResult(new ArrayList<>());
		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
			response.setMessage("failure : " + e.getMessage());
			response.setResult(new ArrayList<>());
		}
		return response;

	}

}
