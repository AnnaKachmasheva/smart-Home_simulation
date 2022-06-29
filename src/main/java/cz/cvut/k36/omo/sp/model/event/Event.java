package cz.cvut.k36.omo.sp.model.event;

import cz.cvut.k36.omo.sp.utils.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Event {

    private TypeEvent type;
    private LocalDateTime startTime;
    private LocalDateTime endTime; // time and date of the end of the event.
    private String context;
    private Entity source;
    private Entity target;

    /**
     * @param type      TypeEvent, is the main characteristic.
     * @param startTime time and date of the start of the event.
     * @param source    event source, creator.
     * @param target    purpose of the event, helps the source to complete the event.
     * @param context   brief description of the event.
     */
    public Event(TypeEvent type, LocalDateTime startTime, Entity source, Entity target, String context) {
        this.type = type;
        this.startTime = startTime;
        this.source = source;
        this.target = target;
        this.context = context;
        this.endTime = null;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", context='" + context + '\'' +
                '}';
    }
}