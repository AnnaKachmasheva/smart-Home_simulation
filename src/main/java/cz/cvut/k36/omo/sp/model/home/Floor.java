package cz.cvut.k36.omo.sp.model.home;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Floor {

    private int id;
    private List<Room> rooms; // there can be several rooms on a floor.

    /**
     * @param id unique id floor.
     */
    public Floor(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Floor{" +
                "id=" + id +
                ", rooms=" + rooms +
                '}';
    }

    public void addRoom(Room room) {
        Objects.requireNonNull(room);
        if (rooms == null)
            rooms = new ArrayList<>();
        rooms.add(room);
    }
}