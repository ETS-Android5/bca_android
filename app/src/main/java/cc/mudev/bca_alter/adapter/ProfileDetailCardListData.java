package cc.mudev.bca_alter.adapter;

public class ProfileDetailCardListData {
    public int cardId;
    public String cardName, cardImageSrc;

    public ProfileDetailCardListData(int cardId, String cardName, String cardImageSrc) {
        this.cardId = cardId;
        this.cardName = cardName;
        this.cardImageSrc = cardImageSrc;
    }
}
