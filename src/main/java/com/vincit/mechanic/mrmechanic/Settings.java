package com.vincit.mechanic.mrmechanic;

import android.app.Activity;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by R on 6/8/2017.
 */

public class Settings {


    //---------------GLOBAL - FIELDS--------------------------------

    public static String LOGIN_URL = "http://vedmal.com/mechanic/mech/app/login.php?";
    public static String GET_DATA_URL = "http://vedmal.com/mechanic/mech/app/getdata.php";
    public static String REGISTER_USER_URL = "http://vedmal.com/mechanic/mech/app/registeruser.php?";
    public static String REGISTER_VENDOR_URL = "http://vedmal.com/mechanic/mech/app/registervendor.php?";
    public static String CHECK_VENDOR_URL = "http://vedmal.com/mechanic/mech/app/check_vendor.php?";
    public static String NOTIFY_VENDOR_URL = "http://vedmal.com/mechanic/mech/app/notify_vendor.php?";
    public static String NOTIFY_VENDOR_URL2 = "http://vedmal.com/mechanic/mech/app/notify_vendor2.php?";
    public static String UPDATE_USER_URL = "http://vedmal.com/mechanic/mech/app/updateuser.php?";
    public static String UPDATE_VENDOR_URL = "http://vedmal.com/mechanic/mech/app/updatevendor.php?";
    public static String NOTIFY_CUST_URL = "http://vedmal.com/mechanic/mech/app/notify_cust.php?";
    public static String GET_SINGLE_RQUEST_URL = "http://vedmal.com/mechanic/mech/app/getsinglerequest.php?";
    public static String GET_REQUESTS_URL = "http://vedmal.com/mechanic/mech/app/getrequests.php?";
    public static String GET_RESPONSE_URL = "http://vedmal.com/mechanic/mech/app/getresponse.php?";
    public static String IMAGE_HANDLER_URL = "http://vedmal.com/mechanic/mech/app/imagehandler.php?";
    public static String GET_BITMAP_STRING = "-1";
    public static String REQUEST_ID = "-1";
    public static String CUSTOMER_CURRENT_LOCATION = "No location chosen";
    public static String CURRENT_LAT = "-1";
    public static String CURRENT_LONG = "-1";
    public static String VENDOR_CURRENT_LOCATION = "No location chosen";
    public static String CUSTOMER_PHOTO_URL = "http://vedmal.com/mechanic/mech/images/customer_photo/";
    public static String VENDOR_PROFILE_URL = "http://vedmal.com/mechanic/mech/images/profile/";
    public static String VENDOR_WORK_URL = "http://vedmal.com/mechanic/mech/images/work/";
    public static int TYPE = -1;           // 0 for customer , 1 for vendor
    public static String MOBILE = "-1";
    public static String PASSWORD = "-1";
    public static boolean isActive = false;

    public static ArrayList STATE = new ArrayList();
    public static ArrayList CITY = new ArrayList();
    public static ArrayList CAT = new ArrayList();
    public static ArrayList SUBCAT = new ArrayList();
    public static ArrayList VENDOR_LIST = new ArrayList();
    public static ArrayList<Request> REQUESTS_LIST = new ArrayList();
    public static ArrayList<Response> RESPONSE_LIST = new ArrayList();
    //--------------CUSTOMER - FIELDS---------------------------------

    public static String CUSTOMER_ID = "-1";
    public static String CUSTOMER_NAME = "-1";
    public static String CUSTOMER_EMAIL = "-1";
    public static String CUSTOMER_ADDRESS = "-1";
    public static String CUSTOMER_SID = "-1";
    public static String CUSTOMER_CCID = "-1";
    public static String CUSTOMER_CAT_ID = "-1";
    public static String CUSTOMER_VEHICLE_MAKE = "-1";
    public static String CUSTOMER_VEHICLE_MODEL = "-1";
    public static String CUSTOMER_VEHICLE_NO = "-1";
    public static String CUSTOMER_VEHICLE_YEAR = "-1";
    public static String CUSTOMER_PHOTO = "-1";
    public static String CUSTOMER_DATE1 = "-1";

