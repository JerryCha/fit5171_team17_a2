package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Album;
import allaboutecm.model.Musician;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration testing of both ECMMiner and the DAO classes together.
 */
class ECMMinerIntegrationTest {
    private DAO dao;
    private ECMMiner ecmMiner;
    private static Session session;
    private static SessionFactory sessionFactory;

    @BeforeEach
    public void setUp() {

        // See @https://neo4j.com/docs/ogm-manual/current/reference/ for more information.

        // To use an impermanent embedded data store which will be deleted on shutdown of the JVM,
        // you just omit the URI attribute.

        // Impermanent embedded store
        Configuration configuration = new Configuration.Builder().build();

        // Disk-based embedded store
        // Configuration configuration = new Configuration.Builder().uri(new File(TEST_DB).toURI().toString()).build();

        // HTTP data store, need to install the Neo4j desktop app and create & run a database first.
//        Configuration configuration = new Configuration.Builder().uri("http://neo4j:password@localhost:7474").build();

        sessionFactory = new SessionFactory(configuration, Musician.class.getPackage().getName());
        session = sessionFactory.openSession();

        dao = new Neo4jDAO(session);
        ecmMiner = new ECMMiner(dao);
    }

    @Test
    @DisplayName("Most Prolific Musician should return one given one")
    public void shouldReturnTheMusicianWhenThereIsOnlyOne() {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        Musician musician = new Musician("Keith Jarrett");
        musician.setAlbums(Sets.newHashSet(album));

        dao.createOrUpdate(musician);
        dao.createOrUpdate(album);

        List<Musician> musicians = ecmMiner.mostProlificMusicians(5, -1, -1);

        assertEquals(1, musicians.size());
        assertTrue(musicians.contains(musician));
    }
    @Test
    @DisplayName("Highest Rated album should return empty list if k=0")
    public void shouldReturnEmptyListOfHighestRatedAlbumWhenKEqualsZero(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.topKRatedAlbums(0);
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Highest Rated album should return list with no null entries")
    public void shouldNotHaveNullInHighestRatedAlbumList(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");

        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.topKRatedAlbums(2);
        assertFalse(albumTest.contains(null));
    }

    @Test
    @DisplayName("Highest Rated album should return empty list if k is negative")
    public void shouldReturnEmptyListOfHighestRatedAlbumsWhenKIsNegative(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");

        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.topKRatedAlbums(-1);
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Highest Rated album should return album with highest rating when given two musicians")
    public void shouldReturnTheHighestRatedAlbumGivenTwoMusicians() {
        Album album = new Album(2005,"2","a");
        Album album1 = new Album(2010,"3","b");
        album.setRating(4);
        album1.setRating(5);
        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);

        List<Album> albumTest = ecmMiner.topKRatedAlbums(1);
        assertEquals(1, albumTest.size());
        assertTrue(albumTest.contains(album1));
    }

    @Test
    @DisplayName("Highest rated album should return list that is size of musicians if k > albums")
    public void shouldReturnListSizeEqualToNumberOfAlbumsIfKGreaterThanAlbums(){
        Album album = new Album(2015,"1","c");
        album.setRating(1);

        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.topKRatedAlbums(4);
        assertEquals(1,albumTest.size());
    }

    @Test
    @DisplayName("Highest rated album should return albums in order based on rating")
    public void shouldReturnHighestRatedAlbumsInOrderHighestToLowest(){
        Album album = new Album(2002,"1","a");
        Album album1 = new Album(2006,"2","b");
        Album album2 = new Album(2010,"3","c");
        album.setRating(4);
        album1.setRating(2);
        album2.setRating(3);

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);

        List<Album> albumTest = ecmMiner.topKRatedAlbums(3);
        assertTrue(albumTest.get(0).getAlbumName().equals("a"));
        assertTrue(albumTest.get(1).getAlbumName().equals("c"));
        assertTrue(albumTest.get(2).getAlbumName().equals("b"));
    }

    @Test
    @DisplayName("Highest Rated musician should return empty list if k=0")
    public void shouldReturnEmptyListOfHighestRatedMusicianWhenKEqualsZero(){
        Musician musician = new Musician("Roger Waters");

        dao.createOrUpdate(musician);

        List<Musician> musicianTest = ecmMiner.topKRatedMusicians(0);
        assertEquals(0,musicianTest.size());
        assertTrue(musicianTest.isEmpty());
    }

