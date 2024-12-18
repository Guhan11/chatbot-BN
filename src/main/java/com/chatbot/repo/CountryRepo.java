package com.chatbot.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chatbot.entity.CountryVO;

@Repository
public interface CountryRepo extends JpaRepository<CountryVO, Long> {

}
