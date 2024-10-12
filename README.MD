# ✅ Spring Web POS System (Backend)

This project is the backend REST API for a Point of Sale (POS) system built with the Spring Framework. It uses a 
layered architecture, providing clean separation between concerns such as data handling, business logic, and API 
exposure.

## Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [License](#license)


## Project Overview

The backend of this POS system exposes RESTful APIs for handling various POS functionalities, including 
customer management, item management, and order processing. It is designed to efficiently communicate with a 
front-end interface via AJAX or Fetch API.

## Architecture

This application follows a well-organized, layered architecture:
1. *Controller Layer*: Handles HTTP requests and maps them to the appropriate service layer functions.
2. *Service Layer*: Implements business logic.
3. *Data Access Layer (DAO)*: Uses Spring Data JPA to perform database operations.
4. *Entity Layer*: Contains JPA entities that map directly to database tables.
5. *DTO Layer*: Facilitates data transfer between client, controller, and service layers.
6. *Exception Handling*: Provides centralized handling for errors and exceptions.

## Tech Stack

- *Spring Web MVC*: For building RESTful web services.
- *Spring Data JPA*: To interact with the database using JPA and Hibernate ORM.
- *Hibernate Core*: Used as the ORM layer to manage database persistence.
- *MySQL*: The relational database management system.
- *Lombok*: For reducing boilerplate code.
- *Jackson Databind*: To handle JSON data serialization and deserialization.
- *SLF4J with Logback*: For logging.
- *JUnit 5*: For unit testing the application.
- *ModelMapper*: For mapping between DTOs and entities.

## Getting Started

### Prerequisites

- *Java 17*: The project is built with Java 17.
- *Maven*: To manage dependencies and build the project.
- *MySQL*: Ensure you have MySQL installed and running.
- *Postman* (optional): For testing the API.

### Setup Instructions

1. *Clone the repository*:
   ```bash
   git clone https://github.com/CHAMUD12/Spring-Web-POS.git
   cd Spring-Web-POS
   
## API Documentation



## License



## Copyright

Copyright (c) 2024 CHAMUD SHAKEEN. All rights reserved.