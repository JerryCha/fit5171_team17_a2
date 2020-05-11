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

    public List<Integer> busiestYears(int k) {
        return Lists.newArrayList();
    }

    /**
     * Most similar albums to a give album. The similarity can be defined in a variety of ways.
     * For example, it can be defined over the musicians in albums, the similarity between names
     * of the albums & tracks, etc.
     *
     * @Param k the number of albums to be returned.
     * @Param album
     */

    public List<Album> mostSimilarAlbums(int k, Album album) {
        return Lists.newArrayList();
    }

    /**
     * TODO: The best-selling k albums in the history
     */
    public List<Album> bestKSellingAlbums(int k) {
        return Lists.newArrayList();
    }

    /**
     * TODO: The k highest rated albums
     */
    public List<Album> topKRatedAlbums(int k) {
        return Lists.newArrayList();
    }

    /**
     * TODO: The k highest rated musicians
     */
    public List<Album> topKRatedMusicians(int k) {
        return Lists.newArrayList();
    }
}
