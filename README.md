# Project Name

<img width="1389" alt="overview" src="https://github.com/user-attachments/assets/11cea748-be93-474d-b443-dbbecab45ca2">


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

## Getting Started

1. **Clone the Repository**

   ```bash
   git clone https://github.com/your-username/your-repository.git

2. Set Up the Infrastructure**

   ```bash
   # Navigate to the infra-setup directory
   cd infra-setup

   # Start the services using Docker Compose
   docker-compose up

3. **Run the services in this order

1. config-server
2. discovery-service
3. security-service
4. user-service
5. post-service
6. email-service
5. api-gateway


## Menu

- [Overview of Architecture and Technologies](#overview-of-architecture-and-technologies)
- [Monolith, SOA, and Microservices](#monolith-soa-and-microservices)
- [Docker](#docker)
- [Spring Security](#spring-security)
- [Kafka](#kafka)
- [Discovery server](#discovery-server)
- [Configurations Management](#configurations-management)

## Overview of Architecture and Technologies

Project interview and technologies used



<img width="1160" alt="section1-pic" src="https://github.com/user-attachments/assets/0ab265ed-af65-492c-8048-1f6e5460bf49">



[section-1.pdf](https://github.com/user-attachments/files/16336258/bolum1.pdf)

## Monolith, SOA, and Microservices

An explanation of Monolith, SOA, and Microservices architecture, their differences, and use cases.



<img width="1160" alt="section2-pic" src="https://github.com/user-attachments/assets/e0a70e03-916d-4335-92e8-880fcfb3a6e4">



[section-2.pdf](https://github.com/user-attachments/files/16336266/bolum2.pdf)


## Docker

A brief description of Docker, its importance, and how it's used in the project.



<img width="1179" alt="section3-pic" src="https://github.com/user-attachments/assets/fd97ee4a-b859-4951-ab8d-34c8f3e1853f">



[section-3.pdf](https://github.com/user-attachments/files/16336269/bolum3.pdf)


## Spring Security

An overview of Spring Security, its features, and its implementation in the project.



<img width="1182" alt="section4-pic" src="https://github.com/user-attachments/assets/3c0c068c-cb78-47fa-b1d4-03fd4a39908f">



[section-4.pdf](https://github.com/user-attachments/files/16336278/bolum4.pdf)



## Kafka

An overview of Apache Kafka, its features, and its implementation in the project.



<img width="1394" alt="section4 2" src="https://github.com/user-attachments/assets/9ce40a92-9038-4736-b83d-6339dd6ff52d">




<img width="1394" alt="section4 2 2" src="https://github.com/user-attachments/assets/e886c83e-2836-4e2c-9ce1-341070bcc709">




[section4.2.pdf](https://github.com/user-attachments/files/16351139/bolum4.2.pdf)



## Discovery Server

An overview of Netflix Eureka, its features, and its implementation in the project.



<img width="1393" alt="section5" src="https://github.com/user-attachments/assets/9071ed7a-b5da-4069-80ca-7eec4dd3f7ec">



[section5.pdf](https://github.com/user-attachments/files/16351182/bolum5.pdf)



## Configurations Management


An overview of Spring cloud,Spring cloud bus, its features, and its implementation in the project.




<img width="854" alt="SECTİON6" src="https://github.com/user-attachments/assets/5503a2a8-b3e5-4d2c-9e9e-0cc9a50a7fd8">




[section6.pdf](https://github.com/user-attachments/files/16362223/bolum6.pdf)




## API Gateway


An overview of Spring cloud gateway, its features, and its implementation in the project.




