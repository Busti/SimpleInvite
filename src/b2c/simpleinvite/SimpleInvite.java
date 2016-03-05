package b2c.simpleinvite;

import b2c.simpleinvite.io.Loader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

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
        if(!savedData.exists()){
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
