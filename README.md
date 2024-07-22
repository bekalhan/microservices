# Project Name

![Project Image]<img width="1389" alt="overview" src="https://github.com/user-attachments/assets/11cea748-be93-474d-b443-dbbecab45ca2">


# Microservices Blog Project

This project is a blog application built using a microservices architecture, leveraging Spring Security and various technologies to ensure robust and scalable functionality. The application is composed of seven distinct services:

- **user-service**: Manages user data and authentication.
- **auth-service**: Handles security with JWT and stores tokens in MongoDB.
- **email-service**: Sends email notifications, including user registration confirmation, using Kafka.
- **post-service**: Manages blog posts.
- **discovery-service**: Uses Eureka for service discovery.
- **config-service**: Manages configuration settings pulled from GitHub.
- **api-gateway**: Acts as the entry point for all client requests, routing them to the appropriate services.

## Key Features

- **JWT Authentication**: Utilizes JWT for secure user authentication, with tokens managed and stored in MongoDB.
- **Email Confirmation**: Employs Kafka to handle email notifications for user registration.
- **Service Discovery**: Eureka-server is used for dynamic service discovery.
- **Centralized Configuration**: Configurations are centrally managed in the config-server, which retrieves settings from GitHub.
- **Microservices Architecture**: Modular design using microservices for scalability and maintainability.


## Technologies Used

- **Spring Security**: For securing the services and managing JWT authentication.
- **MongoDB**: Used for storing tokens and user data.
- **Kafka**: For handling email notifications.
- **Eureka**: For service discovery.
- **GitHub**: For storing and retrieving configuration settings.
- **Spring Boot**: For building and running the microservices.


1. **Clone the Repository**

   ```bash
   git clone https://github.com/your-username/your-repository.git

2. **Set Up the Infrastructure**

 #!/bin/bash


## Menu

- [Overview of Architecture and Technologies](#overview-of-architecture-and-technologies)
- [Monolith, SOA, and Microservices](#monolith-soa-and-microservices)
- [Docker](#docker)
- [Spring Security](#spring-security)

## Overview of Architecture and Technologies

A brief overview of the architecture and technologies used in this project. 

[Download PDF Presentation](path/to/architecture-and-technologies.pdf)

## Monolith, SOA, and Microservices

An explanation of Monolith, SOA, and Microservices architecture, their differences, and use cases.

[Download PDF Presentation](path/to/monolith-soa-microservices.pdf)

## Docker

A brief description of Docker, its importance, and how it's used in the project.

[Download PDF Presentation](path/to/docker.pdf)

## Spring Security

An overview of Spring Security, its features, and its implementation in the project.

[Download PDF Presentation](path/to/spring-security.pdf)
