Events - Microservice that analizes streams of events obtained via the rest interface by applying map/reduce in real time.

Project consists of 2 parts:
1. events-main - microservice application built with Spay, Akka Actors, Spark
2. events-test - load-testing application, sending events to the microservice, built using Gatling

How to run:
1. sbt events-main/run - to run microservice, navigate to http://localhost:8080, or POST event in application/json format to http://localhost:8080/event
2. sbt events-test/gatling:test - to run load test



1. You need to install Cassandra on the local machine.
2. Create namespace "fraud"

CREATE KEYSPACE fraud WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
USE  fraud;
CREATE TABLE fraud.fraud_transactions (transaction_id text PRIMARY KEY, transaction text);
CREATE TABLE fraud.training_set (transaction_id text PRIMARY KEY, amount_id double, class_id double, destination_id double);

Insert training set examples;
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('1', 1.0, 1.0, 0.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('2', 0.0, 0.0, 0.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('3', 0.0, 2.0, 0.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('4', 0.0, 0.0, 2.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('5', 0.0, 1.0, 1.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('6', 1.0, 1.0, 0.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('7', 1.0, 1.0, 0.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('8', 1.0, 1.0, 0.0);