package cz.cvut.k36.omo.sp.model.device.devices;

import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.device.TypeDevice;
import cz.cvut.k36.omo.sp.model.device.content.DeviceContent;
import cz.cvut.k36.omo.sp.model.device.energy.Energy;
import cz.cvut.k36.omo.sp.model.device.energy.TypeEnergy;
import cz.cvut.k36.omo.sp.model.event.TypeEvent;
import cz.cvut.k36.omo.sp.model.home.Room;
import cz.cvut.k36.omo.sp.pattern.facade.Facade;
import cz.cvut.k36.omo.sp.pattern.state.OffState;
import cz.cvut.k36.omo.sp.pattern.visitor.ReportVisitor;

import java.time.LocalDateTime;
import java.util.List;

public class VacuumCleaner extends Device {

    public VacuumCleaner(int id, String name, Room room, TypeDevice type, int basePower) {
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
        if (type == TypeEvent.DANGER) {
            facade.stopUseDevice(this, dateTime);
            this.setState(new OffState(this));
        }
    }
}