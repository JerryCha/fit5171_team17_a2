package allaboutecm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MusicalInstrumentUnitTest {

    private MusicalInstrument musicalInstrument;

    @BeforeEach
    public void setUp() {
        this.musicalInstrument = new MusicalInstrument();
    }

    // Test name

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
        assertEquals(musicalInstrument.getName(), name);
    }

    // Test equal

    @Test
    @DisplayName("Should return true if given two instance with the same name.")
    public void shouldReturnTrueIfGivenTwoInstanceWithSameName() {
        final String name = "Guitar";
        MusicalInstrument that = new MusicalInstrument(name);
        this.musicalInstrument.setName(name);
        that.setName(name);
        assertTrue(this.musicalInstrument.equals(that));
    }

    @Test
    @DisplayName("Should return true if given two instances with different name.")
    public void shouldReturnTrueIfGivenTwoInstancesWithDifferentName() {
        final String name = "Guitar";
        final String name2 = "Piano";
        MusicalInstrument that = new MusicalInstrument();
        this.musicalInstrument.setName(name);
        that.setName(name2);
        assertFalse(this.musicalInstrument.equals(that));
    }
}
