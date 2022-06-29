package cz.cvut.k36.omo.sp.model.device.devices;

import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.device.TypeDevice;
import cz.cvut.k36.omo.sp.model.device.content.DeviceContent;
import cz.cvut.k36.omo.sp.model.device.energy.Energy;
import cz.cvut.k36.omo.sp.model.device.energy.TypeEnergy;
import cz.cvut.k36.omo.sp.model.event.Event;
import cz.cvut.k36.omo.sp.model.event.TypeEvent;
import cz.cvut.k36.omo.sp.model.home.Room;
import cz.cvut.k36.omo.sp.pattern.facade.Facade;
import cz.cvut.k36.omo.sp.pattern.state.ActiveState;
import cz.cvut.k36.omo.sp.pattern.state.OffState;
import cz.cvut.k36.omo.sp.pattern.visitor.ReportVisitor;
import cz.cvut.k36.omo.sp.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

public class Lamp extends Device {

    private static final Logger LOGGER = LogManager.getLogger(Lamp.class.getName());

    public Lamp(int id, String name, Room room, TypeDevice type, int basePower) {
        super(id, name, room, type, basePower);
        setEnergy(new Energy(TypeEnergy.ELECTRIC, basePower));
    }

    @Override
    public List<DeviceContent> getDeviceContent() {
        return null;
    }

    @Override
    public void recoveryContent() {
        // nothing
    }

    @Override
    public String toString() {
        return " name={" + this.getName() + "} id={" + this.getId() + "} ";
    }

    @Override
    public void update(TypeEvent type, LocalDateTime dateTime, Facade facade) {
        switch (type) {
            case LIGHT_DAY:
                if (getState().toString().equals("ACTIVE")) {
                    if (facade.checkLastEvent(this.getEvents(), dateTime, type)) {
                        facade.stopUseDevice(this, dateTime);
                        facade.addEventEnergy(this, dateTime, this.getState().setPower());
                        break;
                    }
                }
            case LIGHT_NIGHT:
                if (this.getState().toString().equals("IDLE") || this.getState().toString().equals("OFF")) {
                    this.setState(new ActiveState(this));
                    if (facade.checkLastEvent(this.getEvents(), dateTime, type)) {
                        Event event = new Event(type, dateTime, null, null, Constants.SENSOR_DEVICE_USE);
                        facade.addEventEnergy(this, dateTime, this.getState().setPower());
                        this.addEvent(event);
                        LOGGER.info("Start " + event + ".");
                    }
                }
                break;
            case DANGER:
                facade.stopUseDevice(this, dateTime);
                setState(new OffState(this));
        }
    }
}