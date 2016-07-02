package eu.goodlike;

import eu.goodlike.validate.Validate;
import eu.goodlike.validate.impl.StringValidator;

public final class UsefulValidators {

    public static final StringValidator INTEGER_STRING_VALIDATOR = Validate.string()
            .not().isNull()
            .not().isBlank()
            .isInteger();

    // PRIVATE

    private UsefulValidators() {
        throw new AssertionError("Do not instantiate, use static methods!");
    }

}
