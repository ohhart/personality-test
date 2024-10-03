package com.bstirbat.personalitytest.repository;

import com.bstirbat.personalitytest.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Quiz findByQuizId(String quizId);
}
