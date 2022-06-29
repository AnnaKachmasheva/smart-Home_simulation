package cz.cvut.k36.omo.sp.simulation;

import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.device.devices.*;
import cz.cvut.k36.omo.sp.model.event.Event;
import cz.cvut.k36.omo.sp.model.event.TypeEvent;
import cz.cvut.k36.omo.sp.model.home.HomeConfiguration;
import cz.cvut.k36.omo.sp.model.inhabitant.StateInhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.person.ActionTypePerson;
import cz.cvut.k36.omo.sp.model.inhabitant.person.AgeGroup;
import cz.cvut.k36.omo.sp.model.inhabitant.person.Person;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.ActionTypePet;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.Pet;
import cz.cvut.k36.omo.sp.model.sensor.sensors.HumiditySensor;
import cz.cvut.k36.omo.sp.model.sensor.sensors.LightSensor;
import cz.cvut.k36.omo.sp.model.sensor.sensors.SmokeSensor;
import cz.cvut.k36.omo.sp.model.sensor.sensors.TemperatureSensor;
import cz.cvut.k36.omo.sp.model.transport.Transport;
import cz.cvut.k36.omo.sp.pattern.facade.Facade;
import cz.cvut.k36.omo.sp.pattern.facade.FacadeImpl;
import cz.cvut.k36.omo.sp.pattern.observer.ManagerObserver;
import cz.cvut.k36.omo.sp.report.ActivityAndUsageReport;
import cz.cvut.k36.omo.sp.report.ConsumptionReport;
import cz.cvut.k36.omo.sp.report.EventReport;
import cz.cvut.k36.omo.sp.report.HouseConfigurationReport;
import cz.cvut.k36.omo.sp.utils.Constants;
import cz.cvut.k36.omo.sp.utils.LoadConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Simulation {

    private static final Logger LOGGER = LogManager.getLogger(Simulation.class.getName());

    private final int numberConfig;
    private final String startDateAndTime;
    private LocalDateTime currentTime; // Current simulation date and time.
    private long duration; // How long the simulation will last.
    private int airHumidity; // Actual air humidity
    private int airSmoke; // actual air pollution, amount of smoke
    private int temperature; // Actual air temperature
    private boolean isDay; // Day now or night
    private LoadConfiguration loadConfiguration;
    private HomeConfiguration homeConfiguration;
    private Facade facade;
    private Random rand;

    /**
     *
     * @param numberConfig     1 or 2.This is the boot initial configuration number.
     * @param startDateAndTime String time and date in format "yyyy-MM-ddTHH:mm:ss".
     * @param duration         duration in minutes.
     */
    public Simulation(int numberConfig, String startDateAndTime, long duration) {
        this.numberConfig = numberConfig;
        this.startDateAndTime = startDateAndTime;
        this.duration = duration;
        this.loadConfiguration =  new LoadConfiguration();
        this.airHumidity = 0;
        this.airSmoke = 0;
        this.temperature = 20;
        this.rand = new Random();
    }

    /**
     * Loading configuration data. Preparing for simulation.
     *
     */
    public void start() {
        LOGGER.info("Start simulation.");

        currentTime = LocalDateTime.parse(startDateAndTime);
        loadConfiguration.loadAllJson(numberConfig);
        homeConfiguration = loadConfiguration.getHomeConfiguration();
        facade = new FacadeImpl(homeConfiguration);
        ManagerObserver managerObserver = new ManagerObserver(homeConfiguration);
        managerObserver.generateSubjects();
        setIsDay();
        run();
    }

    /***
     * Checking the functionality of devices every time.
     * Weather conditions change every time.
     * The sensor parameters are checked every time.
     * Random events for pets are generated with a frequency of 1 time per 5 ticks.
     * Basic events for persons are generated with a frequency of 1 time in 5 ticks.
     * The generation of events for the use of transports and devices for people occurs
     * with periodicity 1 time per 10 ticks.
     * Day or night check 1 time per 10 ticks.
     * The temperature change with the air conditioner turned on and the humidity change with
     * the turned on purifier occur 1 time in 10 ticks
     */
    public void run() {
        LOGGER.info("Run simulation.");

        int tick = 0;
        while (duration > 0) {
            if (tick == 5) {
               generateRandomPetEvent();
               generatePersonBaseEvent();
            } else if (tick == 10) {
                generateRandomTransportEvent();
                generateRandomDeviceEvent();
                setIsDay();
                changeTemperature();
                changeHumidity();
                tick = 0;
            }
            checkDeviceQuality();
            changeWeather();
            checkSensorsForEvents();
            tick++;
            duration--;
            currentTime = currentTime.plusMinutes(1);
        }
        stop();
    }

    /**
     * Sensors read environmental parameters(humidity, temperature, smoke, times of day).
     */
    private void changeWeather() {
        airHumidity = changeWeatherParameters(airHumidity);
        airSmoke = changeWeatherParameters(airSmoke);
        temperature = changeWeatherParameters(temperature);
        List<HumiditySensor> humiditySensors = homeConfiguration.getHumiditySensors();
        if (humiditySensors != null) {
            for (HumiditySensor sensor : humiditySensors)
                sensor.setReadings(airHumidity);
        }
        List<SmokeSensor> smokeSensors = homeConfiguration.getSmokeSensors();
        if (smokeSensors != null) {
            for (SmokeSensor sensor : smokeSensors)
                sensor.setReadings(airSmoke);
        }
        List<TemperatureSensor> temperatureSensors = homeConfiguration.getTemperatureSensors();
        if (temperatureSensors != null) {
            for (TemperatureSensor sensor : temperatureSensors)
                sensor.setReadings(temperature);
        }
        List<LightSensor> lightSensors = homeConfiguration.getLightSensors();
        if (lightSensors != null) {
            for (LightSensor sensor : lightSensors)
                sensor.setReadings(isDay ? 0 : 1);
        }
    }

    /**
     * @param parameter parameter to change.
     * @return          modified parameter.
     */
    private int changeWeatherParameters(int parameter) {
        if (rand.nextInt(2) == 1) {
            int part = rand.nextInt(0, 5);
            if (rand.nextInt(2) == 1) {
                return parameter + part;
            } else
                return parameter - part;
        }
       return parameter;
    }

    /**
     * Time of day (9 - 18)
     * Time of night (18 - 9)
     */
    private void setIsDay() {
        isDay = currentTime.toLocalTime().isBefore(LocalTime.of(17, 59));
        if (isDay) {
            isDay = !currentTime.toLocalTime().isBefore(LocalTime.of(8, 59));
        }
    }

    /**
     * The ACTIVE on air conditioner increases the air temperature by 1 unit per 5 ticks.
     */
    private void changeTemperature() {
        List<AirConditioner> conditioners = homeConfiguration.getAirConditioners();
        if (conditioners != null) {
            for (AirConditioner conditioner : conditioners) {
                if (conditioner.getState().toString().equals("ACTIVE"))
                    temperature ++;
            }
        }
    }

    /**
     * The ACTIVE on air purifier increases the air humidity by 1 unit per 5 ticks.
     */
    private void changeHumidity() {
        List<AirPurifier> purifiers = homeConfiguration.getAirPurifiers();
        if (purifiers != null) {
            for (AirPurifier purifier : purifiers) {
                if (purifier.getState().toString().equals("ACTIVE"))
                    airHumidity ++;
            }
        }
    }

    /**
     * All devices in the home configuration are checked.
     */
    private void checkDeviceQuality() {
        qualityReduction(homeConfiguration.getAirConditioners());
        qualityReduction(homeConfiguration.getAirPurifiers());
        qualityReduction(homeConfiguration.getCameras());
        qualityReduction(homeConfiguration.getCoffeeMachines());
        qualityReduction(homeConfiguration.getDrinkerForPets());
        qualityReduction(homeConfiguration.getFeederForPets());
        qualityReduction(homeConfiguration.getLamps());
        qualityReduction(homeConfiguration.getMusicCenters());
        qualityReduction(homeConfiguration.getPcs());
        qualityReduction(homeConfiguration.getRefrigerators());
        qualityReduction(homeConfiguration.getShowers());
        qualityReduction(homeConfiguration.getTVS());
        qualityReduction(homeConfiguration.getCleaners());
    }

    /**
     * If the device is in state ACTIVE, then its functionality decreases by a fixed value linearly.
     * When functionality drops to 0, the device needs fix.
     * If a free person is not found to fix it or the manual is not loaded, the device will be blocked.
     *
     * @param devices list of devices to check
     */
    private void qualityReduction(List< ? extends Device> devices) {
        if (devices != null)
            for (Device device : devices) {
                if (device.getState().toString().equals("ACTIVE")) {
                    device.setQuality(device.getQuality() - Constants.PORTION_QUALITY_REDUCTION);
                    if (device.getQuality() == 0) {
                        Person person = getFreePerson();
                        if (person!= null)
                            facade.fixDevice(device, person, currentTime);
                        else
                            facade.blockDevice(device, currentTime);
                    }
                }
            }
    }

    /**
     * Before creating a new event, the pet's last event ends as possible.
     * If it completes successfully, then a new event is generated.
     */
    private void generateRandomPetEvent() {
        Pet pet = homeConfiguration.getPets().get(rand.nextInt(homeConfiguration.getPets().size()));
        facade.theEndLastEvent(pet, currentTime);
        if (pet.getState() == StateInhabitant.IDLE) {
            List<ActionTypePet> actions = pet.getActions();
            switch (actions.get(rand.nextInt(actions.size()))) {
                case WALK -> {
                    Person person1 = getFreePerson();
                    if (person1 != null) {
                        pet.setState(StateInhabitant.NOT_AT_HOME);
                        person1.setState(StateInhabitant.NOT_AT_HOME);
                        Event event = new Event(TypeEvent.ACTION, currentTime, pet, person1, Constants.PET_PERSON_WALK);
                        person1.addEvent(event);
                        pet.addEvent(event);

                        LOGGER.info("Start " + event + " Pet=" + pet + ", Person=" + person1);
                    } else
                        LOGGER.info("Not free person. Pet cannot walk! Pet=" + pet);
                }
                case PLAY -> {
                    Event event;
                    Person person2 = getFreePerson();
                    if (person2 != null) {
                        person2.setState(StateInhabitant.ACTIVITY);
                        event = new Event(TypeEvent.ACTION, currentTime, pet, person2, Constants.PET_PERSON_PLAY);
                        pet.addEvent(event);
                    } else
                        event = new Event(TypeEvent.ACTION, currentTime, pet, null, Constants.PET_ALONE_PLAY);
                    pet.addEvent(event);
                    pet.setState(StateInhabitant.ACTIVITY);
                    LOGGER.info("Start " + event + " Pet=" + pet);
                }
                case SLEEP -> {
                    pet.setState(StateInhabitant.IDLE);
                    Event eventSleep = new Event(TypeEvent.ACTION, currentTime, pet, null, Constants.PET_SLEEP);
                    pet.addEvent(eventSleep);
                    LOGGER.info("Start " + eventSleep + " Pet=" + pet);
                }
                case DRINK -> {
                    List<DrinkerForPet> drinkers = homeConfiguration.getDrinkerForPets();
                    if (!drinkers.isEmpty()) {
                        DrinkerForPet drinker = drinkers.get(rand.nextInt(drinkers.size()));
                        Person person = getFreePerson();
                        if (person != null) {
                            facade.onDevice(drinker, person, currentTime);
                            facade.startUseDevice(drinker, pet, currentTime);
                        }
                    }
                }
                case FEED -> {
                    List<FeederForPet> feeders = homeConfiguration.getFeederForPets();
                    if (!feeders.isEmpty()) {
                        FeederForPet feeder = feeders.get(rand.nextInt(feeders.size()));
                        Person person = getFreePerson();
                        if (person != null) {
                            facade.onDevice(feeder, person, currentTime);
                            facade.startUseDevice(feeder, pet, currentTime);
                        }
                    }
                }
            }
        }
    }

    /**
     * Before creating a new event, the person's last event ends as possible.
     * If it completes successfully, then a new event is generated.
     */
    private void generatePersonBaseEvent() {
        Person person = homeConfiguration.getPersons().get(rand.nextInt(homeConfiguration.getPersons().size()));
        facade.theEndLastEvent(person, currentTime);
        if (person.getState() == StateInhabitant.IDLE) {
            List<ActionTypePerson> actions = person.getActions();
            List<ActionTypePerson> actionFromGen = new ArrayList<>();
            for (ActionTypePerson action : actions) {
                if (action.equals(ActionTypePerson.SLEEP) || action.equals(ActionTypePerson.EAT) ||
                        action.equals(ActionTypePerson.PLAY) || action.equals(ActionTypePerson.LEAVE_HOME) ||
                        action.equals(ActionTypePerson.USAGE_AUTO) || action.equals(ActionTypePerson.WALK))
                    actionFromGen.add(action);
            }
            switch (actionFromGen.get(rand.nextInt(actionFromGen.size()))) {
                case SLEEP:
                    person.setState(StateInhabitant.IDLE);
                    Event eventSleep = new Event(TypeEvent.ACTION, currentTime, person, null, Constants.PERSON_SLEEP);
                    person.addEvent(eventSleep);

                    LOGGER.info("Start " + eventSleep + " Person=" + person.getName());
                    break;
                case EAT:
                    if (person.getAgeGroup() != AgeGroup.BABY) {
                        List<Refrigerator> refrigerators = homeConfiguration.getRefrigerators();
                        if (refrigerators != null) {
                            facade.startUseDevice(refrigerators.get(rand.nextInt(refrigerators.size())), person, currentTime);
                        } else
                            LOGGER.info("Not found refrigerators.");
                    }else {
                        Person person1 = getFreePerson();
                        if (person1 != null) {
                            person.setState(StateInhabitant.ACTIVITY);
                            person1.setState(StateInhabitant.ACTIVITY);
                            Event eventEat = new Event(TypeEvent.ACTION, currentTime, person, person1, Constants.BABY_PERSON_EAT);
                            person.addEvent(eventEat);
                            person1.addEvent(eventEat);

                            LOGGER.info("Start " + eventEat + " Baby=" + person + " Person=" + person1);
                        }
                        else
                            LOGGER.info("Not found free person.");
                    }
                case PLAY:
                    if (person.getAgeGroup() == AgeGroup.BABY) {
                        Person person1 = getFreePerson();
                        if (person1 != null) {
                            person.setState(StateInhabitant.ACTIVITY);
                            person1.setState(StateInhabitant.ACTIVITY);
                            Event event = new Event(TypeEvent.ACTION, currentTime, person, person1, Constants.BABY_PERSON_PLAY);
                            person1.addEvent(event);
                            person.addEvent(event);

                            LOGGER.info("Start " + event + " Baby=" + person + " Person=" + person1);
                        } else
                            LOGGER.info("Not found free person.");
                    } else {
                        PC pc = homeConfiguration.getPcs().get(rand.nextInt(homeConfiguration.getPcs().size()));
                        facade.startUseDevice(pc, person, currentTime);
                    }
                    break;
                case LEAVE_HOME:
                    person.setState(StateInhabitant.NOT_AT_HOME);
                    Event goOut = new Event(TypeEvent.ACTION, currentTime, person, null, Constants.PERSON_GO_OUT);
                    person.addEvent(goOut);

                    LOGGER.info("Start " + goOut + " Person=" + person);
                    break;
                case USAGE_AUTO:
                    Transport freeAuto = getFreeAuto();
                    if (freeAuto != null) {
                        freeAuto.setCurrentUser(person);
                        person.setState(StateInhabitant.NOT_AT_HOME);
                        Event event = new Event(TypeEvent.USAGE_TRANSPORT, currentTime, person, freeAuto, Constants.PERSON_AUTO_USE);
                        person.addEvent(event);
                        freeAuto.addEvent(event);

                        LOGGER.info("Start " + event + " Person=" + person + " Auto=" + freeAuto);
                    } else {
                        Transport auto = getAutoWithShorterQueue();
                        auto.addPerson(person);
                        person.setState(StateInhabitant.WAIT);
                        Event event = new Event(TypeEvent.WAIT, currentTime, person, auto, Constants.PERSON_WAIT_AUTO);
                        person.addEvent(event);

                        LOGGER.info("Start " + event + " Person=" + person + " Auto=" + auto);
                    }
                    break;
                case WALK:
                    if (person.getAgeGroup() == AgeGroup.BABY) {
                        Person person1 = getFreePerson();
                        if (person1 != null) {
                            person.setState(StateInhabitant.NOT_AT_HOME);
                            person1.setState(StateInhabitant.NOT_AT_HOME);
                            Event event = new Event(TypeEvent.ACTION, currentTime, person, person1, Constants.BABY_PERSON_WALK);
                            person.addEvent(event);
                            person1.addEvent(event);

                            LOGGER.info("Start " + event + " Baby=" + person + " Person=" + person1);
                        } else
                            LOGGER.info("Not found free person.");

                    }
                    break;
            }
        }
    }

    /**
     * Before creating a new event, the person's last event ends as possible.
     * A person is selected from a list of those who have the ability to use the transport.
     * If the transport is busy, the person queues up. State WAIT.
     */
    private void generateRandomTransportEvent() {
        List<Person> persons = homeConfiguration.getPersons().stream()
                                                            .filter(Person::canUseTransport)
                                                            .collect(Collectors.toList());
        Person person = persons.get(rand.nextInt(persons.size()));
        facade.theEndLastEvent(person, currentTime);
        if (person.getState() == StateInhabitant.IDLE) {
            List<Transport> transports = homeConfiguration.getTransports().stream()
                                                                            .filter(transport -> !transport.isAuto())
                                                                            .collect(Collectors.toList());
            Transport transport = transports.get(rand.nextInt(transports.size()));
            if (transport.getCurrentUser() == null) {
                transport.setCurrentUser(person);
                person.setState(StateInhabitant.NOT_AT_HOME);
                Event event = new Event(TypeEvent.USAGE_TRANSPORT, currentTime, person, transport, Constants.PERSON_TRANSPORT_USE);
                person.addEvent(event);
                transport.addEvent(event);

                LOGGER.info("Start " + event + " Person=" + person + ", Transport=" + transport);
            } else {
                if (person.equals(transport.getCurrentUser()))
                    return;
                transport.addPerson(person);
                person.setState(StateInhabitant.WAIT);
                Event event = new Event(TypeEvent.WAIT, currentTime, person, transport, Constants.PERSON_WAIT_TRANSPORT);
                person.addEvent(event);

                LOGGER.info("Start " + event + " Person=" + person + ", Transport=" + transport);
            }
        }
    }

    /**
     * Before creating a new event, the person's last event ends as possible.
     * A person is selected from a list of those who have the ability to use the device.
     * If the device is busy, the person queues up. State WAIT.
     */
    private void generateRandomDeviceEvent() {
        List<Person> persons = homeConfiguration.getPersons().stream().filter(Person::canUseDevice).collect(Collectors.toList());
        Person person = persons.get(rand.nextInt(persons.size()));
        facade.theEndLastEvent(person, currentTime);
        if (person.getState() == StateInhabitant.IDLE) {
            switch (rand.nextInt(7)) {
                case 0 -> {
                    List<CoffeeMachine> machines = homeConfiguration.getCoffeeMachines();
                    CoffeeMachine machine = machines.get(rand.nextInt(machines.size()));
                    facade.theEndLastEvent(person, currentTime);
                    facade.startUseDevice(machine, person, currentTime);
                }
                case 1 -> {
                    List<Lamp> lamps = homeConfiguration.getLamps();
                    Lamp lamp = lamps.get(rand.nextInt(lamps.size()));
                    facade.theEndLastEvent(person, currentTime);
                    facade.startUseDevice(lamp, person, currentTime);
                }
                case 2 -> {
                    List<MusicCenter> centers = homeConfiguration.getMusicCenters();
                    MusicCenter center = centers.get(rand.nextInt(centers.size()));
                    facade.theEndLastEvent(person, currentTime);
                    facade.startUseDevice(center, person, currentTime);
                }
                case 3 -> {
                    List<PC> pcs = homeConfiguration.getPcs();
                    PC pc = pcs.get(rand.nextInt(pcs.size()));
                    facade.theEndLastEvent(person, currentTime);
                    facade.startUseDevice(pc, person, currentTime);
                }
                case 4 -> {
                    List<Shower> showers = homeConfiguration.getShowers();
                    Shower shower = showers.get(rand.nextInt(showers.size()));
                    facade.theEndLastEvent(person, currentTime);
                    facade.startUseDevice(shower, person, currentTime);
                }
                case 5 -> {
                    List<TV> tvs = homeConfiguration.getTVS();
                    TV tv = tvs.get(rand.nextInt(tvs.size()));
                    facade.theEndLastEvent(person, currentTime);
                    facade.startUseDevice(tv, person, currentTime);
                }
                case 6 -> {
                    List<VacuumCleaner> cleaners = homeConfiguration.getCleaners();
                    VacuumCleaner cleaner = cleaners.get(rand.nextInt(cleaners.size()));
                    facade.theEndLastEvent(person, currentTime);
                    facade.startUseDevice(cleaner, person, currentTime);
                }
            }
        }
    }

    /**
     * @return Person with state IDLE and not BABY or null
     */
    private Person getFreePerson() {
        for (Person person : homeConfiguration.getPersons())
            if (person.getState() == StateInhabitant.IDLE && person.getAgeGroup() != AgeGroup.BABY)
                return person;
        return null;
    }

    /**
     * @return Transport with type AUTO, not busy or null
     */
    private Transport getFreeAuto() {
        for (Transport transport: homeConfiguration.getTransports())
            if (transport.getCurrentUser() == null && transport.isAuto())
                return transport;
        return null;
    }

    /**
     * @return Transport with type AUTO with the shortest queue.
     */
    private Transport getAutoWithShorterQueue() {
        Transport auto = null;
        for (Transport transport: homeConfiguration.getTransports()) {
            if (transport.isAuto()) {
                if (auto == null || auto.getExpects() == null) {
                    auto = transport;
                } else if (transport.getExpects() == null || transport.getExpects().size() < auto.getExpects().size())
                    auto = transport;
            }
        }
        return auto;
    }

    /**
     * If the parameters of the environment exceed the permissible values,
     * then the sensors notify their observers about it.
     * Observers react differently.
     *  Lamps turn on at night, turn off with day.
     *  When the temperature drops, the windows close and the air conditioner turns on.
     *  When the temperature rises, the windows open and the air conditioner turns off.
     *  When the humidity is low, the windows open and the purifier turns on.
     *  With high humidity, the windows are closed and the purifier is turned off.
     *  With increased smoke, all devices are turned off, inhabitants leave the house,
     *  queues at transports and devices are cleared, windows are closed.
     */
    private void checkSensorsForEvents() {
        List<LightSensor> lightSensors = homeConfiguration.getLightSensors();
        if (lightSensors != null) {
            if (isDay) {
                for (LightSensor sensor : lightSensors) {
                    sensor.notifySubscribers(TypeEvent.LIGHT_DAY, currentTime, facade);

                    LOGGER.info("Is day observer" + sensor + ". Time " + currentTime.toLocalTime());
                }
            } else {
                for (LightSensor sensor : lightSensors) {
                    sensor.notifySubscribers(TypeEvent.LIGHT_NIGHT, currentTime, facade);

                    LOGGER.info("Is night observer" +  sensor + ".Time " + currentTime.toLocalTime());
                }
            }
        }
        List<HumiditySensor> humiditySensors = homeConfiguration.getHumiditySensors();
        if (humiditySensors != null) {
            if (airHumidity <= 0) {
                for (HumiditySensor sensor : humiditySensors) {
                    sensor.notifySubscribers(TypeEvent.RAISE_HUMIDITY, currentTime, facade);

                    LOGGER.info("Is humidity observer" +  sensor + ".Humidity " + airHumidity);
                }
            } else if (airHumidity > 50)
                for (HumiditySensor sensor : humiditySensors) {
                    sensor.notifySubscribers(TypeEvent.LOWER_HUMIDITY, currentTime, facade);

                    LOGGER.info("Is humidity observer" +  sensor + ".Humidity " + airHumidity);
                }
        }
        List<TemperatureSensor> temperatureSensors = homeConfiguration.getTemperatureSensors();
        if (temperatureSensors != null) {
            if (temperature <= 0) {
                for (TemperatureSensor sensor : temperatureSensors) {
                    sensor.notifySubscribers(TypeEvent.RAISE_TEMPERATURE, currentTime, facade);

                    LOGGER.info("Is temperature observer" +  sensor + ".Temperature " + temperature);
                }
            } else if (airHumidity > 25)
                for (TemperatureSensor sensor : temperatureSensors) {
                    sensor.notifySubscribers(TypeEvent.LOWER_TEMPERATURE, currentTime, facade);

                    LOGGER.info("Is temperature observer" +  sensor + ".Temperature " + temperature);
                }
        }
        List<SmokeSensor> smokeSensors = homeConfiguration.getSmokeSensors();
        if (smokeSensors != null) {
            if (airSmoke > 50) {
                for (SmokeSensor sensor : smokeSensors) {
                    sensor.notifySubscribers(TypeEvent.DANGER, currentTime, facade);

                    LOGGER.info("Is danger observer" +  sensor + ".Smoke " + airSmoke);
                    stop();
                }
            }
        }
    }

    /***
     * 4 reports are generated before the end of the simulation.
     */
    public void stop() {
        String time = currentTime.format(DateTimeFormatter.ofPattern("d-MM-yyyy_HH-mm"));
        HouseConfigurationReport hr = new HouseConfigurationReport(numberConfig, time);
        hr.visit(homeConfiguration);

        ActivityAndUsageReport aur = new ActivityAndUsageReport(numberConfig, time);
        aur.setCurrentTime(currentTime);
        aur.visit(homeConfiguration);

        ConsumptionReport cr = new ConsumptionReport(numberConfig, time);
        cr.setCurrentTime(currentTime);
        cr.visit(homeConfiguration);

        EventReport er = new EventReport(numberConfig, time);
        er.visit(homeConfiguration);

        LOGGER.info("The end simulation.");
        System.exit(1);
    }
}