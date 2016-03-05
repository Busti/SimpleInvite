package b2c.simpleinvite;

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

    public RegisteredUser(int id, String name, long invitedBy, long joinDate) {
        this.id = id;
        this.name = name;
        this.invitedBy = invitedBy;
        this.joinDate = joinDate;
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



}
