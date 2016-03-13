package b2c.simpleinvite.io;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public class Config {
    public static boolean DEBUG;
    public static int INVITATION_TIMEOUT;
    public static List<String> INVITATION_COMMANDS;
    public static int STRIKE_MOD;
    public static List<String> STRIKE_COMMANDS;
    public static int MAX_INVITES_PER_INTERVALL;
    public static int INVITE_INTERVAL_TIME;
    public static int REASON_LENGTH;
    public static int HOURS_BEFORE_REINVITE;
    public static boolean BINARY_MODE;

    public static void load(FileConfiguration cfg) {
    	Log.getCurrent().log("loading config:");
        DEBUG = cfg.getBoolean("debug");
        INVITATION_TIMEOUT 			= cfg.getInt(		"invite.timeoutMin");
        INVITATION_COMMANDS 		= cfg.getStringList("invite.commands");

        MAX_INVITES_PER_INTERVALL 	= cfg.getInt(		"invite.maxInvitesPerintervall");
        INVITE_INTERVAL_TIME 		= cfg.getInt(		"invite.intervalLengthMin");
        REASON_LENGTH 				= cfg.getInt(		"invite.maxReasonLength");
        HOURS_BEFORE_REINVITE 		= cfg.getInt(		"invite.cooldownReinviteHours");
        STRIKE_MOD 					= cfg.getInt(		"strike.maxAmount");
        STRIKE_COMMANDS 			= cfg.getStringList("strike.action");
        
        BINARY_MODE					= cfg.getBoolean("io.binaryMode");

        if (DEBUG) {
            Log.getCurrent().log("invite.timeoutMin: " + INVITATION_TIMEOUT);
            Log.getCurrent().log("invite.commands: " + INVITATION_COMMANDS);

            Log.getCurrent().log("invite.maxInvitesPerintervall: " + MAX_INVITES_PER_INTERVALL);
            Log.getCurrent().log("invite.intervalLengthMin: " + INVITE_INTERVAL_TIME);
            Log.getCurrent().log("invite.maxReasonLength: " + REASON_LENGTH);
            Log.getCurrent().log("invite.cooldownReinviteHours: " + HOURS_BEFORE_REINVITE);
            Log.getCurrent().log("strike.maxAmount: " + STRIKE_MOD);
            Log.getCurrent().log("strike.action: " + STRIKE_COMMANDS);
        }
        
    }

}
