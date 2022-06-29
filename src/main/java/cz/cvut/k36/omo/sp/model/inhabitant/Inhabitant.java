package cz.cvut.k36.omo.sp.model.inhabitant;

import cz.cvut.k36.omo.sp.model.event.Event;
import cz.cvut.k36.omo.sp.pattern.observer.Observer;
import cz.cvut.k36.omo.sp.pattern.visitor.EntityVisitor;
import cz.cvut.k36.omo.sp.utils.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public abstract class Inhabitant implements Entity, Observer {

    protected String name;
    private TypeInhabitant type;
    protected StateInhabitant state; // inhabitant's state. not all can go to all states.
    protected List<Event> events;    // list of all events associated with the inhabitant.

    /**
     * @param name inhabitant's name.
     * @param type TypeInhabitant PET or PERSON.
     */
    public Inhabitant(String name, TypeInhabitant type) {
        this.name = name;
        this.type = type;
    }

    public void addEvent(Event event) {
        Objects.requireNonNull(event);
        if (events == null) events = new ArrayList<>();
        events.add(event);
    }
}