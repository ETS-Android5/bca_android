package cc.mudev.bca_android.adapter;

public class ProfileFollowingData {
    public String name, description, imageUrl;
    public int profileId;

    public ProfileFollowingData (String name, String description, int profileId, String imageUrl) {
        this.name = name;
        this.description = description;
        this.profileId = profileId;
        this.imageUrl = imageUrl;
    }
}
