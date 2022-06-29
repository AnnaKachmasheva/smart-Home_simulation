package cz.cvut.k36.omo.sp.model.device;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Manual {

    private String deviceName;
    private String content;

    /**
     * @param deviceName the name of the device that owns the manual.
     * @param content    repair instructions and other useful information.
     */
    public Manual(String deviceName, String content) {
        this.deviceName = deviceName;
        this.content = content;
    }
}
