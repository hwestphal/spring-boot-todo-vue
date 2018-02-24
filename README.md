# Todo List Sample Application #

The purpose of the application is to demonstrate the usage of *Vue.js*, *Typescript* and *Webpack* inside a Spring Boot 1.5 application.

Furthermore, the following is used:
- *MyBatis* for SQL mapping
- *Thymeleaf 3* as HTML5 server-side template engine
- *vue-i18n* for internationalizing Vue templates
- *maven-frontend-plugin* for integrating the frontend build process with Maven
- *Jest* for unit testing the UI logic
- *TSLint* for static Typescript code checks
- *cdp4j* for end-to-end integration testing using a headless Chrome browser

Additionally, it shows how to wrap 3rd party widgets as Vue components, in this case *Awesomplete*.


### Running the application

    mvn spring-boot:run

Open [http://localhost:8080/](http://localhost:8080/) in your favourite browser.


### Building the application

    mvn clean verify -Dminify

Having the `-Dminify` option enabled will build the application for production use.
