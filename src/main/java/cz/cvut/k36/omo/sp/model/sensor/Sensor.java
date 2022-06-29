package cz.cvut.k36.omo.sp.model.sensor;

import cz.cvut.k36.omo.sp.model.home.Room;
import cz.cvut.k36.omo.sp.pattern.observer.Subject;
import cz.cvut.k36.omo.sp.pattern.visitor.EntityVisitor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Sensor implements Subject {

    private int id;
    private String name;
    private Room room;
    private TypeSensor type;
    private int readings; // sensor readings(humidity, temperature...)

    /**
     * @param id   unique sensor id.
     * @param name non-unique sensor name
     * @param room the Room in which the sensor is located.
     */
    public Sensor(int id, String name, Room room, TypeSensor type) {
        this.id = id;
        this.name = name;
        this.room = room;
        this.type = type;
        this.readings = 0;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", room=" + room +
                '}';
    }
}