    @Test
    @DisplayName("Highest Rated Musician should return list with no null entries")
    public void shouldNotHaveNullInHighestRatedMusicianList(){
        Musician musician = new Musician("Adele");
        musician.setRating(1);

        dao.createOrUpdate(musician);

        List<Musician > musicianTest = ecmMiner.topKRatedMusicians(2);
        assertFalse(musicianTest.contains(null));
    }

    @Test
    @DisplayName("Highest rated musician should return empty list if k is negative")
    public void shouldOnlyReturnHighestRatedMusicians(){
        Musician musician1 = new Musician("Lady Gaga");
        musician1.setRating(4);

        dao.createOrUpdate(musician1);

        List<Musician > musicianTest = ecmMiner.topKRatedMusicians(-1);
        assertEquals(0,musicianTest.size());
    }

    @Test
    @DisplayName("Highest Rated musician should return musician with highest rating when given two musicians")
    public void shouldReturnTheHighestRatedMusicianGivenTwoMusicians(){
        Musician musician = new Musician("David Gilmour");
        Musician musician1 = new Musician("Syd Barrett");
        musician.setRating(4);
        musician1.setRating(5);

        dao.createOrUpdate(musician);
        dao.createOrUpdate(musician1);

        List<Musician > musicianTest = ecmMiner.topKRatedMusicians(1);
        assertEquals(1,musicianTest.size());
        assertTrue(musicianTest.contains(musician1));
    }

    @Test
    @DisplayName("Highest rated musician should return list that is size of musicians if k > musicians")
    public void shouldReturnListSizeEqualToNumberOfMusiciansIfKGreaterThanMusicians(){
        Musician musician1 = new Musician("Matt Heafy");
        musician1.setRating(1);

        dao.createOrUpdate(musician1);

        List<Musician> musicianTest = ecmMiner.topKRatedMusicians(4);
        assertEquals(1,musicianTest.size());
    }

    @Test
    @DisplayName("Highest rated musician should return musicians in order based on rating")
    public void shouldReturnHighestRatedMusiciansInOrderHighestToLowest(){
        Musician musician1 = new Musician("Dolores O'Riordan");
        Musician musician2 = new Musician("Noel Hogan");
        Musician musician3 = new Musician("Fergal Lawler");
        musician1.setRating(4);
        musician2.setRating(2);
        musician3.setRating(3);

        dao.createOrUpdate(musician1);
        dao.createOrUpdate(musician2);
        dao.createOrUpdate(musician3);

        List<Musician > musicianTest = ecmMiner.topKRatedMusicians(3);
        assertTrue(musicianTest.get(0).getName().equals("Dolores O'Riordan"));
        assertTrue(musicianTest.get(1).getName().equals("Fergal Lawler"));
        assertTrue(musicianTest.get(2).getName().equals("Noel Hogan"));
    }

