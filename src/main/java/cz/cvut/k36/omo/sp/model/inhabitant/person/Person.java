package cz.cvut.k36.omo.sp.model.inhabitant.person;

import cz.cvut.k36.omo.sp.model.inhabitant.Inhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.StateInhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.TypeInhabitant;
import cz.cvut.k36.omo.sp.model.event.TypeEvent;
import cz.cvut.k36.omo.sp.pattern.facade.Facade;
import cz.cvut.k36.omo.sp.pattern.visitor.ReportVisitor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Person extends Inhabitant {

    private AgeGroup ageGroup;
    private List<ActionTypePerson> actions; // list of possible human actions.

    /**
     * @param name      person's name.
     * @param type      TypeInhabitant = PERSON.
     * @param ageGroup  age group. 0-5 BABY, 5-18 CHILD, 18+ ADULT.
     */
    public Person(String name, TypeInhabitant type, AgeGroup ageGroup) {
        super(name, type);
        this.ageGroup = ageGroup;
    }

    @Override
    public String toString() {
        return " name={" + name + "} ";
    }

    @Override
    public void update(TypeEvent type, LocalDateTime dateTime, Facade facade) {
        if (type == TypeEvent.DANGER) {
            facade.theEndLastEvent(this, dateTime);
            this.setState(StateInhabitant.NOT_AT_HOME);
        }
    }

    public String toStringForLog() {
        return "Person{" +
                "name='" + name + '\'' +
                ", events=" + events +
                ", state=" + state +
                ", ageGroup=" + ageGroup +
                ", actions=" + actions +
                '}';
    }

    /**
     * @return true, if a person can use transport.
     */
    public boolean canUseTransport() {
        return actions.contains(ActionTypePerson.USAGE_TRANSPORT);
    }

    /**
     * @return true, if a person can use devices.
     */
    public boolean canUseDevice() {
        return actions.contains(ActionTypePerson.USAGE_DEVICE);
    }
}