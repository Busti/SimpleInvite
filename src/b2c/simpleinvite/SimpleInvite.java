package b2c.simpleinvite;

import b2c.simpleinvite.io.Loader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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

            if(nameOfInvitedPlayer.length() > 16) {
                sender.sendMessage("the name of a player can't be greater than 16");
                return false;
            }
            if(reason.length() > 30){

            }



            Invite invite = new Invite(player.getUniqueId(), new Date(), );


            return true;
        }



        if(cmd.getName().equalsIgnoreCase("simpleInvite")){
            return true;
        }

        return false;
    }

}
