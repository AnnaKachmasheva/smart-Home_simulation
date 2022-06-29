package cz.cvut.k36.omo.sp.report;

import cz.cvut.k36.omo.sp.model.event.Event;
import cz.cvut.k36.omo.sp.model.home.HomeConfiguration;
import cz.cvut.k36.omo.sp.model.inhabitant.person.ActionTypePerson;
import cz.cvut.k36.omo.sp.model.inhabitant.person.AgeGroup;
import cz.cvut.k36.omo.sp.model.inhabitant.person.Person;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.ActionTypePet;
import cz.cvut.k36.omo.sp.model.inhabitant.pet.Pet;
import cz.cvut.k36.omo.sp.pattern.proxy.DownloadManualProxyImpl;
import cz.cvut.k36.omo.sp.utils.Constants;
import cz.cvut.k36.omo.sp.utils.Entity;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActivityAndUsageReport {

    private static final Logger LOGGER = LogManager.getLogger(DownloadManualProxyImpl.class.getName());

    private int numberConfig;
    private final String dateTime;
    @Setter
    private LocalDateTime currentTime;

    public ActivityAndUsageReport(int numberConfig, String dateTime) {
        this.numberConfig = numberConfig;
        this.dateTime = dateTime;
    }

    public void visit(HomeConfiguration configuration) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("reports/" + numberConfig + "_ActivityAndUsageReport_" + dateTime + ".txt"),
                    StandardCharsets.UTF_8));
            out.write(" Pets activities: \n");
            List<Pet> pets = configuration.getPets();
            if (pets != null) {
                int i = 1;
                for (Pet pet : pets) {
                    out.write("  " + i++ + ". " + pet.getType() + " " + pet.getName() + " \n");
                    List<Event> events = pet.getEvents();
                    if (events != null) {
                        for (Event event : events) {
                            if (event.getEndTime() == null)
                                event.setEndTime(currentTime);
                        }
                        if (pet.getActions().contains(ActionTypePet.WALK)) {
                            out.write("-> Walked with person " + getTimeActivities(events, Constants.PET_PERSON_WALK) + "\n");
                            out.write("   Persons:" + getTargetByContext(events, Constants.PET_PERSON_WALK) + "\n");
                        }
                        if (pet.getActions().contains(ActionTypePet.PLAY)) {
                            out.write("-> Played alone " + getTimeActivities(events, Constants.PET_ALONE_PLAY) + "\n");
                            out.write("-> Played with person " + getTimeActivities(events, Constants.PET_PERSON_PLAY) + "\n");
                            out.write("   Persons:" + getTargetByContext(events, Constants.PET_PERSON_PLAY) + "\n");
                        }
                        out.write("-> Slept " + getTimeActivities(events, Constants.PET_SLEEP) + "\n");
                        out.write("-> Use devises " + getTimeActivities(events, Constants.INHABITANT_DEVICE_USE) + "\n");
                        out.write("   Devises:" + getTargetByContext(events, Constants.INHABITANT_DEVICE_USE) + "\n");
                    }
                }
            }
            out.write("\n Persons activities: \n");
            List<Person> persons = configuration.getPersons();
            if (persons != null) {
                int i = 1;
                for (Person person : persons) {
                    out.write("  " + i++ + ". " + person.getAgeGroup() + " " + person.getName() + " \n");
                    List<Event> events = person.getEvents();
                    if (events != null) {
                        for (Event event : events) {
                            if (event.getEndTime() == null) event.setEndTime(currentTime);
                        }
                        out.write("-> Slept " + getTimeActivities(events, Constants.PERSON_SLEEP) + "\n");
                        if (person.getActions().contains(ActionTypePerson.USAGE_AUTO)) {
                            out.write("-> Used the car for " + getTimeActivities(events, Constants.PERSON_AUTO_USE) + "\n");
                            out.write("   Cars: " + getTargetByContext(events, Constants.PERSON_AUTO_USE) + "\n");
                            out.write("   Was waiting in line to use the car " + getTimeActivities(events, Constants.PERSON_WAIT_AUTO) + "\n");
                        }
                        if (person.getAgeGroup() == AgeGroup.BABY) {
                            out.write("-> Ate " + getTimeActivities(events, Constants.BABY_PERSON_EAT) + "\n");
                            out.write("   Person: " + getTargetByContext(events, Constants.BABY_PERSON_EAT) + "\n");
                            out.write("-> Play with person " + getTimeActivities(events, Constants.BABY_PERSON_PLAY) + "\n");
                            out.write("   Persons:" + getTargetByContext(events, Constants.BABY_PERSON_PLAY) + "\n");
                            out.write("-> Walked with person " + getTimeActivities(events, Constants.BABY_PERSON_WALK) + "\n");
                            out.write("   Person: " + getTargetByContext(events, Constants.BABY_PERSON_WALK) + "\n");
                        } else {
                            out.write("-> Walked with pet " + getTimeActivities(events, Constants.PET_PERSON_WALK) + "\n");
                            out.write("   Pets: " + getSourceByContext(events, Constants.PET_PERSON_WALK) + "\n");
                            out.write("-> Played with pet " + getTimeActivities(events, Constants.PET_PERSON_PLAY) + "\n");
                            out.write("   Pets: " + getSourceByContext(events, Constants.PET_PERSON_PLAY) + "\n");
                            out.write("-> Fed the child " + getTimeActivities(events, Constants.BABY_PERSON_EAT) + "\n");
                            out.write("   Babies:" + getSourceByContext(events, Constants.BABY_PERSON_PLAY) + "\n");
                            out.write("-> Played with the babies for " + getTimeActivities(events, Constants.BABY_PERSON_PLAY) + "\n");
                            out.write("   Babies:" + getSourceByContext(events, Constants.BABY_PERSON_PLAY) + "\n");
                            out.write("-> Walked with babies " + getTimeActivities(events, Constants.BABY_PERSON_WALK) + "\n");
                            out.write("   Babies: " + getSourceByContext(events, Constants.BABY_PERSON_WALK) + "\n");
                            out.write("-> Was not at home and did not walk with anyone " + getTimeActivities(events, Constants.PERSON_GO_OUT) + "\n");
                            out.write("-> Used transports " + getTimeActivities(events, Constants.PERSON_TRANSPORT_USE) + "\n");
                            out.write("   Transport: " + getTargetByContext(events, Constants.PERSON_TRANSPORT_USE) + "\n");
                            out.write("   Was waiting in line to use the transport " + getTimeActivities(events, Constants.PERSON_WAIT_TRANSPORT) + "\n");
                            out.write("-> Used device " + getTimeActivities(events, Constants.INHABITANT_DEVICE_USE) + "\n");
                            out.write("   Devices:" + getTargetByContext(events, Constants.INHABITANT_DEVICE_USE) + "\n");
                            out.write("   Was waiting in line to use the devices " + getTimeActivities(events, Constants.INHABITANT_DEVICE_USE) + "\n");
                            out.write("-> Fixed device " + getTimeActivities(events, Constants.PERSON_DEVICE_FIX) + "\n");
                            out.write("-> Add content to device " + getTimeActivities(events, Constants.PERSON_DEVICE_ADD_CONTENT) + "\n");
                        }
                    }
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

    private String getTimeActivities(List<Event> events, String context) {
        List<Event> eventsList = events.stream()
                                        .filter(event -> event.getContext().equals(context))
                                        .collect(Collectors.toList());
        long minutes = 0;
        for (Event event : eventsList)
            minutes += ChronoUnit.MINUTES.between(event.getStartTime(), event.getEndTime());
        return  minutes/60 + " hours " + minutes % 60 + " minutes.";
    }

    private String getTargetByContext(List<Event> events, String context) {
        Map<Entity, Long> eventsMap = events.stream()
                                                .filter(event -> event.getContext().equals(context))
                                                .collect(Collectors.groupingBy(Event::getTarget, Collectors.counting()));
        if (eventsMap.size() == 0) return "";
        StringBuilder s = new StringBuilder();
        for (Entity target : eventsMap.keySet()) {
            s.append(" ")
                    .append(target.toString())
                    .append(eventsMap.get(target))
                    .append(" times,");
            }
        return s.toString();
    }

    private String getSourceByContext(List<Event> events, String context) {
        Map<Entity, Long> eventsMap = events.stream()
                                            .filter(event -> event.getContext().equals(context))
                                            .collect(Collectors.groupingBy(Event::getSource, Collectors.counting()));
        if (eventsMap.size() == 0) return "";
        StringBuilder s = new StringBuilder();
        for (Entity source : eventsMap.keySet()) {
            s.append(" ")
                    .append(source.toString())
                    .append(eventsMap.get(source))
                    .append(" times,");
        }
        return s.toString();
    }
}