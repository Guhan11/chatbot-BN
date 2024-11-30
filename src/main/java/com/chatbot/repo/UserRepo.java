package com.chatbot.repo;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.chatbot.entity.UserVO;

public interface UserRepo extends JpaRepository<UserVO, Long> {

	boolean existsByUserNameOrEmail(String userName, String email);

	UserVO findByUserName(String userName);

	@Query(value = "select u from UserVO u where u.id =?1")
	UserVO getUserById(Long id);

	UserVO findByUserNameAndId(String userName, Long id);

	Optional<UserVO> findByEmail(String email);

	@Query(nativeQuery = true, value = "select age,gender,phone from users where userid=?1")
	Set<Object[]> findUserById(Long id);

//	@Query(nativeQuery = true,value = "select email from users")
//	UserVO getByEmail(String emaill);

}