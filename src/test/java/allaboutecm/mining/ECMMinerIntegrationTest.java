package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration testing of both ECMMiner and the DAO classes together.
 */
class ECMMinerIntegrationTest {
    private static final String TEST_DB = "target/test-data/test-db.neo4j";

    private static DAO dao;
    private static ECMMiner ecmMiner;
    private static Session session;
    private static SessionFactory sessionFactory;

    @BeforeAll
    public static void setUp() {

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

    @AfterEach
    public void tearDownEach() {
        session.purgeDatabase();
    }

    @AfterAll
    public static void tearDown() throws IOException {
        session.purgeDatabase();
        session.clear();
        sessionFactory.close();
        File testDir = new File(TEST_DB);
        if (testDir.exists()) {
//            FileUtils.deleteDirectory(testDir.toPath());
        }
    }

    //Below are all of the tests for mostProlificMusicians method
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

    @ParameterizedTest
    @ValueSource(ints = {0,-1,-2,-3})
    @DisplayName("When mining the most prolific musicians the output can not be zero or less that zero")
    public void MiningTheMostProlificMusiciansKCanNotLessThanOrEqualToZero(int arg)
    {
        assertThrows( IllegalArgumentException.class,()-> ecmMiner.mostProlificMusicians(arg,-1,-1));
    }

    @ParameterizedTest
    @ValueSource(ints = {2009,2019})
    @DisplayName("The end year should greater that start year")
    public void theEndYearShouldGreaterThanTheStartYear(int argument)
    {
        assertThrows( IllegalArgumentException.class,()-> ecmMiner.mostProlificMusicians(1,argument,2008));
    }

    // Below are all of the tests for mostSocialMusician
    @ParameterizedTest
    @DisplayName("mostSocialMusicians function can not have the k less that or equal to zero")
    @ValueSource(ints = {0,-1})
    public void ShouldThrowExceptionIfMostSocialMusicianHaveTheNumberLessThanOrEqualToZero(int argument)
    {
        assertThrows(IllegalArgumentException.class, ()->ecmMiner.mostSocialMusicians(argument));
    }

    @Test
    @DisplayName("Most social musician should return list of one if there is only one musician")
    public void shouldReturnOneMusicianIfThereIsOnlyOne(){
        Musician musician = new Musician("Adele");
        Album album = new Album(2010,"1","a");
        album.setFeaturedMusicians(Lists.newArrayList(musician));
//        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
        dao.createOrUpdate(album);

        List<Musician> albumTest = ecmMiner.mostSocialMusicians(2);
        assertEquals(1, albumTest.size());
    }

    @Test
    @DisplayName("Most Social Musician should throw IllegalException If k is 0")
    public void mostSocialMusicianShouldThrowIllegalExceptionGivenKEqualsZero() {
        assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostSocialMusicians(0));
    }

