package cz.cvut.k36.omo.sp.pattern.observer;

import cz.cvut.k36.omo.sp.model.event.TypeEvent;
import cz.cvut.k36.omo.sp.pattern.facade.Facade;

import java.time.LocalDateTime;

public interface Observer {

    void update(TypeEvent type, LocalDateTime dateTime, Facade facade);

}