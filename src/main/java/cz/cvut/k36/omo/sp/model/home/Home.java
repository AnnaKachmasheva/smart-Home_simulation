package cz.cvut.k36.omo.sp.model.home;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Home {

    private static Home home;   // there can be only 1 instance at home.
    private List<Floor> floors; // the house can have several floors.

    public Home() {}

    /**
     * @return singleton Home.
     */
    public static Home getHome() {
        if (home == null) {
            synchronized (Home.class) {
                if (home == null) home = new Home();
            }
        }
        return home;
    }

    public void addFloor(Floor floor) {
        Objects.requireNonNull(floor);
        if (floors == null)
            floors = new ArrayList<>();
        floors.add(floor);
    }
}