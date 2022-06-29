package cz.cvut.k36.omo.sp.utils;

import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.home.Floor;
import cz.cvut.k36.omo.sp.model.home.HomeConfiguration;
import cz.cvut.k36.omo.sp.model.home.Room;
import cz.cvut.k36.omo.sp.model.home.Window;
import cz.cvut.k36.omo.sp.model.inhabitant.StateInhabitant;
import cz.cvut.k36.omo.sp.model.sensor.Sensor;
import cz.cvut.k36.omo.sp.pattern.builder.DeviceBuilder;
import cz.cvut.k36.omo.sp.pattern.builder.PersonBuilder;
import cz.cvut.k36.omo.sp.pattern.builder.PetBuilder;
import cz.cvut.k36.omo.sp.pattern.builder.TransportBuilder;
import cz.cvut.k36.omo.sp.pattern.factory.FactorySensor;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

@Getter
public class LoadConfiguration {

    private static final Logger LOGGER = LogManager.getLogger(LoadConfiguration.class.getName());

    private static final String PATH = Objects.requireNonNull(LoadConfiguration.class.getResource("/")).getPath() + "/configs",
                                FIRST = "/firstСonfig",
                                SECOND = "/secondСonfig";

    private final HomeConfiguration homeConfiguration;
    private final FactorySensor factorySensor;

    public LoadConfiguration() {
        this.homeConfiguration = new HomeConfiguration();
        this.factorySensor = new FactorySensor();
    }

    /**
     * Loads all JSON files from the configuration.
     *
     * @param numberConfig int (1 or 2) configuration's number
     */
    public void loadAllJson(int numberConfig) {
        String nameConfig;
        if(numberConfig == 1) nameConfig = FIRST;
        else if(numberConfig == 2) nameConfig = SECOND;
        else {
            LOGGER.warn("Configuration with this number \"" + numberConfig +"\" does not exist.");
            return;
        }

        loadHome(nameConfig);
        loadDevices(nameConfig);
        loadSensors(nameConfig);
        loadPersons(nameConfig);
        loadPets(nameConfig);
        loadTransport(nameConfig);
    }

    private void loadHome(String nameConfig) {
        JSONArray array = load(nameConfig, "/home.json");
        int idRoom = 1;
        for (Object o: array) {
            JSONObject homeJson = (JSONObject) o;
            int idFloor = (int)(long)homeJson.get("floor");
            Floor floor = new Floor(idFloor);
            JSONArray roomArray = (JSONArray)homeJson.get("rooms");

            for (Object ob: roomArray) {
                JSONObject roomJson = (JSONObject) ob;
                int countWindows = (int)(long)roomJson.get("windowsCount");
                Room room = new Room(idRoom++);
                for (int i = 1; i <= countWindows; i++) {
                    Window window = new Window(i, room);
                    homeConfiguration.addWindow(window);
                }
                floor.addRoom(room);
            }
            homeConfiguration.getHome().addFloor(floor);

            LOGGER.info("Created " + floor);
        }
    }

    private void loadDevices(String nameConfig) {
        JSONArray array = load(nameConfig, "/devices.json");
        int id = 1;
        for (Object o: array) {
            JSONObject deviceJson = (JSONObject) o;
            int count = (int)(long)deviceJson.get("count");
            Room room = homeConfiguration.getRoomByID((int)(long)deviceJson.get("idRoom"));
            for (int i = 0; i < count; i++) {
                if (room != null) {
                    DeviceBuilder builder = new DeviceBuilder(homeConfiguration);
                    Device device = builder.withId(id++)
                                            .withName((String)deviceJson.get("name"))
                                            .withType((String)deviceJson.get("type"))
                                            .inRoom(room)
                                            .build();
                    if (device != null)
                        LOGGER.info("Created " + device.toStringForLog()  + ".");
                }
            }
        }
    }

    private void loadSensors(String nameConfig) {
        JSONArray array = load(nameConfig, "/sensors.json");
        int id = 1;
        for (Object o: array) {
            JSONObject sensorJson = (JSONObject) o;
            int count = (int)(long)sensorJson.get("count");
            Room room = homeConfiguration.getRoomByID((int)(long)sensorJson.get("idRoom"));
            for (int i = 0; i < count; i++) {
                if (room != null) {
                    String name = (String)sensorJson.get("name");
                    String type = (String)sensorJson.get("type");
                    Sensor sensor = factorySensor.createSensor(id++, type, name, room, homeConfiguration);

                    LOGGER.info("Created " + sensor + ".");
                }
            }
        }
    }


    /**
     * Creating new persons.
     * According to age, each person is assigned an age group and rights(Actions type).
     * If a person has a driver's license, then the ability to drive an auto will be added to him.
     *
     * @param nameConfig String config's name (FIRST or SECOND)
     */
    private void loadPersons(String nameConfig) {
            JSONArray array = load(nameConfig, "/person.json");
            for (Object o: array) {
                JSONObject personJson = (JSONObject) o;
                PersonBuilder builder = new PersonBuilder();
                homeConfiguration.addPerson(builder.withName((String) personJson.get("name"))
                                                    .withAgeGroup((long)personJson.get("age"))
                                                    .withState(StateInhabitant.IDLE)
                                                    .withDrivingLicense(personJson.get("haveDrivingLicense") != null
                                                                        && (boolean) personJson.get("haveDrivingLicense"))
                                                    .build());
                LOGGER.info("Created " + builder.build().toStringForLog());
        }
    }

    /**
     * Creating new pets.
     *
     * @param nameConfig String config's name (FIRST or SECOND)
     */
    private void loadPets(String nameConfig) {
        JSONArray array = load(nameConfig, "/pet.json");
        for (Object o: array) {
            JSONObject petJson = (JSONObject) o;
            PetBuilder builder = new PetBuilder();
            homeConfiguration.addPet(builder.withName((String)petJson.get("name"))
                                            .withType((String)petJson.get("typePet"))
                                            .withState(StateInhabitant.IDLE)
                                            .build());

            LOGGER.info("Created " + builder.build().toStringForLog());
        }
    }

    /**
     * Creating new transports.
     *
     * @param nameConfig String config's name (FIRST or SECOND)
     */
    private void loadTransport(String nameConfig) {
        JSONArray array = load(nameConfig, "/transport.json");
        for (Object o: array) {
            JSONObject transportJson = (JSONObject) o;
            TransportBuilder builder = new TransportBuilder();
            homeConfiguration.addTransport(builder.withName((String)transportJson.get("name"))
                                                  .withType((String)transportJson.get("typeTransport"))
                                                  .withCurrentUser(null)
                                                  .build());

            LOGGER.info("Created " + builder.build().toStringForLog());
        }
    }

    /**
     * Returns an ArrayList of JSON objects along the file path.
     *
     * @param nameConfig configuration's file name "firstСonfig" or "secondСonfig"
     * @param fileName file's name
     * @return ArrayList JSONArray
     */
    private JSONArray load(String nameConfig, String fileName) {
        try {
            JSONParser parser = new JSONParser();
            return (JSONArray)parser.parse(new FileReader(PATH + nameConfig + fileName));
        } catch (IOException | ParseException e) {
            LOGGER.error("An error occurred while loading the file \"" + fileName + "\" with this name.");
            return null;
        }
    }
}