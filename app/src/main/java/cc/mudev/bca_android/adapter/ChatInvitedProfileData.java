package cc.mudev.bca_android.adapter;

public class ChatInvitedProfileData {
    public String name, imgSrc;
    public int profileId;

    public ChatInvitedProfileData (String name, String imgSrc, int profileId) {
        this.name = name;
        this.imgSrc = imgSrc;
        this.profileId = profileId;
    }
}