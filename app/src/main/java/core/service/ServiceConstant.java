package core.service;


import com.hoperlady.BuildConfig;

public interface ServiceConstant {

    String BuildURL = BuildConfig.SERVER_URL;

    String BASE_URL = BuildURL + "mobile/";
    String REgBASE_URL = BuildURL;
    String XMPP_SERVICE_NAME = "messaging.dectar.com";
    String SOCKET_HOST_URL = BuildURL + "notify";
    String SOCKET_CHAT_URL = BuildURL + "chat";
    String Review_image = BuildURL;

    String AVAILABILITY_URL = BASE_URL + "provider/tasker-availability";
    String LOGIN_URL = BASE_URL + "provider/login";
    String MYJOBON_LIST_URL = BASE_URL + "provider/jobs-list";
    String MYJOB_DETAIL_INFORMATION_URL = BASE_URL + "provider/view-job";
    String NEWLEADS_URL = BASE_URL + "provider/new-job";
    String MISSEDJOB_URL = BASE_URL + "provider/missed-jobs";
    String ACCEPT_JOB_URL = BASE_URL + "provider/accept-job";
    String REJECT_JOB_URL = BASE_URL + "";
    String ARRIVED_JOB_URL = BASE_URL + "provider/arrived";
    String STARTOFFJOB_JOB_URL = BASE_URL + "provider/start-off";
    String STARTJOB_URL = BASE_URL + "provider/start-job";
    String JOBCOMPLETE_URL = BASE_URL + "provider/job-completed";
    String PAYMENT_URL = BASE_URL + "provider/job-more-info";
    String JOB_CANCELL_REASON_URL = BASE_URL + "provider/cancellation-reason";
    String JOB_CANCELLED_URL = BASE_URL + "provider/cancel-job";
    String JOB_RECEIVECSH_OTP_URL = BASE_URL + "provider/receive-cash";
    String REQUEST_PAYMENT_URL = BASE_URL + "provider/request-payment";
    String JOB_CASH_RECEIVED_URL = BASE_URL + "provider/cash-received";
    String STATISTICS_EARNINGS_STATE_URL = BASE_URL + "provider/earnings-stats";
    String STATISTICS_JOB_STATES_URL = BASE_URL + "provider/jobs-stats";
    String PROFILEINFO_URL = BASE_URL + "provider/provider-info";
    String EDIT_PROFILE_URL = BASE_URL + "provider/get-edit-info";
    String MYPROFILE_REVIWES_URL = BASE_URL + "provider/provider-rating";
    String GET_BANK_INFO_URL = BASE_URL + "provider/get-banking-info";
    String SAVE_BANK_INFO_URL = BASE_URL + "provider/save-banking-info";
    String JOB_WORKFLOW_URL = BASE_URL + "provider/job-timeline";
    String EDIT_MOBILE_NUMBER_URL = BASE_URL + "provider/update_mobile";
    String EDIT_EMAIL_URL = BASE_URL + "provider/update_email";
    String EDIT_BIO_URL = BASE_URL + "provider/update_bio";
    String EDIT_ADDRESS_URL = BASE_URL + "provider/update_address";
    String EDIT_NAME_URL = BASE_URL + "provider/update_username";
    String EDIT_RADIUS_URL = BASE_URL + "provider/update_radius";
    String EDIT_WORKLOCATION_URL = BASE_URL + "provider/update_worklocation";
    String EDIT_PROFILEIMAGE_URL = BASE_URL + "provider/update_image";
    String UPDATE_URL = BASE_URL + "provider/update-provider-geo";
    String REVIWES_GET_URL = BASE_URL + "get-rattings-options";
    String REVIWES_SUBMIT_URL = BASE_URL + "submit-rattings";
    String FORGOT_PASSWORD_URL = BASE_URL + "provider/forgot-password";
    String CHANGE_PASSWORD_URL = BASE_URL + "provider/change-password";
    String OPEN_CHAT_PAGE_URL = BASE_URL + "app/chat/open";
    String OPEN_CHAT_LIST_URL = BASE_URL + "app/chat/list";
    String CHAT_CHECK_ONLINE_URL = BASE_URL + "app/chat/availablity";

    String myjobs_sortingurl = BASE_URL + "provider/jobs-list";
    String provider_rejectjob_url = BASE_URL + "provider/reject-job";
    String Notification_mode = BASE_URL + "user/notification_mode";
    String chat_list_url = BASE_URL + "app/chat/list";
    String chat_detail_url = BASE_URL + "chat/chathistory";
    String chat_availability_url = BASE_URL + "app/chat/availablity";
    String Register_URL = BASE_URL + "provider/register";
    String Register_Return_URL = REgBASE_URL + "tasker_login";
    String REGISTER_SUCCESS = BASE_URL + "provider/register/success";
    String REGISTER_CANCEL = BASE_URL + "provider/register/cancel";

    String App_Info = BASE_URL + "app/mobile/appinfo";

    String GETMESSAGECHAT_URL = BASE_URL + "app/getmessage";
    //Reviews
    String REVIEW_URL = BASE_URL + "app/get-reviews";

    //Notification URl
    String NOTIFICATION_URL = BASE_URL + "app/notification";

