package cz.cvut.k36.omo.sp.pattern.observer;

import cz.cvut.k36.omo.sp.model.inhabitant.Inhabitant;
import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.home.HomeConfiguration;
import cz.cvut.k36.omo.sp.model.home.Window;
import cz.cvut.k36.omo.sp.model.sensor.Sensor;
import cz.cvut.k36.omo.sp.model.transport.Transport;

import java.util.List;

public class ManagerObserver {

    private HomeConfiguration configuration;

    public ManagerObserver(HomeConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     *  Creation of subjects. They are sensors.
     *  Adding observers to subjects such as devices, inhabitants, transports, windows.
     */
    public void generateSubjects() {
        addDevice(configuration.getHumiditySensors(), configuration.getAirPurifiers());
        addWindow(configuration.getHumiditySensors(), configuration.getWindows());
        addDevice(configuration.getTemperatureSensors(), configuration.getAirConditioners());
        addWindow(configuration.getTemperatureSensors(), configuration.getWindows());
        addDevice(configuration.getLightSensors(), configuration.getLamps());
        addDevice(configuration.getSmokeSensors(), configuration.getAirConditioners());
        addDevice(configuration.getSmokeSensors(), configuration.getAirPurifiers());
        addDevice(configuration.getSmokeSensors(), configuration.getCameras());
        addDevice(configuration.getSmokeSensors(), configuration.getCoffeeMachines());
        addDevice(configuration.getSmokeSensors(), configuration.getDrinkerForPets());
        addDevice(configuration.getSmokeSensors(), configuration.getFeederForPets());
        addDevice(configuration.getSmokeSensors(), configuration.getLamps());
        addDevice(configuration.getSmokeSensors(), configuration.getMusicCenters());
        addDevice(configuration.getSmokeSensors(), configuration.getPcs());
        addDevice(configuration.getSmokeSensors(), configuration.getRefrigerators());
        addDevice(configuration.getSmokeSensors(), configuration.getShowers());
        addDevice(configuration.getSmokeSensors(), configuration.getTVS());
        addDevice(configuration.getSmokeSensors(), configuration.getCleaners());
        addInhabitant(configuration.getSmokeSensors(), configuration.getPersons());
        addInhabitant(configuration.getSmokeSensors(), configuration.getPets());
        addTransport(configuration.getSmokeSensors(), configuration.getTransports());
    }

    private void addDevice(List<? extends Sensor> sensors, List<? extends Device> devices) {
        if (sensors != null) {
            for (Sensor sensor : sensors) {
                if (devices != null)
                    for (Device device : devices)
                        sensor.addSubscriber(device);
            }
        }
    }

    private void addWindow(List<? extends Sensor> sensors, List<Window> windows) {
        if (sensors != null) {
            for (Sensor sensor : sensors) {
                if (windows != null)
                    for (Window window : windows)
                        sensor.addSubscriber(window);
            }
        }
    }

    private void addInhabitant(List<? extends Sensor> sensors, List<? extends Inhabitant> inhabitants) {
        if (sensors != null) {
            for (Sensor sensor : sensors) {
                if (inhabitants != null)
                    for (Inhabitant inhabitant : inhabitants)
                        sensor.addSubscriber(inhabitant);
            }
        }
    }

    private void addTransport(List<? extends Sensor> sensors, List<Transport> transports) {
        if (sensors != null) {
            for (Sensor sensor : sensors) {
                if (transports != null)
                    for (Transport transport : transports)
                        sensor.addSubscriber(transport);
            }
        }
    }
}