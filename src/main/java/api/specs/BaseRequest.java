package api.specs;

import api.models.BaseModel;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public abstract class BaseRequest<T extends BaseModel> {
    protected RequestSpecification requestSpec;
    protected ResponseSpecification responseSpec;

    public BaseRequest(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
    }
}
