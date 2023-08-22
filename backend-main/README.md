# IDATT2106 Team 10 Backend

[![pipeline status](https://gitlab.stud.idi.ntnu.no/idatt2106-2022-10/backend/badges/main/pipeline.svg)](https://gitlab.stud.idi.ntnu.no/idatt2106-2022-10/backend/-/commits/main)
[![coverage report](https://gitlab.stud.idi.ntnu.no/idatt2106-2022-10/backend/badges/main/coverage.svg)](https://gitlab.stud.idi.ntnu.no/idatt2106-2022-10/backend/-/commits/main)

### Product description
BoCo (Borrow Comunity) is a webapp made for private and corporate stakeholders who want to borrow or rent out products. The users can publish posts with items they want to rent out, or borrow items published by others.
### Team members
- Aleksander Brekke Røed
- Erlend Rønning
- Kim Johnstuen Rokling
- Leonard Opsal Taklo
- Maamoun Adnan Hmaidoush
- Markus Solli Pedersen
- Moaaz Bassam Yanes
- Nicolai Thorer Sivesind
- Nicolay Caspersen Roness

### Backend architecture
The backend of our stack is designed in a robust, scalable and RESTful way.


**Database**
- Hibernate H2 (RDBMS)
- JPA

**RESTful API**
- Spring boot with layer model
- JWT authentication
- Refresh tokens
- Swagger


### Installation guide

  Prerequisites for insalling and running the backend:
  - Git, download [here](https://git-scm.com/downloads)
- Java 17, download [here](https://www.oracle.com/java/technologies/downloads/)
- Maven, download [here](https://maven.apache.org/install.html)

1. With Java 17 and Maven installed, you can clone our Git repository by running the following code:
```console
 $ git clone https://gitlab.stud.idi.ntnu.no/idatt2106-2022-10/backend.git
   ``` 
   This will clone the project into a folder in your working directory.
   
2.  Next, you will have to install the Maven dependencies by running this line: 
```console
 $ mvn clean install
   ``` 
   
After doing so, you will be able to run the backend from your favorite IDE or terminal by running the BackendApplication.java file. To run the entire stack, you also have to install and run the frontend, a guide to which can be found [here](https://gitlab.stud.idi.ntnu.no/idatt2106-2022-10/frontend/-/blob/main/README.md).

If you want to use the database console, you can visit this link with the backend running: http://localhost:8000/h2-console/













