package cz.cvut.k36.omo.sp.pattern.state;

public interface State {

    /**
     * In states OFF and BLOCK device power is 0.
     * In state ACTIVE it is 100 percent of the base power.
     * In state IDLE it is 50 percent of the base power.
     *
     * @return device power
     */
    int setPower();
}