package cz.cvut.k36.omo.sp.model.sensor.sensors;

import cz.cvut.k36.omo.sp.model.event.TypeEvent;
import cz.cvut.k36.omo.sp.model.home.Room;
import cz.cvut.k36.omo.sp.model.sensor.Sensor;
import cz.cvut.k36.omo.sp.model.sensor.TypeSensor;
import cz.cvut.k36.omo.sp.pattern.facade.Facade;
import cz.cvut.k36.omo.sp.pattern.observer.Observer;
import cz.cvut.k36.omo.sp.pattern.visitor.ReportVisitor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class HumiditySensor extends Sensor {

    private final Set<Observer> observers = new HashSet<>();

    public HumiditySensor(int id, String name, Room room, TypeSensor type) {
        super(id, name, room, type);
    }

    @Override
    public void addSubscriber(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifySubscribers(TypeEvent type, LocalDateTime dateTime, Facade facade) {
        for (Observer observer : observers) {
            observer.update(type, dateTime, facade);
        }
    }
}