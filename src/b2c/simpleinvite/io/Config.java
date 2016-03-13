package b2c.simpleinvite.io;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public class Config {
    public static int INVITATION_TIMEOUT;
    public static List<String> INVITATION_COMMANDS;
    public static int STRIKE_MOD;
    public static List<String> STRIKE_COMMANDS;
    public static int MAX_INVITES_PER_INTERVALL;
    public static int INVITE_INTERVAL_TIME;
    public static int REASON_LENGTH;
    public static int HOURS_BEFORE_REINVITE;

    public static void load(FileConfiguration cfg) {
        INVITATION_TIMEOUT 			= cfg.getInt("timeoutMin");
        INVITATION_COMMANDS 		= cfg.getStringList("invite.commands");
        STRIKE_MOD 					= cfg.getInt("strike.maxAmount");
        STRIKE_COMMANDS 			= cfg.getStringList("strike.action");
        MAX_INVITES_PER_INTERVALL 	= cfg.getInt("invite.maxInvitesPerintervall");
        INVITE_INTERVAL_TIME 		= cfg.getInt("invite.intervalLengthMin");
        REASON_LENGTH 				= cfg.getInt("maxReasonLength");
        HOURS_BEFORE_REINVITE 		= cfg.getInt("invite.cooldownReinviteHours");
    }

}
