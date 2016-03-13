package b2c.simpleinvite.io;

import b2c.simpleinvite.Invite;
import b2c.simpleinvite.RegisteredUser;

import java.io.*;
import java.util.Date;

/**
 * Created by Peer on 05.03.2016.
 */
public class Loader {

    File file;

    private final static int VERSION = 0;

    public Loader(File file) {
        this.file = file;
    }

    public void readData() throws IOException {    	
    	Log.getCurrent().log("read data file:");
        DataInputStream input = new DataInputStream(new FileInputStream(file));

        int version = input.readInt();
        Log.getCurrent().log("  current version of data file is "+version);
        if (version != Loader.VERSION) {
        	Log.getCurrent().error("  Wrong DataFile version (version:" + version + " != localversion:" + Loader.VERSION + ")");
            throw new RuntimeException("Wrong DataFile version (version:" + version + " != localversion:" + Loader.VERSION + ")");
        }

        int nRegisteredUser = input.readInt();
        Log.getCurrent().log("  start reading "+nRegisteredUser+" registeredUser");
        for (int i = 0; i < nRegisteredUser; ++i) {
            RegisteredUser user = RegisteredUser.read(input);
            RegisteredUser.USERS.add(user);
            Log.getCurrent().log("    "+i+": "+user.toString());
        }

        int nInvite = input.readInt();
        Log.getCurrent().log("  start reading "+nInvite+" Invites");
        Date currentDate = new Date();
        for (int i = 0; i < nInvite; ++i) {
            Invite invite = Invite.read(input);
            Log.getCurrent().log("    "+i+": "+invite.toString());
            if (invite.isValid(currentDate)) {
                Invite.INVITATIONEN.add(invite);
                //load only if the invite is not timed out
            }else{
            	Log.getCurrent().log("  discarding this invite, time expired ("+currentDate.getTime()+"-"+invite.timestamp.getTime()+")");
            }

        }
        Log.getCurrent().log("LoadComplete");
        input.close();
    }

    public void writeData() throws IOException {
        DataOutputStream output = new DataOutputStream(new FileOutputStream(file));

        output.writeInt(Loader.VERSION);

        output.writeInt(RegisteredUser.USERS.size());

        for (RegisteredUser ru : RegisteredUser.USERS) {
            ru.write(output);
        }

        output.writeInt(Invite.INVITATIONEN.size());

        for (Invite invite : Invite.INVITATIONEN) {
            invite.write(output);
        }

        output.flush();
        output.close();
    }

}
