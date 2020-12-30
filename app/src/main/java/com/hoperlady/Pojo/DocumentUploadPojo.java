package com.hoperlady.Pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class DocumentUploadPojo implements Serializable{

    private ArrayList<RegisterLicenceUploadPojo> pojoArrayList;

    public ArrayList<RegisterLicenceUploadPojo> getPojoArrayList() {
        return pojoArrayList;
    }

    public void setPojoArrayList(ArrayList<RegisterLicenceUploadPojo> pojoArrayList) {
        this.pojoArrayList = pojoArrayList;
    }
}
