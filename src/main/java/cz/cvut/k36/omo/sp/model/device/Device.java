package cz.cvut.k36.omo.sp.model.device;

import cz.cvut.k36.omo.sp.model.device.content.DeviceContent;
import cz.cvut.k36.omo.sp.model.device.energy.Energy;
import cz.cvut.k36.omo.sp.model.device.energy.EventEnergy;
import cz.cvut.k36.omo.sp.model.event.Event;
import cz.cvut.k36.omo.sp.model.home.Room;
import cz.cvut.k36.omo.sp.model.inhabitant.Inhabitant;
import cz.cvut.k36.omo.sp.pattern.observer.Observer;
import cz.cvut.k36.omo.sp.pattern.proxy.DownloadManual;
import cz.cvut.k36.omo.sp.pattern.proxy.DownloadManualProxyImpl;
import cz.cvut.k36.omo.sp.pattern.state.OffState;
import cz.cvut.k36.omo.sp.pattern.state.State;
import cz.cvut.k36.omo.sp.pattern.visitor.EntityVisitor;
import cz.cvut.k36.omo.sp.utils.Constants;
import cz.cvut.k36.omo.sp.utils.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public abstract class Device implements Entity, Observer {

    private int id;
    private String name;
    private Room room;
    private TypeDevice type;
    private int basePower;
    private State state;   // Device state(IDLE, ACTIVE, BLOCK or OFF).
    private Energy energy; // Energy consumed by the device.
    private int quality;   // Functionality of the device that decreases linearly with the operating time of the device.
    private DownloadManual manual; // Contains instructions for repair.
    private Inhabitant currentUser; // Inhabitant of the house who is currently using the device.
    private List<Inhabitant> expects; // A queue of inhabitants of the house who are waiting for the device to get off.
    private List<Event> events; // Events that happened to this device.
    private List<EventEnergy> eventEnergy; // Periods of energy consumption depending on the state of the device.

    /**
     * @param id   unique device id.
     * @param name non-unique device name.
     * @param room the Room in which the device is located.
     */
    public Device(int id, String name, Room room, TypeDevice type, int basePower) {
        this.id = id;
        this.name = name;
        this.room = room;
        this.type = type;
        this.basePower = basePower;
        this.energy = null;
        this.state = new OffState(this); // Device creation state OFF.
        this.quality = Constants.BASE_DEVICE_QUALITY;
        this.manual = new DownloadManualProxyImpl(); // Loaded when needed.
    }

    public void addInhabitant(Inhabitant inhabitant) {
        Objects.requireNonNull(inhabitant);
        if (expects == null)
            expects = new ArrayList<>();
        expects.add(inhabitant);
    }

    public void removeInhabitant(Inhabitant inhabitant) {
        Objects.requireNonNull(inhabitant);
        if (expects == null) return;
        expects.remove(inhabitant);
    }

    public void addEvent(Event event) {
        Objects.requireNonNull(event);
        if (events == null)
            events = new ArrayList<>();
        events.add(event);
    }

    public void addEventEnergy(EventEnergy event) {
        Objects.requireNonNull(event);
        if (eventEnergy == null)
            eventEnergy = new ArrayList<>();
        eventEnergy.add(event);
    }

    public String toStringForLog() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", room=" + room +
                ", energy=" + energy +
                ", quality=" + quality +
                ", currentUser=" + currentUser +
                ", expects=" + expects +
                ", events=" + events +
                '}';
    }

    /**
     * @return DeviceContent, not all devices have it.
     */
    public abstract List<DeviceContent> getDeviceContent();

    /**
     * The content, if any, shrinks as you use the device.
     * Returns the amount of content to its original baseline.
     */
    public abstract void recoveryContent();
}