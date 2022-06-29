package cz.cvut.k36.omo.sp.pattern.builder;

import cz.cvut.k36.omo.sp.model.inhabitant.StateInhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.TypeInhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.person.ActionTypePerson;
import cz.cvut.k36.omo.sp.model.inhabitant.person.AgeGroup;
import cz.cvut.k36.omo.sp.model.inhabitant.person.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonBuilder {

    private static final Logger LOGGER = LogManager.getLogger(PersonBuilder.class.getName());

    private String name;
    private StateInhabitant state;
    private AgeGroup ageGroup;
    private boolean drivingLicense;

    public PersonBuilder() {
    }

    public PersonBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder withState(StateInhabitant state) {
        this.state = state;
        return this;
    }

    public PersonBuilder withAgeGroup(long age) {
        this.ageGroup = getAgeGroup(age);
        return this;
    }

    public PersonBuilder withDrivingLicense(boolean drivingLicense) {
        this.drivingLicense = drivingLicense;
        return this;
    }

    /**
     * @param age Int person's age
     * @return AgeGroup
     */
    private AgeGroup getAgeGroup(long age) {
        if (age <= 5) return AgeGroup.BABY;
        else if (age <= 18) return AgeGroup.CHILD;
        return AgeGroup.ADULT;
    }

    public Person build() {
        if (this.name == null || this.state == null || this.ageGroup == null) {
            LOGGER.warn("Some required arguments are not provided: name, state, ageGroup!");
            return null;
        }
        Person person = new Person(this.name, TypeInhabitant.PERSON, this.ageGroup);
        person.setState(this.state);
        person.setActions(setActionTypePerson(person, this.drivingLicense));
        return person;
    }


    /**
     * Adding to a person actions type.
     *
     * @param person Person
     * @param haveDrivingLicense true, if person has license
     */
    private List<ActionTypePerson> setActionTypePerson(Person person, Boolean haveDrivingLicense) {
        List<ActionTypePerson> actions = new ArrayList<>();
        Collections.addAll(actions, ActionTypePerson.SLEEP, ActionTypePerson.PLAY, ActionTypePerson.EAT, ActionTypePerson.WALK);
        if (person.getAgeGroup() != AgeGroup.BABY) {
            Collections.addAll(actions, ActionTypePerson.LEAVE_HOME, ActionTypePerson.USAGE_TRANSPORT,
                    ActionTypePerson.ON_DEVICE, ActionTypePerson.OFF_DEVICE, ActionTypePerson.USAGE_DEVICE);
        }
        if (person.getAgeGroup() == AgeGroup.ADULT) {
            actions.add(ActionTypePerson.FIX_DEVICE);
            if (haveDrivingLicense)
                actions.add(ActionTypePerson.USAGE_AUTO);
        }
        return actions;
    }
}