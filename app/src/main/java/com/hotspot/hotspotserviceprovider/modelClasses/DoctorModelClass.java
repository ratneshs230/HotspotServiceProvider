package com.hotspot.hotspotserviceprovider.modelClasses;

public class DoctorModelClass {

    String doctorName;
    String doctorQualification;
    String uid;
    String registeredPhone;
    String fromTime,toTime;
    String doctorType,doctorSpecialization;
    String servicType;

    public String getServicType() {
        return servicType;
    }

    public void setServicType(String servicType) {
        this.servicType = servicType;
    }

    public String getDoctorType() {
        return doctorType;
    }

    public void setDoctorType(String doctorType) {
        this.doctorType = doctorType;
    }

    public String getDoctorSpecialization() {
        return doctorSpecialization;
    }

    public void setDoctorSpecialization(String doctorSpecialization) {
        this.doctorSpecialization = doctorSpecialization;
    }

    public String getFromTime() {
        return fromTime;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public Boolean getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(Boolean verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    Boolean verificationStatus;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRegisteredPhone() {
        return registeredPhone;
    }

    public void setRegisteredPhone(String registeredPhone) {
        this.registeredPhone = registeredPhone;
    }

    String doctorHigherQualification;

    public String getDoctorHigherQualification() {
        return doctorHigherQualification;
    }

    public void setDoctorHigherQualification(String doctorHigherQualification) {
        this.doctorHigherQualification = doctorHigherQualification;
    }

    String dotorExperience;
    String doctorRegistrationId;
    String doctorProfilePic;
    String pushkey;

    public String getPushkey() {
        return pushkey;
    }

    public void setPushkey(String pushkey) {
        this.pushkey = pushkey;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorQualification() {
        return doctorQualification;
    }

    public void setDoctorQualification(String doctorQualification) {
        this.doctorQualification = doctorQualification;
    }

    public String getDotorExperience() {
        return dotorExperience;
    }

    public void setDotorExperience(String dotorExperience) {
        this.dotorExperience = dotorExperience;
    }

    public String getDoctorRegistrationId() {
        return doctorRegistrationId;
    }

    public void setDoctorRegistrationId(String doctorRegistrationId) {
        this.doctorRegistrationId = doctorRegistrationId;
    }

    public String getDoctorProfilePic() {
        return doctorProfilePic;
    }

    public void setDoctorProfilePic(String doctorProfilePic) {
        this.doctorProfilePic = doctorProfilePic;
    }
}
