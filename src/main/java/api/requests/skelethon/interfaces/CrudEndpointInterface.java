package api.requests.skelethon.interfaces;

import api.models.BaseModel;

public interface CrudEndpointInterface {
    Object post(BaseModel model);
    Object post();
    Object get(int id);
    Object put(BaseModel model);
    Object delete(int id);
}
