package cz.cvut.k36.omo.sp.model.home;

import cz.cvut.k36.omo.sp.model.event.TypeEvent;
import cz.cvut.k36.omo.sp.pattern.facade.Facade;
import cz.cvut.k36.omo.sp.pattern.observer.Observer;
import cz.cvut.k36.omo.sp.pattern.visitor.EntityVisitor;
import cz.cvut.k36.omo.sp.pattern.visitor.ReportVisitor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Window implements Observer {

    private int id;
    private StateWindow state;
    private Room room;

    /**
     * @param id   unique id within the room.
     * @param room the Room in which the window is located.
     */
    public Window(int id, Room room) {
        this.id = id;
        this.room = room;
        this.state = StateWindow.CLOSED;
    }

    @Override
    public String toString() {
        return "Window{" +
                "id=" + id +
                ", state=" + state +
                '}';
    }

    @Override
    public void update(TypeEvent type, LocalDateTime dateTime, Facade facade) {
        switch (type) {
            case LOWER_TEMPERATURE, LOWER_HUMIDITY -> setState(StateWindow.OPEN);
            case RAISE_TEMPERATURE, RAISE_HUMIDITY, DANGER -> setState(StateWindow.CLOSED);
        }
    }
}