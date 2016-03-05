package b2c.simpleinvite.io;

import b2c.simpleinvite.Invite;
import b2c.simpleinvite.RegisteredUser;
import b2c.simpleinvite.SimpleInvite;
import org.bukkit.Server;

import java.io.*;

/**
 * Created by Peer on 05.03.2016.
 */
public class Loader {

    File file;

    public Loader(File file){
        this.file = file;
    }

    public void readData() throws IOException {
        DataInputStream input = new DataInputStream(new FileInputStream(file));
        int nRegisteredUser = input.readInt();
        for(int i = 0; i < nRegisteredUser; ++i){
            RegisteredUser user = RegisteredUser.read(input);
            RegisteredUser.USERS.add(user);
        }

        int nInvite = input.readInt();
        for(int i = 0; i < nInvite; ++i){
            Invite invite = Invite.read(input);
            Invite.INVITATIONEN.add(invite);
        }
        input.close();
    }

    public void writeData() throws IOException {
        DataOutputStream output = new DataOutputStream(new FileOutputStream(file));
        output.writeInt(RegisteredUser.USERS.size());

        for(RegisteredUser ru: RegisteredUser.USERS){
            ru.write(output);
        }

        output.writeInt(Invite.INVITATIONEN.size());

        for(Invite invite: Invite.INVITATIONEN){
            invite.write(output);
        }

        output.flush();
        output.close();
    }

}
