package com.hoperlady.Pojo;

public class RegisterLicenceUploadPojo {

    private String _id = "";
    private String name = "";
    private String imageName = "";
    private String imageWebUrl = "";
    private String licenceCount = "";
    private String expiryDate = "";
    private String string_status = "";
    private String file_typle = "";
    private String mandatory_status="";

    public String getMandatory_status() {
        return mandatory_status;
    }

    public void setMandatory_status(String mandatory_status) {
        this.mandatory_status = mandatory_status;
    }

    public String getFile_typle() {
        return file_typle;
    }

    public void setFile_typle(String file_typle) {
        this.file_typle = file_typle;
    }

    public String getString_status() {
        return string_status;
    }

    public void setString_status(String string_status) {
        this.string_status = string_status;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getLicenceCount() {
        return licenceCount;
    }

    public void setLicenceCount(String licenceCount) {
        this.licenceCount = licenceCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageWebUrl() {
        return imageWebUrl;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setImageWebUrl(String imageWebUrl) {
        this.imageWebUrl = imageWebUrl;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
