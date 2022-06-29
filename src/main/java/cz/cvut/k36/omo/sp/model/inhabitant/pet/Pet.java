package cz.cvut.k36.omo.sp.model.inhabitant.pet;

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
public class Pet extends Inhabitant {

    private TypePet typePet;
    private List<ActionTypePet> actions; // list of possible pet actions.

    /**
     * @param name    pet's name
     * @param type    TypeInhabitant = PET.
     * @param typePet TypePet cat, dog...
     */
    public Pet(String name, TypeInhabitant type, TypePet typePet) {
        super(name, type);
        this.typePet = typePet;
    }

    @Override
    public String toString() {
        return "name={" + name + "} ";
    }

    @Override
    public void update(TypeEvent type, LocalDateTime dateTime, Facade facade) {
        if (type == TypeEvent.DANGER) {
            facade.theEndLastEvent(this, dateTime);
            this.setState(StateInhabitant.NOT_AT_HOME);
        }
    }

    public String toStringForLog() {
        return "Pet{" +
                "name='" + name + '\'' +
                ", events=" + events +
                ", state=" + state +
                ", typePet=" + typePet +
                ", actions=" + actions +
                '}';
    }
}