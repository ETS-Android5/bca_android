package cc.mudev.bca_android.adapter;

public class ProfileDetailCardListData {
    public int cardId;
    public String cardName, cardImageSrc;
    public boolean cardPrivateStatus;

    public ProfileDetailCardListData(int cardId, String cardName, String cardImageSrc, boolean cardPrivateStatus) {
        this.cardId = cardId;
        this.cardName = cardName;
        this.cardImageSrc = cardImageSrc;
        this.cardPrivateStatus = cardPrivateStatus;
    }
}
