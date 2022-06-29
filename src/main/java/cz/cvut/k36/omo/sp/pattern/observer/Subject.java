package cz.cvut.k36.omo.sp.pattern.observer;

import cz.cvut.k36.omo.sp.model.event.TypeEvent;
import cz.cvut.k36.omo.sp.pattern.facade.Facade;

import java.time.LocalDateTime;

public interface Subject {

    void addSubscriber(Observer observer);

    void notifySubscribers(TypeEvent type, LocalDateTime dateTime, Facade facade);

}