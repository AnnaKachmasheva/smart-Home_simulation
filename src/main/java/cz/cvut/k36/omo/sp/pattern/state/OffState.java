package cz.cvut.k36.omo.sp.pattern.state;

import cz.cvut.k36.omo.sp.model.device.Device;

public class OffState implements State {

    private Device device;

    public OffState(Device device) {
        this.device = device;
    }

    @Override
    public int setPower() {
        device.getEnergy().setPower(0);
        return 0;
    }

    @Override
    public String toString() {
        return "OFF";
    }
}
