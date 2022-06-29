package cz.cvut.k36.omo.sp.pattern.state;

import cz.cvut.k36.omo.sp.model.device.Device;

public class IdleState implements State {

    private Device device;

    public IdleState(Device device) {
        this.device = device;
    }

    @Override
    public int setPower() {
        int power = (int)Math.round(device.getBasePower() * 0.5);
        device.getEnergy().setPower(power);
        return power;
    }

    @Override
    public String toString() {
        return "IDLE";
    }
}