    @Test
    @DisplayName("Musician highest rated album should return empty list if k = 0")
    public void shouldReturnEmptyListForMusicianHighestRatedAlbumWhenKEqualsZero(){
        Album album = new Album(2010,"1","a");
        Musician musician = new Musician("Noel Hogan");
        album.setRating(3);

        dao.createOrUpdate(album);
        dao.createOrUpdate(musician);

        List<Album> albumTest = ecmMiner.musiciansHighestRatedAlbums("Noel Hogan",0);
        assertTrue(albumTest.size() == 0);
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Musician highest rated album should return empty list if k is negative")
    public void shouldReturnEmptyListForMusicianHighestRatedAlbumWhenKNegative(){
        Album album = new Album(2010,"1","a");
        Musician musician = new Musician("Noel Hogan");
        album.setRating(3);

        dao.createOrUpdate(album);
        dao.createOrUpdate(musician);
        List<Album> albumTest = ecmMiner.musiciansHighestRatedAlbums("Noel Hogan",-1);
        assertTrue(albumTest.size() == 0);
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Musician highest rated album should return list with no null entries")
    public void shouldNotHaveNullInMusicianHighestRatedAlbumList(){
        Musician musician = new Musician("Adele");
        Album album = new Album(2010,"1","a");
        album.setRating(3);

        dao.createOrUpdate(musician);
        dao.createOrUpdate(album);
        List<Album> albumTest = ecmMiner.musiciansHighestRatedAlbums("Adele",2);
        assertFalse(albumTest.contains(null));
    }

    @Test
    @DisplayName("Musicians highest Rated album should return album with highest rating when given two albums")
    public void shouldReturnMusiciansHighestRatedAlbumGivenTwoAlbums(){
        Album album = new Album(2010,"1","a");
        Album album1 = new Album(2009,"2","b");
        album.setRating(3);
        album1.setRating(4);
        Musician musician = new Musician("Noel Hogan");
        ArrayList<Musician> musicianListTest1 = new ArrayList<>();
        musicianListTest1.add(musician);
        album.setFeaturedMusicians(musicianListTest1);
        album1.setFeaturedMusicians(musicianListTest1);

        dao.createOrUpdate(musician);
        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);

        List<Album> albumTest = ecmMiner.musiciansHighestRatedAlbums("Noel Hogan",1);
        assertEquals(1,albumTest.size());
        assertTrue(albumTest.contains(album1));
    }

    @Test
    @DisplayName("Musicians highest Rated album should return number of musicians albums if k > musicians albums")
    public void shouldReturnMusiciansHighestRatedAlbumsIfKGreaterThanMusiciansAlbums(){
        Album album = new Album(2010,"1","a");
        Album album1 = new Album(2009,"2","b");
        album.setRating(3);
        album1.setRating(4);
        Musician musician = new Musician("Noel Hogan");
        ArrayList<Musician> musicianListTest1 = new ArrayList<>();
        musicianListTest1.add(musician);
        album.setFeaturedMusicians(musicianListTest1);
        album1.setFeaturedMusicians(musicianListTest1);

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);
        dao.createOrUpdate(musician);

        List<Album> albumTest = ecmMiner.musiciansHighestRatedAlbums("Noel Hogan",3);
        assertEquals(2,albumTest.size());
    }

    @Test
    @DisplayName("Musicians highest Rated album should return albums in order from highest rating to lowest")
    public void shouldReturnMusiciansHighestRatedAlbumInOrderHighestTOLowest(){
        Album album = new Album(2010,"1","a");
        Album album1 = new Album(2009,"2","b");
        Album album2 = new Album(2005,"3","c");
        album.setRating(3);
        album1.setRating(4);
        album2.setRating(1);
        Musician musician = new Musician("Noel Hogan");
        ArrayList<Musician> musicianListTest1 = new ArrayList<>();
        musicianListTest1.add(musician);
        album.setFeaturedMusicians(musicianListTest1);
        album1.setFeaturedMusicians(musicianListTest1);
        album2.setFeaturedMusicians(musicianListTest1);

        dao.createOrUpdate(musician);

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        List<Album> albumTest = ecmMiner.musiciansHighestRatedAlbums("Noel Hogan",3);
        assertTrue(albumTest.get(0).getAlbumName().equals("b"));
        assertTrue(albumTest.get(1).getAlbumName().equals("a"));
        assertTrue(albumTest.get(2).getAlbumName().equals("c"));
    }

    @Test
    @DisplayName("Musicians highest Rated album should only return musicians albums if name equals musicianName")
    public void shouldOnlyReturnMusiciansHighestRatedAlbumsIfNameEqualsMusicianName() {
        Album album = new Album(2010, "1", "a");
        Album album1 = new Album(2009, "2", "b");
        album.setRating(4);
        album1.setRating(3);
        Musician musician = new Musician("Noel Hogan");
        Musician musician1 = new Musician("Fergal Lawler");
        ArrayList<Musician> musicianListTest1 = new ArrayList<>();
        ArrayList<Musician> musicianListTest2 = new ArrayList<>();
        musicianListTest1.add(musician);
        musicianListTest2.add(musician);
        musicianListTest2.add(musician1);
        album.setFeaturedMusicians(musicianListTest1);
        album1.setFeaturedMusicians(musicianListTest2);
        dao.createOrUpdate(musician);
        dao.createOrUpdate(musician1);

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);

        List<Album> albumTest = ecmMiner.musiciansHighestRatedAlbums("Fergal Lawler", 1);
        assertEquals(1, albumTest.size());
        assertTrue(albumTest.get(0).getAlbumName().equals("b"));
    }
}