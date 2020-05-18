package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.Musician;
import allaboutecm.model.MusicianInstrument;
import com.google.common.collect.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * TODO: implement and test the methods in this class.
 * Note that you can extend the Neo4jDAO class to make implementing this class easier.
 */
public class ECMMiner {
    private static Logger logger = LoggerFactory.getLogger(ECMMiner.class);

    private final DAO dao;

    public ECMMiner(DAO dao) {
        this.dao = dao;
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
        if (startYear > endYear)    //The end year should greater that start year
            throw new IllegalArgumentException("The end year should greater that start year");
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        Map<String, Musician> nameMap = Maps.newHashMap();
        for (Musician m : musicians) {
            nameMap.put(m.getName(), m);
        }

        ListMultimap<String, Album> multimap = MultimapBuilder.treeKeys().arrayListValues().build();
        ListMultimap<Integer, Musician> countMap = MultimapBuilder.treeKeys().arrayListValues().build();

        for (Musician musician : musicians) {
            Set<Album> albums = musician.getAlbums();
            for (Album album : albums) {
                boolean toInclude =
                        !((startYear > 0 && album.getReleaseYear() < startYear) ||
                                (endYear > 0 && album.getReleaseYear() > endYear));

                if (toInclude) {
                    multimap.put(musician.getName(), album);
                }
            }
        }

        Map<String, Collection<Album>> albumMultimap = multimap.asMap();
        for (String name : albumMultimap.keySet()) {
            Collection<Album> albums = albumMultimap.get(name);
            int size = albums.size();
            countMap.put(size, nameMap.get(name));
        }

        List<Musician> result = Lists.newArrayList();
        List<Integer> sortedKeys = Lists.newArrayList(countMap.keySet());
        sortedKeys.sort(Ordering.natural().reverse());
        for (Integer count : sortedKeys) {
            List<Musician> list = countMap.get(count);
            if (list.size() >= k) {
                break;
            }
            if (result.size() + list.size() >= k) {
                int newAddition = k - result.size();
                for (int i = 0; i < newAddition; i++) {
                    result.add(list.get(i));
                }
            } else {
                result.addAll(list);
            }
        }

        return result;
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

        Map<Musician, Integer> instrumentCounts = Maps.newHashMap();
        for (MusicianInstrument instrument : musicianInstruments) {
            Musician musician = instrument.getMusician();
            if (instrumentCounts.get(musician) != null) {
                int currentSize = instrumentCounts.get(musician);
                instrumentCounts.put(musician, currentSize + instrument.getMusicalInstruments().size());
            } else {
                instrumentCounts.put(musician, instrument.getMusicalInstruments().size());
            }
        }

        List<Integer> countValueList = Lists.newArrayList();
        countValueList.addAll(instrumentCounts.values());
        countValueList.sort((i1, i2) -> { if (i2-i1 > 0) return 1; return -1; });
        Set<Integer> topKValues = Sets.newHashSet();
        int idx = 0;
        while (topKValues.size() < k) {
            int val = countValueList.get(idx);
            if (!topKValues.contains(val))
                topKValues.add(val);
            idx++;
        }

        Iterator<Map.Entry<Musician, Integer>> it = instrumentCounts.entrySet().iterator();
        List<Musician> answer = Lists.newArrayList();
        while (it.hasNext()) {
            Map.Entry<Musician, Integer> entry = it.next();
            if (topKValues.contains(entry.getValue()))
                answer.add(entry.getKey());
        }

        return answer;
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
        ArrayList<Musician> socialMusician = new ArrayList<>();
        ArrayList<Integer> counterArray = new ArrayList<>();
        for(Album album : albums){
            if (album.getFeaturedMusicians().size() > 1){
                for (Musician musician: album.getFeaturedMusicians()){
                    if (!socialMusician.contains(musician)) {
                        socialMusician.add(musician);
                    }
                    int index = socialMusician.indexOf(musician);
                    if(counterArray.get(index) == null){
                        counterArray.add(index,0);
                    }
                    counterArray.add(index,counterArray.get(index) + 1);
                }
            }
        }
        ArrayList<Musician> answer = new ArrayList<>();
        int iterator = 0;
        do {
            int highest = 0;
            for(int musNumber : counterArray) {
                if (musNumber > highest) {
                    highest = musNumber;
                }
            }
            for(int counterValue: counterArray) {
                if(counterValue == highest) {
                    answer.add(socialMusician.get(counterValue));
                    iterator++;
                }
            }
        }
        while(k > iterator);
        return answer;
    }

    /**
     * Busiest year in terms of number of albums released.
     *
     * @Param k the number of years to be returned.
     */

