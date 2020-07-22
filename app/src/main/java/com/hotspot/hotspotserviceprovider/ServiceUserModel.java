package com.hotspot.hotspotserviceprovider;

public class ServiceUserModel {

   private String Name;
    private String mail;
    private String phn;
    private  String add1;
    private String add2;
    private String add3;
    private String Servicetype;
    private String pushkey;
    private String Profileimage;
    private String uid;
    private String WalletBalance;
    private String ReferralCode;
    private String ReferredBy;

    public String getReferralCode() {
        return ReferralCode;
    }

    public void setReferralCode(String referralCode) {
        ReferralCode = referralCode;
    }

    public String getReferredBy() {
        return ReferredBy;
    }

    public void setReferredBy(String referredBy) {
        ReferredBy = referredBy;
    }

    public String getWalletBalance() {
        return WalletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        WalletBalance = walletBalance;
    }

    public String getPushkey() {
        return pushkey;
    }

    public void setPushkey(String pushkey) {
        this.pushkey = pushkey;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getProfileimage() {
        return Profileimage;
    }

    public void setProfileimage(String profileimage) {
        Profileimage = profileimage;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhn() {
        return phn;
    }

    public void setPhn(String phn) {
        this.phn = phn;
    }

    public String getAdd1() {
        return add1;
    }

    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    public String getAdd2() {
        return add2;
    }

    public void setAdd2(String add2) {
        this.add2 = add2;
    }

    public String getAdd3() {
        return add3;
    }

    public void setAdd3(String add3) {
        this.add3 = add3;
    }

    public String getServicetype() {
        return Servicetype;
    }

    public void setServicetype(String servicetype) {
        Servicetype = servicetype;
    }
}
