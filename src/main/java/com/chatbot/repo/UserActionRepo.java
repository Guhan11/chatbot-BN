package com.chatbot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chatbot.entity.UserActionVO;

@Repository
public interface UserActionRepo extends JpaRepository<UserActionVO, Long> {

}