    /*public List<Integer> busiestYears(int k) {
        Collection<Album> albums = dao.loadAll(Album.class);
        ArrayList<Integer> listOfYears = new ArrayList<>();
        ArrayList<Integer> yearCounter = new ArrayList<>();
        for(Album album: albums){
            if(listOfYears.contains(album.getReleaseYear())){
                int index = listOfYears.indexOf(album.getReleaseYear());
                yearCounter.add(index, yearCounter.get(index) + 1);
            }else{
                listOfYears.add(album.getReleaseYear());
                yearCounter.add(1);
            }
        }
        ArrayList<Integer> answer = new ArrayList<>();
        for(int i =0; i < k; i++){
            int highestCount = 0;
            for(Integer yearCount : yearCounter){
                if(yearCount > highestCount && !answer.contains(listOfYears.indexOf(yearCounter))){
                    highestCount = yearCount;
                }
            }
            for(Integer years : listOfYears){
                if(listOfYears.indexOf(years) == yearCounter.indexOf(highestCount)){
                    answer.add(years);
                }
            }
        }
        return answer;
    }*/
    public List<Integer> busiestYears(int k) {
        Collection<Album> albums = dao.loadAll(Album.class);
        ArrayList<Album> album = new ArrayList<>(albums);
        ArrayList<Integer> listOfYears = new ArrayList<>();
        ArrayList<Integer> yearCounter = new ArrayList<>();
        for(int i = 0; i < album.size(); i++){
            if(listOfYears.contains(album.get(i).getReleaseYear())){
                int index = listOfYears.indexOf(album.get(i).getReleaseYear());
                yearCounter.add(index, yearCounter.get(index) + 1);
            }else{
                listOfYears.add(album.get(i).getReleaseYear());
                yearCounter.add(1);
            }
        }
        ArrayList<Integer> answer = new ArrayList<>();
        for(int i = 0; i < k; i++){
            int highestNumber = 0;
            for(int j = 0; j<listOfYears.size();j++){
                if(yearCounter.get(j) > highestNumber && !answer.contains(listOfYears.get(j))){
                    highestNumber = yearCounter.get(i);
                }
            }
            for(int d = 0; d <yearCounter.size();d++){
                if(yearCounter.get(d).equals(highestNumber)){
                    answer.add(listOfYears.get(d));
                }
            }
        }
        return answer;
    }
    /**
     * Most similar albums to a give album. The similarity can be defined in a variety of ways.
     * For example, it can be defined over the musicians in albums, the similarity between names
     * of the albums & tracks, etc.
     *
     * Similarity is based on genre and musician of album. If artist is left empty it is ignored
     *
     * @Param k the number of albums to be returned.
     * @Param album
     */

    public List<Album> mostSimilarAlbums(int k, String genre, String musician) {
        Collection<Album> albums = dao.loadAll(Album.class);
        ArrayList<Album> answer = new ArrayList<>();
        if(musician.length() == 0) {
            while (k > 0) {
                for (Album album : albums) {
                    if (album.getGenre().equals(genre)) {
                        answer.add(album);
                        k--;
                    }
                }
            }
        }else{
            while(k > 0){
                for(Album album : albums){
                    if(album.getGenre().equals(genre) && album.getFeaturedMusicians().contains(musician)){
                        answer.add(album);
                        k--;
                    }
                }
            }
        }
        return answer;
    }

    /**
     * TODO: The best-selling k albums in the history
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
     * TODO: The k highest rated albums
     * Testing:Done
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
     * TODO: The k highest rated musicians
     * Testing:Done
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
     * TODO: A musicians highest rated album (takes in musician name and number of albums to be returned(k))
     * Testing: DONE
     */
    public List<Album> musiciansHighestRatedAlbums(String musicianName, int k){
        Collection<Album> albums = dao.loadAll(Album.class);
        ArrayList<Album> musiciansAlbums = new ArrayList<>();
        ArrayList<Album> answer = new ArrayList<>();

        for(Album album: albums){
            for(int i = 0; i < album.getFeaturedMusicians().size(); i++){
            if(album.getFeaturedMusicians().get(i).getName().equals(musicianName)){
                    musiciansAlbums.add(album);
                }
            }
        }
        for(int i = 0; i < k; i++){
            double highestRating = 0;
            for (Album musAlbum : musiciansAlbums) {
                if (musAlbum.getRating() > highestRating && !answer.contains(musAlbum)) {
                    highestRating = musAlbum.getRating();
                }
            }
            for (Album musAlbum : musiciansAlbums) {
                if (musAlbum.getRating() == highestRating) {
                    answer.add(musAlbum);
                }
            }
        }
        return answer;
    }
}
