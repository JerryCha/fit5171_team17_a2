package allaboutecm.model;

import allaboutecm.dataaccess.neo4j.URLConverter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Represents an album released by ECM records.
 *
 * See {@https://www.ecmrecords.com/catalogue/143038750696/the-koln-concert-keith-jarrett}
 */
@NodeEntity
public class Album extends Entity {

    @Property(name="releaseYear")
    private int releaseYear;

    @Property(name="recordNumber")
    private String recordNumber;

    @Property(name="albumName")
    private String albumName;

    //  extension of feature
    @Property(name = "sales")
    private int sales;

    @Property(name = "rating")
    private int rating;

    @Property(name = "genre")
    private String genre;

    /**
     * CHANGE: instead of a set, now featuredMusicians is a list,
     * to better represent the order in which musicians are featured in an album.
     */
    @Relationship(type="featuredMusicians")
    private List<Musician> featuredMusicians;

    @Relationship(type="instruments")
    private Set<MusicianInstrument> instruments;

    @Convert(URLConverter.class)
    @Property(name="albumURL")
    private URL albumURL;

    @Property(name="tracks")
    private List<String> tracks;

    //
    @Property(name="additionalInformation")
    private String additionalInformation;

    private Album() {
    }

    public Album(int releaseYear, String recordNumber, String albumName) {
        notNull(recordNumber);
        notNull(albumName);

        notBlank(recordNumber);
        notBlank(albumName);

        this.releaseYear = releaseYear;
        this.recordNumber = recordNumber;
        this.albumName = albumName;

        this.albumURL = null;

        this.featuredMusicians = Lists.newArrayList();
        this.instruments = Sets.newHashSet();
        this.tracks = Lists.newArrayList();
        this.additionalInformation = null;
    }

    public String getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(String recordNumber) {
        if (recordNumber == null)
            throw new IllegalArgumentException("record number cannot be null");
        recordNumber = recordNumber.trim();
        if (recordNumber.length() < 8 || !recordNumber.substring(0, 4).equals("ECM "))
            throw new IllegalArgumentException("record number should be in format of \"ECM XXXX\" where XXXX is the number of album");

        this.recordNumber = recordNumber;
    }

    public List<Musician> getFeaturedMusicians() {
        return featuredMusicians;
    }

    public void setFeaturedMusicians(List<Musician> featuredMusicians) {
        if (featuredMusicians == null)
            throw new IllegalArgumentException("featuredMusicians cannot be null");
        this.featuredMusicians = featuredMusicians;
    }

    public Set<MusicianInstrument> getInstruments() {
        return instruments;
    }

    public void setInstruments(Set<MusicianInstrument> instruments) {
        if (instruments == null)
            throw new IllegalArgumentException("instruments cannot be null");
        this.instruments = instruments;
    }

    public URL getAlbumURL() {
        return albumURL;
    }

    public void setAlbumURL(URL albumURL) {
        this.albumURL = albumURL;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public void setTracks(List<String> tracks) {
        if (tracks == null)
            throw new IllegalArgumentException("tracks cannot be null");
        this.tracks = tracks;
    }

    public void addATrack(String aTrack){
        notNull(aTrack);
        notBlank(aTrack);
        this.tracks.add(aTrack);
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        if (releaseYear > Calendar.getInstance().get(Calendar.YEAR))
            throw new IllegalArgumentException("Year invalid");
        else if (releaseYear < 1970 || releaseYear > Calendar.getInstance().get(Calendar.YEAR))
            throw new IllegalArgumentException("Year Cannot be negative");
        this.releaseYear = releaseYear;

    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        notNull(albumName);
        notBlank(albumName);

        this.albumName = albumName;
    }

    // Extension
    public int getSales() {
        return sales;
    }

    //
    // additional functionality added in Assignment 1
    public String getAdditionalInformation() {
        return additionalInformation;
    }

    // additional functionality added in Assignment 1
    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public void setSales(int sales) {
        if (sales < 0)
            throw new IllegalArgumentException("sales cannot be under 0");
        this.sales = sales;
    }

    public double getRating() {
        return rating;
    }

    //  rating: 1-5
    public void setRating(int rating) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating should between 1 and 5");
        this.rating = rating;
    }

    // genre
    public void setGenre(String genre) {
        if (genre == null)
            throw new IllegalArgumentException("Genre cannot be null");
        else if (genre.trim().equals(""))
            throw new IllegalArgumentException("Genre cannot be empty");
        this.genre = genre.trim();
    }

    public String getGenre() {
        return genre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return releaseYear == album.releaseYear &&
                recordNumber.equals(album.recordNumber) &&
                albumName.equals(album.albumName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(releaseYear, recordNumber, albumName);
    }
}
