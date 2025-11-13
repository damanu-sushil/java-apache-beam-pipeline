# channel-integration
This is sole intellectual property of CredenceSoft, New Zealand Ltd. This project can not be used for any other professinal and official purpose without writen confirmation from represntative of CredenceSoft NZ. 
It is the responsibility of the collaborators to enure the privacy and security should be mainitained.Any breach to this should be reported to the represntative of CredenceSoft NZ.
# Getting Started

### Project SetUp

Prerequisite : 

* Any Java IDE such as Elcipse or IntelliJ 
* JDK 1.8
* Apache Maven 
* Git Client such as GitHub Desktop
* Postman or Any Rest Client

## Clone Project 
``` 
git clone https://github.com/samaya-credencesoft/channel-integration.git
```
## Build 
```
cd {replace-this-project-directory}
```
## Run
```
 mvn spring-boot:run
``` 
## Test
* Launch Postman


URL: http://localhost:8080/welcome/Rohit


Http Method: GET

Content-Type: application/json


Response

```
{
    "msg": "Hello Rohit! Welcome to Spring Integration with Spring Boot!",
    "currentTime": "2019/04/25 07:14:48"
}

```
