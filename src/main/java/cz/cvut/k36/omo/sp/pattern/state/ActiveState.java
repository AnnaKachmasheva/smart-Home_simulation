package cz.cvut.k36.omo.sp.pattern.state;

import cz.cvut.k36.omo.sp.model.device.Device;

public class ActiveState implements State {

    private Device device;

    public ActiveState(Device device) {
        this.device = device;
    }

    @Override
    public int setPower() {
        int power = device.getBasePower();
        device.getEnergy().setPower(power);
        return power;
    }

    @Override
    public String toString() {
        return "ACTIVE";
    }
}
