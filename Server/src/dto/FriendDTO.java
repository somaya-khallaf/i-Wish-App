package dto;

public class FriendDTO {

    private String Friendusername;
    private String Friendfullname;

    public FriendDTO(String Friendusername) {
        this.Friendusername = Friendusername;
    }
    public FriendDTO(String Friendusername, String Friendfullname) {
        this.Friendusername = Friendusername;
        this.Friendfullname = Friendfullname;
    }

    public void setFriendusername(String Friendusername) {
        this.Friendusername = Friendusername;
    }

    public void setFriendfullname(String Friendfullname) {
        this.Friendfullname = Friendfullname;
    }

    public String getFriendusername() {
        return Friendusername;
    }

    public String getFriendfullname() {
        return Friendfullname;
    }
}
