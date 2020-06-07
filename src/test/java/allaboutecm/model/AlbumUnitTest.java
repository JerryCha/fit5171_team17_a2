package allaboutecm.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
     * Album year cannot be greater than the current year
     */

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
    @DisplayName("Record number cannot be null")
    public void recordCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> album.setRecordNumber(null));
    }

    @ParameterizedTest
    @DisplayName("Record number should not be null")
    @ValueSource(strings = {"", "ECM 1", "  12345678  "})
    public void recordCannotBeInvalidFormat(String arg) {
        assertThrows(IllegalArgumentException.class, () -> album.setRecordNumber(arg));
    }

    /**
     * Test instruments
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException if set instrument to null")
    public void shouldThrowIllegalArgumentExceptionGivenInstrumentNull() {
        assertThrows(IllegalArgumentException.class, () -> album.setInstruments(null));
    }

    @Test
    @DisplayName("Should set instruments if given valid one")
    public void shouldSetInstrumentsIfGivenValidOne() {
        MusicianInstrument instrument = new MusicianInstrument(new Musician("K"), Sets.newHashSet(new MusicalInstrument("Piano")));
        Set<MusicianInstrument> insSet = Sets.newHashSet(instrument);
        album.setInstruments(insSet);
        assertEquals(insSet, album.getInstruments());
    }

    /**
     * Test featuredMusicians
     */
    @Test
    @DisplayName("FeaturedMusician cannot be null")
    public void featuredMusiciansCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () -> album.setFeaturedMusicians(null));
    }

    @Test
    @DisplayName("Should set featuredMusicians given valid one")
    public void shouldSetFeaturedMusiciansGivenValidOne() {
        List<Musician> musicians = Lists.newArrayList(new Musician("K"));
        album.setFeaturedMusicians(musicians);
        assertEquals(musicians, album.getFeaturedMusicians());
    }

    /**
     * Test tracks
     */
    @Test
    @DisplayName("Tracks cannot be null")
    public void shouldThrowIllegalArgumentExceptionGiveTrackNull() {
        assertThrows(IllegalArgumentException.class, () -> album.setTracks(null));
    }

    @Test
    @DisplayName("Track should be set given valid one")
    public void shouldSetTracksIfGivenValidOne() {
        List<String> tracks = Lists.newArrayList("Decade");
        album.setTracks(tracks);
        assertEquals(tracks, album.getTracks());
    }

    @Test
    @DisplayName("Track to be added cannot be null")
    public void trackToBeAddedCannotBeNull() {
        List<String> tracks = Lists.newArrayList("Decade");
        album.setTracks(tracks);
        assertThrows(NullPointerException.class, () -> album.addATrack(null));
    }

    @Test
    @DisplayName("Track should be successfully added if it is valid")
    public void trackShouldAddIfValid() {
        List<String> tracks = Lists.newArrayList("Decade");
        album.setTracks(tracks);
        album.addATrack("WhiteBird");
        tracks.add("WhiteBird");

        assertEquals(tracks, album.getTracks());
    }

    /**
     * Test URL
     */
    @Test
    @DisplayName("Album URL should be set")
    public void URLShouldBeSetIfValid() {
        URL albumUrl = null;
        try {
            albumUrl = new URL("https://https://www.ecmrecords.com/shop/143038752983/selected-signs-iii-viii-various-artists");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (albumUrl != null) {
            album.setAlbumURL(albumUrl);
            assertEquals(albumUrl, album.getAlbumURL());
        }
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

    /**
     * Test additional information
     */
    @Test
    public void shouldSetAdditionalInformationGivenNull() {
        album.setAdditionalInformation(null);
        assertNull(album.getAdditionalInformation());
    }

    @ParameterizedTest
    @ValueSource(strings = "test information")
    public void shouldSetAdditionalInformationGivenNonEmptyString(String arg) {
        album.setAdditionalInformation(arg);
        assertEquals(arg, album.getAdditionalInformation());
    }

    /**
     * Test equal
     */
    @Test
    public void shouldReturnTrueIfGivenSameAlbum() {
        Album anotherAlbum = new Album(1975, "ECM 1064/65", "The Köln Concert");;
        assertTrue(album.equals(anotherAlbum));
        // compare with itself
        assertTrue(album.equals(album));
    }

    @Test
    public void shouldReturnFalseIfGivenDifferentAlbum() {
        Album anotherAlbum = new Album(1975, "ECM 1064/66", "The different world");
        assertFalse(album.equals(anotherAlbum));
    }

    @Test
    public void shouldReturnFalseIfGivenNullOrDifferentClassOfInstance() {
        assertFalse(album.equals(null));
        assertFalse(album.equals(new Musician("foo")));
    }
}