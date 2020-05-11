package allaboutecm.dataaccess.neo4j;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.Musician;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.support.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

//        dao.delete(musician);
//        assertEquals(0, dao.loadAll(Musician.class).size());
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
     */
    @Test
    public void successfulReadOfAlbumOfMusician() throws MalformedURLException {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");
        musician.setAlbums(Sets.newHashSet(album));

        dao.createOrUpdate(album);
        dao.createOrUpdate(musician);

        Collection<Musician> musicians = dao.loadAll(Musician.class); //collection of the musicians
        assertEquals(1, musicians.size());             //only one musician in musicians collection
        Musician loadedMusician = musicians.iterator().next();

        assertEquals(musician, loadedMusician);
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());
        assertEquals(musician.getAlbums(), loadedMusician.getAlbums());     //get the albums from the musician
        assertEquals(album.getReleaseYear(), loadedMusician.getAlbums().iterator().next().getReleaseYear());
        assertEquals(album.getAlbumName(), loadedMusician.getAlbums().iterator().next().getAlbumName());
        assertEquals(album.getRecordNumber(), loadedMusician.getAlbums().iterator().next().getRecordNumber());
        assertEquals(album.getFeaturedMusicians(),loadedMusician.getAlbums().iterator().next().getFeaturedMusicians());
        assertEquals(album.getAlbumURL(),loadedMusician.getAlbums().iterator().next().getAlbumURL());
        assertEquals(album.getInstruments(),loadedMusician.getAlbums().iterator().next().getInstruments());
        assertEquals(album.getTracks(),loadedMusician.getAlbums().iterator().next().getTracks());

    }


    /**
     * Read of Musician
     */
    @Test
    public void successfulReadOfMusician() throws MalformedURLException {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        dao.createOrUpdate(musician);

        Collection<Musician> musicians = dao.loadAll(Musician.class); //collection of the musicians
        assertEquals(1, musicians.size());             //only one musician in musicians collection
        Musician loadedMusician = musicians.iterator().next();

        assertEquals(musician, loadedMusician);
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());
        assertEquals(musician.getName(), loadedMusician.getName());     //get the albums from the musician
        assertEquals(musician.getMusicianUrl(), loadedMusician.getMusicianUrl());     //get the albums from the musician
    }

    /**
     * Update musician
     */
    @Test
    public void successfulUpdateMusician() throws MalformedURLException {
        Musician musician = new Musician("Keith Jarrett");
        musician.setMusicianUrl(new URL("https://www.keithjarrett.org/"));

        dao.createOrUpdate(musician);

        Collection<Musician> musicians = dao.loadAll(Musician.class); //collection of the musicians
        assertEquals(1, musicians.size());             //only one musician in musicians collection
        Musician loadedMusician = musicians.iterator().next();
        assertEquals(musician, loadedMusician);  //ensure two objects are the same before update
        loadedMusician.setName("Kelvin Jack");
        assertEquals("Kelvin Jack",loadedMusician.getName());
        loadedMusician.setMusicianUrl(new URL("https://www.keithJack.org/"));   //Set the new URL
        assertEquals(new URL("https://www.keithJack.org/"), loadedMusician.getMusicianUrl());
    }


    /**
     * Update album
     */
    @Test
    public void successfulUpdateAlbum() throws MalformedURLException {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");

        dao.createOrUpdate(album);

        Collection<Album> Albums = dao.loadAll(Album.class); //collection of the musicians


        Album loadedAlbum = Albums.iterator().next();
        assertEquals(album, loadedAlbum);  //ensure two objects are the same before update

        loadedAlbum.setAlbumName("The New Köln Concert");
        assertEquals("The New Köln Concert",loadedAlbum.getAlbumName());

        loadedAlbum.setRecordNumber("ECM 1064/66");
        assertEquals("ECM 1064/66",loadedAlbum.getRecordNumber());

        loadedAlbum.setReleaseYear(1976);
        assertEquals(1976,loadedAlbum.getReleaseYear());


    }


    /**
     * Delete album
     */
    @Test
    public void successfulDeleteAlbum() throws MalformedURLException {
        Album album = new Album(1975, "ECM 1064/65", "The Köln Concert");

        dao.createOrUpdate(album);

        Collection<Album> Albums = dao.loadAll(Album.class); //collection of the musicians


        Album loadedAlbum = Albums.iterator().next();

        assertEquals(album, loadedAlbum);  //ensure two objects are the same before update
        loadedAlbum.delete();

        assertEquals(null, loadedAlbum.getAlbumName());
        assertEquals(null, loadedAlbum.getAlbumURL());



    }


    /**
     * Delete a musician and all their albums
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

        musician.setAlbums(null);
        assertEquals(null ,testMusician.getAlbums()); //delete the Album under the musician

        dao.delete(testMusician);
        assertEquals(0, dao.loadAll(Musician.class).size()); //delete the musician in dao


    }
}