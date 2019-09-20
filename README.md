# Todo List Sample Application #

The purpose of the application is to demonstrate the usage of *Vue.js*, *Typescript* and *Webpack* inside a Spring Boot 2.1 application.

Furthermore, the following is used:
- *jOOQ* for SQL mapping
- *Thymeleaf* as HTML5 server-side template engine
- *vue-i18n* for internationalizing Vue templates
- *maven-frontend-plugin* for integrating the frontend build process — powered by Yarn — with Maven
- *Jest* for unit testing the UI logic
- *TSLint* for static Typescript code checks
- *Puppeteer* and *cdp4j* for end-to-end integration tests using a — typically headless — Chrome browser
- *Sass* and *CSS Modules* for styling
- *openapi-generator* for creating client and server stubs from OpenAPI specifications

Additionally, it shows how to wrap 3rd party widgets as Vue components, in this case *Awesomplete*, and use it in combination with the *Element* widget library.


### Running the application

    ./mvnw spring-boot:run

Open [http://localhost:8080/](http://localhost:8080/) in your favourite browser.


### Building the application

    ./mvnw clean verify -Dminify

Having the `-Dminify` option enabled will build the application for production use.
