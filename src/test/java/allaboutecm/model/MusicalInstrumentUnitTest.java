package allaboutecm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MusicialInstrumentUnitTest {

    private MusicalInstrument musicalInstrument;

    @BeforeEach
    public void setUp() {
        this.musicalInstrument = new MusicalInstrument();
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if given name null")
    public void shouldThrowIllegalArgumentExceptionIfGivenNameNull() {
        assertThrows(IllegalArgumentException.class, () -> musicalInstrument.setName(null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if given name empty")
    public void shouldThrowIllegalArgumentExceptionIfGivenNameEmpty() {
        assertThrows(IllegalArgumentException.class, () -> musicalInstrument.setName(""));
    }

    @Test
    @DisplayName("Should set the name if given valid name")
    public void shouldSetNameIfGivenValidName() {
        final String name = "Guitar";
        musicalInstrument.setName(name);
        assertEquals(musicalInstrument.getName(), name.toLowerCase());
    }
}
