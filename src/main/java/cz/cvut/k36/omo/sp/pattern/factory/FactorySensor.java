package cz.cvut.k36.omo.sp.pattern.factory;

import cz.cvut.k36.omo.sp.model.home.HomeConfiguration;
import cz.cvut.k36.omo.sp.model.home.Room;
import cz.cvut.k36.omo.sp.model.sensor.Sensor;
import cz.cvut.k36.omo.sp.model.sensor.TypeSensor;
import cz.cvut.k36.omo.sp.model.sensor.sensors.*;

public class FactorySensor {

    /**
     * @param id            unique sensor id.
     * @param typeSensor          String sensor type
     * @param name          non-unique sensor name.
     * @param room          the Room in which the sensor is located.
     * @param configuration HomeConfiguration
     * @return              Sensor or null, if the sensor failed to create
     */
    public Sensor createSensor(int id, String typeSensor, String name, Room room, HomeConfiguration configuration) {
        TypeSensor type = TypeSensor.valueOf(typeSensor);
        switch (type){
            case LIGHT:
                LightSensor lightSensor = new LightSensor(id, name, room, type);
                configuration.addLightSensor(lightSensor);
                return lightSensor;
            case SMOKE:
                SmokeSensor smokeSensor = new SmokeSensor(id, name, room, type);
                configuration.addSmokeSensor(smokeSensor);
                return smokeSensor;
            case TEMPERATURE:
                TemperatureSensor temperatureSensor = new TemperatureSensor(id, name, room, type);
                configuration.addTemperatureSensor(temperatureSensor);
                return temperatureSensor;
            case HUMIDITY:
                HumiditySensor humiditySensor = new HumiditySensor(id, name, room, type);
                configuration.addHumiditySensor(humiditySensor);
                return humiditySensor;
            default: return null;
        }
    }
}