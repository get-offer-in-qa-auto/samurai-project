package api.requests.skelethon;

import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public abstract class HttpRequest {
    protected Endpoint endpoint;
    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    public HttpRequest(RequestSpecification requestSpec, Endpoint endpoint, ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.endpoint = endpoint;
        this.responseSpec = responseSpec;
    }
}
