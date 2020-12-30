package com.hoperlady.app;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.hoperlady.Pojo.AvailabileArrayPojo;
import com.hoperlady.Pojo.CategoriesDataPojo;
import com.hoperlady.Pojo.DocumentUploadPojo;
import com.hoperlady.Pojo.OldAvailabileArrayPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SessionManagerRegistration {

    public static final String PREFERENCENAME = "REGISTRATIONTASKER";
    public static final String PREFERENCENAME_AVAILABILITY = "REGISTRATION_AVAILABILITY";
    public static final String KEY_TASKER_TYPE = "taskertype";
    public static final String KEY_EMAIL_ID = "emailid";
    public static final String KEY_PHONE_NUMBER = "phonenumber";
    public static final String KEY_COUNTRY_CODE = "countrycode";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FIRST_NAME = "firstname";
    public static final String KEY_LAST_NAME = "lastname";
    public static final String KEY_DATE_OF_BIRTH = "dateofbirth";
    public static final String KEY_NATIONAL_REG_NO = "nationalregno";
    public static final String KEY_TAXPAYER_IDENTIFICATION_NO = "taxpayeridentificationno";
    public static final String KEY_HOUSE_NO = "houseno";
    public static final String KEY_STREET_NAME = "streetname";
    public static final String KEY_PROVINCE = "province";
    public static final String KEY_TOWN = "town";
    public static final String KEY_ZIPCODE = "zipcode";
    public static final String KEY_SUN_DAY = "sunday";
    public static final String KEY_MON_DAY = "monday";
    public static final String KEY_TUE_DAY = "tuesday";
    public static final String KEY_WED_DAY = "wednessday";
    public static final String KEY_THUR_DAY = "thursday";
    public static final String KEY_FRI_DAY = "friday";
    public static final String KEY_SAT_DAY = "saturday";
    public static final String KEY_SUN_FROM_TIME = "sundayfrom";
    public static final String KEY_MON_FROM_TIME = "mondayfrom";
    public static final String KEY_TUE_FROM_TIME = "tuesdayfrom";
    public static final String KEY_WED_FROM_TIME = "wednessdayfrom";
    public static final String KEY_THUR_FROM_TIME = "thursdayfrom";
    public static final String KEY_FRI_FROM_TIME = "fridayfrom";
    public static final String KEY_SAT_FROM_TIME = "saturdayfrom";
    public static final String KEY_SUN_TO_TIME = "sundayto";
    public static final String KEY_MON_TO_TIME = "mondayto";
    public static final String KEY_TUE_TO_TIME = "tuesdayto";
    public static final String KEY_WED_TO_TIME = "wednessdayto";
    public static final String KEY_THUR_TO_TIME = "thursdayto";
    public static final String KEY_FRI_TO_TIME = "fridayto";
    public static final String KEY_SAT_TO_TIME = "saturdayto";
    public static final String KEY_DESCRIBE_YOURSELF = "describeyourself";
    public static final String KEY_WORK_EXPERIENCE = "workexperience";
    public static final String KEY_WHYCLIENT_HIRE = "whyclienthire";
    public static final String KEY_LEVELOF_EDUCATION = "levelofeducation";
    public static final String KEY_CATEGORY_ID = "categoryid";
    public static final String KEY_SUB_CATEGORY_ID = "subcategoryid";
    public static final String KEY_TASK_CATEGORY_ID = "taskcategoryid";
    public static final String KEY_SUB_TASK_CATEGORY_ID = "subtaskcategoryid";
    public static final String KEY_AVAILABILITY_JAON_ARRAY = "availabilityjaonarray";
    public static final String KEY_PLACE_OF_OPERATION = "placeofoperation";
    public static final String KEY_COMPANY_NAME = "companyname";
    public static final String KEY_COMPANY_REG_NO = "companyregno";
    public static final String KEY_COMPANY_TAXPAYER_IDENTIFICATION_NO = "companyidentificationo";
    public static final String KEY_BASE_IMAGE = "base64image";
    public static final String KEY_AVAILABIL_DAYS = "availabledays";
    public static final String KEY_CATEGORIES = "allcategories";
    public static final String KEY_PERSONAL_DETAILS = "personaldetails";
    public static final String KEY_DOCUEMNT = "document";
    public static final String KEY_DOCUEMNT_STATUS = "document_status";

    public static final String driver_late = "LAT";
    public static final String driver_long = "LONG";


    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesAvailability;
    SharedPreferences.Editor editor;
    SharedPreferences.Editor editorAvailability;
    Context context;

    public SessionManagerRegistration(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCENAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public SessionManagerRegistration(Context context, String stringForAvailabilitySession) {
        this.context = context;
        sharedPreferencesAvailability = context.getSharedPreferences(PREFERENCENAME_AVAILABILITY, Context.MODE_PRIVATE);
        editorAvailability = sharedPreferencesAvailability.edit();
    }




    public void setDriver_late(String driverlat) {
        editor.putString(driverlat, driverlat);
        editor.commit();
    }

    public String getDriver_late() {
        return sharedPreferences.getString(driver_late, "");
    }

    public void setDriver_long(String driverlat) {
        editor.putString(driver_long, driverlat);
        editor.commit();
    }

    public String getDriver_long() {
        return sharedPreferences.getString(driver_long, "");
    }

    public void setRegistrationFirstPage(String stringTypeOfTasker, String stringEmailId, String stringCountryCode, String stringPhoneNumber, String stringPassword) {
        editor.putString(KEY_TASKER_TYPE, stringTypeOfTasker);
        editor.putString(KEY_EMAIL_ID, stringEmailId);
        editor.putString(KEY_COUNTRY_CODE, stringCountryCode);
        editor.putString(KEY_PHONE_NUMBER, stringPhoneNumber);
        editor.putString(KEY_PASSWORD, stringPassword);
        editor.commit();
    }


    public HashMap<String, String> getRegistrationFirstPage() {
        HashMap<String, String> hashMapFirstPageDetails = new HashMap<>();
        hashMapFirstPageDetails.put(KEY_TASKER_TYPE, sharedPreferences.getString(KEY_TASKER_TYPE, ""));
        hashMapFirstPageDetails.put(KEY_EMAIL_ID, sharedPreferences.getString(KEY_EMAIL_ID, ""));
        hashMapFirstPageDetails.put(KEY_COUNTRY_CODE, sharedPreferences.getString(KEY_COUNTRY_CODE, ""));
        hashMapFirstPageDetails.put(KEY_PHONE_NUMBER, sharedPreferences.getString(KEY_PHONE_NUMBER, ""));
        hashMapFirstPageDetails.put(KEY_PASSWORD, sharedPreferences.getString(KEY_PASSWORD, ""));
        return hashMapFirstPageDetails;
    }

    public void setRegistrationSecondPage(String stringFirstname, String stringLastName, String stringDateOfBirth, String stringNationalRegNo, String stringTapayerIdentificationNo) {
        editor.putString(KEY_FIRST_NAME, stringFirstname);
        editor.putString(KEY_LAST_NAME, stringLastName);
        editor.putString(KEY_DATE_OF_BIRTH, stringDateOfBirth);
        editor.putString(KEY_NATIONAL_REG_NO, stringNationalRegNo);
        editor.putString(KEY_TAXPAYER_IDENTIFICATION_NO, stringTapayerIdentificationNo);
        editor.commit();
    }


    public HashMap<String, String> getRegistrationSecondPage() {
        HashMap<String, String> hashMapSecondPageDetails = new HashMap<>();
        hashMapSecondPageDetails.put(KEY_FIRST_NAME, sharedPreferences.getString(KEY_FIRST_NAME, ""));
        hashMapSecondPageDetails.put(KEY_LAST_NAME, sharedPreferences.getString(KEY_LAST_NAME, ""));
        hashMapSecondPageDetails.put(KEY_DATE_OF_BIRTH, sharedPreferences.getString(KEY_DATE_OF_BIRTH, ""));
        hashMapSecondPageDetails.put(KEY_NATIONAL_REG_NO, sharedPreferences.getString(KEY_NATIONAL_REG_NO, ""));
        hashMapSecondPageDetails.put(KEY_TAXPAYER_IDENTIFICATION_NO, sharedPreferences.getString(KEY_TAXPAYER_IDENTIFICATION_NO, ""));
        return hashMapSecondPageDetails;
    }


    public void setRegistrationAsCompany(String stringCompanyName, String stringCompanyRegNo, String stringTapayerIdentificationNo) {
        editor.putString(KEY_COMPANY_NAME, stringCompanyName);
        editor.putString(KEY_COMPANY_REG_NO, stringCompanyRegNo);
        editor.putString(KEY_COMPANY_TAXPAYER_IDENTIFICATION_NO, stringTapayerIdentificationNo);
        editor.commit();
    }


    public HashMap<String, String> getRegistrationAsCompany() {
        HashMap<String, String> hashMapCompanyDetails = new HashMap<>();
        hashMapCompanyDetails.put(KEY_COMPANY_NAME, sharedPreferences.getString(KEY_COMPANY_NAME, ""));
        hashMapCompanyDetails.put(KEY_COMPANY_REG_NO, sharedPreferences.getString(KEY_COMPANY_REG_NO, ""));
        hashMapCompanyDetails.put(KEY_COMPANY_TAXPAYER_IDENTIFICATION_NO, sharedPreferences.getString(KEY_COMPANY_TAXPAYER_IDENTIFICATION_NO, ""));
        return hashMapCompanyDetails;
    }


    public void setRegistrationThirdPage(String stringHouseNo, String stringStreetName, String stringProvince, String stringTown, String stringZipCode) {
        editor.putString(KEY_HOUSE_NO, stringHouseNo);
        editor.putString(KEY_STREET_NAME, stringStreetName);
        editor.putString(KEY_PROVINCE, stringProvince);
        editor.putString(KEY_TOWN, stringTown);
        editor.putString(KEY_ZIPCODE, stringZipCode);
        editor.commit();
    }


    public HashMap<String, String> getRegistrationThirdPage() {
        HashMap<String, String> hashMapThirdPageDetails = new HashMap<>();
        hashMapThirdPageDetails.put(KEY_HOUSE_NO, sharedPreferences.getString(KEY_HOUSE_NO, ""));
        hashMapThirdPageDetails.put(KEY_STREET_NAME, sharedPreferences.getString(KEY_STREET_NAME, ""));
        hashMapThirdPageDetails.put(KEY_PROVINCE, sharedPreferences.getString(KEY_PROVINCE, ""));
        hashMapThirdPageDetails.put(KEY_TOWN, sharedPreferences.getString(KEY_TOWN, ""));
        hashMapThirdPageDetails.put(KEY_ZIPCODE, sharedPreferences.getString(KEY_ZIPCODE, ""));
        return hashMapThirdPageDetails;
    }


    public void setRegistrationFourthPage(String stringYourself, String stringWorkExperience, String stringWhyClientHire, String stringHighestLevelOfEducation) {
        editor.putString(KEY_DESCRIBE_YOURSELF, stringYourself);
        editor.putString(KEY_WORK_EXPERIENCE, stringWorkExperience);
        editor.putString(KEY_WHYCLIENT_HIRE, stringWhyClientHire);
        editor.putString(KEY_LEVELOF_EDUCATION, stringHighestLevelOfEducation);
        editor.commit();
    }


    public HashMap<String, String> getRegistrationFourthPage() {
        HashMap<String, String> hashMapFourthPageDetails = new HashMap<>();
        hashMapFourthPageDetails.put(KEY_DESCRIBE_YOURSELF, sharedPreferences.getString(KEY_DESCRIBE_YOURSELF, ""));
        hashMapFourthPageDetails.put(KEY_WORK_EXPERIENCE, sharedPreferences.getString(KEY_WORK_EXPERIENCE, ""));
        hashMapFourthPageDetails.put(KEY_WHYCLIENT_HIRE, sharedPreferences.getString(KEY_WHYCLIENT_HIRE, ""));
        hashMapFourthPageDetails.put(KEY_LEVELOF_EDUCATION, sharedPreferences.getString(KEY_LEVELOF_EDUCATION, ""));
        return hashMapFourthPageDetails;
    }


    public void setDetailsSunDay(String stringDay, String stringFromTime, String stringToTime) {
        editorAvailability.putString(KEY_SUN_DAY, stringDay);
        editorAvailability.putString(KEY_SUN_FROM_TIME, stringFromTime);
        editorAvailability.putString(KEY_SUN_TO_TIME, stringToTime);
        editorAvailability.commit();
    }


    public HashMap<String, String> getDetailsSunDay() {
        HashMap<String, String> hashMapDetails = new HashMap<>();
        hashMapDetails.put(KEY_SUN_DAY, sharedPreferencesAvailability.getString(KEY_SUN_DAY, ""));
        hashMapDetails.put(KEY_SUN_FROM_TIME, sharedPreferencesAvailability.getString(KEY_SUN_FROM_TIME, ""));
        hashMapDetails.put(KEY_SUN_TO_TIME, sharedPreferencesAvailability.getString(KEY_SUN_TO_TIME, ""));
        return hashMapDetails;
    }

    public void clearSunday() {
        editorAvailability.remove(KEY_SUN_DAY);
        editorAvailability.remove(KEY_SUN_FROM_TIME);
        editorAvailability.remove(KEY_SUN_TO_TIME);
        editorAvailability.commit();
    }


    public void setDetailsMonDay(String stringDay, String stringFromTime, String stringToTime) {
        editorAvailability.putString(KEY_MON_DAY, stringDay);
        editorAvailability.putString(KEY_MON_FROM_TIME, stringFromTime);
        editorAvailability.putString(KEY_MON_TO_TIME, stringToTime);
        editorAvailability.commit();
    }


    public HashMap<String, String> getDetailsMonDay() {
        HashMap<String, String> hashMapDetails = new HashMap<>();
        hashMapDetails.put(KEY_MON_DAY, sharedPreferencesAvailability.getString(KEY_MON_DAY, ""));
        hashMapDetails.put(KEY_MON_FROM_TIME, sharedPreferencesAvailability.getString(KEY_MON_FROM_TIME, ""));
        hashMapDetails.put(KEY_MON_TO_TIME, sharedPreferencesAvailability.getString(KEY_MON_TO_TIME, ""));
        return hashMapDetails;
    }

    public void clearMonday() {
        editorAvailability.remove(KEY_MON_DAY);
        editorAvailability.remove(KEY_MON_FROM_TIME);
        editorAvailability.remove(KEY_MON_TO_TIME);
        editorAvailability.commit();
    }


    public void setDetailsTueDay(String stringDay, String stringFromTime, String stringToTime) {
        editorAvailability.putString(KEY_TUE_DAY, stringDay);
        editorAvailability.putString(KEY_TUE_FROM_TIME, stringFromTime);
        editorAvailability.putString(KEY_TUE_TO_TIME, stringToTime);
        editorAvailability.commit();
    }


    public HashMap<String, String> getDetailsTueDay() {
        HashMap<String, String> hashMapDetails = new HashMap<>();
        hashMapDetails.put(KEY_TUE_DAY, sharedPreferencesAvailability.getString(KEY_TUE_DAY, ""));
        hashMapDetails.put(KEY_TUE_FROM_TIME, sharedPreferencesAvailability.getString(KEY_TUE_FROM_TIME, ""));
        hashMapDetails.put(KEY_TUE_TO_TIME, sharedPreferencesAvailability.getString(KEY_TUE_TO_TIME, ""));
        return hashMapDetails;
    }

    public void clearTuesday() {
        editorAvailability.remove(KEY_TUE_DAY);
        editorAvailability.remove(KEY_TUE_FROM_TIME);
        editorAvailability.remove(KEY_TUE_TO_TIME);
        editorAvailability.commit();
    }


    public void setDetailsWedDay(String stringDay, String stringFromTime, String stringToTime) {
        editorAvailability.putString(KEY_WED_DAY, stringDay);
        editorAvailability.putString(KEY_WED_FROM_TIME, stringFromTime);
        editorAvailability.putString(KEY_WED_TO_TIME, stringToTime);
        editorAvailability.commit();
    }


    public HashMap<String, String> getDetailsWedDay() {
        HashMap<String, String> hashMapDetails = new HashMap<>();
        hashMapDetails.put(KEY_WED_DAY, sharedPreferencesAvailability.getString(KEY_WED_DAY, ""));
        hashMapDetails.put(KEY_WED_FROM_TIME, sharedPreferencesAvailability.getString(KEY_WED_FROM_TIME, ""));
        hashMapDetails.put(KEY_WED_TO_TIME, sharedPreferencesAvailability.getString(KEY_WED_TO_TIME, ""));
        return hashMapDetails;
    }

    public void clearWednessday() {
        editorAvailability.remove(KEY_WED_DAY);
        editorAvailability.remove(KEY_WED_FROM_TIME);
        editorAvailability.remove(KEY_WED_TO_TIME);
        editorAvailability.commit();
    }

    public void setDetailsThurDay(String stringDay, String stringFromTime, String stringToTime) {
        editorAvailability.putString(KEY_THUR_DAY, stringDay);
        editorAvailability.putString(KEY_THUR_FROM_TIME, stringFromTime);
        editorAvailability.putString(KEY_THUR_TO_TIME, stringToTime);
        editorAvailability.commit();
    }


    public HashMap<String, String> getDetailsThurDay() {
        HashMap<String, String> hashMapDetails = new HashMap<>();
        hashMapDetails.put(KEY_THUR_DAY, sharedPreferencesAvailability.getString(KEY_THUR_DAY, ""));
        hashMapDetails.put(KEY_THUR_FROM_TIME, sharedPreferencesAvailability.getString(KEY_THUR_FROM_TIME, ""));
        hashMapDetails.put(KEY_THUR_TO_TIME, sharedPreferencesAvailability.getString(KEY_THUR_TO_TIME, ""));
        return hashMapDetails;
    }

    public void clearThursday() {
        editorAvailability.remove(KEY_THUR_DAY);
        editorAvailability.remove(KEY_THUR_FROM_TIME);
        editorAvailability.remove(KEY_THUR_TO_TIME);
        editorAvailability.commit();
    }

    public void setDetailsFriDay(String stringDay, String stringFromTime, String stringToTime) {
        editorAvailability.putString(KEY_FRI_DAY, stringDay);
        editorAvailability.putString(KEY_FRI_FROM_TIME, stringFromTime);
        editorAvailability.putString(KEY_FRI_TO_TIME, stringToTime);
        editorAvailability.commit();
    }


    public HashMap<String, String> getDetailsFriDay() {
        HashMap<String, String> hashMapDetails = new HashMap<>();
        hashMapDetails.put(KEY_FRI_DAY, sharedPreferencesAvailability.getString(KEY_FRI_DAY, ""));
        hashMapDetails.put(KEY_FRI_FROM_TIME, sharedPreferencesAvailability.getString(KEY_FRI_FROM_TIME, ""));
        hashMapDetails.put(KEY_FRI_TO_TIME, sharedPreferencesAvailability.getString(KEY_FRI_TO_TIME, ""));
        return hashMapDetails;
    }

    public void clearFriday() {
        editorAvailability.remove(KEY_FRI_DAY);
        editorAvailability.remove(KEY_FRI_FROM_TIME);
        editorAvailability.remove(KEY_FRI_TO_TIME);
        editorAvailability.commit();
    }

    public void setDetailsSatDay(String stringDay, String stringFromTime, String stringToTime) {
        editorAvailability.putString(KEY_SAT_DAY, stringDay);
        editorAvailability.putString(KEY_SAT_FROM_TIME, stringFromTime);
        editorAvailability.putString(KEY_SAT_TO_TIME, stringToTime);
        editorAvailability.commit();
    }


    public HashMap<String, String> getDetailsSatDay() {
        HashMap<String, String> hashMapDetails = new HashMap<>();
        hashMapDetails.put(KEY_SAT_DAY, sharedPreferencesAvailability.getString(KEY_SAT_DAY, ""));
        hashMapDetails.put(KEY_SAT_FROM_TIME, sharedPreferencesAvailability.getString(KEY_SAT_FROM_TIME, ""));
        hashMapDetails.put(KEY_SAT_TO_TIME, sharedPreferencesAvailability.getString(KEY_SAT_TO_TIME, ""));
        return hashMapDetails;
    }

    public void clearSatday() {
        editorAvailability.remove(KEY_SAT_DAY);
        editorAvailability.remove(KEY_SAT_FROM_TIME);
        editorAvailability.remove(KEY_SAT_TO_TIME);
        editorAvailability.commit();
    }

    public String getCategoryId() {
        return sharedPreferences.getString(KEY_CATEGORY_ID, "");
    }

    public void setCategoryId(String stringCategoryId) {
        editor.putString(KEY_CATEGORY_ID, stringCategoryId);
        editor.commit();
    }

    public String getSubCategoryId() {

        return sharedPreferences.getString(KEY_SUB_CATEGORY_ID, "");
    }

    public void setSubCategoryId(String stringSubCategoryId) {
        editor.putString(KEY_SUB_CATEGORY_ID, stringSubCategoryId);
        editor.commit();
    }

    public String getTaskCategoryId() {

        return sharedPreferences.getString(KEY_TASK_CATEGORY_ID, "");
    }

    public void setTaskCategoryId(String stringTaskCategoryId) {
        editor.putString(KEY_TASK_CATEGORY_ID, stringTaskCategoryId);
        editor.commit();
    }

    public String getSubTaskCategoryId() {

        return sharedPreferences.getString(KEY_SUB_TASK_CATEGORY_ID, "");
    }

    public void setSubTaskCategoryId(String stringSubTaskCategoryId) {
        editor.putString(KEY_SUB_TASK_CATEGORY_ID, stringSubTaskCategoryId);
        editor.commit();
    }

    public void setAvailablityJsonArray(JSONArray availablityJsonArray) {
        editorAvailability.putString(KEY_AVAILABILITY_JAON_ARRAY, availablityJsonArray.toString());
        editorAvailability.commit();
    }

    public JSONArray getAvailabilityJsonArray() throws JSONException {
        JSONArray jsonArrayAvailability = new JSONArray(sharedPreferencesAvailability.getString(KEY_AVAILABILITY_JAON_ARRAY, ""));
        return jsonArrayAvailability;
    }

    public JSONObject getPlaceOfOperation() throws JSONException {
        JSONObject jsonObjectPlaceOfOperation = new JSONObject(sharedPreferences.getString(KEY_PLACE_OF_OPERATION.toString(), ""));
        return jsonObjectPlaceOfOperation;
    }

    public void setPlaceOfOperation(JSONObject jsonObjectPlaceOfOperation) {
        editor.putString(KEY_PLACE_OF_OPERATION, jsonObjectPlaceOfOperation.toString());
        editor.commit();
    }

    public void clearSessionRegistrationData() {
        editor.clear();
        editor.apply();
    }

    public boolean clearAvailabiltySessionData() {
        editorAvailability.clear();
        editorAvailability.apply();
        return true;
    }

    public String getImageStringBase64() {
        return sharedPreferences.getString(KEY_BASE_IMAGE, "");
    }

    public void setImageStringBase64(String stringBase64) {
        editor.putString(KEY_BASE_IMAGE, stringBase64);
        editor.commit();
    }

    public String getAvailablityDays() {
        return sharedPreferences.getString(KEY_AVAILABIL_DAYS, "");
    }

    public void setAvailablityDays(AvailabileArrayPojo availabileArrayPojo) {
        Gson gson = new Gson();
        String json = gson.toJson(availabileArrayPojo);
        editor.putString(KEY_AVAILABIL_DAYS, json);
        editor.commit();
    }

    public String getOldAvailablityDays() {
        return sharedPreferences.getString(KEY_AVAILABIL_DAYS, "");
    }

    public void setOldAvailablityDays(OldAvailabileArrayPojo availabileArrayPojo) {
        Gson gson = new Gson();
        String json = gson.toJson(availabileArrayPojo);
        editor.putString(KEY_AVAILABIL_DAYS, json);
        editor.commit();
    }

    public String getAllCategories() {
        return sharedPreferences.getString(KEY_CATEGORIES, "");
    }

    public void setAllCategories(CategoriesDataPojo allCategories) {
        Gson gson = new Gson();
        String json = gson.toJson(allCategories);
        editor.putString(KEY_CATEGORIES, json);
        editor.commit();
    }

    public String getPersonalDetails() {
        return sharedPreferences.getString(KEY_PERSONAL_DETAILS, "");
    }

    public void setPersonalDetails(String stringTaskCategoryId) {
        editor.putString(KEY_PERSONAL_DETAILS, stringTaskCategoryId);
        editor.commit();
    }

    public String getDocumentDays() {
        return sharedPreferences.getString(KEY_DOCUEMNT, "");
    }

    public void setDocumentDays(DocumentUploadPojo documentArrayPojo) {
        Gson gson = new Gson();
        String json = gson.toJson(documentArrayPojo);
        editor.putString(KEY_DOCUEMNT, json);
        editor.commit();
    }

    public String getDocumentStatus() {
        return sharedPreferences.getString(KEY_DOCUEMNT_STATUS, "0");
    }

    public void setDocumentStatus(String document_upload_status) {
        editor.putString(KEY_DOCUEMNT_STATUS, document_upload_status);
        editor.commit();
    }


}
