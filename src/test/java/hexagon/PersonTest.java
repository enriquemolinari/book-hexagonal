package hexagon;

import hexagon.primary.port.BusinessException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {

    @Test
    public void nameMustNotBeBlank() {
        var e = assertThrows(BusinessException.class, () -> {
            new Person(UUID.randomUUID().toString(), "", "", "");
            fail("a Person must not be instantiated with a blank name");
        });
        assertEquals(Person.NAME_MUST_NOT_BE_BLANK, e.getMessage());
    }

    @Test
    public void surnameMustNotBeBlank() {
        var e = assertThrows(BusinessException.class, () -> {
            new Person(UUID.randomUUID().toString(), "any valid name",
                    "", "");
            fail("a Person must not be instantiated with a blank surname");
        });
        assertEquals(Person.SURNAME_MUST_NOT_BE_BLANK, e.getMessage());
    }

    @Test
    public void emailMustBeValid() {
        var e = assertThrows(BusinessException.class, () -> {
            new Person(UUID.randomUUID().toString(), "any valid name",
                    "any other valid surname", "bla.com");
            fail("a Person must not be instantiated with an invalid email");
        });
        assertEquals(Email.NOT_VALID_EMAIL, e.getMessage());
    }
}
