# Todo List Sample Application #

The purpose of the application is to demonstrate a frontend stack for small and medium sized multi-page applications based on Spring Boot 1.5.

It consists of:
- *Thymeleaf 3* as HTML5 server-side template engine
- *Webjars* for compile-time frontend dependency management based on Maven
- *RequireJS* as Javascript module loader
- *Vue.js 2* for client-side templating and data binding
- *Jasmine* for unit testing the UI logic
- *JSHint* for static Javascript code checks

Additionally, it shows how to wrap 3rd party widgets as Vue components, in this case *Awesomplete*.


### Running the application

    mvn spring-boot:run

Open [http://localhost:8080/](http://localhost:8080/) in your favourite browser.


### Building the application

    mvn clean verify -Dminify

Having the `-Dminify` option enabled will execute the [RequireJS optimizer](http://requirejs.org/docs/optimization.html) during the build. See [requirejs-build.js](requirejs-build.js) for its configuration.
