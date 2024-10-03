CREATE TABLE question(
  id bigint(20) NOT NULL AUTO_INCREMENT,
  body varchar(1000) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE variant(
  id bigint(20) NOT NULL AUTO_INCREMENT,
  question_id bigint(20) NOT NULL,
  place_in_question int NOT NULL,
  body varchar(1000) NOT NULL,
  score int NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT constraint_variant_linked_to_question FOREIGN KEY (question_id) REFERENCES question (id)
);

CREATE TABLE quiz(
  id bigint(20) NOT NULL AUTO_INCREMENT,
  quiz_id varchar(256),
  current_question bigint(20),
  current_score int,
  completed BOOLEAN,
  result varchar(256),
  PRIMARY KEY (id),
  CONSTRAINT constraint_current_question_is_a_question FOREIGN KEY (current_question) REFERENCES question (id)
);

CREATE TABLE extrovert_result_threshold(
  id bigint(20) NOT NULL AUTO_INCREMENT,
  threshold int NOT NULL,
  PRIMARY KEY (id)
)