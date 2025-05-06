package coords;

public class Coordinate {
    public String name;
    public String description;
    public int x, y, z;

    public Coordinate(String name, int x, int y, int z, String description) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.description = description;
    }

    @Override
    public String toString() {
        return name + " @ (" + x + ", " + y + ", " + z + ") - " + description;
    }

    public String toFileString() {
        return name + ";" + x + ";" + y + ";" + z + ";" + (description.equals("") ? " " : description);
    }

    public static Coordinate fromFileString(String line) {

        String[] parts = line.split(";");
        if(parts.length < 5){
            System.out.println("Problem converting " + line + " to coordinate. Not enough pieces");
            return null;
        }
        return new Coordinate(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                              Integer.parseInt(parts[3]), parts[4]);
    }
}
