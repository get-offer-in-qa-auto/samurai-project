package common.annotations;

import api.models.users.Roles;
import common.extensions.AuthUserExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(AuthUserExtension.class)
public @interface WithAuthUser {
    Roles role() default Roles.USER_ROLE;
}
