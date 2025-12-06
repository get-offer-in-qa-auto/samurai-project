package api.requests.skelethon.requesters;

import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import api.requests.skelethon.interfaces.CrudEndpointInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface {
    private CrudRequester crudRequester;

    public ValidatedCrudRequester(RequestSpecification requestSpec, Endpoint endpoint, ResponseSpecification responseSpec) {
        super(requestSpec, endpoint, responseSpec);
        this.crudRequester = new CrudRequester(requestSpec, endpoint, responseSpec);
    }

    @Override
    public T post(BaseModel model) {
        return (T) crudRequester
                .post(model)
                .extract()
                .as(endpoint.getResponseModel());
    }
    @Override
    public T post() {
        return (T) crudRequester
                .post()
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T get(int id) {
        return (T) crudRequester
                .get(id)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T put(BaseModel model) {
        return (T) crudRequester
                .put(model)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T delete(int id) {
        return null;
    }
}
