package cz.cvut.k36.omo.sp.pattern.proxy;

import cz.cvut.k36.omo.sp.model.device.Manual;
import cz.cvut.k36.omo.sp.utils.LoadManual;

public class DownloadManualImpl implements DownloadManual{

    private static final LoadManual loadManual = new LoadManual();

    @Override
    public Manual getManual(String deviceName) {
        return loadManual.load(deviceName);
    }
}
