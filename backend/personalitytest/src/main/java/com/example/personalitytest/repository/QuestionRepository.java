package com.bstirbat.personalitytest.repository;

import com.bstirbat.personalitytest.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
