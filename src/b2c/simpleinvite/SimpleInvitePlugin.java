package b2c.simpleinvite;

import b2c.simpleinvite.io.Config;
import b2c.simpleinvite.io.FileConfigurationLoader;
import b2c.simpleinvite.io.Loader;
import b2c.simpleinvite.io.Log;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class SimpleInvitePlugin extends JavaPlugin implements Listener {

    Loader dataLoader;
    FileConfigurationLoader fileConfigLoader;
    CommandExecuter commandExecuter = new CommandExecuter();

    @Override
    public void onDisable() {
    	
    	if(Config.BINARY_MODE){
    		try {
    			dataLoader.writeData();
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new RuntimeException("Can't save dataFile (Binary)");
    		}
    		
    	}else{
    		YamlConfiguration savedDataYMLData = new YamlConfiguration();
    		fileConfigLoader.save(savedDataYMLData);
    		File savedDataYML = new File(this.getDataFolder(), "simpleInviteData.yml");
    		try {
    			savedDataYMLData.save(savedDataYML);
    		} catch (IOException e) {
    			e.printStackTrace();
    			throw new RuntimeException("Can't save dataFile (YML)");
    		}
    	}
    	
    	
        
        
    }

    @Override
    public void onEnable() {    	
        getServer().getPluginManager().registerEvents(this, this);
        
        try {
			Log.init(new File(this.getDataFolder(), "Log.log"));
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new RuntimeException("Can't create log file");
		}
        
        Log.getCurrent().log("");
        Log.getCurrent().log("Starting the plugin");
        Log.getCurrent().log("");
        
        
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            Log.getCurrent().log("config.yml not found, creating!");
            saveDefaultConfig();
        } else {
            Log.getCurrent().log("config.yml found, loading!");
        }
        
        Config.load(getConfig());
        

        if(Config.BINARY_MODE){
	        File savedData = new File(this.getDataFolder(), "SimpleInviteData.bin");
	        dataLoader = new Loader(savedData);
	        if (!savedData.exists()) {
	            this.getDataFolder().mkdirs();
	
	            try {
	                savedData.createNewFile();
	                dataLoader.writeData();
	            } catch (IOException e) {
	                e.printStackTrace();
	                throw new RuntimeException("Can't create data file");
	            }
	        }
	        try {
	            dataLoader.readData();
	        } catch (IOException e) {
	            e.printStackTrace();
	            throw new RuntimeException("Can't read dataFile");
	        }
        }else{
	        File savedDataYML = new File(this.getDataFolder(), "simpleInviteData.yml");
	        YamlConfiguration savedDataYMLData = new YamlConfiguration();
	        try {
				savedDataYMLData.load(savedDataYML);
			} catch (IOException | InvalidConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        fileConfigLoader = new FileConfigurationLoader();
	        fileConfigLoader.read(savedDataYMLData);
        }   

    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("invite")) {
            if (!sender.hasPermission("simpleinvite.invite")) {
                sender.sendMessage("Sorry, you don't have the permission to do that");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage("you need to type the name of the player and the reason why you want to invite him");
                return false;
            }

            String nameOfInvitedPlayer = args[0];
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 1; i < args.length; ++i) {
                reasonBuilder.append(args[i]);
                reasonBuilder.append(' ');
            }

            if (!(sender instanceof Player)) {
                sender.sendMessage("this command can only be run by a player");
                return true;
            }

            Player player = (Player) sender;

            return commandExecuter.invite(player, nameOfInvitedPlayer, reasonBuilder.toString());
        }

        if (cmd.getName().equalsIgnoreCase("simpleInvite")) {
            if (args.length == 0) {
                commandExecuter.sendAllCommands(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("reload")) {

                if (!sender.hasPermission("simpleinvite.reload")) {
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return true;
                }

                return commandExecuter.reload(sender, this);

            }

            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {

                if (!sender.hasPermission("simpleinvite.listself")) {
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return true;
                }

                if (!(sender instanceof Player)) {
                    sender.sendMessage("this command can only be run by a player");
                    return true;
                }

                Player player = (Player) sender;

                commandExecuter.sendList(sender, player.getUniqueId());
                return true;
            }

            if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {

                if (!sender.hasPermission("simpleinvite.clearself")) {
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return true;
                }

                if (!(sender instanceof Player)) {
                    sender.sendMessage("this command can only be run by a player");
                    return true;
                }

                Player player = (Player) sender;
                commandExecuter.clear(sender, player.getUniqueId());
                return true;
            }

            if (args.length < 2) {
                return false;
            }

            if (args[0].equalsIgnoreCase("info")) {

                if (!sender.hasPermission("simpleinvite.info")) {
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return true;
                }

                commandExecuter.sendInfo(sender, args[1]);
                return true;
            }

            if (args[0].equalsIgnoreCase("list")) {

                if (!sender.hasPermission("simpleinvite.list")) {
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return true;
                }
                RegisteredUser user = RegisteredUser.getUser(args[1]);
                if (user == null) {
                    sender.sendMessage("the given player '" + args[1] + "' is not known");
                    return true;
                }

                commandExecuter.sendList(sender, user.id);
                return true;
            }
            if (args[0].equalsIgnoreCase("clear")) {

                if (!sender.hasPermission("simpleinvite.clear")) {
                    sender.sendMessage("Sorry, you don't have the permission to do that");
                    return true;
                }

                RegisteredUser user = RegisteredUser.getUser(args[1]);
                if (user == null) {
                    sender.sendMessage("the given player '" + args[1] + "' is not known");
                    return true;
                }

                commandExecuter.clear(sender, user.id);
                return true;
            }

        }

        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();

        Invite invite = Invite.getInviteForName(playerName);
        if (invite != null) {

            if (!invite.isValid(new Date())) {
                return; //invite is timed out
            }

            for (String command : Config.INVITATION_COMMANDS) {
                getServer().dispatchCommand(getServer().getConsoleSender(), command.replaceAll("%user%", playerName));
            }
            RegisteredUser.USERS.add(new RegisteredUser(event.getPlayer().getUniqueId(), playerName, invite.guarantorID, new Date(), invite.reason, 0));
            Invite.INVITATIONEN.remove(invite);

            event.getPlayer().sendMessage("Welcome on this Server");
            event.getPlayer().sendMessage(invite.playerName + "is your guarantor!");
        }
    }

    public Loader getDataLoader() {
        return dataLoader;
    }
}
