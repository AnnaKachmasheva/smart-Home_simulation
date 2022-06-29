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

import java.time.LocalDateTime;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AirConditioner extends Device {

    private static final Logger LOGGER = LogManager.getLogger(AirConditioner.class.getName());

    public AirConditioner(int id, String name, Room room, TypeDevice type, int basePower) {
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
            case LOWER_TEMPERATURE:
                if (this.getState().toString().equals("ACTIVE")) {
                    if (facade.checkLastEvent(this.getEvents(), dateTime, type)) {
                        facade.stopUseDevice(this, dateTime);
                        facade.addEventEnergy(this, dateTime, this.getState().setPower());
                        break;
                    }
                }
            case RAISE_TEMPERATURE:
                if (!this.getState().toString().equals("ACTIVE")) {
                    this.setState(new ActiveState(this));
                    if (facade.checkLastEvent(this.getEvents(), dateTime, type)) {
                        Event event = new Event(type, dateTime, null, null, Constants.SENSOR_DEVICE_USE);
                        this.addEvent(event);
                        facade.addEventEnergy(this, dateTime, this.getState().setPower());

                        LOGGER.info("Start " + event + ".");
                        break;
                    }
                }
            case DANGER:
                facade.stopUseDevice(this, dateTime);
                this.setState(new OffState(this));
                break;
        }
    }
}