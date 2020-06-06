package allaboutecm.dataaccess.neo4j;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.MusicalInstrument;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TODO: add test cases to adequately test the Neo4jDAO class.
 */
class Neo4jDAOUnitTest {
    private static final String TEST_DB = "target/test-data/test-db.neo4j";

    private static DAO dao;
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

    @Test
    public void daoIsNotEmpty() {
        assertNotNull(dao);
    }

    /**
     * Creation for Musician
     * @throws MalformedURLException
     */
    @Test
    public void successfulCreationAndLoadingOfMusician() throws MalformedURLException {
        assertEquals(0, dao.loadAll(Musician.class).size());

        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        dao.createOrUpdate(musician);
        Musician loadedMusician = dao.load(Musician.class, musician.getId());

        assertNotNull(loadedMusician.getId());
        assertEquals(musician, loadedMusician);
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());
        assertEquals(1, dao.loadAll(Musician.class).size());

    }

    /**
     * Creation for Album & relationship to musician
     * @throws MalformedURLException
     */
    @Test
    public void successfulCreationOfMusicianAndAlbum() throws MalformedURLException {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        musician.setAlbums(Sets.newHashSet(album));

        dao.createOrUpdate(album);
        dao.createOrUpdate(musician);

        Collection<Musician> musicians = dao.loadAll(Musician.class);
        assertEquals(1, musicians.size());
        Musician loadedMusician = musicians.iterator().next();
        assertEquals(musician, loadedMusician);
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());
        assertEquals(musician.getAlbums(), loadedMusician.getAlbums());
    }

    /**
     * Read of Album of a musician
     * @throws MalformedURLException
     */
    @Test
    public void successfulReadOfAlbumOfMusician() throws MalformedURLException {
        Musician musician = new Musician("Keith Jarrett");     //create a musician object
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");//create a album object
        album.setAlbumURL(new URL("https://tesingalbum.org/"));

        dao.createOrUpdate(album);
        dao.createOrUpdate(musician);

        Collection<Musician> musicians = dao.loadAll(Musician.class); // collection of the musicians
        assertEquals(1, musicians.size());             // only one musician in musicians collection
        Musician loadedMusician = musicians.iterator().next();
        assertEquals(musician.getAlbums(), loadedMusician.getAlbums());     // test empty album
        assertEquals(0, loadedMusician.getAlbums().size());     // test empty album

        musician.setAlbums(Sets.newHashSet(album));   // set the album of the musician
        assertEquals(musician.getAlbums(), loadedMusician.getAlbums());     // test case for reading of album of a musician
        assertEquals(1, loadedMusician.getAlbums().size());     // test empty album

        // Testing findAlbumByYearNumberName
        Album anotherAlbumTest = dao.findAlbumByYearNumberName(1975, "ECM 1064/65", "The Köln Concert");
        assertEquals(album, anotherAlbumTest);
    }

    /**
     * Read MusicalInstrument
     */
    @Test
    public void successfulReadOfMusicalInstrument() {
        MusicalInstrument m1 = new MusicalInstrument("Piano");
        MusicalInstrument m2 = new MusicalInstrument("Violin");
        MusicalInstrument m3 = new MusicalInstrument("Guitar");

        // Insert to neo4j for read
        dao.createOrUpdate(m1);
        dao.createOrUpdate(m2);
        dao.createOrUpdate(m3);

        // Read by loadAll
        Collection<MusicalInstrument> instruments = dao.loadAll(MusicalInstrument.class);
        assertEquals(3, instruments.size());
        assertTrue(instruments.contains(m1));
        assertTrue(instruments.contains(m2));
        assertTrue(instruments.contains(m3));

        // Read by findMusicalInstrumentByName
        // Existed
        MusicalInstrument fetchedInstrument = dao.findMusicalInstrumentByName("Piano");
        assertEquals(m1, fetchedInstrument);
        // Not existed
        MusicalInstrument fetchedInstrument2 = dao.findMusicalInstrumentByName("Keyboard");
        assertEquals(null, fetchedInstrument2);
    }

    /**
     * Read of Musician
     * @throws MalformedURLException
     */
    @Test
    public void successfulReadOfMusician() throws MalformedURLException {
        Musician musician = new Musician("Keith Jarrett");        //create a musician object
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        dao.createOrUpdate(musician);

        Collection<Musician> musicians = dao.loadAll(Musician.class); //collection of the musicians
        assertEquals(1, musicians.size());             //only one musician in musicians collection
        Musician loadedMusician = musicians.iterator().next();

        assertEquals(musician, loadedMusician);    //read the Musician object
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());
        assertEquals(musician.getName(), loadedMusician.getName());     //get the name from the musician
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());     //get the url from the musician
    }

    /**
     * Update musician information
     * @throws MalformedURLException
     */
    @Test
    public void successfulUpdateMusician() throws MalformedURLException {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));
        musician.setRating(5);

        dao.createOrUpdate(musician);
        Collection<Musician> musicians = dao.loadAll(Musician.class); //collection of the musicians
        Musician loadedMusician = dao.load(Musician.class, musician.getId());

        assertEquals(1, musicians.size());             //only one musician in musicians collection

        //Musician loadedMusician = musicians.iterator().next();
        assertEquals(musician, loadedMusician);  //ensure two objects are the same before update
        loadedMusician.setName("Kelvin Jack");    //update the name of the musician
        assertEquals("Kelvin Jack",loadedMusician.getName()); //check if it is updated
        loadedMusician.setMusicianUrl(new URL("https://www.keithJack.org/"));   //Set the new URL
        assertEquals(new URL("https://www.keithJack.org/"), loadedMusician.getMusicianUrl()); //check if it is updated
        loadedMusician.setRating(3);   //Set the new rating
        assertEquals(3, loadedMusician.getRating()); //check if it is updated
    }


    /**
     * Update album
    * @throws MalformedURLException
     */
    @Test
    public void successfulUpdateAlbum() throws MalformedURLException {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");

        dao.createOrUpdate(album);

        Collection<Album> Albums = dao.loadAll(Album.class); //collection of the musicians


        Album loadedAlbum = Albums.iterator().next();
        assertEquals(album, loadedAlbum);  //ensure two objects are the same before update

        loadedAlbum.setAlbumName("The New Köln Concert");  //update the name of the album
        assertEquals("The New Köln Concert",loadedAlbum.getAlbumName());

        loadedAlbum.setRecordNumber("ECM 1064/66");   //update the recordNumber of the album
        assertEquals("ECM 1064/66",loadedAlbum.getRecordNumber());

        loadedAlbum.setReleaseYear(1976);  //update the release year of the album
        assertEquals(1976,loadedAlbum.getReleaseYear());


    }


    /**
     * Delete album
     *  @throws MalformedURLException
     */
    @Test
    public void successfulDeleteAlbum() throws MalformedURLException {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");

        dao.createOrUpdate(album);

        Collection<Album> Albums = dao.loadAll(Album.class); //collection of the musicians


        Album loadedAlbum = Albums.iterator().next();
        assertEquals(1, dao.loadAll(Album.class).size());  //the size of album is one
        assertEquals(album, loadedAlbum);  //ensure two objects are the same before update
//      loadedAlbum.delete();

        dao.delete(album);
        assertEquals(0, dao.loadAll(Album.class).size());  //the size of album is zero after delete the album
//        assertEquals(null, loadedAlbum.getAlbumName());
//        assertEquals(null, loadedAlbum.getAlbumURL());

    }


    /**
     * Delete a musician and all their albums
     * @throws MalformedURLException
     */
    @Test
    public void successfulDeleteMusicianInformation() throws MalformedURLException {
        //assertEquals(0, dao.loadAll(Musician.class).size());

        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));
        musician.setAlbums(Collections.singleton(new Album(1975, "ECM 1064/65", "The Köln Concert")));

        //dao.findMusicianByName("Keith Jarrett");
        dao.createOrUpdate(musician);
        Musician testMusician = dao.load(Musician.class, musician.getId());
        assertEquals(1,testMusician.getAlbums().size());
        assertEquals(1, dao.loadAll(Musician.class).size());

        musician.deleteAlbums();
        assertEquals(0,testMusician.getAlbums().size()); //delete the Album under the musician

        dao.delete(testMusician);
        assertEquals(0, dao.loadAll(Musician.class).size()); //delete the musician in dao
        
    }
    /**
     * Successful Find the musician by name
     * @throws MalformedURLException
     */
    @Test
    public void successfulFindMusicianByName() throws MalformedURLException {
        //assertEquals(0, dao.loadAll(Musician.class).size());

        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));
       // musician.setAlbums(Collections.singleton(new Album(1975, "ECM 1064/65", "The Köln Concert")));

        dao.createOrUpdate(musician);
       // Musician testMusician = dao.load(Musician.class, musician.getId());
        Musician musician2 = dao.findMusicianByName("Keith Jarrett");   //find the musician by name
        Musician musician3 = dao.findMusicianByName("Keith");   //find the not exist musician
        assertEquals(musician, musician2);
        assertEquals(null, musician3);
    }
}