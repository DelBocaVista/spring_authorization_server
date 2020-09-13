# Spring Authorization Server

## Description

Spring Boot based server for handling authentication and authorization through OAuth 2.0 in a room booking system. The server provides a REST API for creating organizations containing sub organizations and users. Developed within a project course at [KTH Royal Institute of Technology](https://www.kth.se/en).

## Getting Started

### Dependencies

* Java JDK 8.0
* Docker

### Installing

* Configure a MySQL server e.g.,
```
docker run --name some_name -p 3306:3306 -e MYSQL_ROOT_PASSWORD=some_password -d mysql:5
```
```
create schema oauth_server;
```

* Clone the repository, adjust values in ../src/main/resources/application.properties and build a Docker image.
```
docker image build -t authserver .
```

### Executing

* Run a Docker container using the created image.
```
docker container run -p 8080:8080 -d authserver
```

## Authors

[jonlundv](jonlundv@kth.se)/[DelBocaVista](https://github.com/DelBocaVista/)
