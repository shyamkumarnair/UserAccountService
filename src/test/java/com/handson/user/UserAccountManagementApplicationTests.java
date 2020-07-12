package com.handson.user;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.UnsupportedEncodingException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.handson.user.response.Response;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class UserAccountManagementApplicationTests {

	@Autowired
	MockMvc mockMvc;

	Fixture fixture = new Fixture();

	@Test
	void test_saveUser() {
		try {
			fixture.callPostService(fixture.NEW_USER_REQUEST);
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage(fixture.SUCCESS_MESSAGE_NEW);
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_saveUser_missingFirstName() {
		try {
			fixture.callPostService(fixture.NEW_USER_MISSING_FIRSTNAME);
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.BAD_REQUEST.name());
			fixture.verifyResponseFailureMessage();
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_saveUser_missingLastName() {
		try {
			fixture.callPostService(fixture.NEW_USER_MISSING_LASTNAME);
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.BAD_REQUEST.name());
			fixture.verifyResponseFailureMessage();
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_saveUser_missingCountry() {
		try {
			fixture.callPostService(fixture.NEW_USER_MISSING_COUNTRY);
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.BAD_REQUEST.name());
			fixture.verifyResponseFailureMessage();
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_saveUser_missingPassword() {
		try {
			fixture.callPostService(fixture.NEW_USER_MISSING_PASSWORD);
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.BAD_REQUEST.name());
			fixture.verifyResponseFailureMessage();
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_modifyUser() {
		try {
			fixture.callPutService(fixture.MOD_USER_REQUEST);
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage(fixture.SUCCESS_MESSAGE_MOD);
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_modifyUser_missingFirstName() {
		try {
			fixture.callPutService(fixture.MOD_USER_MISSING_FIRSTNAME);
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.BAD_REQUEST.name());
			fixture.verifyResponseFailureMessage();
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_modifyUser_missingLastName() {
		try {
			fixture.callPutService(fixture.MOD_USER_MISSING_LASTNAME);
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.BAD_REQUEST.name());
			fixture.verifyResponseFailureMessage();
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_modifyUser_missingCountry() {
		try {
			fixture.callPutService(fixture.MOD_USER_MISSING_COUNTRY);
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.BAD_REQUEST.name());
			fixture.verifyResponseFailureMessage();
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_modifyUser_missingPassword() {
		try {
			fixture.callPutService(fixture.MOD_USER_MISSING_PASSWORD);
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.BAD_REQUEST.name());
			fixture.verifyResponseFailureMessage();
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_deleteUser() {
		try {
			fixture.callDeleteService("1");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage(fixture.SUCCESS_MESSAGE_DEL);
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_deleteUser_notpresent() {
		try {
			fixture.callDeleteService("100");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.BAD_REQUEST.name());
			fixture.verifyResponseFailureMessage();
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserById() {
		try {
			fixture.callSearchByIdService("2");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserById_NotPresent() {
		try {
			fixture.callSearchByIdService("102");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.BAD_REQUEST.name());
			fixture.verifyResponseMessage("failure : user id not present");
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByFirstName() {
		try {
			fixture.callSearchService("firstName=first");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByFirstName_NotPresent() {
		try {
			fixture.callSearchService("firstName=Blah");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByLastName() {
		try {
			fixture.callSearchService("lastName=last");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByLastName_NotPresent() {
		try {
			fixture.callSearchService("lastName=Blah");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByFirstandLastName() {
		try {
			fixture.callSearchService("firstName=first&lastName=last");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByFirstandLastName_NotPresent() {
		try {
			fixture.callSearchService("firstName=Blaht&lastName=Blah");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByNickName() {
		try {
			fixture.callSearchService("nickName=nick");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByNickName_NotPresent() {
		try {
			fixture.callSearchService("nickName=Blah");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByEmail() {
		try {
			fixture.callSearchService("email=email");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByEmail_NotPresent() {
		try {
			fixture.callSearchService("email=Blah");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByCountry() {
		try {
			fixture.callSearchService("country=UK");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_SearchUserByCountry_NotPresent() {
		try {
			fixture.callSearchService("country=IN");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	void test_searchUserByFirstName_LastName_NickName_Email_Country() {
		try {
			fixture.callSearchService("firstName=first&lastName=last&nickName=nick&email=email&country=UK");
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	void test_getUsers() {
		try {
			fixture.callGetService();
			fixture.thenDoResponseMapping();
			fixture.verifyStatus();
			fixture.verifyResponseStatus(HttpStatus.OK.name());
			fixture.verifyResponseMessage("success");
			fixture.verifyResultIsNotEmpty();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	class Fixture {

		MvcResult mvcResult;
		ObjectMapper mapper;
		Response response;

		final String NEW_USER_REQUEST = "{\"firstName\":\"firstNameNew\",\"lastName\":\"LastNameNew\",\"nickName\":\"nickNameNew\",\"password\":\"passwordNew\",\"email\":\"emailNew\",\"country\":\"UK\"}";
		final String NEW_USER_MISSING_FIRSTNAME = "{\"lastName\":\"LastNameNew\",\"nickName\":\"nickNameNew\",\"password\":\"passwordNew\",\"email\":\"emailNew\",\"country\":\"UK\"}";
		final String NEW_USER_MISSING_LASTNAME = "{\"firstName\":\"firstNameNew\",\"nickName\":\"nickNameNew\",\"password\":\"passwordNew\",\"email\":\"emailNew\",\"country\":\"UK\"}";
		final String NEW_USER_MISSING_NICKNAME = "{\"firstName\":\"firstNameNew\",\"lastName\":\"LastNameNew\",\"password\":\"passwordNew\",\"email\":\"emailNew\",\"country\":\"UK\"}";
		final String NEW_USER_MISSING_PASSWORD = "{\"firstName\":\"firstNameNew\",\"lastName\":\"LastNameNew\",\"nickName\":\"nickNameNew\",\"email\":\"emailNew\",\"country\":\"UK\"}";
		final String NEW_USER_MISSING_EMAIL = "{\"firstName\":\"firstNameNew\",\"lastName\":\"LastNameNew\",\"nickName\":\"nickNameNew\",\"password\":\"passwordNew\",\"country\":\"UK\"}";
		final String NEW_USER_MISSING_COUNTRY = "{\"firstName\":\"firstNameNew\",\"lastName\":\"LastNameNew\",\"nickName\":\"nickNameNew\",\"password\":\"passwordNew\",\"email\":\"emailNew\"}";

		final String MOD_USER_REQUEST = "{\"id\":2,\"firstName\":\"firstNameMod\",\"lastName\":\"LastNameMod\",\"nickName\":\"nickNameMod\",\"password\":\"passwordMod\",\"email\":\"emailMod\",\"country\":\"UK\"}";
		final String MOD_USER_MISSING_FIRSTNAME = "{\"id\":2,\"lastName\":\"LastNameMod\",\"nickName\":\"nickNameMod\",\"password\":\"passwordMod\",\"email\":\"emailMod\",\"country\":\"UK\"}";
		final String MOD_USER_MISSING_LASTNAME = "{\"id\":2,\"firstName\":\"firstNameMod\",\"nickName\":\"nickNameMod\",\"password\":\"passwordMod\",\"email\":\"emailMod\",\"country\":\"UK\"}";
		final String MOD_USER_MISSING_PASSWORD = "{\"id\":2,\"firstName\":\"firstNameMod\",\"lastName\":\"LastNameMod\",\"nickName\":\"nickNameMod\",\"email\":\"emailMod\",\"country\":\"UK\"}";
		final String MOD_USER_MISSING_EMAIL = "{\"id\":2,\"firstName\":\"firstNameMod\",\"lastName\":\"LastNameMod\",\"nickName\":\"nickNameMod\",\"password\":\"passwordMod\",\"country\":\"UK\"}";
		final String MOD_USER_MISSING_COUNTRY = "{\"id\":2,\"firstName\":\"firstNameMod\",\"lastName\":\"LastNameMod\",\"nickName\":\"nickNameMod\",\"password\":\"passwordMod\",\"email\":\"emailMod\"}";

		final String SUCCESS_MESSAGE_NEW = "successfully created";
		final String SUCCESS_MESSAGE_MOD = "successfully modified";
		final String SUCCESS_MESSAGE_DEL = "successfully deleted";

		public void callGetService() throws Exception {
			mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/").accept(MediaType.APPLICATION_JSON))
					.andReturn();
		}

		public void callSearchService(String searchParam) throws Exception {
			mvcResult = mockMvc.perform(
					MockMvcRequestBuilders.get("/users/search?" + searchParam).accept(MediaType.APPLICATION_JSON))
					.andReturn();
		}

		public void callSearchByIdService(String id) throws Exception {
			mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/users/" + id).accept(MediaType.APPLICATION_JSON))
					.andReturn();
		}

		public void callDeleteService(String id) throws Exception {
			mvcResult = mockMvc
					.perform(MockMvcRequestBuilders.delete("/users/" + id).accept(MediaType.APPLICATION_JSON))
					.andReturn();
		}

		public void callPostService(String request) throws Exception {
			mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/users/").contentType(MediaType.APPLICATION_JSON)
					.content(request).accept(MediaType.APPLICATION_JSON)).andReturn();
		}

		public void callPutService(String request) throws Exception {
			mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/users/").contentType(MediaType.APPLICATION_JSON)
					.content(request).accept(MediaType.APPLICATION_JSON)).andReturn();
		}

		public void thenDoResponseMapping()
				throws JsonMappingException, JsonProcessingException, UnsupportedEncodingException {
			mapper = new ObjectMapper();
			response = mapper.readValue(mvcResult.getResponse().getContentAsString(), Response.class);
		}

		public void verifyResponseStatus(String status) {
			assertTrue(status.equals(response.getStatus()));
		}

		public void verifyResponseMessage(String message) {
			assertTrue(message.equals(response.getMessage()));
		}

		public void verifyResponseFailureMessage() {
			assertTrue(response.getMessage().startsWith("failure :"));
		}

		public void verifyResultIsNotEmpty() {
			assertTrue(response.getResult().size() > 0);
		}

		public void verifyResultIsEmpty() {
			assertTrue(response.getResult().size() == 0);
		}

		public void verifyPostFailureResult(String response)
				throws UnsupportedEncodingException, JsonMappingException, JsonProcessingException {
			mapper = new ObjectMapper();
			assertTrue(response.equalsIgnoreCase(mvcResult.getResponse().getContentAsString()));
			assertEquals(mapper.readTree(response), mapper.readTree(mvcResult.getResponse().getContentAsString()));
		}

		public void verifyStatus() {
			assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.OK.value());
		}

	}

}
