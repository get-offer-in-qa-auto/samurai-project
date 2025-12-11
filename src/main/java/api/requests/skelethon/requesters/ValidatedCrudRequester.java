package api.requests.skelethon.requesters;

import api.models.BaseModel;
import api.requests.skelethon.Endpoint;
import api.requests.skelethon.HttpRequest;
import api.requests.skelethon.interfaces.CrudEndpointInterface;
import api.requests.skelethon.interfaces.GetWithQueryParams;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.Map;

public class ValidatedCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface, GetWithQueryParams {
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
    public T post(BaseModel model, int id) {
        return (T) crudRequester
                .post(model, id)
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
    public T get(String id) {
        return (T) crudRequester
                .get(id)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T get() {
        return (T) crudRequester
                .get()
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T put(BaseModel model, int id) {
        return (T) crudRequester
                .put(model, id)
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
    public String put(String id, String text) {
        return  crudRequester
                .put(id, text)
                .extract()
                .asString();
    }

    @Override
    public T delete(int id) {
        return (T) crudRequester
                .get(id)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T delete(String id) {
        return (T) crudRequester
                .delete(id)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T get(Map<String, Object> queryParams) {
        return (T) crudRequester
                .get(queryParams)
                .extract()
                .as(endpoint.getResponseModel());
    }
}