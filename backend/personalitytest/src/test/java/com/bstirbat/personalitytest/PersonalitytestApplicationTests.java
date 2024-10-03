package com.bstirbat.personalitytest;

import com.bstirbat.personalitytest.entity.Question;
import com.bstirbat.personalitytest.entity.Quiz;
import com.bstirbat.personalitytest.model.AnswerQuizModel;
import com.bstirbat.personalitytest.model.CreateQuestionModel;
import com.bstirbat.personalitytest.model.CreateVariantModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class PersonalitytestApplicationTests {

	final private static String baseUrl = "http://localhost:8080";

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	void listInitialQuestions() {
		ResponseEntity<List<Question>> exchange = this.testRestTemplate
				.exchange(baseUrl + "/questions/",
						HttpMethod.GET,
						new HttpEntity<>(createHttpHeaders()),
						new ParameterizedTypeReference<List<Question>>() {});

		assertEquals(HttpStatus.OK, exchange.getStatusCode());
		assertEquals(5, exchange.getBody().size());
	}

	@Test
	void createUpdateDeleteQuestion() {
		// Step 1: add a new question
		CreateVariantModel v1 = new CreateVariantModel();
		v1.setBody("v1");
		v1.setScore(1);
		v1.setPlaceInQuestion(1);

		CreateVariantModel v2 = new CreateVariantModel();
		v2.setBody("v2");
		v2.setScore(2);
		v2.setPlaceInQuestion(2);

		CreateQuestionModel createQuestionModel = new CreateQuestionModel();
		createQuestionModel.setBody("body");
		createQuestionModel.setVariants(Arrays.asList(v1, v2));

		ResponseEntity<Question> createdQuestionExchange = this.testRestTemplate
				.exchange(baseUrl + "/questions",
						HttpMethod.POST,
						new HttpEntity<>(createQuestionModel, createHttpHeaders()),
						Question.class);

		assertEquals(HttpStatus.OK, createdQuestionExchange.getStatusCode());

		Question createdQuestion = createdQuestionExchange.getBody();

		// Step 2: update the newly created question
		createQuestionModel.setBody("body2");
		createQuestionModel.setVariants(Arrays.asList());

		ResponseEntity<Question> updatedQuestionExchange = this.testRestTemplate
				.exchange(baseUrl + "/questions/" + createdQuestion.getId(),
						HttpMethod.PUT,
						new HttpEntity<>(createQuestionModel, createHttpHeaders()),
						Question.class);

		assertEquals(HttpStatus.OK, updatedQuestionExchange.getStatusCode());

		Question updatedQuestion = updatedQuestionExchange.getBody();
		assertEquals(createdQuestion.getId(), updatedQuestion.getId());
		assertEquals("body2", updatedQuestion.getBody());
		assertEquals(createdQuestion.getVariants().size(), updatedQuestion.getVariants().size());

		//Step 3: make a GET request for the newly created question
		ResponseEntity<Question> viewedQuestionBeforeDelete = this.testRestTemplate
				.exchange(baseUrl + "/questions/" + createdQuestion.getId(),
						HttpMethod.GET,
						new HttpEntity<>(createHttpHeaders()),
						Question.class);

		assertEquals(HttpStatus.OK, viewedQuestionBeforeDelete.getStatusCode());
		assertEquals(createdQuestion.getId(), viewedQuestionBeforeDelete.getBody().getId());

		//Step 4: update the question again
		CreateVariantModel v3 = new CreateVariantModel();
		v3.setBody("v2");
		v3.setScore(3);
		v3.setPlaceInQuestion(3);

		createQuestionModel.setBody(null);
		createQuestionModel.setVariants(Arrays.asList(v1, v2, v3));

		ResponseEntity<Question> updatedQuestionExchangeV2 = this.testRestTemplate
				.exchange(baseUrl + "/questions/" + createdQuestion.getId(),
						HttpMethod.PUT,
						new HttpEntity<>(createQuestionModel, createHttpHeaders()),
						Question.class);

		assertEquals(HttpStatus.OK, updatedQuestionExchangeV2.getStatusCode());

		Question updatedQuestionV2 = updatedQuestionExchangeV2.getBody();
		assertEquals(createdQuestion.getId(), updatedQuestionV2.getId());
		assertEquals("body2", updatedQuestionV2.getBody());
		assertEquals(createdQuestion.getVariants().size(), updatedQuestion.getVariants().size());

		//Step 5: delete the newly created question
		ResponseEntity<Object> deletedQuestionExchange = this.testRestTemplate
				.exchange(baseUrl + "/questions/" + createdQuestion.getId(),
						HttpMethod.DELETE,
						new HttpEntity<>(createHttpHeaders()),
						Object.class);

		assertEquals(HttpStatus.NO_CONTENT, deletedQuestionExchange.getStatusCode());

		//Step 6: make a new GET request for the newly created question, after it was deleted
		ResponseEntity<Question> viewedQuestionAfterDelete = this.testRestTemplate
				.exchange(baseUrl + "/questions/" + createdQuestion.getId(),
						HttpMethod.GET,
						new HttpEntity<>(createHttpHeaders()),
						Question.class);

		assertEquals(HttpStatus.NOT_FOUND, viewedQuestionAfterDelete.getStatusCode());
	}

	@Test
	void createAnswerDeleteQuiz() {
		// Step 1: view list of quizzes, before none of them was created
		ResponseEntity<List<Quiz>> quizzesBeforeExchange = this.testRestTemplate
				.exchange(baseUrl + "/quizzes",
						HttpMethod.GET,
						new HttpEntity<>(createHttpHeaders()),
						new ParameterizedTypeReference<List<Quiz>>() {
						});

		assertEquals(HttpStatus.OK, quizzesBeforeExchange.getStatusCode());
		assertEquals(0, quizzesBeforeExchange.getBody().size());

		// Step 2: create a new quiz
		ResponseEntity<Quiz> createQuizExchange = this.testRestTemplate
				.exchange(baseUrl + "/quizzes",
						HttpMethod.POST,
						new HttpEntity<>(createHttpHeaders()),
						Quiz.class);

		assertEquals(HttpStatus.OK, createQuizExchange.getStatusCode());

		Quiz createdQuiz = createQuizExchange.getBody();

		// Step 3: view list of quizzes after one was created
		ResponseEntity<List<Quiz>> quizzesAfterExchange = this.testRestTemplate
				.exchange(baseUrl + "/quizzes",
						HttpMethod.GET,
						new HttpEntity<>(createHttpHeaders()),
						new ParameterizedTypeReference<List<Quiz>>() {
						});

		assertEquals(HttpStatus.OK, quizzesAfterExchange.getStatusCode());
		assertEquals(1, quizzesAfterExchange.getBody().size());
		assertEquals(createdQuiz.getQuizId(), quizzesAfterExchange.getBody().get(0).getQuizId());

		// Step 4: answer the quiz
		Quiz currentQuiz = createdQuiz;
		assertEquals(1l, currentQuiz.getCurrentQuestion().getId());
		assertEquals(false, currentQuiz.getCompleted());
		assertEquals(0, currentQuiz.getCurrentScore());

		//Answer questions 1 - 4
		AnswerQuizModel answerQuizModel = new AnswerQuizModel();
		ResponseEntity<Quiz> answeredQizExchange;
		int currentScore = 0;

		for (int i = 1; i <= 4; i++) {
			answerQuizModel.setVariant(i);
			currentScore += i;
			answeredQizExchange = this.testRestTemplate
					.exchange(baseUrl + "/quizzes/" + currentQuiz.getQuizId(),
							HttpMethod.PUT,
							new HttpEntity<>(answerQuizModel, createHttpHeaders()),
							Quiz.class);

			currentQuiz = answeredQizExchange.getBody();
			assertEquals(i + 1l, currentQuiz.getCurrentQuestion().getId());
			assertEquals(false, currentQuiz.getCompleted());
			assertEquals(currentScore, currentQuiz.getCurrentScore());
		}

		//Answer question 5
		answerQuizModel.setVariant(2);
		answeredQizExchange = this.testRestTemplate
				.exchange(baseUrl + "/quizzes/" + currentQuiz.getQuizId(),
						HttpMethod.PUT,
						new HttpEntity<>(answerQuizModel, createHttpHeaders()),
						Quiz.class);

		currentQuiz = answeredQizExchange.getBody();
		assertEquals(12, currentQuiz.getCurrentScore());
		assertEquals(true, currentQuiz.getCompleted());
		assertEquals("INTROVERT", currentQuiz.getResult());

		// Step 5: delete the quiz
		ResponseEntity<Object> deleteExchange = this.testRestTemplate
				.exchange(baseUrl + "/quizzes/" + currentQuiz.getQuizId(),
						HttpMethod.DELETE,
						new HttpEntity<>(createHttpHeaders()),
						Object.class);

		assertEquals(HttpStatus.NO_CONTENT, deleteExchange.getStatusCode());

		// Step 6: check that there are no quizzes left
		ResponseEntity<List<Quiz>> finalQuizzesExchange = this.testRestTemplate
				.exchange(baseUrl + "/quizzes",
						HttpMethod.GET,
						new HttpEntity<>(createHttpHeaders()),
						new ParameterizedTypeReference<List<Quiz>>() {
						});

		assertEquals(HttpStatus.OK, finalQuizzesExchange.getStatusCode());
		assertEquals(0, finalQuizzesExchange.getBody().size());
	}

	private HttpHeaders createHttpHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Accept", "application/json");

		return headers;
	}

}
