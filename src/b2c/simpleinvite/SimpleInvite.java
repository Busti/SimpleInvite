package b2c.simpleinvite;

import b2c.simpleinvite.io.Loader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class SimpleInvite extends JavaPlugin implements Listener {

    Loader dataLoader;

    @Override
    public void onDisable() {
        try {
            dataLoader.writeData();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't save dataFile");
        }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,  this);

        File savedData = new File(this.getDataFolder(), "SimpleInviteData");
        if(!savedData.exists()) {
            this.getDataFolder().mkdirs();

            try {
                savedData.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Can't create data file");
            }
        }

        dataLoader = new Loader(savedData);
        try {
            dataLoader.readData();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Can't read dataFile");
        }

        Config.load(getConfig());

    }



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){


        if(cmd.getName().equalsIgnoreCase("invite")){

            if(!sender.hasPermission("simpleinvite.invite")){
                sender.sendMessage("Sorry, you don't have the permission to do that");
                return false;
            }

            if(!(sender instanceof Player)){
                sender.sendMessage("this command can only be run by a player");
                return false;
            }

            Player player = (Player)sender;

            RegisteredUser invitator = RegisteredUser.getUserBy(player.getUniqueId());

            if(invitator == null){
                invitator = new RegisteredUser(player.getUniqueId(), player.getName(), new UUID(0, 0), new Date(0), "INITIATOR", 0);
                RegisteredUser.USERS.add(invitator);
                //if the user is not in the RegisteredUser List put him into that list

            }else if(!invitator.name.equals(player.getName())){
                RegisteredUser.USERS.remove(invitator);
                invitator = new RegisteredUser(invitator.id, player.getName(), invitator.id, invitator.joinDate, invitator.reason, invitator.strikes);
                RegisteredUser.USERS.add(invitator);
                //if the saved name and the actual name of the player differs remove the old and add the changed RegisteredUser (because of it final fields)
            }

            if(args.length < 2){
                sender.sendMessage("you need to type the name of the player and the reason why you want to invite him");
                return false;
            }

            String nameOfInvitedPlayer = args[0];
            StringBuilder reasonBuilder = new StringBuilder();
            for(int i = 1; i < args.length; ++i){
                reasonBuilder.append(args[i]);
                reasonBuilder.append(' ');
            }
            String reason = reasonBuilder.toString();
            int invitedPlayerInIntervall = Invite.getInvitesFromPlayer(Config.INVITE_INTERVAL_TIME*60000, player.getUniqueId()).size()
                    + RegisteredUser.getUserInvitedBy(Config.INVITE_INTERVAL_TIME*60000, player.getUniqueId()).size();

            if( invitedPlayerInIntervall >= Config.MAX_INVITES_PER_INTERVALL){
                sender.sendMessage("You can only invite "+Config.MAX_INVITES_PER_INTERVALL+" per "+Config.INVITE_INTERVAL_TIME+" min");
                return false;
            }
            if(nameOfInvitedPlayer.length() > 16) {
                sender.sendMessage("the name of a player can't be greater than 16");
                return false;
            }
            if(reason.length() > Config.REASON_LENGTH){
                sender.sendMessage("the reason must not be greater than "+Config.REASON_LENGTH);
                return false;
            }

            if(Invite.getInviteForName(nameOfInvitedPlayer) != null){
                sender.sendMessage("this player was already invited");
                return false;
            }

            Invite invite = new Invite(player.getUniqueId(), new Date(), nameOfInvitedPlayer, reason);
            Invite.INVITATIONEN.add(invite);

            return true;
        }

        if(cmd.getName().equalsIgnoreCase("simpleInvite")){
            if(args.length == 0){

                sender.sendMessage("possible commands:");

                if(sender.hasPermission("simpleinvite.invite")) {
                    sender.sendMessage("  /invite <playerName> <reason>");
                    sender.sendMessage("    invites a player to this server. If he joins you are his guarantor");
                    sender.sendMessage("    <playerName>: the name of the player you want to invite");
                    sender.sendMessage("    <reason>: the reason why you want to invite him");
                }

                if(sender.hasPermission("simpleinvite.info")){
                    sender.sendMessage("  /simpleInvite info <playerName>");
                    sender.sendMessage("  Display the reason, the joindate and the guarantor of this player.");
                    sender.sendMessage("    <playerName>: the name of the player");
                }

                if(sender.hasPermission("simpleinvite.listself")){
                    sender.sendMessage("  /simpleInvite list");
                    sender.sendMessage("  list all invitations from you");
                }

                if(sender.hasPermission("simpleinvite.list")){
                    sender.sendMessage("  /simpleInvite info <playerName>");
                    sender.sendMessage("  list all invitations from this player.");
                    sender.sendMessage("    <playerName>: the name of the player");
                }

                if(sender.hasPermission("simpleinvite.reload")){
                    sender.sendMessage("  /simpleInvite reload");
                    sender.sendMessage("  reload the config file and saves the datafile.");
                }

                if(sender.hasPermission("simpleinvite.clearself")){
                    sender.sendMessage("  /simpleInvite clear");
                    sender.sendMessage("  remove all invites from you.");
                }

                if(sender.hasPermission("simpleinvite.clear")){
                    sender.sendMessage("  /simpleInvite clear <playername>");
                    sender.sendMessage("  remove all invites from the player.");
                    sender.sendMessage("    <playerName>: the name of the player you want to invite");
                }

                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {

                if(!sender.hasPermission("simpleinvite.reload")){
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return false;
                }

                Config.load(getConfig());
                try {
                    dataLoader.writeData();
                    sender.sendMessage("&6[&fSI&6]&a Configuration successfully reloaded.");
                } catch (IOException e) {
                    sender.sendMessage("ERROR: failed to save DataFile:");
                    sender.sendMessage(e.getMessage());
                    e.printStackTrace();
                }
                return true;

            }

            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {

                if(!sender.hasPermission("simpleinvite.listself")){
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return false;
                }

                if(!(sender instanceof Player)){
                    sender.sendMessage("this command can only be run by a player");
                    return false;
                }

                Player player = (Player)sender;

                DateFormat dateFormat = new SimpleDateFormat( "dd.MM.yy, hh:mm:ss" );

                player.sendMessage("List of your invites:");
                for(Invite invite: Invite.getInvitesFromPlayer(Config.INVITE_INTERVAL_TIME*60000, player.getUniqueId())){
                    player.sendMessage(String.format("  %16s %s for %s", invite.playerName, dateFormat.format(invite.timestamp), invite.reason));
                }
                player.sendMessage("List of all friends you have already invited:");
                for(RegisteredUser ru: RegisteredUser.getUserInvitedBy(Config.INVITE_INTERVAL_TIME*60000, player.getUniqueId())){
                    player.sendMessage(String.format("  %16s joined at %s with reason: %s", ru.name, dateFormat.format(ru.joinDate), ru.reason));
                }
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {

                if(!sender.hasPermission("simpleinvite.clearself")){
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return false;
                }

                if(!(sender instanceof Player)){
                    sender.sendMessage("this command can only be run by a player");
                    return false;
                }

                Player player = (Player)sender;
                ArrayList<Invite> invitesFromPlayer = Invite.getInvitesFromPlayer(Config.INVITE_INTERVAL_TIME*60000, player.getUniqueId());
                Invite.INVITATIONEN.removeAll(invitesFromPlayer);
                return true;
            }

            if(args.length < 2){
                return false;
            }

            if (args[0].equalsIgnoreCase("info")) {

                if(!sender.hasPermission("simpleinvite.info")){
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return false;
                }

                RegisteredUser user = RegisteredUser.getUser(args[1]);
                if(user == null){
                    sender.sendMessage("the given player '"+args[1]+"' is not known");
                    return true;
                }
                DateFormat dateFormat = new SimpleDateFormat( "dd.MM.yy, hh:mm:ss" );
                sender.sendMessage("the player '"+user.name+"' was invited by '"+RegisteredUser.getUserBy(user.invitedBy).name+"' with the reason '"+user.reason+
                        "'. He joined "+dateFormat.format(user.joinDate)+" and has "+user.strikes+"/"+Config.STRIKE_MOD+" strikes.");
                return true;
            }

            if(args[0].equalsIgnoreCase("list")){

                if(!sender.hasPermission("simpleinvite.list")){
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return false;
                }

                DateFormat dateFormat = new SimpleDateFormat( "dd.MM.yy, hh:mm:ss" );

                RegisteredUser user = RegisteredUser.getUser(args[1]);
                if(user == null){
                    sender.sendMessage("the given player '"+args[1]+"' is not known");
                    return true;
                }

                sender.sendMessage("List of invites:");
                for(Invite invite: Invite.getInvitesFromPlayer(Config.INVITE_INTERVAL_TIME*60000, user.id)){
                    sender.sendMessage(String.format("  %16s %s for %s", invite.playerName, dateFormat.format(invite.timestamp), invite.reason));
                }
                sender.sendMessage("List of all friends you have already invited:");
                for(RegisteredUser ru: RegisteredUser.getUserInvitedBy(Config.INVITE_INTERVAL_TIME*60000, user.id)){
                    sender.sendMessage(String.format("  %16s joined at %s with reason: %s", ru.name, dateFormat.format(ru.joinDate), ru.reason));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("clear")) {

                if(!sender.hasPermission("simpleinvite.clear")){
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return false;
                }

                RegisteredUser user = RegisteredUser.getUser(args[1]);
                if(user == null){
                    sender.sendMessage("the given player '"+args[1]+"' is not known");
                    return true;
                }

                ArrayList<Invite> invitesFromPlayer = Invite.getInvitesFromPlayer(Config.INVITE_INTERVAL_TIME*60000, user.id);
                Invite.INVITATIONEN.removeAll(invitesFromPlayer);
                return true;
            }

        }

        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        String playerName = event.getPlayer().getName();

        Invite invite = Invite.getInviteForName(playerName);
        if( invite != null){

            if(!invite.isValid(new Date())){
                return; //invite is timed out
            }

            for(String command: Config.INVITATION_COMMANDS){
                getServer().dispatchCommand(getServer().getConsoleSender(), command.replaceAll("%player%", playerName));
            }
            RegisteredUser.USERS.add(new RegisteredUser(event.getPlayer().getUniqueId(), playerName, invite.guarantorID, new Date(), invite.reason, 0));
            Invite.INVITATIONEN.remove(invite);

            event.getPlayer().sendMessage("Welcome on this Server");
            event.getPlayer().sendMessage(invite.playerName + "is your guarantor!" );
        }
    }

}
