package api.models.error;

import lombok.Getter;

import java.util.List;

@Getter
public class ErrorResponse {
    private List<Error> errors;
}
