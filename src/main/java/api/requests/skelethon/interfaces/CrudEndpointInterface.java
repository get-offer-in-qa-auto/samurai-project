package api.requests.skelethon.interfaces;

import api.models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);

    Object post(BaseModel model, int id);

    Object post();

    Object get(int id);

    Object get(String id);

    Object get();

    Object put(BaseModel model);

    Object put(String id, String text);

    Object put(BaseModel model, int id);

    Object delete(int id);

    Object deleteById(String id);
}
