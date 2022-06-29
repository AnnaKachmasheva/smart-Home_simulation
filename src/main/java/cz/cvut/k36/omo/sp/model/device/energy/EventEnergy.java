package cz.cvut.k36.omo.sp.model.device.energy;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
public class EventEnergy {

    private LocalDateTime start;
    private LocalDateTime end;
    private int power;

    /**
     * @param start start time of the energy consumption interval.
     * @param end   the end time of the energy consumption interval.
     * @param power power of the energy consumption interval.
     */
    public EventEnergy(LocalDateTime start, LocalDateTime end, int power) {
        this.start = start;
        this.end = end;
        this.power = power;
    }
}