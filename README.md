# My Blog Backend

## Overview

Backend для блога на **Spring Framework 6**.  
Приложение собирается в **WAR** и разворачивается в **Tomcat**, работает с базой данных **PostgreSQL** (в Docker‑контейнере)

Основные сущности:

- `Post` — пост в блоге (заголовок, текст, теги, лайки, число комментариев, картинка);
- `Comment` — комментарий к посту.

---

## Technology Stack

- **Java 21**
- **Spring Framework 6.2 (Spring Web MVC)**
- **Spring JDBC / Spring Data JDBC**
- **Tomcat 10.1 (Servlet 5, Jakarta)** — развёртывание WAR
- **PostgreSQL 16** (в Docker‑контейнере)
- **Maven 3.8+**
- **JUnit 5**
- **Mockito**
- **Spring Test**
- **SLF4J + slf4j-simple**
- **Lombok**

---

## Project Structure

```text
src/main/java/com/B0cka/
├── model/
│   ├── Post.java
│   └── Comment.java
├── dto/
│   ├── FrontPostsRequest.java
│   ├── PostFullDto.java
│   ├── PostsResponse.java
│   └── CommentRequestDto.java
├── repository/
│   ├── PostsRepository.java
│   ├── PostsRepositoryImpl.java
│   ├── CommentsRepository.java
│   └── CommentsRepositoryImpl.java
├── service/
│   ├── PostService.java
│   └── CommentService.java
└── controllers/
    ├── PostsController.java
    └── CommentsController.java

src/main/resources/
├── schema.sql            # SQL-скрипт создания схемы БД

src/main/webapp/WEB-INF/
├── web.xml               # DispatcherServlet + CORS
```

## Build & Run

### Prerequisites
- JDK 21+
- Maven 3.8+

### Build Project
```bash
mvn clean package

Run Tests
mvn clean test
```
Запустить Docker-compose в корне проекта и все готово!

p.s. к большому сожалению, я не смог решить проблему, в виде ошибки, когда загружаете на сайт пост(создаете его) и ошибки, когда
изменяется комментарий. Честно, я пытался, много раз, логирование, переписывал проект, решали всей пачкой, но безуспешно.
```
>_<
```