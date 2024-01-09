package hexagon;

import hexagon.primary.port.BusinessException;

class Email {

    private final String email;
    static final String NOT_VALID_EMAIL = "Email address is not valid";
    private static final String REGEX = "^[\\w-_.+]*[\\w-_.]@(\\w+\\.)+\\w+\\w$";

    public Email(String email) {
        if (!email.matches(REGEX)) {
            throw new BusinessException(NOT_VALID_EMAIL);
        }

        this.email = email;
    }

    public String asString() {
        return email;
    }

}
