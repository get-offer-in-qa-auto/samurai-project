package api.requests.skelethon.requesters;

import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import api.requests.skelethon.interfaces.CrudEndpointInterface;
import api.requests.skelethon.interfaces.GetWithQueryParams;
import common.helpers.StepLogger;
import io.qameta.allure.Step;
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
        return StepLogger.log("POST запрос " + endpoint.getUrl(), () -> {
            return given()
                    .spec(requestSpec)
                    .body(model)
                    .post(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override
    public ValidatableResponse post(BaseModel model, int id) {
        String formattedId = endpoint.formatId(id);
        return StepLogger.log("POST запрос " + endpoint.getUrl() + " {id=" + id + "}", () -> {
            return given()
                    .pathParam("id", formattedId)
                    .spec(requestSpec)
                    .body(model)
                    .urlEncodingEnabled(false)
                    .post(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override
    public ValidatableResponse post() {
        return StepLogger.log("POST запрос " + endpoint.getUrl(), () -> {
            return given()
                    .spec(requestSpec)
                    .post(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override
    @Step("GET запрос на {endpoint} c id {id}")
    public ValidatableResponse get(int id) {
        String formattedId = endpoint.formatId(id);
        return StepLogger.log("GET запрос " + endpoint.getUrl() + " {id=" + id + "}", () -> {
            return given()
                    .pathParam("id", formattedId)
                    .spec(requestSpec)
                    .urlEncodingEnabled(false)
                    .get(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override

    public ValidatableResponse get(String id) {
        String formattedId = endpoint.formatId(id);
        return StepLogger.log("GET запрос " + endpoint.getUrl() + " {id=" + id + "}", () -> {
            return given()
                    .pathParam("id", formattedId)
                    .spec(requestSpec)
                    .urlEncodingEnabled(false)
                    .get(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override
    @Step("GET запрос на {endpoint}")
    public ValidatableResponse get() {
        return StepLogger.log("GET запрос " + endpoint.getUrl(), () -> {
            return given()
                    .spec(requestSpec)
                    .get(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override

    public ValidatableResponse put(BaseModel model) {
        return StepLogger.log("PUT запрос " + endpoint.getUrl(), () -> {
            return given()
                    .spec(requestSpec)
                    .body(model)
                    .put(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override
    @Step("PUT запрос на {endpoint} с path-параметром id={id} и телом {model}")
    public ValidatableResponse put(String id, String text) {
        String formattedId = endpoint.formatId(id);
        return StepLogger.log("PUT запрос " + endpoint.getUrl() + " {id=" + id + "}", () -> {
            return given()
                    .pathParam("id", formattedId)
                    .spec(requestSpec)
                    .body(text)
                    .put(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override

    public ValidatableResponse put(BaseModel model, int id) {
        String formattedId = endpoint.formatId(id);
        return StepLogger.log("PUT запрос " + endpoint.getUrl() + " {id=" + id + "}", () -> {
            return given()
                    .pathParam("id", formattedId)
                    .spec(requestSpec)
                    .urlEncodingEnabled(false)
                    .body(model)
                    .put(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override
    public ValidatableResponse delete(int id) {
        String formattedId = endpoint.formatId(id);
        return StepLogger.log("DELETE запрос " + endpoint.getUrl() + " {id=" + id + "}", () -> {
            return given()
                    .pathParam("id", formattedId)
                    .spec(requestSpec)
                    .urlEncodingEnabled(false)
                    .delete(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override
    @Step("DELETE запрос на {endpoint} с id {id}")
    public ValidatableResponse deleteById(String id) {
        String formattedId = endpoint.formatId(id);
        return StepLogger.log("DELETE запрос " + endpoint.getUrl() + " {id=" + id + "}", () -> {
            return given()
                    .pathParam("id", formattedId)
                    .spec(requestSpec)
                    .urlEncodingEnabled(false)
                    .delete(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    public void deleteByName(String name) {
        String formattedId = endpoint.formatId(name);
        StepLogger.log("DELETE запрос " + endpoint.getUrl() + " {name=" + name + "}", () -> {
            given()
                    .pathParam("name", formattedId)
                    .spec(requestSpec)
                    .urlEncodingEnabled(false)
                    .delete(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override
    @Step("DELETE запрос на {endpoint}")
    public ValidatableResponse deleteNoContent(int id) {
        return StepLogger.log("DELETE запрос " + endpoint.getUrl(), () -> {
            return given()
                    .spec(requestSpec)
                    .pathParam("id", endpoint.formatId(id))
                    .delete(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }

    @Override
    @Step("GET запрос на {endpoint} с параметрами {queryParams}")
    public ValidatableResponse get(Map<String, Object> queryParams) {
        return StepLogger.log("GET запрос " + endpoint.getUrl() + "с параметрами = " + queryParams, () -> {
            return given()
                    .queryParams(queryParams)          // добавляем query-параметры
                    .spec(requestSpec)
                    .get(endpoint.getUrl())
                    .then()
                    .spec(responseSpec);
        });
    }
}
