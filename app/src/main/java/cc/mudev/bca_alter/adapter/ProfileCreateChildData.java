package cc.mudev.bca_alter.adapter;

public class ProfileCreateChildData implements Comparable<ProfileCreateChildData> {
    public int index;
    public String key, value;

    public ProfileCreateChildData(int index, String key, String value) {
        this.index = index;
        this.key = (key != null) ? key : "";
        this.value = (value != null) ? value : "";
    }

    @Override
    public int compareTo(ProfileCreateChildData o) {
        if (o.index < this.index) {
            return 1;
        } else if (o.index > this.index) {
            return -1;
        }
        return 0;
    }
}
