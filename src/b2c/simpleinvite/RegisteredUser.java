package b2c.simpleinvite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Peer on 05.03.2016.
 */
public class RegisteredUser {

    public static ArrayList<RegisteredUser> USERS = new ArrayList<RegisteredUser>();

    public final long   id;
    public final String name;
    public final long   invitedBy;
    public final long   joinDate;
    public final String reason;

    public RegisteredUser(long id, String name, long invitedBy, long joinDate, String reason) {
        this.id = id;
        this.name = name;
        this.invitedBy = invitedBy;
        this.joinDate = joinDate;
        this.reason = reason;
    }


    public static RegisteredUser getUserByID(long id){
        for(RegisteredUser ru: RegisteredUser.USERS){
            if(ru.id == id){
                return ru;
            }
        }
        return null;
    }

    public static long getFreeID(){
        for(long current = 0; !isIDFree(current); ++current){
            return current;
        }
        throw new RuntimeException("all IDs are in use");
    }

    public static boolean isIDFree(long id){
        for(RegisteredUser ru: RegisteredUser.USERS){
            if(ru.id == id){
                return false;
            }
        }
        return true;
    }


    public static RegisteredUser read(DataInputStream input) throws IOException {
        long id         = input.readLong();
        String name     = input.readUTF();
        long invitedBy  = input.readLong();
        long joinDate   = input.readLong();
        String reason   = input.readUTF();
        return new RegisteredUser(id, name, invitedBy, joinDate, reason);
    }

    public void write(DataOutputStream output) throws IOException {
        output.writeLong(id);
        output.writeUTF(name);
        output.writeLong(invitedBy);
        output.writeLong(joinDate);
        output.writeUTF(reason);
    }
}
