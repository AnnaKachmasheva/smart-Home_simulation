package cz.cvut.k36.omo.sp.pattern.factory;

import cz.cvut.k36.omo.sp.model.device.Device;
import cz.cvut.k36.omo.sp.model.device.TypeDevice;
import cz.cvut.k36.omo.sp.model.device.devices.*;
import cz.cvut.k36.omo.sp.model.home.HomeConfiguration;
import cz.cvut.k36.omo.sp.model.home.Room;
import cz.cvut.k36.omo.sp.utils.Constants;

public class FactoryDevice {

    /**
     * @param id            unique device id.
     * @param deviceType    String device type.
     * @param name          non-unique device name.
     * @param room          the Room in which the device is located.
     * @param configuration HomeConfiguration
     * @return              Device or null, if the device failed to create
     */
    public Device createDevice(int id, String deviceType, String name, Room room, HomeConfiguration configuration) {
        TypeDevice type = TypeDevice.valueOf(deviceType);
        switch (type){
            case AIR_CONDITIONER:
                AirConditioner airConditioner = new AirConditioner(id, name, room, type, Constants.POWER_AIR_CONDITIONER);
                configuration.addConditioner(airConditioner);
                return airConditioner;
            case AIR_PURIFIER:
                AirPurifier airPurifier = new AirPurifier(id, name, room, type, Constants.POWER_AIR_PURIFIER);
                configuration.addAirPurifier(airPurifier);
                return airPurifier;
            case CAMERA:
                Camera camera = new Camera(id, name, room, type, Constants.POWER_CAMERA);
                configuration.addCamera(camera);
                return camera;
            case COFFEE_MACHINE:
                CoffeeMachine coffeeMachine = new CoffeeMachine(id, name, room, type, Constants.POWER_COFFEE_MACHINE);
                configuration.addCoffeeMachine(coffeeMachine);
                return coffeeMachine;
            case DRINKER_FOR_PET:
                DrinkerForPet drinkerForPet = new DrinkerForPet(id, name, room, type, Constants.POWER_DRINKER_FOR_PET);
                configuration.addDrinkerForPet(drinkerForPet);
                return drinkerForPet;
            case FEEDER_FOR_PET:
                FeederForPet feederForPet = new FeederForPet(id, name, room, type, Constants.POWER_FEEDER_FOR_PET);
                configuration.addFeederForPet(feederForPet);
                return feederForPet;
            case LAMP:
                Lamp lamp = new Lamp(id, name, room, type, Constants.POWER_LAMP);
                configuration.addLamp(lamp);
                return lamp;
            case MUSIC_CENTER:
                MusicCenter center = new MusicCenter(id, name, room, type, Constants.POWER_MUSIC_CENTER);
                configuration.addMusicCenter(center);
                return center;
            case PC:
                PC pc = new PC(id, name, room, type, Constants.POWER_PC);
                configuration.addPC(pc);
                return pc;
            case REFRIGERATOR:
                Refrigerator refrigerator = new  Refrigerator(id, name,room, type, Constants.POWER_REFRIGERATOR);
                configuration.addRefrigerator(refrigerator);
                return refrigerator;
            case SHOWER:
                Shower shower = new Shower(id, name, room, type, Constants.POWER_SHOWER);
                configuration.addShower(shower);
                return shower;
            case TV:
                TV tv = new TV(id, name, room, type, Constants.POWER_TV);
                configuration.addTV(tv);
                return tv;
            case VACUUM_CLEANER:
                VacuumCleaner cleaner = new VacuumCleaner(id, name, room, type, Constants.POWER_CLEANER);
                configuration.addVacuumCleaner(cleaner);
                return cleaner;
            default: return null;
        }
    }
}