    public static String CUSTOMER_SERVICE_SELECT = "-1";
    public static String CUSTOMER_SUB_SERVICE_SELECT = "-1";
    public static String CUSTOMER_LAT_SELECT = "-1";
    public static String CUSTOMER_LONG_SELECT = "-1";
    public static String CUSTOMER_ADDRESS_SELECT = "-1";
    public static ArrayList<SubCat> CUSTOMER_SUB_SERVICE_FOR_SELECT = new ArrayList<>();
    public static HashMap<String,Integer> CUSTOMER_SUB_SERVICE_SELECTED_HASH = new HashMap<>();

    public static AppCompatActivity CUSTOMER_SERVICE_SELECT_ACTIVITY = null;
    public static AppCompatActivity CUSTOMER_SUB_SERVICE_SELECT_ACTIVITY = null;
    public static AppCompatActivity CUSTOMER_MAP_SELECT_ACTIVITY = null;
    public static AppCompatActivity CUSTOMER_GENERAL_SELECT_ACTIVITY = null;

    //---------------VENDOR - FIELDS-------------------------------------
    public static String VENDOR_ID = "-1";
    public static String VENDOR_NAME = "-1";
    public static String VENDOR_EMAIL = "-1";
    public static String VENDOR_MOBILE = "-1";
    public static String VENDOR_PINCODE = "-1";
    public static String VENDOR_ADDRESS1 = "-1";
    public static String VENDOR_ADDRESS2 = "-1";
    public static String VENDOR_SID = "-1";
    public static String VENDOR_CCID = "-1";
    public static String VENDOR_LOCATION = "-1";
    public static String VENDOR_LAT = "-1";
    public static String VENDOR_LONG = "-1";
    public static String VENDOR_CAT_ID = "-1";
    public static String VENDOR_SUBCAT_ID = "-1";
    public static String VENDOR_BANK_ACCOUNT = "-1";
    public static String VENDOR_BANK_IFSC = "-1";
    public static String VENDOR_BANK_BRANCH = "-1";
    public static String VENDOR_BANK_ACCOUNT_TYPE = "-1";
    public static String VENDOR_PHOTO = "-1";
    public static String VENDOR_WORK_PHOTO ="-1";
    public static String VENDOR_DESCRIPTION = "-1";
    public static String VENDOR_START_TIME =  "-1";
    public static String VENDOR_END_TIME = "-1";
    public static String VENDOR_PASSWORD = "-1";
    public static String Latitude = "-1";
    public static String Longitude = "-1";
    public static String Address = "-1";


}


//-----------------DATA STRUCTRE-----------------------------------------
class State
{
    public String sid = "-1";
    public String sname = "-1";
    public String scode = "-1";
}
class City
{
    public String cid = "-1";
    public String sid = "-1";
    public String ccname = "-1";
}
class Cat
{
    public String cat_id = "-1";
    public String cat_name = "-1";
}

class SubCat
{
    public String subcat_id = "-1";
    public String subcat_name = "-1";
    public String cat_id = "-1";
}
class Vendor2
{
    public String vendor_id = "";
    public String vendor_name = "";

}

class User
{

    public String name = "";
    public String email = "";
    public String mobile = "";
    public String address = "";
    public String sid = "";
    public String ccid = "";
    public String cat_id = "";
    public String vehicle_make = "";
    public String vehicle_model = "";
    public String vehicle_no = "";
    public String vehicle_year = "";
    public String password = "";
    public String FCM = "";
    public String photo = "";
}

class Vendor
{

    public String name = "";
    public String email = "";
    public String mobile = "";
    public String pincode = "";
    public String address1 = "";
    public String address2 = "";
    public String sid = "";
    public String ccid = "";
    public String location = "";
    public String lat = "";
    public String lng = "";
    public String cat_id = "";
    public String subcat_id = "";
    public String vendor_photo = "";
    public String work_photo = "";
    public String description = "";
    public String bank_account = "";
    public String bank_ifsc = "";
    public String bank_account_type = "";
    public String bank_branch = "";
    public String start_time = "";
    public String end_time = "";
    public String password = "";
    public String FCM = "";
}

class Request
{
    public String rid = "";
    public String Address = "";
    public String Name = "";
    public String Lat = "";
    public String Lang = "";
    public String Phone = "";
    public String Status = "";
    public String Service = "";
    public String SubService = "";
    public String Date = "";
    public String Distance = "";
    public String Rating = "";
}

class Response
{
    public String rid = "";
    public String amount = "";
    public String eta = "";
    public String phone = "";
    public String name = "";
    public String status = "";
    public String Rating = "";
}