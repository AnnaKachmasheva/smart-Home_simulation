package cz.cvut.k36.omo.sp.pattern.proxy;

import cz.cvut.k36.omo.sp.model.device.Manual;

public interface DownloadManual {

    Manual getManual(String deviceName);

}
