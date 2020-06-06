package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.Entity;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.*;

import java.util.*;

/**
 * Note that you can extend the Neo4jDAO class to make implementing this class easier.
 */
public class ECMMiner {

    private final DAO dao;

    public ECMMiner(DAO dao) {
        this.dao = dao;
    }

    private <T extends Entity> List<T> buildListAnswer(int k, Map<Integer, List<T>> countMap) {
        List<Integer> countList = Lists.newArrayList(countMap.keySet());
        countList.sort((i1, i2) -> i2 > i1 ? 1 : -1);

        List<T> answer = Lists.newArrayList();
        for (Integer i : countList.subList(0, k)) {
            List<T> toAdd = countMap.get(i);
            if ((answer.size() + toAdd.size()) <= k)
                answer.addAll(toAdd);
        }
        return answer;
    }

    /**
     * Returns the most prolific musician in terms of number of albums released.
     *
     * @Param k the number of musicians to be returned.
     * @Param startYear, endYear between the two years [startYear, endYear].
     * When startYear/endYear is negative, that means startYear/endYear is ignored.
     */
    public List<Musician> mostProlificMusicians(int k, int startYear, int endYear) {
        if (k <= 0)     //The input number of k can not less than or equal to zero
            throw new IllegalArgumentException("The input number of k can not less than or equal to zero");
        if (endYear != -1 && startYear > endYear)    //The end year should greater that start year
            throw new IllegalArgumentException("The end year should greater that start year");

        // Loading all the musicians
        Collection<Musician> musicians = dao.loadAll(Musician.class);

        // For case when total musicians is less than k
        if (musicians.size() < k)
            return Lists.newArrayList(musicians);

        // # of musician > k
        Map<Integer, List<Musician>> albumsCount = Maps.newHashMap();
        for (Musician musician : musicians) {
            int count = 0;
            for (Album album : musician.getAlbums()) {
                int releaseYear = album.getReleaseYear();
                boolean toInclude = (startYear == -1 || releaseYear >= startYear) && (endYear == -1 || releaseYear <= endYear);
                if (toInclude)
                    count += 1;
            }
            List<Musician> musicianList = albumsCount.getOrDefault(count, Lists.newArrayList());
            musicianList.add(musician);
            albumsCount.put(count, musicianList);
        }
        return this.buildListAnswer(k, albumsCount);
    }

    /**
     * Most talented musicians by the number of different musical instruments they play
     *
     * @Param k the number of musicians to be returned.
     */
    public List<Musician> mostTalentedMusicians(int k) {
        if (k <= 0)
            throw new IllegalArgumentException("k cannot be smaller than one");

        Collection<MusicianInstrument> musicianInstruments = dao.loadAll(MusicianInstrument.class);
        // Case where size of musicians < k
        if (musicianInstruments.size() < k) {
            List<Musician> musicians = Lists.newArrayList();
            for (MusicianInstrument musicianInstrument : musicianInstruments)
                musicians.add(musicianInstrument.getMusician());
            return musicians;
        }

        Map<Integer, List<Musician>> instrumentCounts = Maps.newHashMap();
        for (MusicianInstrument ins : musicianInstruments) {
            int count = ins.getMusicalInstruments().size();
            List<Musician> musicianList = instrumentCounts.getOrDefault(count, Lists.newArrayList());
            musicianList.add(ins.getMusician());
            instrumentCounts.put(count, musicianList);
        }

        return this.buildListAnswer(k, instrumentCounts);
    }

    /**
     * Musicians that collaborate the most widely, by the number of other musicians they work with on albums.
     *
     * @Param k the number of musicians to be returned.
     */
    public List<Musician> mostSocialMusicians(int k) {
        if (k <= 0)
            throw new IllegalArgumentException("The input number of k can not less than or equal to zero");

        Collection<Album> albums = dao.loadAll(Album.class);

        // Count for each musician by iterating featuredMusician within each album
        Map<Musician, Integer> musicianCount = Maps.newHashMap();
        for (Album album : albums) {
            for (Musician musician : album.getFeaturedMusicians()) {
                int count = musicianCount.getOrDefault(musician, 0);
                count += 1;
                musicianCount.put(musician, count);
            }
        }

        // Case where # of musicians <= k
        if (musicianCount.size() <= k) {
            return Lists.newArrayList(musicianCount.keySet());
        }

        // # of musicians > k
        Map<Integer, List<Musician>> participationCount = Maps.newHashMap();
        for (Map.Entry<Musician, Integer> entry : musicianCount.entrySet()) {
            List<Musician> musicianList = participationCount.getOrDefault(entry.getValue(), Lists.newArrayList());
            musicianList.add(entry.getKey());
            participationCount.put(entry.getValue(), musicianList);
        }
        return this.buildListAnswer(k, participationCount);
    }

    /**
     * Busiest year in terms of number of albums released.
     *
     * @Param k the number of years to be returned.
     */

