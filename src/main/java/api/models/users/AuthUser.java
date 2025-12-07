package api.models.users;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUser {
    private final String username;
    private final String password;
    private final String token;
    private  final int id;
}