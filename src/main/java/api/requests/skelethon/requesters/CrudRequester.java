package api.requests.skelethon.requesters;

import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import api.requests.skelethon.interfaces.CrudEndpointInterface;
import api.requests.skelethon.interfaces.GetWithQueryParams;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface, GetWithQueryParams {
    public CrudRequester(RequestSpecification requestSpec, Endpoint endpoint, ResponseSpecification responseSpec) {
        super(requestSpec, endpoint, responseSpec);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        return given()
                .spec(requestSpec)
                .body(model)
                .post(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse post(BaseModel model, int id) {
        String formattedId = endpoint.formatId(id);
        return given()
                .pathParam("id", formattedId)
                .spec(requestSpec)
                .body(model)
                .urlEncodingEnabled(false)
                .post(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse post() {
        return given()
                .spec(requestSpec)
                .post(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse get(int id) {
        String formattedId = endpoint.formatId(id);
        return given()
                .pathParam("id", formattedId)
                .spec(requestSpec)
                .urlEncodingEnabled(false)
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse get(String id) {
        String formattedId = endpoint.formatId(id);
        return given()
                .pathParam("id", formattedId)
                .spec(requestSpec)
                .urlEncodingEnabled(false)
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse get() {
        return given()
                .spec(requestSpec)
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse put(BaseModel model) {
        return given()
                .spec(requestSpec)
                .body(model)
                .put(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse put(String id, String text) {
        String formattedId = endpoint.formatId(id);
        return given()
                .pathParam("id", formattedId)
                .spec(requestSpec)
                .body(text)
                .put(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse put(BaseModel model, int id) {
        String formattedId = endpoint.formatId(id);
        return given()
                .pathParam("id", formattedId)
                .spec(requestSpec)
                .urlEncodingEnabled(false)
                .body(model)
                .put(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse delete(int id) {
        String formattedId = endpoint.formatId(id);
        return given()
                .pathParam("id", formattedId)
                .spec(requestSpec)
                .urlEncodingEnabled(false)
                .delete(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse deleteById(String id) {
        String formattedId = endpoint.formatId(id);
        return given()
                .pathParam("id", formattedId)
                .spec(requestSpec)
                .urlEncodingEnabled(false)
                .delete(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    public void deleteByName(String name) {
        String formattedId = endpoint.formatId(name);
        given()
                .pathParam("name", formattedId)
                .spec(requestSpec)
                .urlEncodingEnabled(false)
                .delete(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse get(Map<String, Object> queryParams) {
        return given()
                .queryParams(queryParams)          // добавляем query-параметры
                .spec(requestSpec)
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }
}