    public List<Integer> busiestYears(int k) {
        Collection<Album> albums = dao.loadAll(Album.class);
        ArrayList<Album> album = new ArrayList<>(albums);
        ArrayList<Integer> listOfYears = new ArrayList<>();
        ArrayList<Integer> yearCounter = new ArrayList<>();
        for(int i = 0; i < album.size(); i++){
            if(listOfYears.contains(album.get(i).getReleaseYear())){
                int index = listOfYears.indexOf(album.get(i).getReleaseYear());
                yearCounter.set(index, yearCounter.get(index) + 1);
            }else{
                listOfYears.add(album.get(i).getReleaseYear());
                yearCounter.add(1);
            }
        }
        if (listOfYears.size() <= k)
            return listOfYears;

        ArrayList<Integer> answer = new ArrayList<>();
        for(int i = 0; i < k; i++){
            int highestNumber = 0;
            int idx = 0;
            for(int j = 0; j<listOfYears.size();j++){
                if(yearCounter.get(j) > highestNumber && !answer.contains(listOfYears.get(j))){
                    highestNumber = yearCounter.get(j);
                    idx = j;
                }
            }
            answer.add(listOfYears.get(idx));
        }
        return answer;
    }
    /**
     * Similarity is based on genre and musician of album. If artist is left empty it is ignored
     *
     * @Param k the number of albums to be returned.
     * @Param genre is the genre of music to be returned (cannot be blank)
     * @Param musician is the musician to be returned, if musician is blank it is ignored (search by genre only)
     */

    public List<Album> mostSimilarAlbums(int k, String genre, String featuredMusician) {
        if (k <= 0 || genre == null || genre.isEmpty())
            return Lists.newArrayList();

        Collection<Album> albums = dao.loadAll(Album.class);

        List<Album> filteredAlbums = Lists.newArrayList();
        for (Album album : albums) {
            boolean matchGenre = album.getGenre().equals(genre);
            boolean featured =
                    featuredMusician == null ||
                    featuredMusician.isEmpty() ||
                    album.getFeaturedMusicians().contains(new Musician(featuredMusician));
            if (matchGenre && featured)
                filteredAlbums.add(album);
        }

        // Case where size of all albums <= k
        if (filteredAlbums.size() <= k)
            return Lists.newArrayList(filteredAlbums);

        // size of all albums > k
        List<Album> answer = Lists.newArrayList();
        Iterator<Album> it = filteredAlbums.iterator();
        for (int i = 0; i < k; i++) {
            Album toAdd = it.next();
            if (toAdd.getGenre().equals(genre))
                answer.add(toAdd);
        }
        return answer;
    }

    /**
     * Best K selling albums of all time
     *
     * @Param k is the amount of albums to be returned
     */
    public List<Album> bestKSellingAlbums(int k) {
        Collection<Album> albums = dao.loadAll(Album.class);
        ArrayList<Album> answer = new ArrayList<>();
        for(int i = 0; i < k; i++) {
            int highestSales = 0;
            for (Album album : albums) {
                if (album.getSales() > highestSales  && !answer.contains(album)) {
                    highestSales = album.getSales();
                }
            }
            for(Album album : albums){
                if(album.getSales() == highestSales){
                    answer.add(album);
                }
            }
        }
        return answer;
    }

    /**
     * Top k rated albums of all time (released by any musician)
     *
     * @Param k is the amount of albums to be returned
     */
    public List<Album> topKRatedAlbums(int k) {
        Collection<Album> albums = dao.loadAll(Album.class);
        ArrayList<Album> answer = new ArrayList<>();
        for(int i = 0; i < k; i++){
            double highestRating = 0;
            for (Album album : albums) {
                if (album.getRating() > highestRating && !answer.contains(album)) {
                    highestRating = album.getRating();
                }
            }
            for (Album album : albums) {
                if (album.getRating() == highestRating) {
                    answer.add(album);
                }
            }
        }
        return answer;  
    }

    /**
     * Top k rated musicians of all time
     *
     * @Param k is the amount of musicians to be returned
     */
    public List<Musician> topKRatedMusicians(int k) {
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        ArrayList<Musician> answer = new ArrayList<>();
        for(int i = 0; i < k; i++){
            int highestRating = 0;
            for (Musician musician : musicians) {
                if(musician.getRating() > highestRating && !answer.contains(musician)) {
                    highestRating = musician.getRating();
                }
            }
            for(Musician musician : musicians) {
                if (musician.getRating() == highestRating) {
                    answer.add(musician);
                }
            }
        }

        return answer;
    }

    /**
     * This method returns an individual musicians top k rated albums of all time
     *
     * @Param k is the amount of albums to be returned
     * @Param featuredMusician is the name of the musician participated in the album.
     */
    public List<Album> musiciansHighestRatedAlbums(String featuredMusician, int k) {
        if (k <= 0 || featuredMusician == null || featuredMusician.isEmpty())
            return Lists.newArrayList();

        Collection<Album> albums = dao.loadAll(Album.class);
        List<Album> filteredAlbums = Lists.newArrayList();
        for (Album album : albums) {
            if (album.getFeaturedMusicians().contains(new Musician(featuredMusician)))
                filteredAlbums.add(album);
        }
        filteredAlbums.sort((a1, a2) -> {
            if (a2.getRating() > a1.getRating())
                return 1;
            else
                return -1;
        });

        if (filteredAlbums.size() <= k)
            return filteredAlbums;
        return filteredAlbums.subList(0, k);
    }
}
