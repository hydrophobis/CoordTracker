package coords;

import java.util.Arrays;

public class Group {
    public String name;
    public String description;
    public Coordinate[] coords;

    public Group(String name, String description, Coordinate[] coords) {
        this.name = name;
        this.description = description;
        this.coords = coords;
    }

    @Override
    public String toString() {
        String coordString = "[";
        for(Coordinate c : coords){
            coordString += "\n\t\t" + c.toString();
        }
        coordString += "\n\t]";
        return "Group{\n\tname='" + name + "',\n\tdescription='" + description + "',\n\tcoords=" + coordString + "\n}";
    }

    public void addCoordinate(Coordinate coord) {
        if (coords == null) {
            coords = new Coordinate[] { coord };
        } else {
            Coordinate[] newCoords = new Coordinate[coords.length + 1];
            System.arraycopy(coords, 0, newCoords, 0, coords.length);
            newCoords[coords.length] = coord;
            coords = newCoords;
        }
    }
}
