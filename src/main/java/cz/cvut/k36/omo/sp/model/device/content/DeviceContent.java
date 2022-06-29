package cz.cvut.k36.omo.sp.model.device.content;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DeviceContent {

    private TypeDeviceContent typeContent;
    private int count;

    /**
     * @param typeContent device content type.
     * @param count       amount of device content. Unit of measure grams or milliliters.
     */
    public DeviceContent(TypeDeviceContent typeContent, int count) {
        this.typeContent = typeContent;
        this.count = count;
    }
}