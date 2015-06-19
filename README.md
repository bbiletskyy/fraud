# Fraud - Realtime fraud transaction prediction engine example

Fraud - Microservice that analizes in real time streams of transactions submitted via the rest interface and detects fraud ones.

How to run:

1. Set up database (see below)
2. sbt fraud-main/run - to run microservice, navigate to http://localhost:8080, or POST transaction with application/json Content-Type to http://localhost:8080/transaction
3. sbt fraud-test/gatling:test - to load test with 10000 transactions and 10 concurrent users

Project consists of 2 parts:

1. fraud-main - microservice that predicts fraud transactions using Naive Bayes algorythm built with Spay, Akka Actors, Spark and Cassandra
2. fraud-test - load-testing application, sending events to the microservice, built using Gatling

Setting up database

1. Install Cassandra on the local machine.
2. Create namespace "fraud"

```
CREATE KEYSPACE fraud WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
```

3. Create a table for storing detected fraud transactions

```
CREATE TABLE fraud.fraud_transactions (transaction_id text PRIMARY KEY, transaction text);
```

4. Create a table for storing training set examples

```
CREATE TABLE fraud.training_set (transaction_id text PRIMARY KEY, amount_id double, class_id double, destination_id double);
```

5. Insert training set examples:

```
USE  fraud;
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('1', 1.0, 1.0, 0.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('2', 0.0, 0.0, 0.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('3', 0.0, 2.0, 0.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('4', 0.0, 0.0, 2.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('5', 0.0, 1.0, 1.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('6', 1.0, 1.0, 0.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('7', 1.0, 1.0, 0.0);
INSERT INTO training_set (transaction_id, class_id, destination_id, amount_id) VALUES ('8', 1.0, 1.0, 0.0);
```
