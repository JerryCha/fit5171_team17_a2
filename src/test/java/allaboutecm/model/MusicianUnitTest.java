package allaboutecm.model;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MusicianUnitTest {
    private Musician musician;

    @BeforeEach
    public void setUp() {
        musician = new Musician();
    }

    // A musician needs to have a non-null and non-empty name.

    @Test
    public void shouldThrowIllegalArgumentGivenNullName() {
        assertThrows(IllegalArgumentException.class, () -> new Musician(null));
    }

    @Test
    public void shouldThrowIllegalArgumentGivenEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Musician(""));
    }

    @ParameterizedTest
    @ValueSource(strings = "AVISHAI COHEN")
    public void musicianNameShouldBeSetGivenValidName(String arg) {
        Musician musician = new Musician(arg);
        assertEquals(arg, musician.getName());
    }

    // A musician needs to have a non-null set of albums.
    @Test
    public void shouldThrowIllegalArgumentGivenNullAlbums() {
        assertThrows(IllegalArgumentException.class, () -> musician.setAlbums(null));
    }

    @Test
    public void shouldThrowIllegalArgumentGivenEmptyAlbums() {
        Set<Album> albums = Sets.newHashSet();
        assertThrows(IllegalArgumentException.class, () -> musician.setAlbums(albums));
    }

    @ParameterizedTest
    @CsvSource(value = {"'ECM2680','BIG VICIOUS',2020"})
    public void shouldBeSetIfGivenASetOfAlbumInSizeOne(String recordNumber, String albumName, int releaseYear) {
        Album a = new Album(releaseYear, recordNumber, albumName);
        Set<Album> albums = Sets.newHashSet();
        albums.add(a);
        musician.setAlbums(albums);

        Set<Album> musicianAlbums = musician.getAlbums();
//        Album musicianAlbum = null;
//        while (musicianAlbums.iterator().hasNext())
//            musicianAlbum = musicianAlbums.iterator().next();
//        assertEquals(musicianAlbum, a);
        assertEquals(musicianAlbums, albums);
    }
}
