package b2c.simpleinvite;

import b2c.simpleinvite.io.Config;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Peer on 06.03.2016.
 */
public class CommandExecuter {

    public CommandExecuter() {

    }


    public boolean invite(Player player, String nameOfInvitedPlayer, String reason) {

        RegisteredUser invitator = RegisteredUser.getUserBy(player.getUniqueId());

        if (invitator == null) {
            invitator = new RegisteredUser(player.getUniqueId(), player.getName(), new UUID(0, 0), new Date(0), "INITIATOR", 0);
            RegisteredUser.USERS.add(invitator);
            //if the user is not in the RegisteredUser List put him into that list

        } else if (!invitator.name.equals(player.getName())) {
            RegisteredUser.USERS.remove(invitator);
            invitator = new RegisteredUser(invitator.id, player.getName(), invitator.id, invitator.joinDate, invitator.reason, invitator.strikes);
            RegisteredUser.USERS.add(invitator);
            //if the saved name and the actual name of the player differs remove the old and add the changed RegisteredUser (because of it final fields)
        }
        int invitedPlayerInIntervall = Invite.getInvitesFromPlayer(Config.INVITE_INTERVAL_TIME * 60000, player.getUniqueId()).size()
                + RegisteredUser.getUserInvitedBy(Config.INVITE_INTERVAL_TIME * 60000, player.getUniqueId()).size();

        if (invitedPlayerInIntervall >= Config.MAX_INVITES_PER_INTERVALL) {
            player.sendMessage("You can only invite " + Config.MAX_INVITES_PER_INTERVALL + " per " + Config.INVITE_INTERVAL_TIME + " min");
            return false;
        }
        Date currentDate = new Date();
        if ((currentDate.getTime() - invitator.joinDate.getTime()) > (Config.HOURS_BEFORE_REINVITE * 24 * 60 * 1000)) {
            player.sendMessage("You must play an amount of time on this server if you want to  invite someone");
            return false;
        }

        if (nameOfInvitedPlayer.length() > 16) {
            player.sendMessage("the name of a player can't be greater than 16");
            return false;
        }
        if (reason.length() > Config.REASON_LENGTH) {
            player.sendMessage("the reason must not be greater than " + Config.REASON_LENGTH);
            return false;
        }

        if (Invite.getInviteForName(nameOfInvitedPlayer) != null) {
            player.sendMessage("this player was already invited");
            return false;
        }

        Invite invite = new Invite(player.getUniqueId(), new Date(), nameOfInvitedPlayer, reason);
        Invite.INVITATIONEN.add(invite);

        return true;
    }

    public void sendAllCommands(CommandSender sender) {
        sender.sendMessage("possible commands:");

        if (sender.hasPermission("simpleinvite.invite")) {
            sender.sendMessage("  /invite <playerName> <reason>");
            sender.sendMessage("    invites a player to this server. If he joins you are his guarantor");
            sender.sendMessage("    <playerName>: the name of the player you want to invite");
            sender.sendMessage("    <reason>: the reason why you want to invite him");
        }

        if (sender.hasPermission("simpleinvite.info")) {
            sender.sendMessage("  /simpleInvite info <playerName>");
            sender.sendMessage("  Display the reason, the joindate and the guarantor of this player.");
            sender.sendMessage("    <playerName>: the name of the player");
        }

        if (sender.hasPermission("simpleinvite.listself")) {
            sender.sendMessage("  /simpleInvite list");
            sender.sendMessage("  list all invitations from you");
        }

        if (sender.hasPermission("simpleinvite.list")) {
            sender.sendMessage("  /simpleInvite info <playerName>");
            sender.sendMessage("  list all invitations from this player.");
            sender.sendMessage("    <playerName>: the name of the player");
        }

        if (sender.hasPermission("simpleinvite.reload")) {
            sender.sendMessage("  /simpleInvite reload");
            sender.sendMessage("  reload the config file and saves the datafile.");
        }

        if (sender.hasPermission("simpleinvite.clearself")) {
            sender.sendMessage("  /simpleInvite clear");
            sender.sendMessage("  remove all invites from you.");
        }

        if (sender.hasPermission("simpleinvite.clear")) {
            sender.sendMessage("  /simpleInvite clear <playername>");
            sender.sendMessage("  remove all invites from the player.");
            sender.sendMessage("    <playerName>: the name of the player you want to invite");
        }
    }

    public boolean reload(CommandSender sender, SimpleInvite plugin) {
        Config.load(plugin.getConfig());
        try {
            plugin.getDataLoader().writeData();
            sender.sendMessage("&6[&fSI&6]&a Configuration successfully reloaded.");
        } catch (IOException e) {
            sender.sendMessage("ERROR: failed to save DataFile:");
            sender.sendMessage(e.getMessage());
            e.printStackTrace();
        }
        return true;

    }

    public void sendList(CommandSender sender, UUID idFromPlayerToShow) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy, hh:mm:ss");

        sender.sendMessage("List of your invites:");
        for (Invite invite : Invite.getInvitesFromPlayer(Config.INVITE_INTERVAL_TIME * 60000, idFromPlayerToShow)) {
            sender.sendMessage(String.format("  %16s %s for %s", invite.playerName, dateFormat.format(invite.timestamp), invite.reason));
        }
        sender.sendMessage("List of all friends you have already invited:");
        for (RegisteredUser ru : RegisteredUser.getUserInvitedBy(Config.INVITE_INTERVAL_TIME * 60000, idFromPlayerToShow)) {
            sender.sendMessage(String.format("  %16s joined at %s with reason: %s", ru.name, dateFormat.format(ru.joinDate), ru.reason));
        }
    }

    public void clear(CommandSender sender, UUID idFromPlayerToClear) {
        ArrayList<Invite> invitesFromPlayer = Invite.getInvitesFromPlayer(Config.INVITE_INTERVAL_TIME * 60000, idFromPlayerToClear);
        Invite.INVITATIONEN.removeAll(invitesFromPlayer);
    }

    public void sendInfo(CommandSender sender, String name) {
        RegisteredUser user = RegisteredUser.getUser(name);
        if (user == null) {
            sender.sendMessage("the given player '" + name + "' is not known");
            return;
        }
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy, hh:mm:ss");
        sender.sendMessage("the player '" + user.name + "' was invited by '" + RegisteredUser.getUserBy(user.invitedBy).name + "' with the reason '" + user.reason +
                "'. He joined " + dateFormat.format(user.joinDate) + " and has " + user.strikes + "/" + Config.STRIKE_MOD + " strikes.");
    }


}
