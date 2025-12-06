package api.requests.skelethon.requesters;

import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import api.requests.skelethon.interfaces.CrudEndpointInterface;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {
    public CrudRequester(RequestSpecification requestSpec, Endpoint endpoint, ResponseSpecification responseSpec) {
        super(requestSpec, endpoint, responseSpec);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        var body = model == null ? "" : model;
        return given()
                .spec(requestSpec)
                .body(body)
                .post(endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse get(int id) {
        String formattedId = endpoint.formatId(id);
        return given()
                .pathParam("id", formattedId)
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
                .assertThat()
                .spec(responseSpec);
    }

    @Override
    public ValidatableResponse delete(int id) {
        return given()
                .spec(requestSpec)
                .pathParam("id", id)
                .when()
                .delete(endpoint.getUrl())
                .then()
                .spec(responseSpec);
    }
}
