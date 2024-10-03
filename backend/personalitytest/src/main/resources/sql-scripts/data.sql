INSERT INTO question(id, body)
VALUES
(1, 'You are really busy at work and a colleague is telling you their life story and personal woes. You:');

INSERT INTO variant(id, question_id, place_in_question, body, score)
VALUES
(1, 1, 1, 'Don`t dare to interrupt them', 1),
(2, 1, 2, 'Think it is more important to give them some of your time; work can wait', 2),
(3, 1, 3, 'Listen, but with only with half an ear', 3),
(4, 1, 4, 'Interrupt and explain that you are really busy at the moment', 4);

INSERT INTO question(id, body)
VALUES
(2, 'You have been sitting in the doctor`s waiting room for more than 25 minutes. You:');

INSERT INTO variant(id, question_id, place_in_question, body, score)
VALUES
(5, 2, 1, 'Look at your watch every two minutes', 1),
(6, 2, 2, 'Bubble with inner anger, but keep quiet', 2),
(7, 2, 3, 'Explain to other equally impatient people in the room that the doctor is always running late', 3),
(8, 2, 4, 'Complain in a loud voice, while tapping your foot impatiently', 4);

INSERT INTO question(id, body)
VALUES
(3, 'You are having an animated discussion with a colleague regarding a project that you`re in charge of. You:');

INSERT INTO variant(id, question_id, place_in_question, body, score)
VALUES
(9, 3, 1, 'Don`t dare contradict them', 1),
(10, 3, 2, 'Think that they are obviously right', 2),
(11, 3, 3, 'Defend your own point of view, tooth and nail', 3),
(12, 3, 4, 'Continuously interrupt your colleague', 4);

INSERT INTO question(id, body)
VALUES
(4, 'You are taking part in a guided tour of a museum. You:');

INSERT INTO variant(id, question_id, place_in_question, body, score)
VALUES
(13, 4, 1, 'Are a bit too far towards the back so don`t really hear what the guide is saying', 1),
(14, 4, 2, 'Follow the group without question', 2),
(15, 4, 3, 'Make sure that everyone is able to hear properly', 3),
(16, 4, 4, 'Are right up the front, adding your own comments in a loud voice', 4);

INSERT INTO question(id, body)
VALUES
(5, 'During dinner parties at your home, you have a hard time with people who:');

INSERT INTO variant(id, question_id, place_in_question, body, score)
VALUES
(17, 5, 1, 'Are a bit too far towards the back so don’t really hear what the guide is saying', 1),
(18, 5, 2, 'Follow the group without question', 2),
(19, 5, 3, 'Make sure that everyone is able to hear properly', 3),
(20, 5, 4, 'Are right up the front, adding your own comments in a loud voice', 4);

INSERT INTO extrovert_result_threshold(id, threshold)
VALUES
(1, 15)
