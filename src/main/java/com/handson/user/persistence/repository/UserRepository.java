package com.handson.user.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.handson.user.persistence.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("select u from User u where country like ?1%")
	List<User> findByCountry(String country);

	@Query("select u from User u where firstName like ?1%")
	List<User> findByFirstName(String firstName);

	@Query("select u from User u where lastName like ?1%")
	List<User> findByLastName(String lastName);

	@Query("select u from User u where email like ?1%")
	List<User> findByEmail(String email);

	@Query("select u from User u where nickName like ?1%")
	List<User> findByNickName(String nickName);

}
