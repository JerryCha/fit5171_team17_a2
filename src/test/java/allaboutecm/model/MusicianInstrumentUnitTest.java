package allaboutecm.model;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
//import sun.invoke.empty.Empty;

import javax.smartcardio.Card;

import static org.junit.jupiter.api.Assertions.*;

class MusicianInstrumentUnitTest {
    private MusicianInstrument musicianInstrument;

    @BeforeEach
    public void setUp() {musicianInstrument= new MusicianInstrument(new Musician("kelvin"), Sets.newHashSet(new MusicalInstrument("Piano"))); }


    /**
     * Testing musician.
     * musician cannot be null
     */
    @Test
    @DisplayName("musicianCanNotBeNull")
    public void musicianCannotBeNull() {
        assertThrows(NullPointerException.class, () -> musicianInstrument.setMusician(null));
    }

    /**
     * Testing musicalInstrument.
     * musicalInstrument cannot be null
     */
    @Test
    @DisplayName("musicalInstrumentCanNotBeNull")
    public void musicalInstrumentCannotBeNull() {
        assertThrows(NullPointerException.class, () -> musicianInstrument.setMusicalInstruments(null));
    }

    /**
     * Testing musicalInstruments
     * musicalInstruments cannot be empty
     */
    @Test
    @DisplayName("musicalInstrumentsCannotBeEmpty")
    public void musicalInstrumentsCannotBeEmpty() {
        assertThrows(IllegalArgumentException.class, () -> musicianInstrument.setMusicalInstruments(Sets.newHashSet()));
    }

    /**
     * Testing constructor.
     * Musician Name is kelvin and musicalInstrument is piano
     */
    @Test
    @DisplayName("shouldSuccessInstateAMusicianInstrument")
    public void instateAMusicianInstrument() {
        MusicianInstrument musicianInstrument1 =new MusicianInstrument(new Musician("kelvin"), Sets.newHashSet(new MusicalInstrument("Piano")));
        assertEquals(musicianInstrument, musicianInstrument1);
    }

    /**
     * Testing equal.
     * same MusicianInstrument should return true
     */
    @Test
    @DisplayName("shouldReturnTrueIfGivenTheSameMusicianInstrumentOtherwiseReturnFalse")
    public void sameMusicianAndMusicalInstrumentSameMusicianInstrument() {
        MusicianInstrument musicianInstrument1 =new MusicianInstrument(new Musician("kelvin"), Sets.newHashSet(new MusicalInstrument("Piano")));
        MusicianInstrument musicianInstrument2 =new MusicianInstrument(new Musician("Jane"), Sets.newHashSet(new MusicalInstrument("Piano")));
        MusicianInstrument musicianInstrument3 =new MusicianInstrument(new Musician("Jane"), Sets.newHashSet(new MusicalInstrument("Violin")));
        // Comparing with itself
        assertTrue(musicianInstrument1.equals(musicianInstrument1));
        // Comparing with another
        assertEquals(musicianInstrument, musicianInstrument1);
        assertTrue(musicianInstrument.equals(musicianInstrument1));
        assertFalse(musicianInstrument.equals(musicianInstrument2));
        assertFalse(musicianInstrument.equals(musicianInstrument3));
    }

    @Test
    @DisplayName("should return false if comparing with null or instance of another class")
    public void shouldReturnFalseIfComparingWithNullOrInstanceOfAnotherClass() {
        assertFalse(musicianInstrument.equals(null));
        assertFalse(musicianInstrument.equals(new Musician("foo")));
    }

    /**
     * Testing getMusician name.
     * getter function should get the correct name value
     */
    @Test
    @DisplayName("shouldSuccessGetTheCorrectValueOfMusician")
    public void getSameValueOfMusician() { //test the getMusician() if it can get the correct value
        MusicianInstrument musicianInstrument1 =new MusicianInstrument(new Musician("kelvin"), Sets.newHashSet(new MusicalInstrument("Piano")));
        musicianInstrument1.setMusician(new Musician("Jane"));
        assertTrue(musicianInstrument1.getMusician().getName() == "Jane");
    }

    /**
     * Testing getMusicalInstrument name .
     * getter function should get the correct name value
     */
    @Test
    @DisplayName("shouldSuccessGetTheCorrectValueOfMusicalInstrument")
    public void getSameValueOfMusicalInstrument() {   //test the getMusicalInstrument() if it can get the correct value
        MusicianInstrument musicianInstrument1 =new MusicianInstrument(new Musician("kelvin"), Sets.newHashSet(new MusicalInstrument("Violin")));
        musicianInstrument1.setMusicalInstruments(Sets.newHashSet(new MusicalInstrument("Violin")));
        assertTrue(musicianInstrument1.getMusicalInstruments().iterator().next().getName() == "Violin");
    }

    /**
     * Testing hash code function.
     * hash code should not be equal
     */
    @Test
    @DisplayName("shouldSuccessGenerateToHashCode")
    public void hashCodeNotSame() {   //test the getMusicalInstrument() if it can get the correct value
        MusicianInstrument musicianInstrument1 =new MusicianInstrument(new Musician("kelvin"), Sets.newHashSet(new MusicalInstrument("Violin")));
        MusicianInstrument musicianInstrument2 =new MusicianInstrument(new Musician("Jame"), Sets.newHashSet(new MusicalInstrument("Piano")));
        assertNotEquals(musicianInstrument1.hashCode(), musicianInstrument2.hashCode());
    }

}