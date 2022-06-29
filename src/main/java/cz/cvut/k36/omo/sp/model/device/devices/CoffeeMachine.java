package cz.cvut.k36.omo.sp.model.device.devices;

import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.device.TypeDevice;
import cz.cvut.k36.omo.sp.model.device.content.DeviceContent;
import cz.cvut.k36.omo.sp.model.device.content.TypeDeviceContent;
import cz.cvut.k36.omo.sp.model.device.energy.Energy;
import cz.cvut.k36.omo.sp.model.device.energy.TypeEnergy;
import cz.cvut.k36.omo.sp.model.event.TypeEvent;
import cz.cvut.k36.omo.sp.model.home.Room;
import cz.cvut.k36.omo.sp.pattern.facade.Facade;
import cz.cvut.k36.omo.sp.pattern.state.OffState;
import cz.cvut.k36.omo.sp.pattern.visitor.ReportVisitor;
import cz.cvut.k36.omo.sp.utils.Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CoffeeMachine extends Device implements Entity {

    private List<DeviceContent> contents;

    public CoffeeMachine(int id, String name, Room room, TypeDevice type, int basePower) {
        super(id, name, room, type, basePower);
        this.contents = createDeviceContent();
        setEnergy(new Energy(TypeEnergy.ELECTRIC, basePower));
    }

    @Override
    public List<DeviceContent> getDeviceContent() {
        return contents;
    }

    @Override
    public void recoveryContent() {
        this.contents = createDeviceContent();
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

    /**
     * @return initial content list.
     */
    private List<DeviceContent> createDeviceContent() {
        List<DeviceContent> contents = new ArrayList<>();
        contents.add(new DeviceContent(TypeDeviceContent.WATER_FOR_CM, 2000));
        contents.add(new DeviceContent(TypeDeviceContent.COFFEE, 500));
        return contents;
    }
}