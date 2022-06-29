package cz.cvut.k36.omo.sp.pattern.proxy;

import cz.cvut.k36.omo.sp.model.device.Manual;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadManualProxyImpl implements DownloadManual{

    private static final Logger LOGGER = LogManager.getLogger(DownloadManualProxyImpl.class.getName());

    private DownloadManual downloadManual;

    public DownloadManualProxyImpl() {}

    @Override
    public Manual getManual(String deviceName) {
        if (downloadManual == null) {
            LOGGER.info("Download manual for device: " + deviceName);
            downloadManual = new DownloadManualImpl();
        }
        return downloadManual.getManual(deviceName);
    }
}
