package com.handson.user.controller;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handson.user.persistence.entity.User;
import com.handson.user.response.Response;
import com.handson.user.search.SearchParam;
import com.handson.user.service.UserAccountManagementService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	UserAccountManagementService mock_UserAccountManagementService;

	Fixture fixture = new Fixture();

	@Test
	void test_getUsers() {
		try {
			fixture.callGetUsers();
			fixture.thenConfirmServiceFindAllCalled();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_getUserById() {
		try {
			fixture.setUserIdArgumentCaptor();
			fixture.callGetUserById(1);
			fixture.thenConfirmServicefindByIdCalled();
			fixture.thenConfirmArgumentCaptured(1);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_addUser() {
		try {
			fixture.setUserArgumentCaptor();
			fixture.setUserData();
			fixture.callAddUser();
			fixture.thenConfirmServiceSaveCalled();
			fixture.thenConfirmCorrectUserCaptured();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_modifyUser() {
		try {
			fixture.setUserIdArgumentCaptor();
			fixture.setUserArgumentCaptor();
			fixture.setModifyUserData();
			fixture.callModifyUser();
			fixture.thenConfirmServiceUpdateCalled();
			fixture.thenConfirmCorrectUserCaptured();
			fixture.thenConfirmArgumentCaptured(10);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_deleteUser() {
		try {
			fixture.setUserIdArgumentCaptor();
			fixture.callDeleteUser(2);
			fixture.thenConfirmServiceDeleteCalled();
			fixture.thenConfirmArgumentCaptured(2);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_searchUser() {
		try {
			fixture.setSearchParamCaptor();
			fixture.setSearchData();
			fixture.callSearchUser("firstName=first&lastName=last&nickName=nick&email=email&country=UK");
			fixture.thenConfirmServiceSearchCalled();
			fixture.thenConfirmSearchArgumentCaptured();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	class Fixture {
		List<User> result;
		MvcResult mvcResult;
		ObjectMapper mapper;
		Response response;
		ArgumentCaptor<Integer> userIdCaptor;
		ArgumentCaptor<User> userCaptor;
		ArgumentCaptor<SearchParam> searchParamCaptor;
		User user;
		SearchParam param;

		final String ADD_USER_REQUEST = "{\"firstName\": \"FirstName1\",\"lastName\": \"LastName1\",\"nickName\": \"NickName1\",\"password\": \"password1\",\"email\": \"email1\", \"country\": \"Country1\"}";
		final String MODIFY_USER_REQUEST = "{\"id\":10,\"firstName\": \"FirstName10\",\"lastName\": \"LastName10\",\"nickName\": \"NickName10\",\"password\": \"password10\",\"email\": \"email10\", \"country\": \"Country10\"}";

		public void setMockUsersData() {
			result = new ArrayList<User>();
			result.add(new User(1, "FirstName1", "LastName1", "nickName1", "password1", "email1", "Country1"));
			result.add(new User(2, "FirstName2", "LastName2", "nickName2", "password2", "email2", "Country2"));
		}

		public void setSearchData() {
			param = new SearchParam("first", "last", "nick", "email", "UK");
		}

		public void thenConfirmSearchArgumentCaptured() {
			assertEquals(param, searchParamCaptor.getValue());
		}

		public void thenConfirmServiceSearchCalled() {
			verify(mock_UserAccountManagementService).search(searchParamCaptor.capture());
		}

		public void callSearchUser(String searchParam) throws Exception {
			mvcResult = mockMvc.perform(
					MockMvcRequestBuilders.get("/users/search?" + searchParam).accept(MediaType.APPLICATION_JSON))
					.andReturn();
		}

		public void setSearchParamCaptor() {
			searchParamCaptor = ArgumentCaptor.forClass(SearchParam.class);
		}

		public void setModifyUserData() {
			user = new User(10, "FirstName10", "LastName10", "NickName10", "password10", "email10", "Country10");
		}

		public void thenConfirmCorrectUserCaptured() {
			assertEquals(user, userCaptor.getValue());
		}

		public void thenConfirmServiceSaveCalled() {
			verify(mock_UserAccountManagementService).save(userCaptor.capture());
		}

		public void thenConfirmServiceUpdateCalled() {
			verify(mock_UserAccountManagementService).update(userIdCaptor.capture(), userCaptor.capture());
		}

		public void thenConfirmServiceDeleteCalled() {
			verify(mock_UserAccountManagementService).delete(userIdCaptor.capture());
		}

		public void callAddUser() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.post("/users/").contentType(MediaType.APPLICATION_JSON)
					.content(ADD_USER_REQUEST));
		}

		public void callModifyUser() throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.put("/users/").contentType(MediaType.APPLICATION_JSON)
					.content(MODIFY_USER_REQUEST));
		}

		public void callDeleteUser(int id) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + id).contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)).andReturn();
		}

		public void setUserData() {
			user = new User(null, "FirstName1", "LastName1", "NickName1", "password1", "email1", "Country1");
		}

		public void thenConfirmArgumentCaptured(int i) {
			assertEquals(i, userIdCaptor.getValue());
		}

		public void thenConfirmServicefindByIdCalled() {
			verify(mock_UserAccountManagementService).findById(userIdCaptor.capture());
		}

		public void callGetUserById(int i) throws Exception {
			mockMvc.perform(MockMvcRequestBuilders.get("/users/" + i).accept(MediaType.APPLICATION_JSON)).andReturn();
		}

		public void setUserIdArgumentCaptor() {
			userIdCaptor = ArgumentCaptor.forClass(Integer.class);
		}

		public void setUserArgumentCaptor() {
			userCaptor = ArgumentCaptor.forClass(User.class);
		}

		public void thenConfirmServiceFindAllCalled() {
			verify(mock_UserAccountManagementService).findAll();
		}

		public void thenDoResponseMapping()
				throws JsonMappingException, JsonProcessingException, UnsupportedEncodingException {
			mapper = new ObjectMapper();
			response = mapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
		}

		public void callGetUsers() throws Exception {
			mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/").accept(MediaType.APPLICATION_JSON))
					.andReturn();
		}

	}

}