    @Test
    @DisplayName("Most Social Musician should throw IllegalException If k is negative")
    public void mostSocialMusicianShouldThrowIllegalExceptionGivenKNegative() {
        assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostSocialMusicians(-1));
    }

    @Test
    @DisplayName("Most social musician should only return list with most social musician given three musicians")
    public void shouldReturnListWithMostSocialMusicianGivenThree(){
        Musician musician = new Musician("Adele");
        Musician musician1 = new Musician("David Gilmour");
        Musician musician2 = new Musician("Lady Gaga");
        Album album = new Album(2010,"1","Best Pop Songs");
        Album album1 = new Album(2019,"2","Best Rock Songs");
        ArrayList<Musician> socialTest1 = new ArrayList<>();
        ArrayList<Musician> socialTest2 = new ArrayList<>();
        socialTest1.add(musician);
        socialTest1.add(musician2);
        socialTest2.add(musician1);
        socialTest2.add(musician2);
        album.setFeaturedMusicians(socialTest1);
        album1.setFeaturedMusicians(socialTest2);

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);

        List<Musician> albumTest = ecmMiner.mostSocialMusicians(1);
        assertEquals(1,albumTest.size());
        assertTrue(albumTest.get(0).getName().equals("Lady Gaga"));
    }

    @Test
    public void shouldReturnThreeMusiciansWhenThereAreFive() {
        Musician mus1 = new Musician("Keith Jarrett");
        Musician mus2 = new Musician("Adale");
        Musician mus3 = new Musician("Lady");
        Musician mus4 = new Musician("MJ");
        Musician mus5 = new Musician("K");

        Album m1a1 = new Album(1975, "ECM 1064/65", "The Köln Concert");
        Album m1a2 = new Album(1973,"1","The Dark Side of the Moon");
        Album m1a3 = new Album(1995,"5","Shogun");
        mus1.setAlbums(Sets.newHashSet(m1a1, m1a2, m1a3));
        Album m2a1 = new Album(1979,"12","Rolling");
        Album m2a2 = new Album(1973,"2","The Dark Side of the Moon");
        mus2.setAlbums(Sets.newHashSet(m2a1, m2a2));
        Album m3a1 = new Album(1989,"3","Animals");
        Album m3a2 = new Album(2004, "ECM 33", "Hello World");
        mus3.setAlbums(Sets.newHashSet(m3a1, m3a2));
        Album m4a1 = new Album(1986,"4","Division Bell");
        mus4.setAlbums(Sets.newHashSet(m4a1));
        Album m5a1 = new Album(1995,"6","No Need To Argue");
        mus5.setAlbums(Sets.newHashSet(m5a1));

        dao.createOrUpdate(mus1);
        dao.createOrUpdate(mus2);
        dao.createOrUpdate(mus3);
        dao.createOrUpdate(mus4);
        dao.createOrUpdate(mus5);

        List<Musician> results = ecmMiner.mostProlificMusicians(3, -1, -1);
        assertEquals(3, results.size());
    }

    //Below are all of the tests for busiestYears method
    @Test
    @DisplayName("Busiest year list should return empty list if k=0")
    public void shouldReturnEmptyListOfBusiestYearsWhenKEqualsZero(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        dao.createOrUpdate(album);

        List<Integer> albumTest = ecmMiner.busiestYears(0);
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Busiest year list should return empty list when k is negative")
    public void shouldReturnEmptyListOfBusiestYearsWhenKIsNegative(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");

        dao.createOrUpdate(album);

        List<Integer> albumTest = ecmMiner.busiestYears(-1);
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Busiest year list should not contain null values")
    public void busiestYearListShouldNotContainNullValues(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");

        dao.createOrUpdate(album);

        List<Integer> albumTest = ecmMiner.busiestYears(2);
        assertFalse(albumTest.contains(null));
        assertNotNull(albumTest.get(0));
    }


    @Test
    @DisplayName("Busiest year list should only return year with most albums released when k = 1")
    public void shouldReturnBusiestYearWhenKEqualsOne(){
        Album album = new Album(1979,"2","Rolling");
        Album album1 = new Album(1973,"1","The Dark Side of the Moon");
        Album album2 = new Album(1973,"2","Animals");

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);

        List<Integer> albumTest = ecmMiner.busiestYears(1);
        assertEquals(1,albumTest.size());
        assertEquals(1973, (int) albumTest.get(0));
    }

    @Test
    @DisplayName("Busiest year list should only return list of amount of years when k is greater than amount of years")
    public void shouldOnlyReturnBusiestYearsWhenKGreaterThanAmountOfYears(){
        Album album = new Album(1979,"2","Rolling");

        dao.createOrUpdate(album);

        List<Integer> albumTest = ecmMiner.busiestYears(2);
        assertEquals(1,albumTest.size());
    }

    @Test
    @DisplayName("Busiest year list should return list in order from busiest to least busy")
    public void shouldReturnBusiestYearListInOrderFromBusiestToLeastBusy(){
        Album album = new Album(1979,"1","Rolling");
        Album album1 = new Album(1973,"2","The Dark Side of the Moon");
        Album album2 = new Album(1973,"3","Animals");
        Album album3 = new Album(1973,"4","Division Bell");
        Album album4 = new Album(1995,"5","Shogun");
        Album album5 = new Album(1995,"6","No Need To Argue");

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);

        List<Integer> albumTest = ecmMiner.busiestYears(3);
        assertEquals(3,albumTest.size());
        assertTrue(albumTest.contains(1973));
        assertTrue(albumTest.contains(1995));
        assertTrue(albumTest.contains(1979));
    }


    @Test
    @DisplayName("Should return one year if all the albums were released in the same year")
    public void shouldReturnOneYearIfAllTheAlbumsReleasedInTheSameYear() {
        Album album1 = new Album(1973,"1","The Dark Side of the Moon");
        Album album2 = new Album(1973, "ECM 1064/65", "The Köln Concert");

        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);

        List<Integer> years = ecmMiner.busiestYears(5);
        assertEquals(1, years.size());
        assertEquals(1973, years.get(0));
    }

    @Test
    @DisplayName("Should return two years if there three year have albums released given k=2")
    public void shouldReturnTwoYearIfThereAreThreeYearsInTotalGivenKisTwo() {
        Album album1 = new Album(1973,"1","The Dark Side of the Moon");
        Album album2 = new Album(1973, "ECM 1064/65", "The Köln Concert");
        Album album3 = new Album(1973, "ECM 1064/65", "The Köln Concert");Album album = new Album(2002,"1","a");
        Album album4 = new Album(2006,"2","b");
        Album album5 = new Album(2006,"3","c");
        Album album6 = new Album(2002,"4","d");

        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);
        dao.createOrUpdate(album3);
        dao.createOrUpdate(album4);
        dao.createOrUpdate(album5);
        dao.createOrUpdate(album6);

        List<Integer> years = ecmMiner.busiestYears(2);
        assertEquals(2, years.size());
        assertTrue(years.contains(1973));
        assertTrue(years.contains(2006));
    }

    //Below are all of the tests for mostSimilarAlbum method
    @Test
    @DisplayName("Similar album list should return empty list if k=0")
    public void shouldReturnEmptyListOfSimilarAlbumIfKEqualsZero(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");

        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.mostSimilarAlbums(0,"Rock","David Gilmour");
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Similar album list should return empty list if k is negative")
    public void shouldReturnEmptyListOfSimilarAlbumIfKNegative(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");

        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.mostSimilarAlbums(-1,"Rock","David Gilmour");
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Similar album list should return empty list if genre is blank")
    public void shouldReturnEmptyListOfSimilarAlbumIfGenreIsBlank(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");

        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.mostSimilarAlbums(1,"","David Gilmour");
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Similar album list should not contain null values")
    public void similarAlbumListShouldNotContainNullValues(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setGenre("Rock");
        Musician musician = new Musician("David Gilmour");
        ArrayList<Musician> musicianListTest1 = new ArrayList<>();
        musicianListTest1.add(musician);
        album.setFeaturedMusicians(musicianListTest1);

        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.mostSimilarAlbums(2,"Rock","David Gilmour");
        assertFalse(albumTest.contains(null));
        assertNotNull(albumTest.get(0));
    }

    @Test
    @DisplayName("Similar album list should return albums with same genre given two albums with same genre")
    public void similarAlbumListShouldReturnSimilarGenreGivenTwoAlbumsWithSameGenre(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setGenre("Rock");
        Album album1 = new Album(1979,"2","Animals");
        album1.setGenre("Rock");

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);

        List<Album> albumTest = ecmMiner.mostSimilarAlbums(1,"Rock","");
        assertEquals(1, albumTest.size());
    }

    @Test
    @DisplayName("Similar album should return empty albums list if no album genre matches input genre")
    public void similarAlbumListShouldReturnEmptyListIfNoAlbumMatch(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setGenre("Rock");
        Album album1 = new Album(1979,"2","Animals");
        album1.setGenre("Rock");
        Musician musician = new Musician("David Gilmour");
        ArrayList<Musician> musicianListTest1 = new ArrayList<>();
        musicianListTest1.add(musician);
        album.setFeaturedMusicians(musicianListTest1);
        Musician musician1 = new Musician("Matt Farrell");
        ArrayList<Musician> musicianListTest2 = new ArrayList<>();
        musicianListTest2.add(musician1);
        album1.setFeaturedMusicians(musicianListTest2);

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);

        List<Album> albumTest = ecmMiner.mostSimilarAlbums(1,"Rock","Matt Farrell");
        assertFalse(albumTest.contains(album));
        assertTrue(albumTest.contains(album1));
    }

    @Test
    @DisplayName("Similar album list should return albums with same genre and musician (as input) given two musicians")
    public void similarAlbumListShouldReturnSimilarAlbumWhosMusicianMatchesInputMusician(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setGenre("Rock");
        Album album1 = new Album(1979,"2","Animals");
        album1.setGenre("Rock");

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);

        List<Album> albumTest = ecmMiner.mostSimilarAlbums(2,"Rock","");
        assertTrue(albumTest.contains(album));
        assertTrue(albumTest.contains(album1));
    }

    //Below are all of the tests for bestKSellingAlbums method
    @Test
    @DisplayName("Best selling albums should return empty list if k=0")
    public void shouldReturnEmptyListOfBestSellingAlbumsWhenKEqualsZero(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setSales(100000);

        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.bestKSellingAlbums(0);
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Best selling albums should return empty list if k is negative")
    public void shouldReturnEmptyListOfBestSellingAlbumsWhenKNegative(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setSales(100000);

        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.bestKSellingAlbums(-1);
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Best selling albums should not return null values")
    public void shouldNotHaveNullInBestSellingAlbumList(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setSales(100000);

        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.bestKSellingAlbums(2);
        assertFalse(albumTest.contains(null));
    }

    @Test
    @DisplayName("Best selling albums should return list of size of objects when k is greater than number of objects")
    public void shouldReturnListWithSameSizeAsObjectsWhenKIsGreaterThanNumberOfObjects(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setSales(100000);

        dao.createOrUpdate(album);

        List<Album> albumTest = ecmMiner.bestKSellingAlbums(3);
        assertEquals(1,albumTest.size());

    }

    @Test
    @DisplayName("Best selling albums should return best selling album when given two if k = 1")
    public void shouldReturnBestSellingAlbumGivenTwoAlbums(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setSales(100000);
        Album album1 = new Album(1979,"2","Animals");
        album1.setSales(200000);

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);

        List<Album> albumTest = ecmMiner.bestKSellingAlbums(1);
        assertEquals(1,albumTest.size());
        assertTrue(albumTest.contains(album1));
    }

    @Test
    @DisplayName("Best selling albums should return list sorted from highest sales to lowest")
    public void shouldReturnBestSellingAlbumsSortedFromHighestToLowest(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        Album album1 = new Album(1979,"2","Animals");
        Album album2 = new Album(1994,"3","Division Bell");
        album.setSales(100000);
        album1.setSales(200000);
        album2.setSales(90000);

        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);
        dao.createOrUpdate(album2);

        List<Album> albumTest = ecmMiner.bestKSellingAlbums(3);
        assertTrue(albumTest.get(0).getAlbumName().equals("Animals"));
        assertTrue(albumTest.get(1).getAlbumName().equals("The Dark Side of the Moon"));
        assertTrue(albumTest.get(2).getAlbumName().equals("Division Bell"));
    }

    //Below are all of the tests for mostTalentedMusicians method
    @Test
    @DisplayName("Most Talented Musicians should throw IllegalException If k is invalid")
    public void shouldThrowIllegalExceptionGivenKBelowOne() {
        assertThrows(IllegalArgumentException.class, () -> ecmMiner.mostTalentedMusicians(0));
    }

    @Test
    @DisplayName("Most Talented Musicians should return one if there is exactly one")
    public void shouldReturnOneMusicianIfThereIsOnlyOneMusicianInstrument() {
        MusicalInstrument instrument1 = new MusicalInstrument("Piano");
        Musician musician1 = new Musician("Keith Jarrett");

        dao.createOrUpdate(new MusicianInstrument(musician1, Sets.newHashSet(instrument1)));

        List<Musician> musicians = ecmMiner.mostTalentedMusicians(5);
        assertEquals(musicians.size(), 1);
        assertTrue(musicians.contains(musician1));
    }

    @Test
    @DisplayName("Most Talented Musicians should return three musicians if there are five musicians in total when given k=3")
    public void shouldReturnThreeIfThereAreFiveMusicianInstrumentsGivenKISThree() {
        MusicalInstrument instrument1 = new MusicalInstrument("Piano");
        MusicalInstrument instrument2 = new MusicalInstrument("Violin");
        MusicalInstrument instrument3 = new MusicalInstrument("Guitar");
        MusicalInstrument instrument4 = new MusicalInstrument("Sax");

        Musician musician1 = new Musician("Keith Jarrett");
        Musician musician2 = new Musician("Adele");
        Musician musician3 = new Musician("Lady Gaga");
        Musician musician4 = new Musician("Yoshino Nanjo");
        Musician musician5 = new Musician("Kazuya Kamenashi");

        MusicianInstrument expectedOne = new MusicianInstrument(musician1, Sets.newHashSet(instrument1, instrument2, instrument3));
        MusicianInstrument expectedTwo = new MusicianInstrument(musician3, Sets.newHashSet(instrument1, instrument4, instrument3, instrument2));
        MusicianInstrument expectedThree = new MusicianInstrument(musician4, Sets.newHashSet(instrument1, instrument2));
        MusicianInstrument notExpectedOne = new MusicianInstrument(musician2, Sets.newHashSet(instrument1));
        MusicianInstrument notExpectedTwo = new MusicianInstrument(musician5, Sets.newHashSet(instrument2));

        dao.createOrUpdate(expectedOne);
        dao.createOrUpdate(expectedTwo);
        dao.createOrUpdate(expectedThree);
        dao.createOrUpdate(notExpectedOne);
        dao.createOrUpdate(notExpectedTwo);

        List<Musician> musicians = ecmMiner.mostTalentedMusicians(3);

        assertEquals(3, musicians.size());
        assertTrue(musicians.contains(expectedOne.getMusician()));
        assertTrue(musicians.contains(expectedTwo.getMusician()));
        assertTrue(musicians.contains(expectedThree.getMusician()));
    }

    //Below are all of the tests for topKRatedAlbums method
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
    @DisplayName("Highest Rated album should return album with highest rating when given two albums")
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
    @DisplayName("Highest rated album should return list that is size of albums if k > albums")
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

    //Below are all of the tests for topKRatedMusicians method
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

    //Below are all of the tests for musiciansHighestRatedAlbums method
    @Test
    @DisplayName("Musician highest rated album should return empty list if k = 0")
    public void shouldReturnEmptyListForMusicianHighestRatedAlbumWhenKEqualsZero(){
        Album album = new Album(2010,"1","a");
        Musician musician = new Musician("Noel Hogan");
        album.setRating(3);

        dao.createOrUpdate(musician);
        dao.createOrUpdate(album);

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

        dao.createOrUpdate(musician);
        dao.createOrUpdate(album);

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

        dao.createOrUpdate(musician);
        dao.createOrUpdate(album);
        dao.createOrUpdate(album1);

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