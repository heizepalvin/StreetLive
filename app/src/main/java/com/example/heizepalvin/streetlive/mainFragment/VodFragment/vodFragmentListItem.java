package com.example.heizepalvin.streetlive.mainFragment.VodFragment;

public class vodFragmentListItem {

    private String vodThumnail;
    private String vodTitle;
    private String vodNickname;
    private String vodUrl;

    public vodFragmentListItem(String vodTitle,String vodNickname,String vodThumnail,String vodUrl){
        this.vodTitle = vodTitle;
        this.vodNickname = vodNickname;
        this.vodThumnail = vodThumnail;
        this.vodUrl = vodUrl;
    }

    public String getVodUrl() {
        return vodUrl;
    }

    public void setVodUrl(String vodUrl) {
        this.vodUrl = vodUrl;
    }

    public String getVodThumnail() {
        return vodThumnail;
    }

    public void setVodThumnail(String vodThumnail) {
        this.vodThumnail = vodThumnail;
    }

    public String getVodTitle() {
        return vodTitle;
    }

    public void setVodTitle(String vodTitle) {
        this.vodTitle = vodTitle;
    }

    public String getVodNickname() {
        return vodNickname;
    }

    public void setVodNickname(String vodNickname) {
        this.vodNickname = vodNickname;
    }
}
