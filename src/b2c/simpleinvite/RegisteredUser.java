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
public class RegisteredUser {

    public final static ArrayList<RegisteredUser> USERS = new ArrayList<RegisteredUser>();

    public final UUID id;
    public final String name;
    public final UUID invitedBy;
    public final Date joinDate;
    public final String reason;

    int strikes;

    public RegisteredUser(UUID id, String name, UUID invitedBy, Date joinDate, String reason, int strikes) {
        this.id = id;
        this.name = name;
        this.invitedBy = invitedBy;
        this.joinDate = joinDate;
        this.reason = reason;

        this.strikes = strikes;
    }

    public static ArrayList<RegisteredUser> getUserInvitedBy(long periodOfTime, UUID invitator) {
        ArrayList<RegisteredUser> user = new ArrayList<RegisteredUser>();
        Date currentTime = new Date();
        for (RegisteredUser ru : RegisteredUser.USERS) {
            if (ru.invitedBy.equals(invitator)) {
                if (currentTime.getTime() - ru.joinDate.getTime() <= periodOfTime) {
                    user.add(ru);
                }
            }
        }
        return user;
    }


    public static RegisteredUser getUserBy(UUID id) {
        for (RegisteredUser ru : RegisteredUser.USERS) {
            if (ru.id.equals(id)) {
                return ru;
            }
        }
        return null;
    }

    public static RegisteredUser getUser(String name) {
        for (RegisteredUser ru : RegisteredUser.USERS) {
            if (ru.name.equalsIgnoreCase(name)) {
                return ru;
            }
        }
        return null;
    }


    public static RegisteredUser read(DataInputStream input) throws IOException {
        long UUIDMostSig = input.readLong();
        long UUIDLeastSig = input.readLong();
        String name = input.readUTF();
        long invitedByUUIDMost = input.readLong();
        long invitedByUUIDLeast = input.readLong();
        long joinDate = input.readLong();
        String reason = input.readUTF();

        int strikes = input.readInt();

        return new RegisteredUser(new UUID(UUIDMostSig, UUIDLeastSig), name, new UUID(invitedByUUIDMost, invitedByUUIDLeast), new Date(joinDate), reason, strikes);
    }

    public void write(DataOutputStream output) throws IOException {
        output.writeLong(id.getMostSignificantBits());
        output.writeLong(id.getLeastSignificantBits());
        output.writeUTF(name);
        output.writeLong(invitedBy.getMostSignificantBits());
        output.writeLong(invitedBy.getLeastSignificantBits());
        output.writeLong(joinDate.getTime());
        output.writeUTF(reason);

        output.writeInt(strikes);
    }
}
