package b2c.simpleinvite;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleInvite extends JavaPlugin implements Listener {
    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this,  this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(!(sender instanceof Player)){
            sender.sendMessage("this command can only be run by a player");
            return false;
        }

        if(!cmd.getName().equalsIgnoreCase("mirror")){
            return false;
        }
        Player player = (Player)sender;

        return true;
    }

}