    //Transaction
    String TRANSACTION_URL = BASE_URL + "app/provider-transaction";
    String TRANSACTION_DETAIL_URL = BASE_URL + "app/providerjob-transaction";

    //------------------Xmpp key for Notification
    String ACTION_LABEL = "action";
    String ACTION_LEFT_JOB = "Left_job";
    String AVAILABILITYSTATUS = "availability_status";
    String ACTION_PENDING_TASK = "Accept_task";
    String ACTION_TAG_JOB_REQUEST = "job_request";
    String ACTION_TAG_JOB_CANCELLED = "job_cancelled";
    String ACTION_TAG_JOB_ASSIGNED = "job_assigned";
    String ACTION_TAG_RECEIVE_CASH = "receive_cash";
    String ACTION_TAG_PAYMENT_PAID = "payment_paid";
    String ACTION_TAG_PARTIALLY_PAID = "partially_paid";
    String ACTION_TAG_ADVERSIMENT = "adds";
    String ACTION_REJECT_TASK = "rejecting_task";
    String Admin_Notification = "admin_notification";
    String Plumbal_partnerAgent = "";
    String Plumbal_partner_Apptype = "android";
    String Plumbal_partner_UserId = "";
    String Plumbal_partner_apptocken = "1018763507003";

    String Accout_status = "account_status";
    String Accout_deleted = "account_deleted";

    String ACTION_TAG = "action";
    String MESSAGE_TAG = "message";
    String KEY1_TAG = "key0";
    String KEY11_TAG = "key1";
    String place_search_url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyCBVv6JVaZiBBgOabwr0xfBnhtcfaZh3cU&input=";
    String place_search_1_url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=";
    String place_search_2_url = "&input=";
    String GetAddressFrom_LatLong_url = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyCBVv6JVaZiBBgOabwr0xfBnhtcfaZh3cU&placeid=";
    String GetAddressFrom_LatLong_1_url = "https://maps.googleapis.com/maps/api/place/details/json?key=";
    String GetAddressFrom_LatLong_2_url = "&placeid=";

    //-------------filter--
    String Filter_booking_url = BASE_URL + "provider/recent-list";
//-----------------------------Notification Process-----------------

    String MODEUPDATE_URL = BASE_URL + "user/notification_mode";
    String Availability_Edit = BASE_URL + "provider/update-workingdadys";

    //----------------Log out-------------------

    String logout_url = BASE_URL + "app/provider/logout";
    //-----------------------Category Edit Add Delet--------------------------------
    String ADD_CATEGORY_DATA = BASE_URL + "tasker/add-category";
    String GET_CATEGORY_DETAILS = BASE_URL + "tasker/category-detail";
    String DELETE_CATEGORY_DATA = BASE_URL + "tasker/delete-category";
    String GET_MAIN_CATEGORY = BASE_URL + "provider/get-maincategory";
    String GET_SUB_CATEGORY = BASE_URL + "provider/get-subcategory";
    String GET_SUB_CATEGORY_DETAILS = BASE_URL + "provider/get-subcategorydetails";
    String UPDATE_CATEGORY = BASE_URL + "tasker/update-category";
    String Aboutus_Url = BASE_URL + "app/mobile/aboutus";
    String App_Categories_URl = BASE_URL + "app/categories";
    String Terms_and_conditions = BASE_URL + "app/mobile/termsandconditions";
    String Privacy_Polocy = BASE_URL + "app/mobile/privacypolicy";
    String Register_Form1 = BASE_URL + "provider/register/form1";
    String Register_Form2 = BASE_URL + "provider/register/form2";
    String Register_Form3 = BASE_URL + "provider/register/form3";
    String QUESTION_ANSWER_URL = BASE_URL + "provider/register/questions";
    String REGISTER_CATEGORY_URL = BASE_URL + "provider/register/parent-category";
    String REGISTER_SUB_CATEGORY_URL = BASE_URL + "provider/register/child-category";
    String REGISTER_EXPERIANCE_LEVEL_URL = BASE_URL + "provider/register/experience";
    String REGISTER_FINAL_LEVEL_URL = BASE_URL + "provider/register/save";
    String REGISTER_IMAGE_UPLOAD_URL = BASE_URL + "provider/register/image-upload";

    //-----------------New Changes URL-----------------------
    String HOME_MOBILE_NUMBER_URL = BASE_URL + "app/check-tasker";
    String HOME_PAGE_PROVIDER_INFO_URL = BASE_URL + "provider/provider-home";
    String EARNINGS_ONE_WEEK_LIST_URL = BASE_URL + "provider/weekly-earningsstats";
    String EARNINGS_ONE_DAY_LIST_URL = BASE_URL + "provider/daily-earningsstats";

    //-----------------Document Upload-----------------------
    String GETTASKERDOCUMENT = BASE_URL + "provider/document-list";
    String UPLOADTASKERDOCUMENT = BASE_URL + "provider/upload-document";

    String UPLOADEDITDO = BASE_URL + "provider/upload-editdocument";
    String UPDATEFILES = BASE_URL + "provider/update-document";

    String UPDATE_TASKER_DOCUMENT = BASE_URL + "provider/upload-editdocument";
    String REMOVE_DOCUMENT=BASE_URL+"provider/remove-document";
}
