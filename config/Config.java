package config;


public class Config {
    public static boolean debug = false;
    public static boolean killerr = false;
    public static String filename = "coords.txt";

    public static void err(String m){
        System.out.println(m);
        System.exit(1);
    }
}
