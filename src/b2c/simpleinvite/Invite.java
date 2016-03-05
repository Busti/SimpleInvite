package b2c.simpleinvite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Peer on 05.03.2016.
 */
public class Invite {

    public final static ArrayList<Invite> INVITATIONEN = new ArrayList<Invite>();

    public final long guarantorID;
    public final long timestamp;
    public final String playerName;

    public Invite(long guarantorID, long timestamp, String playerName) {
        this.guarantorID = guarantorID;
        this.timestamp = timestamp;
        this.playerName = playerName;
    }


    public static Invite read(DataInputStream input) throws IOException {
        long guarantorID = input.readLong();
        long timestamp = input.readLong();
        String playerName = input.readUTF();
        return new Invite(guarantorID, timestamp, playerName);
    }

    public void write(DataOutputStream output) throws IOException {
        output.writeLong(guarantorID);
        output.writeLong(timestamp);
        output.writeUTF(playerName);
    }

}
