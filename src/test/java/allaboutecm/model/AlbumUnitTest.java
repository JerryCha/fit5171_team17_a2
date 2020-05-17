package allaboutecm.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AlbumUnitTest {
    private Album album;

    @BeforeEach
    public void setUp() {
        album = new Album(1975, "ECM 1064/65", "The Köln Concert");
    }

    /**
     * Testing constructor.
     * Album release Year is 1975 , record number is ECM 1064/65 and album name is The The Köln Concert
     */

    @Test
    @DisplayName("shouldSuccessInstateAlbum")
    public void instateAlbum() {
        Album album1=new Album(1975, "ECM 1064/65", "The Köln Concert");
        assertEquals(album, album1);
    }

    @Test
    @DisplayName("Album name cannot be null")
    public void albumNameCannotBeNull() {
        assertThrows(NullPointerException.class, () -> album.setAlbumName(null));
    }


    @ParameterizedTest
    @ValueSource(strings = {"", " ", "    \t"})
    @DisplayName("Album name cannot be empty or blank")
    public void albumNameConnotBeEmptyOrBlank(String arg) {
        assertThrows(IllegalArgumentException.class, () -> album.setAlbumName(arg));
    }

    /**
     * Testing Album.
     * Album year cannot be negative value*/

    @Test
    @DisplayName("Release year cannot be negative")
    public void yearCannotBeNegative() {
        assertThrows(IllegalArgumentException.class, () ->album.setReleaseYear(-1));
    }

    /**
     * Testing Album.
     * Album year cannot be greater than the current year*/

    @Test
    @DisplayName("Year cannot be greater than current year")
    public void yearShallBeValid() {
        assertThrows(IllegalArgumentException.class, () ->album.setReleaseYear(Calendar.getInstance().get(Calendar.YEAR)+1));
    }

    @Test
    public void sameNameAndNumberMeansSameAlbum() {
        Album album1 = new Album(1975, "ECM 1064/65", "The Köln Concert");

        assertEquals(album, album1);
    }
    /**
     * Testing Album.
     * Album record number shall not be empty
     */

    @Test
    @DisplayName("Record number cannot be empty")
    public void recordCannotBeNull() {
        assertThrows(NullPointerException.class, () -> album.setRecordNumber(null));

    }

    /**
     * Testing Album sales.
     */
    @Test
    public void salesCannotBeSmallerThanZero() {
        assertThrows(IllegalArgumentException.class, () -> album.setSales(-1));
    }

    @Test
    public void shouldSetSalesIfGivenSalesOfOne() {
        album.setSales(1);
        assertEquals(1, album.getSales());
    }

    /**
     * Testing Album rating
     */
    @Test
    public void ratingCannotBeGreaterThanFive() {
        assertThrows(IllegalArgumentException.class, () -> album.setRating(0));
    }

    @Test
    public void ratingCannotBeSmallerThanOne() {
        assertThrows(IllegalArgumentException.class, () -> album.setRating(6));
    }

    @Test
    public void shouldSetRatingIfGivenValueBetweenOneAndFive() {
        album.setRating(3);
        assertEquals(3, album.getRating());
    }

    /**
     * Testing Album genre
     */
    @Test
    public void genreCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> album.setGenre(null));
    }

    @Test
    public void genreCannotBeEmpty() {
        assertThrows(IllegalArgumentException.class, () -> album.setGenre(""));
    }

    @Test
    public void genreShouldBeSetIfGivenValidOne() {
        album.setGenre("Jazz");
        assertEquals("Jazz", album.getGenre());
    }
}