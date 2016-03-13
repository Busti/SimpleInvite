package b2c.simpleinvite.io;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import b2c.simpleinvite.Invite;
import b2c.simpleinvite.RegisteredUser;

public class FileConfigurationLoader {
	
	final static int VERSION = 0;
	
	public void save(FileConfiguration dataFile){
		dataFile.set("version", FileConfigurationLoader.VERSION);
		
		for(RegisteredUser ru: RegisteredUser.USERS){
			String user = "registeredUser."+ru.name;
			
			//dataFile.set(user+".name", ru.name);
			dataFile.set(user+".id", ru.id.toString());
			dataFile.set(user+".invite", ru.invitedBy.toString());
			dataFile.set(user+".reason", ru.reason);
			dataFile.set(user+".joinDate", ru.joinDate.getTime());
			dataFile.set(user+".strikes", ru.getStrikes());
		}
		
		for(Invite invite: Invite.INVITATIONEN){
			String user = "invite."+invite.playerName;
			
			//dataFile.set(user+".name", invite.playerName);
			dataFile.set(user+".guarantorID", invite.guarantorID.toString());
			dataFile.set(user+".reason", invite.reason);
			dataFile.set(user+".timestamp", invite.timestamp.getTime());
		}
		
	}
	public void read(FileConfiguration dataFile){
		Log.getCurrent().log("loading dataFile (YML)");
		
		int version = dataFile.getInt("version");
		Log.getCurrent().log("datafile version "+version);
		
		if(version > FileConfigurationLoader.VERSION){
			Log.getCurrent().error("the datafileversion is to high. Please update this plugin");
			return;
		}
		Log.getCurrent().log("reading RegisteredUser section");
		ConfigurationSection registeredUserSection = dataFile.getConfigurationSection("registeredUser");
		
		if(registeredUserSection != null){
			
			Set<String> names = registeredUserSection.getKeys(false);
			
			for(String name: names){
				RegisteredUser user = new RegisteredUser(
						UUID.fromString(registeredUserSection.getString(name+".id")), 
						name, 
						UUID.fromString(registeredUserSection.getString(name+".invite")), 
						new Date(registeredUserSection.getLong(name+".joinDate")), 
						registeredUserSection.getString(name+".reason"), 
						registeredUserSection.getInt(name+".strikes"));
				
						RegisteredUser.USERS.add(user);
						Log.getCurrent().log("    "+user.toString());
	            
			}
		}
		

		
		
		Log.getCurrent().log("reading Invite section");
		ConfigurationSection inviteSection = dataFile.getConfigurationSection("invite");
		
		if(inviteSection != null){
			
			Set<String> names = inviteSection.getKeys(false);
			
			Date currentDate = new Date();
	        for(String name: names){
	        	
	        	Invite invite = new Invite(
	        			UUID.fromString(inviteSection.getString(name+".guarantorID")),
	        			new Date(inviteSection.getLong(name+".timestamp")),
	        			name, 
	        			inviteSection.getString(name+".reason"));
	        	
	        	Log.getCurrent().log("    "+invite.toString());
	        	if(invite.isValid(currentDate)){
	        		Invite.INVITATIONEN.add(invite);
	        	}else{
	        		Log.getCurrent().log("    discard, invite expired");
	        	}
	        	
	        }
	        
		}
		
        
        Log.getCurrent().log("load complete");
		
	}

}
