package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
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
    public void shouldReturnOneAlbumOfTheMusician() {
        assertEquals(true, false);

    }

}