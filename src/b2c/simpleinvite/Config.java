package b2c.simpleinvite;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public class Config {
    public static int INVITATION_TIMEOUT;
    public static List<String> INVITATION_COMMANDS;
    public static int STRIKE_MOD;
    public static List<String> STRIKE_COMMANDS;
    public static int INTERVAL_INVITES;
    public static int INTERVAL_TIME;
    public static int REASON_LENGTH;

    public static void load(FileConfiguration cfg) {
        cfg.addDefault("Invitation timeout in minutes", 60);
        INVITATION_TIMEOUT = cfg.getInt("Invitation timeout in minutes");
        cfg.addDefault("Commands on invite", Arrays.asList("say There is a new member %user% on the server!", "pex user %user% group set Member"));
        INVITATION_COMMANDS = cfg.getStringList("Commands on invite");
        cfg.addDefault("Modulo strike amount", 3);
        STRIKE_MOD = cfg.getInt("Modulo strike amount");
        cfg.addDefault("Strike action", Arrays.asList("ban %user%", "say %user% has now %strikes% strikes and is being punished"));
        STRIKE_COMMANDS = cfg.getStringList("Strike action");
        cfg.addDefault("Maximum invites per interval", 5);
        INTERVAL_INVITES = cfg.getInt("Maximum invites per interval");
        cfg.addDefault("Interval length", 30);
        INTERVAL_TIME = cfg.getInt("Interval length");
        cfg.addDefault("Maximum reason length", 32);
        REASON_LENGTH = cfg.getInt("Maximum reason length");
    }

}
