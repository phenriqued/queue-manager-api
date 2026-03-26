package phenriqued.github.queue_manager_api.infra.validation.cpf;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CPFValidatorImpl.class)
public @interface ValidCPF {

    String message() default "Invalid CPF";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
