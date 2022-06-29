package cz.cvut.k36.omo.sp.pattern.builder;

import cz.cvut.k36.omo.sp.model.inhabitant.StateInhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.TypeInhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.ActionTypePet;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.Pet;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.TypePet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PetBuilder {

    private static final Logger LOGGER = LogManager.getLogger(PetBuilder.class.getName());

    private String name;
    private StateInhabitant state;
    private TypePet type;

    public PetBuilder() {
    }

    public PetBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PetBuilder withState(StateInhabitant state) {
        this.state = state;
        return this;
    }

    public PetBuilder withType(String type) {
        this.type = getTypePet(type);
        return this;
    }

    /**
     * @param type String pet's type
     * @return TypePet equivalent type
     */
    private TypePet getTypePet(String type) {
        return TypePet.valueOf(type);
    }

    public Pet build() {
        if (this.name == null || this.state == null || this.type == null) {
            LOGGER.warn("Some required arguments are not provided: name, type, state!");
            return null;
        }
        Pet pet = new Pet(this.name, TypeInhabitant.PET, this.type);
        pet.setState(this.state);
        pet.setActions(setActionTypePet(pet));
        return pet;
    }

    /**
     * All pets can eat and sleep. Everyone except fish can drink.
     * Dogs and cats can play.
     * Only dogs can walk.
     *
     * @param pet which the activity types will be added
     */
    private List<ActionTypePet> setActionTypePet(Pet pet) {
        List<ActionTypePet> actions = new ArrayList<>();
        actions.add(ActionTypePet.FEED);
        actions.add(ActionTypePet.SLEEP);
        if (pet.getTypePet() != TypePet.FISH)
            actions.add(ActionTypePet.DRINK);
        if (pet.getTypePet() == TypePet.DOG)
            actions.add(ActionTypePet.WALK);
        if (pet.getTypePet() == TypePet.DOG || pet.getTypePet() == TypePet.CAT)
            actions.add(ActionTypePet.PLAY);
        return actions;
    }
}