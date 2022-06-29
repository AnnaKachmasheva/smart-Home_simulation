package cz.cvut.k36.omo.sp.utils;

public class Constants {

    public static final long BASIC_ELECTRICITY_RATE = 10,
                                NIGHT_ELECTRICITY_RATE = 5,
                                BASIC_WATER_RATE = 15,
                                NIGHT_WATER_RATE = 7,
                                NUMBER_HOURS_DAY = 15;

    public static final int PORTION_FEED = 50,
                            PORTION_FOOD = 200,
                            PORTION_COFFEE = 10,
                            PORTION_WATER = 50,
                            PORTION_WATER_FOR_CM = 20,
                            PORTION_WATER_FOR_SHOWER = 45000;

    public static final int PORTION_QUALITY_REDUCTION = 1,
                            BASE_DEVICE_QUALITY = 100;

    public static final int POWER_AIR_CONDITIONER = 2000,
                            POWER_AIR_PURIFIER = 35,
                            POWER_CAMERA = 5,
                            POWER_COFFEE_MACHINE = 1500,
                            POWER_DRINKER_FOR_PET = 50,
                            POWER_FEEDER_FOR_PET = 50,
                            POWER_LAMP = 10,
                            POWER_MUSIC_CENTER = 150,
                            POWER_PC = 450,
                            POWER_REFRIGERATOR = 350,
                            POWER_SHOWER = 800,
                            POWER_TV = 100,
                            POWER_CLEANER = 500;

    public static final String  PET_PERSON_WALK = "PERSON_WALK_WITH_PET",
                                PET_PERSON_PLAY = "PERSON_PLAY_WITH_PET",
                                PET_ALONE_PLAY = "PET_PLAY",
                                PET_SLEEP = "PET_SLEEP",
                                PERSON_GO_OUT = "PERSON_GO_OUT",
                                PERSON_AUTO_USE = "PERSON_USED_AUTO",
                                PERSON_WAIT_AUTO = "PERSON_WAIT_AUTO",
                                PERSON_SLEEP = "PERSON_SLEEP",
                                BABY_PERSON_PLAY = "PERSON_PLAY_WITH_BABY",
                                BABY_PERSON_WALK = "PERSON_WALK_WITH_BABY",
                                BABY_PERSON_EAT = "PERSON_WALK_WITH_BABY",
                                PERSON_DEVICE_FIX = "PERSON_DEVICE_FIX",
                                PERSON_DEVICE_ADD_CONTENT = "PERSON_ADDED_CONTENT_TO_DEVICE",
                                PERSON_TRANSPORT_USE = "PERSON_USED_TRANSPORT",
                                PERSON_WAIT_TRANSPORT = "PERSON_WAIT_TRANSPORT",
                                INHABITANT_DEVICE_USE = "INHABITANT_DEVICE_USE",
                                INHABITANT_DEVICE_WAIT = "INHABITANT_DEVICE_WAIT",
                                SENSOR_DEVICE_USE = "DEVICE_RESPONSE_TO_SENSOR",
                                DEVICE_BLOCK = "DEVICE_BLOCK";
}