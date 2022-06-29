package cz.cvut.k36.omo.sp.model.device.energy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Energy {

    private TypeEnergy type;
    private int power;

    /**
     * @param type  type of energy consumed by the device (water or electricity).
     * @param power power consumption (kilowatts per hour or liters per hour).
     */
    public Energy(TypeEnergy type, int power) {
        this.type = type;
        this.power = power;
    }

    @Override
    public String toString() {
        return "Energy{" +
                "type=" + type +
                ", power=" + power +
                '}';
    }
}