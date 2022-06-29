package cz.cvut.k36.omo.sp.pattern.facade;

import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.device.Manual;
import cz.cvut.k36.omo.sp.model.device.content.DeviceContent;
import cz.cvut.k36.omo.sp.model.device.content.TypeDeviceContent;
import cz.cvut.k36.omo.sp.model.device.devices.*;
import cz.cvut.k36.omo.sp.model.device.energy.EventEnergy;
import cz.cvut.k36.omo.sp.model.event.Event;
import cz.cvut.k36.omo.sp.model.event.TypeEvent;
import cz.cvut.k36.omo.sp.model.home.HomeConfiguration;
import cz.cvut.k36.omo.sp.model.inhabitant.Inhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.StateInhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.TypeInhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.person.AgeGroup;
import cz.cvut.k36.omo.sp.model.inhabitant.person.Person;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.Pet;
import cz.cvut.k36.omo.sp.model.transport.Transport;
import cz.cvut.k36.omo.sp.model.transport.TypeTransport;
import cz.cvut.k36.omo.sp.pattern.state.ActiveState;
import cz.cvut.k36.omo.sp.pattern.state.BlockState;
import cz.cvut.k36.omo.sp.pattern.state.IdleState;
import cz.cvut.k36.omo.sp.pattern.state.OffState;
import cz.cvut.k36.omo.sp.utils.Constants;
import cz.cvut.k36.omo.sp.utils.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FacadeImpl implements Facade {

    private static final Logger LOGGER = LogManager.getLogger(FacadeImpl.class.getName());

    private HomeConfiguration configuration;

    public FacadeImpl(HomeConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void onDevice(Device device, Inhabitant inhabitant, LocalDateTime dateTime) {
        if (inhabitant.getType() == TypeInhabitant.PERSON) {
            Person person = getPersonByInhabitant(inhabitant);
            if (person!= null && person.getAgeGroup() != AgeGroup.BABY && person.getState() == StateInhabitant.IDLE) {
                if (device.getState().toString().equals("OFF")) {
                    device.setState(new IdleState(device));
                    addEventEnergy(device, dateTime, device.getState().setPower());
                    Event event = new Event(TypeEvent.ON_DEVICE, dateTime, person, device, Constants.INHABITANT_DEVICE_USE);
                    person.addEvent(event);
                    device.addEvent(event);
                    event.setEndTime(dateTime);
                    person.setState(StateInhabitant.IDLE);

                    LOGGER.info("Instant event " + event + " Person=" + person + ", Device=" + device);
                }
            }
        }
    }

    /**
     * @param inhabitant Inhabitant to find a person.
     * @return Person bz inhabitant. If the person does not find, then null is returned.
     */
    private Person getPersonByInhabitant(Inhabitant inhabitant) {
        List<Person> persons = configuration.getPersons();
        if (persons != null) {
            for (Person person : persons) {
                if (inhabitant.equals(person)) return person;
            }
        }
        return null;
    }

    @Override
    public void startUseDevice(Device device, Inhabitant inhabitant, LocalDateTime dateTime) {
        if (inhabitant.getState() == StateInhabitant.IDLE) {
            if (device.getState().toString().equals("BLOCK"))
                LOGGER.info("The device cannot be used. It is blocked");
            else if (device.getState().toString().equals("OFF")) {
                onDevice(device, inhabitant, dateTime);
                startUseDevice(device, inhabitant, dateTime);
            } else if (device.getState().toString().equals("IDLE") && device.getCurrentUser() == null) {
                if (contentReduction(device)) {
                    inhabitant.setState(StateInhabitant.ACTIVITY);
                    device.setState(new ActiveState(device));
                    addEventEnergy(device, dateTime, device.getState().setPower());
                    device.setCurrentUser(inhabitant);
                    Event event = new Event(TypeEvent.USAGE_DEVICE, dateTime, inhabitant, device, Constants.INHABITANT_DEVICE_USE);
                    inhabitant.addEvent(event);
                    device.addEvent(event);

                    LOGGER.info("Start " + event + " Inhabitant=" + inhabitant + ", Device=" + device);
                } else
                    addContent(device, dateTime);
            } else if (device.getState().toString().equals("ACTIVE")) {
                if (device.getCurrentUser() == null) {
                    device.setState(new IdleState(device));
                    addEventEnergy(device, dateTime, device.getState().setPower());
                    startUseDevice(device, inhabitant, dateTime);
                } else if (!device.getCurrentUser().equals(inhabitant)) {
                    device.addInhabitant(inhabitant);
                    inhabitant.setState(StateInhabitant.WAIT);
                    Event event = new Event(TypeEvent.WAIT, dateTime, inhabitant, device, Constants.INHABITANT_DEVICE_WAIT);
                    inhabitant.addEvent(event);
                    device.addEvent(event);

                    LOGGER.info("Start " + event + " Inhabitant=" + inhabitant + ", Device=" + device);
                }
            }
        }
    }

    /**
     * Returns true if the device should have no content.
     *
     * @param device Device that will have reduced content
     * @return true, if there is enough content and the device can be used.
     */
    private boolean contentReduction(Device device) {
        List<DeviceContent> contents = device.getDeviceContent();
        if (contents == null)
            return true;
        else {
            if (contents.size() == 1) {
                if (contents.get(0).getTypeContent() == TypeDeviceContent.FEED &&
                        (contents.get(0).getCount() - Constants.PORTION_FEED) >= 0) {
                    contents.get(0).setCount(contents.get(0).getCount() - Constants.PORTION_FEED);
                    return true;
                } else if (contents.get(0).getTypeContent() == TypeDeviceContent.FOOD &&
                        (contents.get(0).getCount() - Constants.PORTION_FOOD) >= 0) {
                    contents.get(0).setCount(contents.get(0).getCount() - Constants.PORTION_FOOD);
                    return true;
                } else if (contents.get(0).getTypeContent() == TypeDeviceContent.WATER_FOR_SHOWER) {
                    contents.get(0).setCount(contents.get(0).getCount() - Constants.PORTION_WATER_FOR_SHOWER);
                    return true;
                } else if (contents.get(0).getTypeContent() == TypeDeviceContent.WATER &&
                        (contents.get(0).getCount() - Constants.PORTION_WATER) >= 0) {
                    contents.get(0).setCount(contents.get(0).getCount() - Constants.PORTION_WATER);
                    return true;
                }
            } else if (contents.size() == 2){
                if ((contents.get(0).getTypeContent() == TypeDeviceContent.WATER_FOR_CM &&
                        (contents.get(0).getCount() - Constants.PORTION_WATER_FOR_CM) >= 0) &&
                        (contents.get(1).getTypeContent() == TypeDeviceContent.COFFEE &&
                                (contents.get(1).getCount() - Constants.PORTION_COFFEE) >= 0)) {
                    contents.get(0).setCount(contents.get(0).getCount() - Constants.PORTION_WATER_FOR_CM);
                    contents.get(1).setCount(contents.get(0).getCount() - Constants.PORTION_COFFEE);
                    return true;
                }
            }
            LOGGER.info("Content is not enough in the Device=" + device);
            return false;
        }
    }

    /**
     * Content can only be done by free person.
     * Content replenishment occurs to its original value.
     * The event occurs instantly, so the end time of the event coincides with the time of its start.
     *
     * @param device   Device to be replenished
     * @param dateTime actual time of the content replenishment event.
     */
    public void addContent(Device device, LocalDateTime dateTime) {
        Person person = getFreePerson();
        if (person != null) {
            device.recoveryContent();
            Event event = new Event(TypeEvent.ADD_CONTENT, dateTime, person, device, Constants.PERSON_DEVICE_ADD_CONTENT);
            person.addEvent(event);
            device.addEvent(event);
            event.setEndTime(dateTime);

            LOGGER.info("Start " + event + " Person=" + person + ", Device=" + device);
        } else
            LOGGER.info("Failed to replenish content. No free persons. Device=" + device);
    }

    /**
     * @return Person not BABY with state IDLE or return null.
     */
    private Person getFreePerson() {
        List<Person> persons = configuration.getPersons();
        if (persons != null) {
            for (Person person : persons)
                if (person.getState() == StateInhabitant.IDLE && person.getAgeGroup() != AgeGroup.BABY)
                    return person;
        }
        return null;
    }

    @Override
    public void stopUseDevice(Inhabitant inhabitant, LocalDateTime dateTime) {
        if (inhabitant.getState() == StateInhabitant.ACTIVITY) {
            List<Event> events = inhabitant.getEvents();
            if (events != null) {
                inhabitant.setState(StateInhabitant.IDLE);
                Event lastEvent = events.get(events.size() - 1);
                Device device = getDeviceLastEvent(lastEvent);
                if (device != null) {
                    if (device.getState().toString().equals("OFF")) {
                        LOGGER.info("The device is already turned off.");
                    } else if (device.getState().toString().equals("BLOCK")) {
                        LOGGER.info("The device is blocked.");
                    } else {
                        lastEvent.setEndTime(dateTime);
                        device.setCurrentUser(null);
                        device.setState(new IdleState(device));
                        addEventEnergy(device, dateTime, device.getState().setPower());
                        LOGGER.info("The end " + lastEvent + " Inhabitant=" + inhabitant + ", Device=" + device);
                        List<Inhabitant> expects = device.getExpects();
                        if (expects != null && expects.size() != 0) {
                            Inhabitant inhabitant1 = expects.get(0);
                            device.removeInhabitant(inhabitant1);
                            inhabitant1.setState(StateInhabitant.IDLE);
                            startUseDevice(device, inhabitant1, dateTime);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param event Event with target for device search.
     * @return the Device by event target.
     */
    private Device getDeviceLastEvent(Event event) {
        Entity target = event.getTarget();
        List<CoffeeMachine> machines = configuration.getCoffeeMachines();
        if (machines != null) {
            for (CoffeeMachine machine : machines)
                if (machine.equals(target)) return machine;
        }
        List<DrinkerForPet> drinkers = configuration.getDrinkerForPets();
        if (drinkers != null) {
            for (DrinkerForPet drinker : drinkers)
                if (drinker.equals(target)) return drinker;
        }
        List<FeederForPet> feeders = configuration.getFeederForPets();
        if (feeders != null) {
            for (FeederForPet feeder : feeders)
                if (feeder.equals(target)) return feeder;
        }
        List<Lamp> lamps = configuration.getLamps();
        if (lamps != null) {
            for (Lamp lamp : lamps)
                if (lamp.equals(target)) return lamp;
        }
        List<MusicCenter> centers = configuration.getMusicCenters();
        if (centers != null) {
            for (MusicCenter center : centers)
                if (center.equals(target)) return center;
        }
        List<PC> pcs = configuration.getPcs();
        if (pcs != null) {
            for (PC pc : pcs)
                if (pc.equals(target)) return pc;
        }
        List<Refrigerator> refrigerators = configuration.getRefrigerators();
        if (refrigerators != null) {
            for (Refrigerator refrigerator : refrigerators)
                if (refrigerator.equals(target)) return refrigerator;
        }
        List<Shower> showers = configuration.getShowers();
        if (showers != null) {
            for (Shower shower : showers)
                if (shower.equals(target)) return shower;
        }
        List<VacuumCleaner> cleaners = configuration.getCleaners();
        if (cleaners != null) {
            for (VacuumCleaner cleaner : cleaners)
                if (cleaner.equals(target)) return cleaner;
        }
        return null;
    }

    @Override
    public void stopUseDevice(Device device, LocalDateTime dateTime) {
        if (device.getState().toString().equals("ACTIVE")) {
            List<Event> events = device.getEvents();
            if (events != null) {
                Event last = events.get(events.size() - 1);
                Entity source = last.getSource();
                if (source != null) {
                    Inhabitant inhabitant = getInhabitantByEntity(source);
                    if (inhabitant != null)
                        inhabitant.setState(StateInhabitant.IDLE);
                }
                last.setEndTime(dateTime);
            }
            device.setCurrentUser(null);
            device.setState(new IdleState(device));
            addEventEnergy(device, dateTime, device.getState().setPower());
            List<Inhabitant> expects = device.getExpects();
            if (expects != null && expects.size() != 0) {
                for (Inhabitant inhabitant : expects)
                    inhabitant.setState(StateInhabitant.IDLE);
            }
        } else
            LOGGER.info("The device is not used by anyone.");
        device.setExpects(null);
    }

    /**
     * @param entity Entity to find an inhabitant
     * @return Inhabitant by Entity or null
     */
    private Inhabitant getInhabitantByEntity(Entity entity) {
        List<Pet> pets = configuration.getPets();
        if(pets != null) {
            for (Pet pet : pets) {
                if (entity.equals(pet)) return pet;
            }
        }
        List<Person> persons = configuration.getPersons();
        if(persons != null) {
            for (Person person : persons) {
                if (entity.equals(person)) return person;
            }
        }
        return null;
    }

    @Override
    public void offDevice(Device device, Inhabitant inhabitant, LocalDateTime dateTime) {
        if (device.getState().toString().equals("OFF"))
            LOGGER.info("The device is already turned off.");
        else if (device.getState().toString().equals("BLOCK"))
            LOGGER.info("The device cannot be turned off until it is unlocked.");
        else if (inhabitant.getType() == TypeInhabitant.PERSON) {
            if (device.getState().toString().equals("ACTIVE")) {
                stopUseDevice(inhabitant, dateTime);
                offDevice(device, inhabitant, dateTime);
            } else {
                if (inhabitant.getType() == TypeInhabitant.PERSON) {
                    Person person = getPersonByInhabitant(inhabitant);
                    if (person != null && person.getAgeGroup() != AgeGroup.BABY) {
                        device.setState(new OffState(device));
                        addEventEnergy(device, dateTime, device.getState().setPower());
                        Event event = new Event(TypeEvent.OFF_DEVICE, dateTime, person, device, Constants.INHABITANT_DEVICE_USE);
                        person.addEvent(event);
                        device.addEvent(event);
                        event.setEndTime(dateTime);
                        person.setState(StateInhabitant.IDLE);

                        LOGGER.info("The device is turned off. Device=" + device + " Person=" + person);
                    } else
                        LOGGER.info("The device cannot be turned off by this person.");
                } else
                    LOGGER.info("Pets cannot turn on the device.");
            }
        } else
            LOGGER.info("Pets cannot turn off the device.");
    }

    @Override
    public void fixDevice(Device device, Person person, LocalDateTime dateTime) {
        stopUseDevice(device, dateTime);
        Manual manual = device.getManual().getManual(device.getType().toString());
        if (manual != null) {
            LOGGER.info("The user opened the manual." + manual);
            device.setQuality(Constants.BASE_DEVICE_QUALITY);
            Event event = new Event(TypeEvent.FIX_DEVICE, dateTime, person, device, Constants.PERSON_DEVICE_FIX);
            person.addEvent(event);
            device.addEvent(event);
            event.setEndTime(dateTime);
            LOGGER.info("Start " + event + " Person=" + person + ", Device=" + device);
        } else {
            LOGGER.info("The person could not open the manual. " +
                                            "The device will not be repaired. Device:" + device);
            blockDevice(device, dateTime);
        }
    }

    @Override
    public void blockDevice(Device device, LocalDateTime dateTime) {
        if (device.getState().toString().equals("BLOCK")) {
            LOGGER.info("The device is already locked. Device=" + device);
        } else if (device.getState().toString().equals("OFF")) {
            LOGGER.info("The device cannot be locked because it is turned off. Device=" + device);
        } else {
            stopUseDevice(device, dateTime);
            List<Inhabitant> expects = device.getExpects();
            if (expects != null && expects.size() != 0) {
                for (Inhabitant inhabitant : expects)
                    inhabitant.setState(StateInhabitant.IDLE);
            }
            device.setExpects(null);
            device.setState(new BlockState(device));
            Event event = new Event(TypeEvent.BLOCK_DEVICE, dateTime, null, device, Constants.DEVICE_BLOCK);
            device.addEvent(event);
            event.setEndTime(dateTime);

            addEventEnergy(device, dateTime, device.getState().setPower());
            LOGGER.info("Device is locked=" + device);
        }
    }

    @Override
    public void addEventEnergy(Device device, LocalDateTime dateTime, int power) {
        List<EventEnergy> events = device.getEventEnergy();
        if (events != null && events.get(events.size() - 1).getEnd() == null)
            events.get(events.size() - 1).setEnd(dateTime);
        EventEnergy event =  new EventEnergy(dateTime, null, power);
        device.addEventEnergy(event);
    }

    @Override
    public void theEndLastEvent(Person person, LocalDateTime currentTime) {
        List<Event> events = person.getEvents();
        if (events != null && person.getState() != StateInhabitant.WAIT) {
            Event lastEvent = events.get(events.size() - 1);
            if (lastEvent.getEndTime() == null) {
                if (lastEvent.getContext().equals(Constants.INHABITANT_DEVICE_USE)) {
                    stopUseDevice(person, currentTime);
                    return;
                }
                if (lastEvent.getContext().equals(Constants.PERSON_AUTO_USE) || lastEvent.getContext().equals(Constants.PERSON_TRANSPORT_USE)) {
                    Transport transport = getTransportByEntity(lastEvent.getTarget());
                    if (transport != null) {
                        transport.setCurrentUser(null);
                        lastEvent.setEndTime(currentTime);
                        person.setState(StateInhabitant.IDLE);

                        LOGGER.info("The end " + lastEvent + " Person=" + person + ". Transport=" + transport);
                        if (transport.getExpects() != null && transport.getExpects().size() != 0) {
                            Person person1 = transport.getExpects().stream().findFirst().get();
                            transport.removePerson(person1);
                            transport.setCurrentUser(person1);
                            person1.setState(StateInhabitant.NOT_AT_HOME);
                            Event event;
                            if (transport.getType() == TypeTransport.AUTO)
                                event = new Event(TypeEvent.USAGE_TRANSPORT, currentTime, person1, transport, Constants.PERSON_AUTO_USE);
                            else
                                event = new Event(TypeEvent.USAGE_TRANSPORT, currentTime, person1, transport, Constants.PERSON_TRANSPORT_USE);
                            person.addEvent(event);
                            transport.addEvent(event);

                            LOGGER.info("Start " + event + " Person=" + person1 + ", Transport=" + transport);
                        }
                    }
                } else if (lastEvent.getContext().equals(Constants.PET_PERSON_WALK) || lastEvent.getContext().equals(Constants.PET_PERSON_PLAY)) {
                    Pet pet = getPetByEntity(lastEvent.getSource());
                    if (pet != null) pet.setState(StateInhabitant.IDLE);
                    lastEvent.setEndTime(currentTime);
                    person.setState(StateInhabitant.IDLE);

                    LOGGER.info("The end " + lastEvent + " Person=" + person + ", Pet=" + pet);
                } else if (lastEvent.getContext().equals(Constants.BABY_PERSON_PLAY) ||
                        lastEvent.getContext().equals(Constants.BABY_PERSON_WALK) ||
                        lastEvent.getContext().equals(Constants.BABY_PERSON_EAT)) {
                    Entity entity = lastEvent.getTarget();
                    Person person1 = getPersonByEntity(entity);
                    if (person1 != null)
                        person1.setState(StateInhabitant.IDLE);
                    Person baby = getPersonByEntity(lastEvent.getSource());
                    if (baby != null)
                        baby.setState(StateInhabitant.IDLE);
                    LOGGER.info("The end " + lastEvent + " for Person=" + person);
                }
            }
        }
    }

    /**
     * @param entity Entity to find a transport
     * @return Transport by Entity or null
     */
    private Transport getTransportByEntity(Entity entity) {
        List<Transport> transports = configuration.getTransports();
        if (transports != null) {
            for (Transport transport : transports) {
                if (entity.equals(transport))
                    return transport;
            }
        }
        return null;
    }

    /**
     * @param entity Entity to find a person
     * @return Person by Entity or null
     */
    private Person getPersonByEntity(Entity entity) {
        List<Person> persons = configuration.getPersons();
        if (persons != null) {
            for (Person person : persons) {
                if (entity.equals(person))
                    return person;
            }
        }
        return null;
    }
    /**
     * @param entity Entity to find a pet
     * @return Pet by Entity or null
     */
    private Pet getPetByEntity(Entity entity) {
        List<Pet> pets = configuration.getPets();
        if (pets != null) {
            for (Pet pet : pets) {
                if (entity.equals(pet))
                    return pet;
            }
        }
        return null;
    }

    @Override
    public void theEndLastEvent(Pet pet, LocalDateTime currentTime) {
        List<Event> events = pet.getEvents();
        if (events != null && pet.getState() != StateInhabitant.WAIT) {
            Event lastEvent = events.get(events.size() - 1);
            if (lastEvent.getContext().equals(Constants.PET_PERSON_WALK) || lastEvent.getContext().equals(Constants.PET_PERSON_PLAY)) {
                Entity entity = lastEvent.getTarget();
                Person person1 = getPersonByEntity(entity);
                if (person1 != null)
                    person1.setState(StateInhabitant.IDLE);
            } else if (lastEvent.getContext().equals(Constants.INHABITANT_DEVICE_USE)) {
                stopUseDevice(pet, currentTime);
            } else {
                lastEvent.setEndTime(currentTime);
                pet.setState(StateInhabitant.IDLE);
                LOGGER.info("The end " + lastEvent + " for Pet=" + pet.getName() + "}.");
            }
        }
    }

    @Override
    public boolean checkLastEvent(List<Event> events, LocalDateTime dateTime, TypeEvent type) {
        if (events != null) {
            Event last = events.get(events.size() - 1);
            if (last.getEndTime() != null && Duration.between(last.getEndTime(), dateTime).toMinutes() <= 1 &&
                    type == last.getType()) {
                last.setEndTime(dateTime);
                return false;
            }
        }
        return true;
    }
}