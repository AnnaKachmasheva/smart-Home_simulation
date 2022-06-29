package cz.cvut.k36.omo.sp.utils;

import cz.cvut.k36.omo.sp.model.device.Manual;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

public class LoadManual {

    private static final Logger LOGGER = LogManager.getLogger(LoadConfiguration.class.getName());

    private static final String PATH = Objects.requireNonNull(LoadManual.class.getResource("/")).getPath()
                                                                                                + "/configs/manuals/";

    public Manual load(String deviceType) {
        try {
            Object ob = new JSONParser().parse(new FileReader(PATH + deviceType +".json"));
            JSONObject js = (JSONObject) ob;
            return new Manual((String) js.get("name"), (String)js.get("content"));
        } catch (IOException | ParseException e) {
            LOGGER.error("No manual found for a device named  \"" + deviceType + "\" .");
            return null;
        }
    }
}
