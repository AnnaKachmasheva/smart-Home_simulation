package cz.cvut.k36.omo.sp.pattern.builder;

import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.device.devices.AirConditioner;
import cz.cvut.k36.omo.sp.model.home.HomeConfiguration;
import cz.cvut.k36.omo.sp.model.home.Room;
import cz.cvut.k36.omo.sp.pattern.factory.FactoryDevice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeviceBuilder {

    private static final Logger LOGGER = LogManager.getLogger(DeviceBuilder.class.getName());

    private int id;
    private String name;
    private String type;
    private Room room;
    private final FactoryDevice factoryDevice;
    private final HomeConfiguration configuration;

    public DeviceBuilder(HomeConfiguration configuration) {
        this.configuration = configuration;
        this.factoryDevice = new FactoryDevice();
    }

    public DeviceBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public DeviceBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public DeviceBuilder withType(String type) {
        this.type = type;
        return this;
    }

    public DeviceBuilder inRoom(Room room) {
        this.room = room;
        return this;
    }

    public Device build() {
        if (this.name == null || this.type == null || this.room == null) {
            LOGGER.warn("Some required arguments are not provided: name, type, room!");
            return null;
        }
        return factoryDevice.createDevice(this.id, this.type, this.name , this.room, configuration);
    }
}