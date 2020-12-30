package com.hoperlady.Pojo;

public class Category_Pojo {
    private String Category_id;
    private String SubCategory_id;
    private String CategoryName = "";
    private String SubCategory_Name = "";
    private String Category_Image = "";
    private String Hourly_Rate = "";
    private String ratetype = "";
    private String ratetypestatus = "";


    public String getCategory_id() {
        return Category_id;
    }

    public void setCategory_id(String category_id) {
        Category_id = category_id;
    }

    public String getSubCategory_id() {
        return SubCategory_id;
    }

    public void setSubCategory_id(String subCategory_id) {
        SubCategory_id = subCategory_id;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getSubCategory_Name() {
        return SubCategory_Name;
    }

    public void setSubCategory_Name(String subCategory_Name) {
        SubCategory_Name = subCategory_Name;
    }

    public String getCategory_Image() {
        return Category_Image;
    }

    public void setCategory_Image(String category_Image) {
        Category_Image = category_Image;
    }

    public String getHourly_Rate() {
        return Hourly_Rate;
    }

    public void setHourly_Rate(String hourly_Rate) {
        Hourly_Rate = hourly_Rate;
    }

    public String getRatetype() {
        return ratetype;
    }

    public void setRatetype(String ratetype) {
        this.ratetype = ratetype;
    }

    public String getRatetypestatus() {
        return ratetypestatus;
    }

    public void setRatetypestatus(String ratetypestatus) {
        this.ratetypestatus = ratetypestatus;
    }
}
