package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Musician;
import org.junit.jupiter.api.BeforeEach;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO: perform integration testing of both ECMMiner and the DAO classes together.
 */
class ECMMinerIntegrationTest {

    private ECMMiner miner;

    @BeforeEach
    public void setUp() {
        // Impermanent embedded store
        Configuration configuration = new Configuration.Builder().build();

        // Disk-based embedded store
        // Configuration configuration = new Configuration.Builder().uri(new File(TEST_DB).toURI().toString()).build();

        // HTTP data store, need to install the Neo4j desktop app and create & run a database first.
//        Configuration configuration = new Configuration.Builder().uri("http://neo4j:password@localhost:7474").build();

        SessionFactory sessionFactory = new SessionFactory(configuration, Musician.class.getPackage().getName());
        Session session = sessionFactory.openSession();

        DAO dao = new Neo4jDAO(session);
        miner = new ECMMiner(dao);
    }

    //  TODO: Prepare sample data for testing.
    private void dataPreparation() {
        // Musician

        // MusicianInstrument

        // Album
    }
}