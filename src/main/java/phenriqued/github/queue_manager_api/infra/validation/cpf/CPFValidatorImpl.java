package phenriqued.github.queue_manager_api.infra.validation.cpf;

import br.com.caelum.stella.validation.CPFValidator;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CPFValidatorImpl implements ConstraintValidator<ValidCPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext constraintValidatorContext) {
        if (cpf == null || cpf.isBlank()) return true;
        try {
            new CPFValidator().assertValid(cpf);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
