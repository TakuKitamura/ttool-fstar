#!/bin/sh

mvn compile
mvn package
mvn install

java -jar target/gui-test-1.0.jar
