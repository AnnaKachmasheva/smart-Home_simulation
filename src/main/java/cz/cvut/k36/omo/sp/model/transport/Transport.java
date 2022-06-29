package cz.cvut.k36.omo.sp.model.transport;

import cz.cvut.k36.omo.sp.model.event.Event;
import cz.cvut.k36.omo.sp.model.event.TypeEvent;
import cz.cvut.k36.omo.sp.model.inhabitant.person.Person;
import cz.cvut.k36.omo.sp.pattern.facade.Facade;
import cz.cvut.k36.omo.sp.pattern.observer.Observer;
import cz.cvut.k36.omo.sp.pattern.visitor.ReportVisitor;
import cz.cvut.k36.omo.sp.utils.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
public class Transport implements Entity, Observer {

    private String name;
    private TypeTransport type;
    private Person currentUser;   // Person who is currently using the device.
    private Set<Person> expects; // A queue of persons who are waiting for the device to get off.
    private List<Event> events;   // Events that happened to this transport.

    /**
     * @param name non-unique transport name.
     * @param type TypeTransport AUTO or SKIS or BICYCLE.
     */
    public Transport(String name, TypeTransport type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return  "name={" + name + "} ";
    }

    @Override
    public void update(TypeEvent type, LocalDateTime dateTime, Facade facade) {
        if (type == TypeEvent.DANGER) {
            this.setExpects(null);
        }
    }

    /**
     * @return thue, if this transport is of AUTO type.
     */
    public boolean isAuto() {
        return type == TypeTransport.AUTO;
    }

    public void addPerson(Person person) {
        Objects.requireNonNull(person);
        if (expects == null)
            expects = new HashSet<>();
        expects.add(person);
    }

    public void removePerson(Person person) {
        Objects.requireNonNull(person);
        if (expects == null) return;
        expects.remove(person);
    }

    public void addEvent(Event event) {
        Objects.requireNonNull(event);
        if (events == null)
            events = new ArrayList<>();
        events.add(event);
    }

    public String toStringForLog() {
        return "Transport{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", currentUser=" + currentUser +
                ", expects=" + expects +
                ", events=" + events +
                '}';
    }
}