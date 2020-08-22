package com.hotspot.hotspotserviceprovider.modelClasses;

public class ShopDetailModel {
    String ShopImage;
    String ownerName;
    String shopName;
    String ownerContact;
    String shopCategory;
    String ownerMail;
    String shopAddress;
    String state;
    String city;
    String pushkey;
    String pincode;

    public String getMedicalStoreCategory() {
        return medicalStoreCategory;
    }

    public void setMedicalStoreCategory(String medicalStoreCategory) {
        this.medicalStoreCategory = medicalStoreCategory;
    }

    public String getDoctorCategory() {
        return doctorCategory;
    }

    public void setDoctorCategory(String doctorCategory) {
        this.doctorCategory = doctorCategory;
    }

    public String getEleopathicCategory() {
        return eleopathicCategory;
    }

    public void setEleopathicCategory(String eleopathicCategory) {
        this.eleopathicCategory = eleopathicCategory;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    String userID;
    Boolean verificationStatus;
    String  medicalStoreCategory,doctorCategory,eleopathicCategory;

    public Boolean getVerificationStatus() {
        return verificationStatus;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public void setVerificationStatus(Boolean verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getPushkey() {
        return pushkey;
    }

    public void setPushkey(String pushkey) {
        this.pushkey = pushkey;
    }

    public String getShopImage() {
        return ShopImage;
    }

    public void setShopImage(String shopImage) {
        ShopImage = shopImage;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getOwnerContact() {
        return ownerContact;
    }

    public void setOwnerContact(String ownerContact) {
        this.ownerContact = ownerContact;
    }

    public String getShopCategory() {
        return shopCategory;
    }

    public void setShopCategory(String shopCategory) {
        this.shopCategory = shopCategory;
    }

    public String getOwnerMail() {
        return ownerMail;
    }

    public void setOwnerMail(String ownerMail) {
        this.ownerMail = ownerMail;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
