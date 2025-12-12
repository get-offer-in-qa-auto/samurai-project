package api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class ResponseSpecs {
    private ResponseSpecs() {
    }

    private static ResponseSpecBuilder defaultResponseSpecBuilder() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification ignoreErrors() {
        return defaultResponseSpecBuilder().build();

    }

    public static ResponseSpecification entityWasCreated() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification requestReturnsOK() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification requestReturnsNoContent() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_NO_CONTENT)
                .build();
    }

    public static ResponseSpecification requestReturnsOKWithMessage(String expectedMessage) {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectBody("message", equalTo(expectedMessage))
                .build();
    }

    public static ResponseSpecification requestReturns400BadRequest() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .build();
    }

    public static ResponseSpecification requestReturns404NotFound() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_NOT_FOUND)
                .build();
    }

    public static ResponseSpecification requestReturns400WithoutKeyValue(String errorValue) {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(equalTo(errorValue))
                .build();
    }

    public static ResponseSpecification requestReturns400ContainingString(String str) {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(containsString(str))
                .build();
    }

    public static ResponseSpecification requestReturnsInternalServerError() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .build();
    }

    public static ResponseSpecification requestReturnsInternalServerError(String msg) {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .expectBody(Matchers.containsString(msg))
                .build();
    }

    public static ResponseSpecification requestReturns403Forbidden() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .build();
    }
}