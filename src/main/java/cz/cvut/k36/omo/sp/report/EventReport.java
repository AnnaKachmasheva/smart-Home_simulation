package cz.cvut.k36.omo.sp.report;

import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.device.devices.*;
import cz.cvut.k36.omo.sp.model.event.Event;
import cz.cvut.k36.omo.sp.model.home.*;
import cz.cvut.k36.omo.sp.model.inhabitant.Inhabitant;
import cz.cvut.k36.omo.sp.model.inhabitant.person.Person;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.Pet;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EventReport implements ReportVisitor {

    private static final Logger LOGGER = LogManager.getLogger(EventReport.class.getName());

    private int numberConfig;
    private String dateTime;

    public EventReport(int numberConfig, String dateTime) {
        this.numberConfig = numberConfig;
        this.dateTime = dateTime;
    }

    @Override
    public void visit(HomeConfiguration configuration) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("reports/" + numberConfig + "_EventReport_" + dateTime + ".txt"),
                    StandardCharsets.UTF_8));
            out.write(" Event Report: \n ");
            out.write(" Event Pet: \n");
            List<Pet> pets = configuration.getPets();
            if (pets!= null) {
                int i = 1;
                for (Pet pet: pets) {
                    out.write("\n  " + i ++ + "). " + inhabitantVisit(pet));
                }
            }
            out.write("\n \n Event Person: \n");
            List<Person> persons = configuration.getPersons();
            if (persons!= null) {
                int i = 1;
                for (Person person: persons) {
                    out.write("\n  " + i ++ + "). " + inhabitantVisit(person));
                }
            }
            out.write("\n \n Event Devices: \n");
            int i = 1;
            List<AirConditioner> airConditioners = configuration.getAirConditioners();
            if (airConditioners!= null) {
                for (AirConditioner device: airConditioners) {
                    out.write("\n  " + i ++ + "). " + deviceVisit(device));
                }
            }
            List<AirPurifier> airPurifiers = configuration.getAirPurifiers();
            if (airPurifiers!= null) {
                for (AirPurifier device: airPurifiers) {
                    out.write("\n  " + i ++ + "). " + deviceVisit(device));
                }
            }
            List<Camera> cameras = configuration.getCameras();
            if (cameras!= null) {
                for (Camera device: cameras) {
                    out.write("\n  " + i ++ + "). " + deviceVisit(device));
                }
            }
            List<CoffeeMachine> machines = configuration.getCoffeeMachines();
            if (machines!= null) {
                for (CoffeeMachine device: machines) {
                    out.write("\n  " + i ++ + "). " + deviceVisit(device));
                }
            }
            List<DrinkerForPet> drinkers = configuration.getDrinkerForPets();
            if (drinkers!= null) {
                for (DrinkerForPet device: drinkers) {
                    out.write("\n  " + i++  + "). " + deviceVisit(device));
                }
            }
            List<FeederForPet> feeders = configuration.getFeederForPets();
            if (feeders!= null) {
                for (FeederForPet device: feeders) {
                    out.write("\n  " + i++ + "). " + deviceVisit(device));
                }
            }
            List<Lamp> lamps = configuration.getLamps();
            if (lamps!= null) {
                for (Lamp device: lamps) {
                    out.write("\n  " + i++ + "). " + deviceVisit(device));
                }
            }
            List<MusicCenter> centers = configuration.getMusicCenters();
            if (centers!= null) {
                for (MusicCenter device: centers) {
                    out.write("\n  " + i++ + "). " + deviceVisit(device));
                }
            }
            List<PC> pcs = configuration.getPcs();
            if (pcs!= null) {
                for (PC device: pcs) {
                    out.write("\n  " + i++ + "). " + deviceVisit(device));
                }
            }
            List<Refrigerator> refrigerators = configuration.getRefrigerators();
            if (refrigerators!= null) {
                for (Refrigerator device: refrigerators) {
                    out.write("\n  " + i++ + "). " + deviceVisit(device));
                }
            }
            List<Shower> showers = configuration.getShowers();
            if (showers!= null) {
                for (Shower device: showers) {
                    out.write("\n  " + i++ + "). " + deviceVisit(device));
                }
            }
            List<TV> tvs = configuration.getTVS();
            if (tvs!= null) {
                for (TV device: tvs) {
                    out.write("\n  " + i++ + "). " + deviceVisit(device));
                }
            }
            List<VacuumCleaner> cleaners = configuration.getCleaners();
            if (cleaners != null) {
                for (VacuumCleaner device: cleaners) {
                    out.write("\n  " + i++ + "). " + deviceVisit(device));
                }
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

    private String deviceVisit(Device device) {
        StringBuilder s = new StringBuilder();
        List<Event> events = device.getEvents();
        s.append(" ").append(device).append("\n ");
        if (events != null) {
            List<Event> events1 = events.stream()
                    .sorted(Comparator
                            .comparing(Event::getType)
                            .thenComparing(Event::getContext))
                    .collect(Collectors.toList());
            for (Event event: events1) {
                s.append(event).append("\n ");
            }
            return s.toString();
        }
        return null;
    }

    private String inhabitantVisit(Inhabitant inhabitant) {
        StringBuilder s = new StringBuilder();
        List<Event> events = inhabitant.getEvents();
        s.append(" ").append(inhabitant).append("\n ");
        if (events != null) {
            List<Event> events1 = events.stream()
                    .sorted(Comparator
                            .comparing(Event::getType)
                            .thenComparing(Event::getContext))
                    .collect(Collectors.toList());
            for (Event event: events1) {
                s.append(event).append("\n ");
            }
            return s.toString();
        }
        return null;
    }
}
