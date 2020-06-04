package allaboutecm.model;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    /**
     * Testing Musician rating
     */
    @Test
    public void ratingCannotBeGreaterThanFive() {
        assertThrows(IllegalArgumentException.class, () -> musician.setRating(0));
    }

    @Test
    public void ratingCannotBeSmallerThanOne() {
        assertThrows(IllegalArgumentException.class, () -> musician.setRating(6));
    }

    @Test
    public void shouldSetRatingIfGivenValueBetweenOneAndFive() {
        musician.setRating(3);
        assertEquals(3, musician.getRating());
    }

    @ParameterizedTest
    @CsvSource(value = {"'ECM2680','BIG VICIOUS',2020"})
    public void shouldBeSetIfGivenASetOfAlbumInSizeOne(String recordNumber, String albumName, int releaseYear) {
        Album a = new Album(releaseYear, recordNumber, albumName);
        Set<Album> albums = Sets.newHashSet();
        albums.add(a);
        musician.setAlbums(albums);

        Set<Album> musicianAlbums = musician.getAlbums();
        Album retrievedAlbum = musicianAlbums.iterator().next();
        assertEquals(albums.size(), musicianAlbums.size());
        assertEquals(retrievedAlbum, a);
    }

    // Test URL
    @Test
    public void shouldSetMusicianUrlIfGivenValidURLInstance() {
        URL url = null;
        try {
            url = new URL("https://https://stevetibbetts.com/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) {
            musician.setMusicianUrl(url);
            assertEquals(url, musician.getMusicianUrl());
        }
    }

    @Test
    public void shouldSetMusicianUrlGivenNull() {
        musician.setMusicianUrl(null);
        assertNull(musician.getMusicianUrl());
    }

    // Test biography
    @Test
    public void shouldSetBiographyGivenNonEmptyString() {
        String bio = "Master saxophonist Roscoe Mitchell (born in Chicago in 1940) is one of the great innovators in creative music of the post-Coltrane, post-Ayler era.";
        musician.setBiography(bio);
        assertEquals(bio, musician.getBiography());
    }

    @Test
    public void shouldSetBiographyGivenNull() {
        musician.setBiography(null);
        assertNull(musician.getBiography());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionIfGivenEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> musician.setBiography(""));
    }

    // Test group
    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    public void shouldSetGivenGroupTrueAndFalse(String arg) {
        boolean group = Boolean.parseBoolean(arg);
        musician.setGroup(group);
        assertEquals(group, musician.getGroup());
    }
}
