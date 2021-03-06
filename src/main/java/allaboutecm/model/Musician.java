package allaboutecm.model;

import allaboutecm.dataaccess.neo4j.URLConverter;
import com.google.common.collect.Sets;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.net.URL;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * An artist that has been featured in (at least) one ECM record.
 *
 * See {@https://www.ecmrecords.com/artists/1435045745}
 */
@NodeEntity
public class Musician extends Entity {
    @Property(name="name")
    private String name;

    @Property(name = "rating")
    private int rating;

    @Convert(URLConverter.class)
    @Property(name="musicianURL")
    private URL musicianUrl;

    @Relationship(type="albums")
    private Set<Album> albums;

    //
    @Property(name="musicianBiography")
    private String biography;   // if musician is a group, member names will be put here

    @Property(name="musicianGroup")
    private boolean group;

    public Musician() {
    }

    public Musician(String name) {
        checkName(name);
        this.name = name.trim();
        this.musicianUrl = null;

        albums = Sets.newHashSet();
        this.biography = null;
        this.group = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkName(name);             //the name can not be be null or empty
        this.name = name.trim();

    }

    public Set<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(Set<Album> albums) {
        checkAlbums(albums);
        this.albums = albums;
    }

    public void deleteAlbums() {

        this.albums = Collections.emptySet();
    }

    //
    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        if (biography == null)
            this.biography = null;
        else {
            biography = biography.trim();
            if (biography.isEmpty())
                throw new IllegalArgumentException("");
            this.biography = biography;
        }
    }

    public boolean getGroup() {
        return this.group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Musician that = (Musician) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public URL getMusicianUrl() {
        return musicianUrl;
    }

    public void setMusicianUrl(URL musicianUrl) {
        this.musicianUrl = musicianUrl;
    }

    public void setRating(int rating) {
        checkRating(rating);
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    private void checkName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null");
        else if (name.trim().equals(""))
            throw new IllegalArgumentException("Name cannot be empty");
    }

    private void checkAlbums(Set<Album> albums) {
        if (albums == null)
            throw new IllegalArgumentException("Albums cannot be null");
        else if (albums.isEmpty())
            throw new IllegalArgumentException("Albums cannot be empty");
    }

    private void checkRating(int rating) {
        if (rating < 1 || rating > 5)
            throw new IllegalArgumentException("Rating should between 1 and 5");
    }
}
