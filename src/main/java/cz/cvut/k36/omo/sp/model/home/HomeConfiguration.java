package cz.cvut.k36.omo.sp.model.home;

import cz.cvut.k36.omo.sp.model.inhabitant.person.Person;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.Pet;
import cz.cvut.k36.omo.sp.model.device.devices.*;
import cz.cvut.k36.omo.sp.model.sensor.sensors.HumiditySensor;
import cz.cvut.k36.omo.sp.model.sensor.sensors.LightSensor;
import cz.cvut.k36.omo.sp.model.sensor.sensors.SmokeSensor;
import cz.cvut.k36.omo.sp.model.sensor.sensors.TemperatureSensor;
import cz.cvut.k36.omo.sp.model.transport.Transport;
import cz.cvut.k36.omo.sp.pattern.visitor.EntityVisitor;
import cz.cvut.k36.omo.sp.pattern.visitor.ReportVisitor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Contains information about all devices, sensors, residents and transports.
 */
@Getter
public class HomeConfiguration implements EntityVisitor {

    private final Home home;
    private final List<Transport> transports;
    private final List<Person> persons;
    private final List<Pet> pets;
    private List<Window> windows;
    // Devices
    private List<AirConditioner> airConditioners;
    private List<AirPurifier> airPurifiers;
    private List<Camera> cameras;
    private List<CoffeeMachine> coffeeMachines;
    private List<DrinkerForPet> drinkerForPets;
    private List<FeederForPet> feederForPets;
    private List<Lamp> lamps;
    private List<MusicCenter> musicCenters;
    private List<PC> pcs;
    private List<Refrigerator> refrigerators;
    private List<Shower> showers;
    private List<TV> TVS;
    private List<VacuumCleaner> cleaners;
    // Sensors
    private List<HumiditySensor> humiditySensors;
    private List<LightSensor> lightSensors;
    private List<SmokeSensor> smokeSensors;
    private List<TemperatureSensor> temperatureSensors;

    public HomeConfiguration() {
        this.home = Home.getHome();
        this.persons =  new ArrayList<>();
        this.pets =  new ArrayList<>();
        this.transports = new ArrayList<>();
    }

    @Override
    public void acceptVisitor(ReportVisitor visitor) {
        visitor.visit(this);
    }

    public void addTransport(Transport transport) {
        Objects.requireNonNull(transport);
        transports.add(transport);
    }

    public void addPerson(Person person) {
        Objects.requireNonNull(person);
        persons.add(person);
    }

    public void addPet(Pet pet) {
        Objects.requireNonNull(pet);
        pets.add(pet);
    }

    public void addWindow(Window window) {
        Objects.requireNonNull(window);
        if (windows == null) windows = new ArrayList<>();
        windows.add(window);
    }

    public void addConditioner(AirConditioner airConditioner) {
        Objects.requireNonNull(airConditioner);
        if (airConditioners == null) airConditioners = new ArrayList<>();
        airConditioners.add(airConditioner);
    }

    public void addAirPurifier(AirPurifier airPurifier) {
        Objects.requireNonNull(airPurifier);
        if (airPurifiers == null) airPurifiers = new ArrayList<>();
        airPurifiers.add(airPurifier);
    }

    public void addCamera(Camera camera) {
        Objects.requireNonNull(camera);
        if (cameras == null) cameras = new ArrayList<>();
        cameras.add(camera);
    }

    public void addCoffeeMachine(CoffeeMachine coffeeMachine) {
        Objects.requireNonNull(coffeeMachine);
        if (coffeeMachines == null) coffeeMachines = new ArrayList<>();
        coffeeMachines.add(coffeeMachine);
    }

    public void addDrinkerForPet(DrinkerForPet drinkerForPet) {
        Objects.requireNonNull(drinkerForPet);
        if (drinkerForPets == null) drinkerForPets = new ArrayList<>();
        drinkerForPets.add(drinkerForPet);
    }

    public void addFeederForPet(FeederForPet feederForPet) {
        Objects.requireNonNull(feederForPet);
        if (feederForPets == null) feederForPets = new ArrayList<>();
        feederForPets.add(feederForPet);
    }

    public void addLamp(Lamp lamp) {
        Objects.requireNonNull(lamp);
        if (lamps == null) lamps = new ArrayList<>();
        lamps.add(lamp);
    }

    public void addMusicCenter(MusicCenter musicCenter) {
        Objects.requireNonNull(musicCenter);
        if (musicCenters == null) musicCenters = new ArrayList<>();
        musicCenters.add(musicCenter);
    }

    public void addPC(PC pc) {
        Objects.requireNonNull(pc);
        if (pcs == null) pcs = new ArrayList<>();
        pcs.add(pc);
    }

    public void addRefrigerator(Refrigerator refrigerator) {
        Objects.requireNonNull(refrigerator);
        if (refrigerators == null) refrigerators = new ArrayList<>();
        refrigerators.add(refrigerator);
    }

    public void addShower(Shower shower) {
        Objects.requireNonNull(shower);
        if (showers == null) showers = new ArrayList<>();
        showers.add(shower);
    }

    public void addTV(TV TV) {
        Objects.requireNonNull(TV);
        if (TVS == null) TVS = new ArrayList<>();
        TVS.add(TV);
    }

    public void addVacuumCleaner(VacuumCleaner vacuumCleaner) {
        Objects.requireNonNull(vacuumCleaner);
        if (cleaners == null) cleaners = new ArrayList<>();
        cleaners.add(vacuumCleaner);
    }

    public void addHumiditySensor(HumiditySensor sensor) {
        Objects.requireNonNull(sensor);
        if (humiditySensors == null) humiditySensors = new ArrayList<>();
        humiditySensors.add(sensor);
    }

    public void addLightSensor(LightSensor sensor) {
        Objects.requireNonNull(sensor);
        if (lightSensors == null) lightSensors = new ArrayList<>();
        lightSensors.add(sensor);
    }

    public void addSmokeSensor(SmokeSensor sensor) {
        Objects.requireNonNull(sensor);
        if (smokeSensors == null) smokeSensors = new ArrayList<>();
        smokeSensors.add(sensor);
    }

    public void addTemperatureSensor(TemperatureSensor sensor) {
        Objects.requireNonNull(sensor);
        if (temperatureSensors == null) temperatureSensors = new ArrayList<>();
        temperatureSensors.add(sensor);
    }

    /**
     * If the configuration of the house does not contain a room like this id, then returns null.
     *
     * @param id Room's id.
     * @return   Room by id.
     */
    public Room getRoomByID(int id) {
        for (Floor f: home.getFloors()) {
            for (Room r: f.getRooms()) {
                if (r != null && r.getId() == id) {
                    return r;
                }
            }
        }
        return null;
    }
}