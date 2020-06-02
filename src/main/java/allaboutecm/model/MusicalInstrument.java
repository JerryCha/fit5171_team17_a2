package allaboutecm.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import java.util.Objects;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

@NodeEntity
public class MusicalInstrument extends Entity {
    @Property(name="musicalInstrumentName")
    private String name;

    public MusicalInstrument() {
    }

    public MusicalInstrument(String name) {
        checkName(name);
        name = name.trim();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkName(name);
        name = name.trim();
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicalInstrument that = (MusicalInstrument) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    private void checkName(String name) {
        if (name == null)
            throw new IllegalArgumentException("name cannot be null");
        else if (name.trim().equals(""))
            throw new IllegalArgumentException("name cannot be empty");
    }
}
