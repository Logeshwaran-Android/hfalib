package com.hoperlady.Pojo;

public class AvailableDocumentPojo {

    private String Doc_id="";
    private String Doc_name="";
    private String Doc_Mandatory="";

    public void setDocId(String doc_id){

        Doc_id=doc_id;
    }

    public String getDocID(){

        return Doc_id;
    }

    public void setDocName(String doc_name){

        Doc_name=doc_name;
    }

    public String getDocName(){

        return Doc_name;
    }

    public void setDocMandatory(String mandatory_status){

        Doc_Mandatory=mandatory_status;
    }

    public String getDocMandatory(){

        return Doc_Mandatory;
    }
}
