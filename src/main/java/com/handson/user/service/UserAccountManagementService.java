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

	private static final String USER_SUCCESSFULLY_MODIFIED = "successfully modified";

	private static final String USER_SUCCESSFULLY_CREATED = "successfully created";

	private static final String NEW_USER_HAS_BEEN_CREATED = "New user has been created";

	private static final String USER_DETAILS_MODIFIED = "User details modified";

	private static final String SUCCESS_MESSAGE = "success";

	private static final String USER_DETAILS_REMOVED = "User details removed";

	private static final String SUCCESSFULLY_DELETED = "successfully deleted";

	private static final Logger log = LoggerFactory.getLogger(UserAccountManagementService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	UserChangePublisher publisher;

	public Response findAll() {
		Response response = getResponse();
		try {
			response.setResult(userRepository.findAll());
		} catch (Exception e) {
			processException(response, e);
		}
		return response;
	}

	public Response findById(Integer id) {
		Response response = getResponse();
		try {
			response.getResult().add(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
		} catch (Exception e) {
			processException(response, e);
		}
		return response;
	}

	public Response search(SearchParam param) {
		Response response = getResponse();
		try {
			response.setResult(searchUser(param).orElseThrow(() -> new UserNotFoundException()));
		} catch (Exception e) {
			processException(response, e);
		}
		return response;
	}

	public Response save(User user) {
		Response response = getResponse();
		try {
			response.setMessage(USER_SUCCESSFULLY_CREATED);
			response.getResult().add(userRepository.save(user));
			publishEvent(UserAccountEventType.NEW_USER_CREATED);
		} catch (Exception e) {
			processException(response, e);
		}
		return response;
	}

	public Response update(Integer id, User user) {
		Response response = getResponse();
		try {
			response.setMessage(USER_SUCCESSFULLY_MODIFIED);
			response.getResult().add(updateUser(user.getId(), user));
			publishEvent(UserAccountEventType.USER_MODIFIED);
		} catch (Exception e) {
			processException(response, e);
		}
		return response;
	}
	
	private Optional<List<User>> searchUser(SearchParam param) {
		Predicate<User> lastNameSearch = user -> user.getLastName().toLowerCase().startsWith(param.getLastName().toLowerCase());
		Predicate<User> countrySearch = user -> user.getCountry().toLowerCase().startsWith(param.getCountry().toLowerCase());
		Predicate<User> nickNameSearch = user -> user.getNickName().toLowerCase().startsWith(param.getNickName().toLowerCase());
		Predicate<User> emailSearch = user -> user.getEmail().toLowerCase().startsWith(param.getEmail().toLowerCase());
		return Optional.of(this.userRepository.findByFirstName(param.getFirstName()).stream()
				.filter(lastNameSearch.and(countrySearch).and(nickNameSearch).and(emailSearch))
				.collect(Collectors.toList()));
	}

	private void publishEvent(UserAccountEventType userEvent) {
		UserAccountEvent userAccountEvent= null;
		switch (userEvent) {
		case NEW_USER_CREATED:
			userAccountEvent = new UserAccountEvent(UserAccountEventType.NEW_USER_CREATED, NEW_USER_HAS_BEEN_CREATED);
			break;
		case USER_MODIFIED:
			userAccountEvent = new UserAccountEvent(UserAccountEventType.USER_MODIFIED, USER_DETAILS_MODIFIED);
			break;
		case USER_DELETED:
			userAccountEvent = new UserAccountEvent(UserAccountEventType.USER_DELETED, USER_DETAILS_REMOVED);
			break;
		}
		publisher.publish(userAccountEvent);
		log.info(String.format("successfully published event [%s]", userAccountEvent));
	}

	public Response delete(int id) {
		Response response = getResponse();
		try {
			log.debug(String.format("user id for deletion [%s]", id));
			User user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
			this.userRepository.deleteById(user.getId());
			response.getResult().add(user);
			response.setMessage(SUCCESSFULLY_DELETED);
			publishEvent(UserAccountEventType.USER_DELETED);
			log.info("Successfully deleted user and event published");
		} catch (Exception e) {
			processException(response, e);
		}
		return response;
	}
	
	private User updateUser(int id, User modifiedUser) {
		return this.userRepository.findById(id).map(user -> {
			mapUserData(modifiedUser, user);
			return userRepository.save(user);
		}).orElseThrow(() -> new UserNotFoundException(id));
	}

	private Response getResponse() {
		Response response = new Response();
		response.setStatus(HttpStatus.OK.name());
		response.setMessage(SUCCESS_MESSAGE);
		response.setResult(new ArrayList<User>());
		return response;
	}

	private void mapUserData(User modifiedUser, User user) {
		log.debug(String.format("mapping user data - [%s] to [%s]", modifiedUser, user));
		user.setFirstName(modifiedUser.getFirstName());
		user.setLastName(modifiedUser.getLastName());
		user.setNickName(modifiedUser.getNickName());
		user.setEmail(modifiedUser.getEmail());
		user.setPassword(modifiedUser.getPassword());
		user.setCountry(modifiedUser.getCountry());
	}

	private void processException(Response response, Exception e) {
		log.error("Error happened - " + e.getCause());
		if (e instanceof UserNotFoundException) {
			response.setStatus(HttpStatus.BAD_REQUEST.name());
			response.setMessage("failure : " + e.getCause().getMessage());
		} else if (e instanceof DataIntegrityViolationException) {
			response.setStatus(HttpStatus.BAD_REQUEST.name());
			response.setMessage("failure : " + e.getCause().getMessage());
		} else if (e instanceof Exception) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.name());
			response.setMessage("failure : " + e.getCause().getMessage());
		}
	}
}
