package com.handson.user.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.handson.user.exception.UserNotFoundException;
import com.handson.user.persistence.entity.User;
import com.handson.user.persistence.repository.UserRepository;
import com.handson.user.publish.UserAccountEvent;
import com.handson.user.publish.UserAccountEventType;
import com.handson.user.publish.UserChangePublisher;
import com.handson.user.response.Response;
import com.handson.user.search.SearchParam;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class UserAccountManagementServiceTest {

	@Mock
	private UserRepository mockUserRepository;

	@Mock
	UserChangePublisher mockPublisher;

	@InjectMocks
	UserAccountManagementService userAccountManagementService = new UserAccountManagementService();

	Fixture fixture = new Fixture();

	@Test
	void test_saveUser() {
		fixture.setNewUserData();
		fixture.setUserEventCapture();
		fixture.whenServiceSaveCalled();
		fixture.callSaveService();
		fixture.thenConfirmRepoSaveCalled();
		fixture.thenConfirmEventPublishCalled();
		fixture.thenConfirmNewUserEvent();
		fixture.thenConfirmSaveResponse();
	}

	@Test
	void test_saveUser_MissingField() {
		fixture.setNewUserWithMissingData();
		fixture.setUserEventCapture();
		fixture.whenServiceSaveCalled_throwError();
		fixture.callSaveService();
		fixture.thenConfirmRepoSaveCalled();
		fixture.thenConfirmEventPublishNeverCalled();
		fixture.thenConfirmSaveErrorResponse();
	}

	@Test
	void test_updateUser() {
		fixture.setNewUserData();
		fixture.setUserEventCapture();
		fixture.whenServiceUpdateCalled();
		fixture.callUpdateService();
		fixture.thenConfirmRepoUpdateCalled();
		fixture.thenConfirmEventPublishCalled();
		fixture.thenConfirmModifyUserEvent();
		fixture.thenConfirmUpdateResponse();
	}

	@Test
	void test_updateUser_MissingField() {
		fixture.setNewUserWithMissingData();
		fixture.setUserEventCapture();
		fixture.whenServiceUpdateCalled();
		fixture.whenServiceUpdateCalled_throwError();
		fixture.callUpdateService();
		fixture.thenConfirmRepoUpdateCalled();
		fixture.thenConfirmEventPublishNeverCalled();
		fixture.thenConfirmSaveErrorResponse();
	}

	@Test
	void test_deleteUser() {
		fixture.setNewUserData();
		fixture.setUserEventCapture();
		fixture.whenServiceUpdateCalled();
		fixture.callDeleteService();
		fixture.thenConfirmRepoDeleteCalled();
		fixture.thenConfirmEventPublishCalled();
		fixture.thenConfirmDeleteUserEvent();
		fixture.thenConfirmDeleteResponse();
	}

	@Test
	void test_deleteUser_NotFound() {
		fixture.setNewUserWithMissingData();
		fixture.setUserEventCapture();
		fixture.whenServiceUpdateCalled();
		fixture.whenServiceUpdateCalled_throwError();
		fixture.callUpdateService();
		// fixture.thenConfirmRepoDeleteCalled();
		fixture.thenConfirmEventPublishNeverCalled();
		fixture.thenConfirmSaveErrorResponse();
	}

	@Test
	void test_findAll() {
		fixture.setUserListData();
		fixture.whenServiceFindAllCalled();
		fixture.callSearchService();
		fixture.thenConfirmSearchSuccessfulResponse();
	}

	@Test
	void test_findAll_Exception() {
		fixture.setUserListData();
		fixture.whenServiceFindAllCalled_thenthrowError();
		fixture.callSearchService();
		fixture.thenConfirmSearchFailureResponse();
	}

	@Test
	void test_findById() {
		fixture.setNewUserData();
		fixture.whenServiceFindbyIdCalled();
		fixture.callfindByIdService();
		fixture.thenConfirmFindByIdSuccessfulResponse();
	}

	@Test
	void test_findById_UserNotFound() {
		fixture.setNewUserData();
		fixture.whenServiceFindbyIdCalled_thenthrowError();
		fixture.callfindByIdService();
		fixture.thenConfirmFindByIdFailureResponse();
	}

	@Test
	void test_search() {
		fixture.setSearchParam();
		fixture.setUserListData();
		fixture.whenSearchServiceCalled();
		fixture.callSearch();
		fixture.thenConfirmSuccessfulSearchResponse();
	}

	class Fixture {

		User user;
		List<User> users;
		Response response;
		ArgumentCaptor<UserAccountEvent> userAccountEventCaptor;
		SearchParam searchParam;

		public void whenServiceFindAllCalled() {
			when(mockUserRepository.findAll()).thenReturn(users);
		}

		public void whenSearchServiceCalled_emptyUser() {
			when(mockUserRepository.findByFirstName(Mockito.anyString())).thenReturn(new ArrayList<User>());
		}

		public void callSearch() {
			response = userAccountManagementService.search(searchParam);
		}

		public void whenSearchServiceCalled() {
			when(mockUserRepository.findByFirstName(Mockito.anyString())).thenReturn(users);
		}

		public void setSearchParam() {
			searchParam = new SearchParam("F", "L", "n", "e", "C");
		}

		public void whenServiceFindbyIdCalled() {
			when(mockUserRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
		}

		public void whenServiceFindbyIdCalled_thenthrowError() {
			when(mockUserRepository.findById(Mockito.anyInt()))
					.thenThrow(new UserNotFoundException("User not found", new Exception("No such user")));
		}

		public void whenServiceFindAllCalled_thenthrowError() {
			when(mockUserRepository.findAll())
					.thenThrow(new UserNotFoundException(user.getId() + "not found", new Exception("No such user")));
		}

		public void callSearchService() {
			response = userAccountManagementService.findAll();
		}

		public void callfindByIdService() {
			response = userAccountManagementService.findById(user.getId());
		}

		public void setNewUserData() {
			user = new User(3, "Fname", "Lname", "Nname", "Ppass", "Email", "Country");
		}

		public void setUserListData() {
			user = new User(3, "Fname", "Lname", "Nname", "Ppass", "Email", "Country");
			users = new ArrayList<User>();
			users.add(user);
		}

		public void thenConfirmModifyUserEvent() {
			assertEquals(UserAccountEventType.USER_MODIFIED, userAccountEventCaptor.getValue().getChangeEvent());
			assertEquals("User details modified", userAccountEventCaptor.getValue().getMessage());
		}

		public void thenConfirmDeleteUserEvent() {
			assertEquals(UserAccountEventType.USER_DELETED, userAccountEventCaptor.getValue().getChangeEvent());
			assertEquals("User details removed", userAccountEventCaptor.getValue().getMessage());
		}

		public void callUpdateService() {
			response = userAccountManagementService.update(3, user);
		}

		public void callDeleteService() {
			response = userAccountManagementService.delete(3);
		}

		public void callSaveService() {
			response = userAccountManagementService.save(user);
		}

		public void setNewUserWithMissingData() {
			user = new User(1, "Fname", null, "Nname", "Ppass", "Email", "Country");
		}

		public void thenConfirmSaveResponse() {
			assertEquals("successfully created", response.getMessage());
			assertEquals(HttpStatus.OK.name(), response.getStatus());
			assertEquals(user, response.getResult().get(0));
		}

		public void thenConfirmSearchSuccessfulResponse() {
			assertEquals("success", response.getMessage());
			assertEquals(HttpStatus.OK.name(), response.getStatus());
			assertTrue(response.getResult().size() > 0);
		}

		public void thenConfirmFindByIdSuccessfulResponse() {
			assertEquals("success", response.getMessage());
			assertEquals(HttpStatus.OK.name(), response.getStatus());
			assertTrue(response.getResult().size() > 0);
		}

		public void thenConfirmSuccessfulSearchResponse() {
			assertEquals("success", response.getMessage());
			assertEquals(HttpStatus.OK.name(), response.getStatus());
			assertTrue(response.getResult().size() > 0);
		}

		public void thenConfirmSearchFailedResponse() {
			assertEquals("failure : User not found", response.getMessage());
			assertEquals(HttpStatus.BAD_REQUEST.name(), response.getStatus());
			assertTrue(response.getResult().size() == 0);
		}

		public void thenConfirmFindByIdFailureResponse() {
			assertEquals("failure : No such user", response.getMessage());
			assertEquals(HttpStatus.BAD_REQUEST.name(), response.getStatus());
			assertTrue(response.getResult().size() == 0);
		}

		public void thenConfirmSearchFailureResponse() {
			assertEquals("failure : No such user", response.getMessage());
			assertEquals(HttpStatus.BAD_REQUEST.name(), response.getStatus());
			assertTrue(response.getResult().size() == 0);
		}

		public void thenConfirmUpdateResponse() {
			assertEquals("successfully modified", response.getMessage());
			assertEquals(HttpStatus.OK.name(), response.getStatus());
			assertEquals(user, response.getResult().get(0));
		}

		public void thenConfirmDeleteResponse() {
			assertEquals("successfully deleted", response.getMessage());
			assertEquals(HttpStatus.OK.name(), response.getStatus());
			assertEquals(user, response.getResult().get(0));
		}

		public void thenConfirmSaveErrorResponse() {
			assertEquals("failure : Mandatory field missing", response.getMessage());
			assertEquals(HttpStatus.BAD_REQUEST.name(), response.getStatus());
		}

		public void thenConfirmUpdateErrorResponse() {
			assertEquals("failure : Mandatory field missing", response.getMessage());
			assertEquals(HttpStatus.BAD_REQUEST.name(), response.getStatus());
		}

		public void thenConfirmNewUserEvent() {
			assertEquals(UserAccountEventType.NEW_USER_CREATED, userAccountEventCaptor.getValue().getChangeEvent());
			assertEquals("New user has been created", userAccountEventCaptor.getValue().getMessage());
		}

		public void setUserEventCapture() {
			userAccountEventCaptor = ArgumentCaptor.forClass(UserAccountEvent.class);
		}

		public void thenConfirmEventPublishCalled() {
			verify(mockPublisher).publish(userAccountEventCaptor.capture());
		}

		public void thenConfirmEventPublishNeverCalled() {
			verify(mockPublisher, never()).publish(userAccountEventCaptor.capture());
		}

		public void whenServiceSaveCalled() {
			when(mockUserRepository.save(Mockito.any(User.class))).thenReturn(user);
		}

		public void whenServiceUpdateCalled() {
			when(mockUserRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
			when(mockUserRepository.save(Mockito.any(User.class))).thenReturn(user);
		}

		public void whenServiceDeleteCalled() {
			when(mockUserRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
			// when(mockUserRepository.delete(Mockito.any(User.class))).thenReturn(user);
		}

		public void whenServiceSaveCalled_throwError() {
			when(mockUserRepository.save(Mockito.any(User.class))).thenThrow(
					new DataIntegrityViolationException("Last name missing", new Exception("Mandatory field missing")));
		}

		public void whenServiceUpdateCalled_throwError() {
			when(mockUserRepository.save(Mockito.any(User.class))).thenThrow(
					new DataIntegrityViolationException("Last name missing", new Exception("Mandatory field missing")));
		}

		public void whenServiceUpdateCalled_throwNoUserFoundError() {
			when(mockUserRepository.save(Mockito.any(User.class)))
					.thenThrow(new UserNotFoundException("User not found", new Exception("wrong Id provided")));
		}

		public void thenConfirmRepoUpdateCalled() {
			verify(mockUserRepository).save(Mockito.any(User.class));
		}

		public void thenConfirmRepoDeleteCalled() {
			verify(mockUserRepository).deleteById(Mockito.anyInt());
		}

		public void thenConfirmRepoSaveCalled() {
			verify(mockUserRepository).save(Mockito.any(User.class));
		}
	}
}
