package allaboutecm.mining;

import allaboutecm.dataaccess.DAO;
import allaboutecm.model.Album;
import allaboutecm.model.Musician;
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
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        Map<String, Musician> nameMap = Maps.newHashMap();
        for (Musician m : musicians) {
            nameMap.put(m.getName(), m);
        }

        ListMultimap<String, Album> multimap = MultimapBuilder.treeKeys().arrayListValues().build();
        ListMultimap<Integer, Musician> countMap = MultimapBuilder.treeKeys().arrayListValues().build();

        // Categorize albums according to musician
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

        // Count the number of albums per musician
        Map<String, Collection<Album>> albumMultimap = multimap.asMap();
        for (String name : albumMultimap.keySet()) {
            Collection<Album> albums = albumMultimap.get(name);
            int size = albums.size();
            countMap.put(size, nameMap.get(name));
        }

        // Sorting their number of albums
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

        return Lists.newArrayList();
    }

    /**
     * Musicians that collaborate the most widely, by the number of other musicians they work with on albums.
     *
     * @Param k the number of musicians to be returned.
     */

    public List<Musician> mostSocialMusicians(int k) {
        Collection<Album> albums = dao.loadAll(Album.class);
        ArrayList<Musician> socialMusician = new ArrayList<>();
        ArrayList<Integer> counterArray = new ArrayList<>();
        for(Album album : albums){
            if (album.getFeaturedMusicians().size() >= 1){
                for (Musician musician: album.getFeaturedMusicians()){
                    if (!socialMusician.contains(musician)) {
                        socialMusician.add(musician);
                    }
                    int index = socialMusician.indexOf(musician);
                    if(index > counterArray.size()-1){
                        counterArray.add(index,0);
                    }
                    counterArray.set(index,counterArray.get(index) + 1);
                }
            }
        }
        ArrayList<Musician> answer = new ArrayList<>();
        // TODO: Fix top K algorithm
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
                    answer.add(socialMusician.get(counterArray.indexOf(counterValue)));
                    iterator++;
                }
            }
            counterArray.remove(new Integer(highest));
        }
        while(k > iterator && counterArray.size() != 0);
        return answer;
    }

    /**
     * Busiest year in terms of number of albums released.
     *
     * @Param k the number of years to be returned.
     */

    public List<Integer> busiestYears(int k) {
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
        while(k > 0){
            int highestCount = 0;
            for(Integer yearCount : yearCounter){
                if(yearCount > highestCount){
                    highestCount = yearCount;
                }
            }
            for(Integer years : listOfYears){
                if(listOfYears.indexOf(years) == yearCounter.indexOf(highestCount)){
                    answer.add(years);
                    k -=1;
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
        ArrayList<Album> clonedAlbum = new ArrayList<>(albums);
        ArrayList<Album> answer = new ArrayList<>();
        while(k > 0) {
            int highestSales = 0;
            for (Album album : clonedAlbum) {
                if (album.getSales() > highestSales) {
                    highestSales = album.getSales();
                }
            }
            for(Album album : clonedAlbum){
                if(highestSales == album.getSales()){
                    answer.add(album);
                    clonedAlbum.remove(album);
                    k--;
                }
            }
        }
        return answer;
    }

    /**
     * TODO: The k highest rated albums
     */
    public List<Album> topKRatedAlbums(int k) {
        Collection<Album> albums = dao.loadAll(Album.class);
        ArrayList<Album> clonedAlbum = new ArrayList<>(albums);
        ArrayList<Album> answer = new ArrayList<>();
        while(k > 0) {
            double highestRating = 0;
            for (Album album : clonedAlbum) {
                if (album.getRating() > highestRating) {
                    highestRating = album.getRating();
                }
            }
            for (Album album : clonedAlbum) {
                if (album.getRating() == highestRating) {
                    answer.add(album);
                    clonedAlbum.remove(album);
                    k--;
                }
            }
        }
        return answer;  
    }

    /**
     * TODO: The k highest rated musicians
     */
    public List<Musician> topKRatedMusicians(int k) {
        Collection<Musician> musicians = dao.loadAll(Musician.class);
        ArrayList<Musician> clonedMusician = new ArrayList<>(musicians);
        ArrayList<Musician> answer = new ArrayList<>();
        int iterator = 0;
        do {
            int highestRating = 0;
            for (Musician musician : clonedMusician) {
                if(musician.getRating() > highestRating) {
                    highestRating = musician.getRating();
                }
            }
            for(Musician musician : clonedMusician){
                if(musician.getRating() == highestRating){
                    answer.add(musician);
                    clonedMusician.remove(musician);
                    iterator++;
                }
            }
        }
        while(k > iterator);
        return answer;
    }

    /**
     * TODO: A musicians highest rated album (takes in musician name and number of albums to be returned(k))
     */
    public List<Album> musiciansHighestRatedAlbums(String musicianName, int k){
        Collection<Album> albums = dao.loadAll(Album.class);
        ArrayList<Album> musiciansAlbums = new ArrayList<>();
        ArrayList<Album> answer = new ArrayList<>();
        for(Album album : albums){
            if(album.getFeaturedMusicians().contains(musicianName)){
                musiciansAlbums.add(album);
            }
        }
        while(k>0) {
            double highestRating = 0;
            for (Album musAlbum : musiciansAlbums) {
                if (musAlbum.getRating() > highestRating) {
                    highestRating = musAlbum.getRating();
                }
            }
            for (Album musAlbum : musiciansAlbums) {
                if (musAlbum.getRating() == highestRating) {
                    answer.add(musAlbum);
                    musiciansAlbums.remove(musAlbum);
                    k--;
                }
            }
        }
        return answer;
    }
}
