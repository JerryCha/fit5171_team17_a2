package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO: perform unit testing on the ECMMiner class, by making use of mocking.
 */
class ECMMinerUnitTest {
    private DAO dao;
    private ECMMiner ecmMiner;

    @BeforeEach
    public void setUp() {

        dao = mock(Neo4jDAO.class);
        ecmMiner = new ECMMiner(dao);
    }

    // Test mostProlificMusicians
    @Test
    public void shouldReturnTheMusicianWhenThereIsOnlyOne() {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));

        List<Musician> musicians = ecmMiner.mostProlificMusicians(5, -1, -1);

        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician));
    }

    @Test
    public void shouldReturnThreeWhenThereAreFive() {
        assertEquals(true, false);
    }

    // Test mostTalentedMusicians
    @Test
    public void shouldReturnTheMusicianWhenThereIsOnlyOneMusician() {
        Musician musician = new Musician("Keith Jarrett");
        MusicalInstrument instrument = new MusicalInstrument("Piano");
        MusicianInstrument musicianInstrument = new MusicianInstrument(musician, Sets.newHashSet(instrument));
        when(dao.loadAll(MusicianInstrument.class)).thenReturn(Sets.newHashSet(musicianInstrument));

        List<Musician> musicians = ecmMiner.mostTalentedMusicians(5);
        assertEquals(musicians.size(), 1);
        assertTrue((musicians.contains(musician)));
    }

    @Test
    public void shouldReturnThreeMusiciansWhenGivenFiveMusicianInstruments() {
        assertEquals(true, false);
    }

    // Test mostSocialMusicians
    @Test
    public void shouldReturnOneMusicianIfThereAreOnlyOneMusician() {
        Musician musician = new Musician("Keith Jarrett");
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        album.setFeaturedMusicians(Lists.newArrayList(musician));
        musician.setAlbums(Sets.newHashSet(album));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));

        List<Musician> musicians = ecmMiner.mostSocialMusicians(5);

        assertEquals(musicians.size(), 1);
        assertTrue(musicians.contains(musician));
    }

    @Test
    public void shouldReturnThreeMusiciansIfThereAreFiveMusiciansCollaboratingWithEachOther() {
        assertEquals(true, false);
    }

    // Test busiestYears
    @Test
    public void shouldReturnTheBusiestYear() {
        assertEquals(true, false);
    }

    // Test mostSimilarAlbums
    @Test
    public void shouldReturnOneAlbumIfThereIsOnlyOneSimilarAlbum() {
        assertEquals(true, false);
    }

    @Test
    public void shouldReturnThreeAlbumsIfThereAreFiveSimilarAlbums() {
        assertEquals(true, false);
    }

    // Test bestKSellingAlbums
    @Test
    public void shouldReturnOneAlbumIfThereIsOnlyOneAlbumWithSales() {
        assertEquals(true, false);
    }

    @Test
    public void shouldReturnThreeAlbumWhenThereAreFiveAlbumsGivenKIsThree() {
        assertEquals(true, false);
    }

    // Test topKRatedAlbums
    @Test
    public void shouldReturnOneAlbumIfThereIsOnlyOneAlbumWithRating() {
        assertEquals(true, false);
    }

    @Test
    public void shouldReturnThreeIfThereAreFiveAlbumsGivenKISThree() {
        assertEquals(true, false);
    }

    // Test topKRatedMusicians
    @Test
    public void shouldReturnOneMusicianOfThereIsOnlyOneMusician() {
        assertEquals(true, false);
    }

    // Test musicianHighestRatedAlbums
    @Test
    public void shouldReturnThreeAlbumOutOfFiveOfTheMusician() {
        List<Album> albums = new ArrayList<>();
        Album album1 = new Album(2018, "1111", "rating 4");
        album1.setRating(4);
        Album album2 = new Album(2018, "2222", "rating 3");
        album2.setRating(3);
        Album album3 = new Album(2019, "3333", "rating 4");
        album3.setRating(4);
        Album album4 = new Album(2017, "4444", "rating 3");
        album4.setRating(3);
        Album album5 = new Album(2019, "5555", "rating 5");
        album5.setRating(5);
        Musician musician = new Musician("Anonymous");
        album1.setFeaturedMusicians(Lists.newArrayList(musician));
        album2.setFeaturedMusicians(Lists.newArrayList(musician));
        album3.setFeaturedMusicians(Lists.newArrayList(musician));
        album4.setFeaturedMusicians(Lists.newArrayList(musician));
        album5.setFeaturedMusicians(Lists.newArrayList(musician));

        albums.add(album1);
        albums.add(album2);
        albums.add(album3);
        albums.add(album4);
        albums.add(album5);

        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));
        when(dao.loadAll(Album.class)).thenReturn(albums);

        List<Album> result = ecmMiner.musiciansHighestRatedAlbums("Anonymous", 3);
        assertEquals(3, result.size());
        int[] ratingResults = new int[]{5, 4, 4};
        for (int i = 0; i < 3; i++)
            assertEquals(ratingResults[i], result.get(i).getRating());
    }

}