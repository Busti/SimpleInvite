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

            if(!(sender instanceof Player)){
                sender.sendMessage("this command can only be run by a player");
                return false;
            }

            Player player = (Player)sender;

            RegisteredUser invitator = RegisteredUser.getUserBy(player.getUniqueId());

            if(invitator == null){
                invitator = new RegisteredUser(player.getUniqueId(), player.getName(), new UUID(0, 0), new Date(0), "INITIATOR", 0);
                RegisteredUser.USERS.add(invitator);
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

            if(Invite.getInvitesFromPlayer(Config.INVITE_INTERVAL_TIME*60000, player.getUniqueId()).size() >= Config.MAX_INVITES_PER_INTERVALL){
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

            return true;
        }

        if(cmd.getName().equalsIgnoreCase("simpleInvite")){
            if (args[0].equalsIgnoreCase("reload")) {
                Config.load(getConfig());
                sender.sendMessage("&6[&fSI&6]&a Configuration successfully reloaded.");
            }
            return true;
        }

        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        String playerName = event.getPlayer().getName();

        Invite invite = Invite.getInviteForName(playerName);
        if( invite != null){

            if(((new Date().getTime()-invite.timestamp.getTime())/60000) > Config.INVITATION_TIMEOUT){
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
