package cz.cvut.k36.omo.sp.report;

import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.device.devices.*;
import cz.cvut.k36.omo.sp.model.home.*;
import cz.cvut.k36.omo.sp.model.inhabitant.Inhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.person.Person;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.Pet;
import cz.cvut.k36.omo.sp.model.sensor.Sensor;
import cz.cvut.k36.omo.sp.model.sensor.sensors.HumiditySensor;
import cz.cvut.k36.omo.sp.model.sensor.sensors.LightSensor;
import cz.cvut.k36.omo.sp.model.sensor.sensors.SmokeSensor;
import cz.cvut.k36.omo.sp.model.sensor.sensors.TemperatureSensor;
import cz.cvut.k36.omo.sp.model.transport.Transport;
import cz.cvut.k36.omo.sp.pattern.visitor.ReportVisitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class HouseConfigurationReport implements ReportVisitor {

    private static final Logger LOGGER = LogManager.getLogger(HouseConfigurationReport.class.getName());

    private int numberConfig;
    private String dateTime;
    private int number = 0; // variable for numbering the list

    /**
     * @param dateTime date and time of the report
     */
    public HouseConfigurationReport(int numberConfig, String dateTime) {
        this.numberConfig = numberConfig;
        this.dateTime = dateTime;
    }

    @Override
    public void visit(HomeConfiguration configuration) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("reports/" + numberConfig + "_HouseConfReport_" + dateTime + ".txt"),
                    StandardCharsets.UTF_8));
            out.write(visit(Home.getHome()));
            out.write(" Floors: \n");
            for (Floor floor : configuration.getHome().getFloors()) {
                out.write(visit(floor));
                for (Room room : floor.getRooms()) {
                    out.write(visit(room));
                    List<Window> windows = configuration.getWindows().stream()
                            .filter(window -> window.getRoom().getId() == room.getId()).collect(Collectors.toList());
                    if (!windows.isEmpty()) {
                        out.write("   Windows: \n");
                        for (Window window : windows) {
                            out.write(visit(window));
                        }
                    }
                    List<AirConditioner> allConditioners = configuration.getAirConditioners();
                    if (allConditioners != null) {
                        List<AirConditioner> conditioners = allConditioners.stream()
                                .filter(airConditioner -> airConditioner.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!conditioners.isEmpty()) {
                            int i = 1;
                            out.write("   Air conditioners: \n");
                            for (AirConditioner conditioner : conditioners) {
                                out.write("  " + i++ + ". " + visit(conditioner));
                            }
                        }
                    }
                    List<AirPurifier> allPurifiers =  configuration.getAirPurifiers();
                    if (allPurifiers != null) {
                        List<AirPurifier> purifiers = allPurifiers.stream()
                                .filter(airPurifier -> airPurifier.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!purifiers.isEmpty()) {
                            int i = 1;
                            out.write("   Air Purifiers: \n");
                            for (AirPurifier purifier : purifiers) {
                                out.write("  " + i++ + ". " + visit(purifier));
                            }
                        }
                    }
                    List<Camera> allCameras =  configuration.getCameras();
                    if (allCameras != null) {
                        List<Camera> cameras = allCameras.stream()
                                .filter(camera -> camera.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!cameras.isEmpty()) {
                            int i = 1;
                            out.write("   Cameras: \n");
                            for (Camera camera : cameras) {
                                out.write("  " + i++ + ". " + visit(camera));
                            }
                        }
                    }
                    List<CoffeeMachine> allCoffeeMachines =  configuration.getCoffeeMachines();
                    if (allCoffeeMachines != null) {
                        List<CoffeeMachine> machines = allCoffeeMachines.stream()
                                .filter(coffeeMachine -> coffeeMachine.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!machines.isEmpty()) {
                            int i = 1;
                            out.write("   Coffee machines: \n");
                            for (CoffeeMachine coffeeMachine : machines) {
                                out.write("  " + i++ + ". " + visit(coffeeMachine));
                            }
                        }
                    }
                    List<DrinkerForPet> allDrinkerForPets =  configuration.getDrinkerForPets();
                    if (allDrinkerForPets != null) {
                        List<DrinkerForPet> drinkers = allDrinkerForPets.stream()
                                .filter(drinkerForPet -> drinkerForPet.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!drinkers.isEmpty()) {
                            int i = 1;
                            out.write("   Drinkers: \n");
                            for (DrinkerForPet drinker : drinkers) {
                                out.write("  " + i++ + ". " + visit(drinker));
                            }
                        }
                    }
                    List<FeederForPet> allFeederForPets =  configuration.getFeederForPets();
                    if (allFeederForPets != null) {
                        List<FeederForPet> feeders = allFeederForPets.stream()
                                .filter(feederForPet -> feederForPet.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!feeders.isEmpty()) {
                            int i = 1;
                            out.write("   Feeders: \n");
                            for (FeederForPet feederForPet : feeders) {
                                out.write("  " + i++ + ". " + visit(feederForPet));
                            }
                        }
                    }
                    List<Lamp> allLamps =  configuration.getLamps();
                    if (allLamps != null) {
                        List<Lamp> lamps = allLamps.stream()
                                .filter(lamp -> lamp.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!lamps.isEmpty()) {
                            int i = 1;
                            out.write("   Lamps: \n");
                            for (Lamp lamp : lamps) {
                                out.write("  " + i++ + ". " + visit(lamp));
                            }
                        }
                    }
                    List<MusicCenter> allMusicCenters =  configuration.getMusicCenters();
                    if (allMusicCenters != null) {
                        List<MusicCenter> centers = allMusicCenters.stream()
                                .filter(musicCenter -> musicCenter.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!centers.isEmpty()) {
                            int i = 1;
                            out.write("   Music Centers: \n");
                            for (MusicCenter center : centers) {
                                out.write("  " + i++ + ". " + visit(center));
                            }
                        }
                    }
                    List<PC> ALlPCs =  configuration.getPcs();
                    if (ALlPCs != null) {
                        List<PC> pcs = ALlPCs.stream()
                                .filter(pc -> pc.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!pcs.isEmpty()) {
                            int i = 1;
                            out.write("   PCs: \n");
                            for (PC pc : pcs) {
                                out.write("  " + i++ + ". " + visit(pc));
                            }
                        }
                    }
                    List<Refrigerator> ALlRefrigerators =  configuration.getRefrigerators();
                    if (ALlRefrigerators != null) {
                        List<Refrigerator> refrigerators = ALlRefrigerators.stream()
                                .filter(refrigerator -> refrigerator.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!refrigerators.isEmpty()) {
                            int i = 1;
                            out.write("   Refrigerators: \n");
                            for (Refrigerator refrigerator : refrigerators) {
                                out.write("  " + i++ + ". " + visit(refrigerator));
                            }
                        }
                    }
                    List<Shower> ALlShowers =  configuration.getShowers();
                    if (ALlShowers != null) {
                        List<Shower> showers = ALlShowers.stream()
                                .filter(shower -> shower.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!showers.isEmpty()) {
                            int i = 1;
                            out.write("   Showers: \n");
                            for (Shower shower : showers) {
                                out.write("  " + i++ + ". " + visit(shower));
                            }
                        }
                    }
                    List<TV> ALlTVs =  configuration.getTVS();
                    if (ALlTVs != null) {
                        List<TV> tvs = ALlTVs.stream()
                                .filter(tv -> tv.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!tvs.isEmpty()) {
                            int i = 1;
                            out.write("   TVs: \n");
                            for (TV tv : tvs) {
                                out.write("  " + i++ + ". " + visit(tv));
                            }
                        }
                    }
                    List<VacuumCleaner> aLlVacuumCleaners =  configuration.getCleaners();
                    if (aLlVacuumCleaners != null) {
                        List<VacuumCleaner> cleaners = aLlVacuumCleaners.stream()
                                .filter(vacuumCleaner -> vacuumCleaner.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!cleaners.isEmpty()) {
                            int i = 1;
                            out.write("   Cleaners: \n");
                            for (VacuumCleaner cleaner : cleaners) {
                                out.write("  " + i++ + ". " + visit(cleaner));
                            }
                        }
                    }
                    List<HumiditySensor> humiditySensors =  configuration.getHumiditySensors();
                    if (humiditySensors != null) {
                        List<HumiditySensor> humiditySensors1 = humiditySensors.stream()
                                .filter(humiditySensor -> humiditySensor.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!humiditySensors1.isEmpty()) {
                            int i = 1;
                            out.write("   Humidity sensors: \n");
                            for (HumiditySensor humiditySensor : humiditySensors1) {
                                out.write("  " + i++ + ". " + visit(humiditySensor));
                            }
                        }
                    }
                    List<LightSensor> lightSensors =  configuration.getLightSensors();
                    if (lightSensors != null) {
                        List<LightSensor> lightSensors1 = lightSensors.stream()
                                .filter(lightSensor -> lightSensor.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!lightSensors1.isEmpty()) {
                            int i = 1;
                            out.write("   Light sensors: \n");
                            for (LightSensor lightSensor : lightSensors1) {
                                out.write("  " + i++ + ". " + visit(lightSensor));
                            }
                        }
                    }
                    List<SmokeSensor> smokeSensors =  configuration.getSmokeSensors();
                    if (smokeSensors != null) {
                        List<SmokeSensor> smokeSensors1 = smokeSensors.stream()
                                .filter(smokeSensor -> smokeSensor.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!smokeSensors1.isEmpty()) {
                            int i = 1;
                            out.write("   Smoke sensors: \n");
                            for (SmokeSensor smokeSensor : smokeSensors1) {
                                out.write("  " + i++ + ". " + visit(smokeSensor));
                            }
                        }
                    }
                    List<TemperatureSensor> temperatureSensors =  configuration.getTemperatureSensors();
                    if (temperatureSensors != null) {
                        List<TemperatureSensor> temperatureSensors1 = temperatureSensors.stream()
                                .filter(temperatureSensor -> temperatureSensor.getRoom().getId() == room.getId())
                                .collect(Collectors.toList());
                        if (!temperatureSensors1.isEmpty()) {
                            int i = 1;
                            out.write("   Radon sensors: \n");
                            for (TemperatureSensor temperatureSensor : temperatureSensors1) {
                                out.write("  " + i++ + ". " + visit(temperatureSensor));
                            }
                        }
                    }
                }
            }
            out.write("\n Persons: \n");
            for (Person person : configuration.getPersons()) {
                number++;
                out.write(visit(person));
            }
            number = 0;
            out.write("\n Pets: \n");
            for (Pet pet : configuration.getPets()) {
                number++;
                out.write(visit(pet));
            }
            number = 0;
            out.write("\n Transports: \n");
            for (Transport transport : configuration.getTransports()) {
                number++;
                out.write(visit(transport));
            }
        } catch (IOException ex) {
            LOGGER.error("Error in HouseConfigurationReport");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    LOGGER.error("Failed to close the output stream in" + getClass());
                }
            }
        }
    }

    public String visit(Home home) {
        return "  Home \n";
    }

    public String visit(Floor floor) {
        StringBuilder s = new StringBuilder();
        return s.append(floor.getId())
                .append(") Count rooms: ")
                .append(floor.getRooms().size())
                .append(".\n").toString();
    }

    public String visit(Room room) {
        StringBuilder s = new StringBuilder();
        return  s.append(" -> Room id ")
                .append(room.getId())
                .append(".\n").toString();
    }

    public String visit(Window window) {
        StringBuilder s = new StringBuilder();
        return s.append("  ")
                .append(window.getId())
                .append(". State: ")
                .append(window.getState())
                .append("\n").toString();
    }

    private String visit(Device device){
        StringBuilder s = new StringBuilder();
        return s.append("ID ")
                .append(device.getId())
                .append(". ")
                .append(device.getName())
                .append(". State: ")
                .append(device.getState())
                .append(device.getCurrentUser() == null ? "" : ". Current user: " + device.getCurrentUser().getName())
                .append(device.getExpects() == null || device.getExpects().size() == 0 ? "" : getInhabitantInLine(device.getExpects()))
                .append("\n").toString();
    }

    private String visit(Sensor sensor){
        StringBuilder s = new StringBuilder();
        return s.append("ID ")
                .append(sensor.getId())
                .append(". ")
                .append(sensor.getName())
                .append(". Type: ")
                .append(sensor.getType())
                .append(". Data: ")
                .append(sensor.getReadings())
                .append(" \n").toString();
    }

    public String visit(Person person) {
        StringBuilder s = new StringBuilder();
        return s.append(number)
                .append(". ")
                .append(person.getAgeGroup())
                .append(" ")
                .append(person.getName())
                .append(". State: ")
                .append(person.getState())
                .append("\n").toString();
    }

    public String visit(Pet pet) {
        StringBuilder s = new StringBuilder();
        return s.append(number)
                .append(". ")
                .append(pet.getType())
                .append(" ")
                .append(pet.getName())
                .append(". State: ")
                .append(pet.getState())
                .append("\n").toString();
    }

    public String visit(Transport transport) {
        StringBuilder s = new StringBuilder();
        return  s.append(number)
                .append(". ")
                .append(transport.getType())
                .append(" ")
                .append(transport.getName())
                .append(transport.getCurrentUser() == null ? "" : ". Current user: " + transport.getCurrentUser().getName())
                .append(transport.getExpects() == null || transport.getExpects().size() == 0 ? "" :
                                    getInhabitantInLine(transport.getExpects().stream().toList()))
                .append(".\n").toString();
    }

    private String getInhabitantInLine(List<? extends Inhabitant> persons) {
        String str = persons.stream()
                      .map(Inhabitant::getName)
                      .reduce(". Waiting in line:", (s, s2) -> s.concat(" ").concat(s2 + ","));
        return str.substring(0, str.length() - 1);
    }
}