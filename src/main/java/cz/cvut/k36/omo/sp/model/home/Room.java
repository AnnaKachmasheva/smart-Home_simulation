package cz.cvut.k36.omo.sp.model.home;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {

    private int id;

    /**
     * @param id unique id room.
     */
    public Room(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                '}';
    }
}
