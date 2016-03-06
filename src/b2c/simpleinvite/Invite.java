package b2c.simpleinvite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Peer on 05.03.2016.
 */
public class Invite {

    public final static ArrayList<Invite> INVITATIONEN = new ArrayList<Invite>();

    public final UUID guarantorID;
    public final Date timestamp;
    public final String playerName;
    public final String reason;

    public Invite(UUID guarantorID, Date timestamp, String playerName, String reason) {
        this.guarantorID = guarantorID;
        this.timestamp = timestamp;
        this.playerName = playerName;
        this.reason = reason;
    }

    public static Invite getInviteForName(String name){
        for(Invite invite: Invite.INVITATIONEN){
            if(invite.playerName.equalsIgnoreCase(name)){
                return invite;
            }
        }
        return null;
    }

    public static ArrayList<Invite> getInvitesFromPlayer(long periodOfTime, UUID playerUUID){
        ArrayList<Invite> invites = new ArrayList<Invite>();
        Date currentDate = new Date();
        for(Invite inv: Invite.INVITATIONEN){
            if(inv.guarantorID.equals(playerUUID)){
                if( currentDate.getTime()-inv.timestamp.getTime() <= periodOfTime){
                    invites.add(inv);
                }
            }
        }
        return invites;
    }

    public boolean isValid(Date currentDate) {
        return currentDate.getTime() - timestamp.getTime() <= Config.INVITATION_TIMEOUT*60000;
    }


    public static Invite read(DataInputStream input) throws IOException {
        long guarantorIDMost    = input.readLong();
        long guarantorIDLeast   = input.readLong();
        long timestamp          = input.readLong();
        String playerName       = input.readUTF();
        String reason           = input.readUTF();
        return new Invite( new UUID(guarantorIDMost, guarantorIDLeast), new Date(timestamp), playerName, reason);
    }

    public void write(DataOutputStream output) throws IOException {
        output.writeLong(guarantorID.getMostSignificantBits());
        output.writeLong(guarantorID.getLeastSignificantBits());
        output.writeLong(timestamp.getTime());
        output.writeUTF(playerName);
        output.writeUTF(reason);
    }


}
