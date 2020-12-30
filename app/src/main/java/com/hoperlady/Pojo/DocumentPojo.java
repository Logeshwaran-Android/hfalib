package com.hoperlady.Pojo;

import android.graphics.Bitmap;

public class DocumentPojo{

    private String _id = "";
    private String name = "";
    private String imageName = "";
    private String imageWebUrl = "";
    private String licenceCount = "";
    private String expiryDate = "";
    private byte[] byteImage = new byte[0];
    private Bitmap finalPic;
    private String string_status = "";
    private String file_typle = "";
    private String Document_path="";


    public String getDocumentPath() {
        return Document_path;
    }

    public void setDocumentPath(String Document_path) {
        this.Document_path = Document_path;
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

    public Bitmap getFinalPic() {
        return finalPic;
    }

    public void setFinalPic(Bitmap finalPic) {
        this.finalPic = finalPic;
    }

    public byte[] getByteImage() {
        return byteImage;
    }

    public void setByteImage(byte[] byteImage) {
        this.byteImage = byteImage;
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
