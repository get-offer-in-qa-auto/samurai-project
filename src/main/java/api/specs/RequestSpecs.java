package api.specs;

import api.configs.Config;
import api.models.users.AuthUser;
import api.requests.skelethon.requesters.CrudRequester;
import common.extensions.AuthUserExtension;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public class RequestSpecs {
    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty("server") + Config.getProperty("api.basePath"));
    }

    public static RequestSpecification unAuthSpec() {
        return defaultRequestSpecBuilder().build();
    }

    public static RequestSpecification adminAuthSpec() {
        return defaultRequestSpecBuilder()
                .setAuth(RestAssured.basic(
                        Config.getProperty("admin.username"),
                        Config.getProperty("admin.password")))
                .build();
    }

    public static RequestSpecification userAuthSpecWithoutToken(String username,String password) {
        return defaultRequestSpecBuilder()
                .setAuth(RestAssured.basic((username), password))
                .build();
    }
    public static RequestSpecification userAuthSpecWithToken() {
        AuthUser authUser = AuthUserExtension.getAuthUser();
        if (authUser == null) {
            throw new IllegalStateException("AuthUser не создан. Проверь, что тест аннотирован @ExtendWith(AuthUserExtension.class)");
        }

        return defaultRequestSpecBuilder()
                .addHeader("Authorization", "Bearer " + authUser.getToken())
                .build();
    }


}




