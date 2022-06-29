package cz.cvut.k36.omo.sp.report;

import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.device.devices.*;
import cz.cvut.k36.omo.sp.model.device.energy.EventEnergy;
import cz.cvut.k36.omo.sp.model.home.HomeConfiguration;
import cz.cvut.k36.omo.sp.pattern.visitor.ReportVisitor;
import cz.cvut.k36.omo.sp.utils.Constants;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ConsumptionReport implements ReportVisitor {

    private static final Logger LOGGER = LogManager.getLogger(ConsumptionReport.class.getName());

    @Setter
    private LocalDateTime currentTime;
    private int numberConfig;
    private final String dateTime;
    private final NumberFormat f = new DecimalFormat("#.00");

    public ConsumptionReport(int numberConfig, String dateTime) {
        this.numberConfig = numberConfig;
        this.dateTime = dateTime;
    }

    @Override
    public void visit(HomeConfiguration configuration) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("reports/" + numberConfig + "_ConsumptionReport_" + dateTime + ".txt"),
                    StandardCharsets.UTF_8));
            out.write("Energy costs. Devices: \n");
            List<AirConditioner> conditioners = configuration.getAirConditioners();
            if (conditioners != null) {
                out.write("\n Conditioner: ");
                int i = 1;
                for (AirConditioner airConditioner : conditioners) {
                    out.write(" " + visitDevice(airConditioner) + getCalculationElectricity(airConditioner));
                }
            }
            List<AirPurifier> purifiers = configuration.getAirPurifiers();
            if (purifiers != null) {
                out.write("\n \n Air purifiers: ");
                int i = 1;
                for (AirPurifier purifier : purifiers) {
                    out.write(" " + visitDevice(purifier) + getCalculationElectricity(purifier));
                }
            }
            List<Camera> cameras = configuration.getCameras();
            if (cameras != null) {
                out.write("\n \n Cameras: ");
                int i = 1;
                for (Camera camera : cameras) {
                    out.write(" " + visitDevice(camera) + getCalculationElectricity(camera));
                }
            }
            List<CoffeeMachine> coffeeMachines = configuration.getCoffeeMachines();
            if (coffeeMachines != null) {
                out.write("\n \n CoffeeMachines: ");
                int i = 1;
                for (CoffeeMachine machine : coffeeMachines) {
                    out.write(" " + visitDevice(machine) + getCalculationElectricity(machine));
                }
            }
            List<DrinkerForPet> drinkers = configuration.getDrinkerForPets();
            if (drinkers != null) {
                out.write("\n \n DrinkerForPets: ");
                int i = 1;
                for (DrinkerForPet drinker : drinkers) {
                    out.write(" " + visitDevice(drinker) + getCalculationElectricity(drinker));
                }
            }
            List<FeederForPet> feeders = configuration.getFeederForPets();
            if (feeders != null) {
                out.write("\n \n FeederForPets: ");
                int i = 1;
                for (FeederForPet feeder : feeders) {
                    out.write(" " + visitDevice(feeder) + getCalculationElectricity(feeder));
                }
            }
            List<Lamp> lamps = configuration.getLamps();
            if (lamps != null) {
                out.write("\n \n Lamps: ");
                int i = 1;
                for (Lamp lamp : lamps) {
                    out.write(" " + visitDevice(lamp) + getCalculationElectricity(lamp));
                }
            }
            List<MusicCenter> centers = configuration.getMusicCenters();
            if (centers != null) {
                out.write("\n \n MusicCenters: ");
                int i = 1;
                for (MusicCenter center : centers) {
                    out.write(" " + visitDevice(center) + getCalculationElectricity(center));
                }
            }
            List<PC> pcs = configuration.getPcs();
            if (pcs != null) {
                out.write("\n \n PCs: ");
                int i = 1;
                for (PC pc : pcs) {
                    out.write(" " + visitDevice(pc) + getCalculationElectricity(pc));
                }
            }
            List<Refrigerator> refrigerators = configuration.getRefrigerators();
            if (refrigerators != null) {
                out.write("\n \n Refrigerators: ");
                int i = 1;
                for (Refrigerator refrigerator : refrigerators) {
                    out.write(" " + visitDevice(refrigerator) + getCalculationElectricity(refrigerator));
                }
            }
            List<Shower> showers = configuration.getShowers();
            if (showers != null) {
                out.write("\n \n Showers: ");
                int i = 1;
                for (Shower shower : showers) {
                    out.write(" " + visitDevice(shower) + getCalculationWater(shower));
                }
            }
            List<TV> tvs = configuration.getTVS();
            if (tvs != null) {
                out.write("\n \n TVs: ");
                int i = 1;
                for (TV tv : tvs) {
                    out.write(" " + visitDevice(tv) + getCalculationElectricity(tv));
                }
            }
            List<VacuumCleaner> cleaners = configuration.getCleaners();
            if (cleaners != null) {
                out.write("\n \n VacuumCleaners: ");
                int i = 1;
                for (VacuumCleaner cleaner : cleaners) {
                    out.write(" " + visitDevice(cleaner) + getCalculationElectricity(cleaner));
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

    private String visitDevice(Device device) {
        StringBuilder s = new StringBuilder();
        return s.append("\n").append(device.getType())
                .append(" with id={")
                .append(device.getId())
                .append("} and name={")
                .append(device.getName())
                .append("}.\n Energy: ")
                .append(device.getEnergy().getType()).append("\n")
                .toString();
    }

    private String getCalculationWater(Device device) {
        StringBuilder s = new StringBuilder();
        List<EventEnergy> events = device.getEventEnergy();
        float cost = 0;
        float amount = 0;
        long timeDay;
        long timeNight;
        if (events != null) {
            for (EventEnergy event : events) {
                timeDay = getConsumptionPerDay(event);
                timeNight = getConsumptionPerNight(event);
                s.append(" ")
                        .append(timeDay)
                        .append("/ 60 * ")
                        .append(Constants.BASIC_WATER_RATE)
                        .append(" * ")
                        .append(event.getPower())
                        .append(" + ")
                        .append(timeNight)
                        .append("/ 60 * ")
                        .append(Constants.NIGHT_WATER_RATE)
                        .append(" * ")
                        .append(event.getPower())
                        .append(" +");
                cost =  cost + ((float) timeDay/ 60) * Constants.BASIC_WATER_RATE * event.getPower() +
                        ((float) timeNight/ 60) * Constants.NIGHT_WATER_RATE * event.getPower();
                amount = amount + (((float) timeDay/ 60)  + ((float) timeNight/ 60))* event.getPower();
            }
            s.deleteCharAt(s.lastIndexOf("+"))
                    .append("= ")
                    .append(f.format(cost))
                    .append("\n")
                    .append("Total energy consumed ")
                    .append(f.format(amount));
        }
        return s.toString();
    }

    private String getCalculationElectricity(Device device) {
        StringBuilder s = new StringBuilder();
        List<EventEnergy> events = device.getEventEnergy();
        float cost = 0;
        float amount = 0;
        long timeDay;
        long timeNight;
        if (events != null) {
            for (EventEnergy event : events) {
                timeDay = getConsumptionPerDay(event);
                timeNight = getConsumptionPerNight(event);
                s.append(" ")
                        .append(timeDay)
                        .append("/ 60 * ")
                        .append(Constants.BASIC_ELECTRICITY_RATE)
                        .append(" * ")
                        .append(event.getPower())
                        .append(" + ")
                        .append(timeNight)
                        .append("/ 60 * ")
                        .append(Constants.NIGHT_ELECTRICITY_RATE)
                        .append(" * ")
                        .append(event.getPower())
                        .append(" +");
                cost = cost + ((float)timeDay / 60) * Constants.BASIC_ELECTRICITY_RATE * event.getPower() +
                        ((float) timeNight / 60) * Constants.NIGHT_ELECTRICITY_RATE * event.getPower();
                amount = amount + (((float) timeDay/ 60)  + ((float) timeNight/ 60))* event.getPower();
            }
            s.deleteCharAt(s.lastIndexOf("+"))
                    .append("= ")
                    .append(f.format(cost)).append("\n")
                    .append("Total energy consumed ")
                    .append(f.format(amount));
        }
        return s.toString();
    }

    private long getConsumptionPerDay(EventEnergy event) {
        int min = 0;
        LocalTime inStartDay = event.getStart().toLocalTime();
        LocalTime inEndDay = event.getStart().toLocalTime();
        LocalTime timeStartDay = LocalTime.of(8, 59);
        LocalTime timeEndDay = LocalTime.of(23, 59);
        if (event.getEnd() == null) event.setEnd(currentTime);
        long days = Duration.between(event.getEnd(), event.getStart()).toDays();
        if (days > 0) {
            min += days * Constants.NUMBER_HOURS_DAY * 60;
            if (inStartDay.isAfter(timeStartDay))
                min += Duration.between(inStartDay, timeStartDay).toMinutes();
            if (inEndDay.isAfter(timeStartDay))
                min += Duration.between(timeEndDay, inEndDay).toMinutes();
        } else if (Duration.between(event.getEnd(), event.getStart()).toMinutes() != 0)
            min += Duration.between(timeStartDay, inEndDay).toMinutes() > 0 ? Duration.between(timeStartDay, inEndDay).toMinutes() : 0;
        return min;
    }

    private int getConsumptionPerNight(EventEnergy event) {
        int min = 0;
        if (event.getEnd() == null) event.setEnd(currentTime);
        min += Duration.between(event.getStart(), event.getEnd()).toMinutes();
        min -= getConsumptionPerDay(event);
        return min > 0 ? min : 0;
    }
}