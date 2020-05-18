package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.dataaccess.neo4j.Neo4jDAO;
import allaboutecm.model.Album;
import allaboutecm.model.Musician;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Test
    @DisplayName("Most Prolific Musician should return one given one")
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
    @DisplayName("Busiest year list should return empty list if k=0")
    public void shouldReturnEmptyListOfBusiestYearsWhenKEqualsZero(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
        List<Integer> albumTest = ecmMiner.busiestYears(0);
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Busiest year list should return empty list when k is negative")
    public void shouldReturnEmptyListOfBusiestYearsWhenKIsNegative(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
        List<Integer> albumTest = ecmMiner.busiestYears(-1);
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Busiest year list should not contain null values")
    public void busiestYearListShouldNotContainNullValues(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
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
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album,album1,album2));
        List<Integer> albumTest = ecmMiner.busiestYears(1);
        assertEquals(1,albumTest.size());
        assertEquals(1973, (int) albumTest.get(0));
    }

    @Test
    @DisplayName("Busiest year list should only return list of amount of years when k is greater than amount of years")
    public void shouldOnlyReturnBusiestYearsWhenKGreaterThanAmountOfYears(){
        Album album = new Album(1979,"2","Rolling");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
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
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album,album1,album2,album3,album4,album5));
        List<Integer> albumTest = ecmMiner.busiestYears(3);
        assertEquals(3,albumTest.size());
        assertTrue(albumTest.get(0) == 1973);
        assertTrue(albumTest.get(1) == 1995);
        assertTrue(albumTest.get(2) == 1979);
    }

    @Test
    @DisplayName("Best selling albums should return empty list if k=0")
    public void shouldReturnEmptyListOfBestSellingAlbumsWhenKEqualsZero(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setSales(100000);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
        List<Album> albumTest = ecmMiner.bestKSellingAlbums(0);
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Best selling albums should return empty list if k is negative")
    public void shouldReturnEmptyListOfBestSellingAlbumsWhenKNegative(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setSales(100000);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
        List<Album> albumTest = ecmMiner.bestKSellingAlbums(-1);
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Best selling albums should not return null values")
    public void shouldNotHaveNullInBestSellingAlbumList(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setSales(100000);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
        List<Album> albumTest = ecmMiner.bestKSellingAlbums(2);
        assertFalse(albumTest.contains(null));
    }

    @Test
    @DisplayName("Best selling albums should return list of size of objects when k is greater than number of objects")
    public void shouldReturnListWithSameSizeAsObjectsWhenKIsGreaterThanNumberOfObjects(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        album.setSales(100000);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
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
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album, album1));
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
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album, album1, album2));
        List<Album> albumTest = ecmMiner.bestKSellingAlbums(3);
        assertTrue(albumTest.get(0).getAlbumName().equals("Animals"));
        assertTrue(albumTest.get(1).getAlbumName().equals("The Dark Side of the Moon"));
        assertTrue(albumTest.get(2).getAlbumName().equals("Division Bell"));
    }

    @Test
    @DisplayName("Highest Rated album should return empty list if k=0")
    public void shouldReturnEmptyListOfHighestRatedAlbumWhenKEqualsZero(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
        List<Album> albumTest = ecmMiner.topKRatedAlbums(0);
        assertEquals(0,albumTest.size());
        assertTrue(albumTest.isEmpty());
    }

    @Test
    @DisplayName("Highest Rated album should return list with no null entries")
    public void shouldNotHaveNullInHighestRatedAlbumList(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
        List<Album> albumTest = ecmMiner.topKRatedAlbums(2);
        assertFalse(albumTest.contains(null));
    }

    @Test
    @DisplayName("Highest Rated album should return empty list if k is negative")
    public void shouldReturnEmptyListOfHighestRatedAlbumsWhenKIsNegative(){
        Album album = new Album(1973,"1","The Dark Side of the Moon");
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
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
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album, album1));
        List<Album> albumTest = ecmMiner.topKRatedAlbums(1);
        assertEquals(1, albumTest.size());
        assertTrue(albumTest.contains(album1));
    }

    @Test
    @DisplayName("Highest rated album should return list that is size of musicians if k > albums")
    public void shouldReturnListSizeEqualToNumberOfAlbumsIfKGreaterThanAlbums(){
        Album album = new Album(2015,"1","c");
        album.setRating(1);
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
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
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album, album1, album2));
        List<Album> albumTest = ecmMiner.topKRatedAlbums(3);
        assertTrue(albumTest.get(0).getAlbumName().equals("a"));
        assertTrue(albumTest.get(1).getAlbumName().equals("c"));
        assertTrue(albumTest.get(2).getAlbumName().equals("b"));
    }

    @Test
    @DisplayName("Highest Rated musician should return empty list if k=0")
        public void shouldReturnEmptyListOfHighestRatedMusicianWhenKEqualsZero(){
        Musician musician = new Musician("Roger Waters");
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));
        List<Musician> musicianTest = ecmMiner.topKRatedMusicians(0);
        assertEquals(0,musicianTest.size());
        assertTrue(musicianTest.isEmpty());
        }

    @Test
    @DisplayName("Highest Rated Musician should return list with no null entries")
    public void shouldNotHaveNullInHighestRatedMusicianList(){
        Musician musician = new Musician("Adele");
        musician.setRating(1);
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));
        List<Musician > musicianTest = ecmMiner.topKRatedMusicians(2);
        assertFalse(musicianTest.contains(null));
    }

    @Test
    @DisplayName("Highest rated musician should return empty list if k is negative")
    public void shouldOnlyReturnHighestRatedMusicians(){
        Musician musician1 = new Musician("Lady Gaga");
        musician1.setRating(4);
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));
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
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1, musician));
        List<Musician > musicianTest = ecmMiner.topKRatedMusicians(1);
        assertEquals(1,musicianTest.size());
        assertTrue(musicianTest.contains(musician1));
    }

    @Test
    @DisplayName("Highest rated musician should return list that is size of musicians if k > musicians")
    public void shouldReturnListSizeEqualToNumberOfMusiciansIfKGreaterThanMusicians(){
        Musician musician1 = new Musician("Matt Heafy");
        musician1.setRating(1);
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1));
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
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician1, musician2, musician3));
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
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
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
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
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
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album));
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
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album, album1));
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
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album, album1));
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
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album, album1, album2));
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
        when(dao.loadAll(Musician.class)).thenReturn(Sets.newHashSet(musician, musician1));
        when(dao.loadAll(Album.class)).thenReturn(Sets.newHashSet(album, album1));
        List<Album> albumTest = ecmMiner.musiciansHighestRatedAlbums("Fergal Lawler", 1);
        assertEquals(1, albumTest.size());
        assertTrue(albumTest.get(0).getAlbumName().equals("b"));
    }
    @ParameterizedTest
    @ValueSource(ints = {0,-1,-2,-3})
    @DisplayName("When mining the most prolific musicians the output can no be zero or less that zero")
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

    @ParameterizedTest
    @DisplayName("mostSocialMusicians function can not have the k less that or equal to zero")
    @ValueSource(ints = {0,-1})
    public void ShouldThrowExceptionIfMostSocialMusicianHaveTheNumberLessThanOrEqualToZero(int argument)
    {
         assertThrows(IllegalArgumentException.class, ()->ecmMiner.mostSocialMusicians(argument));
    }
}