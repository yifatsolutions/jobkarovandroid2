package karov.shemi.oz;

import android.view.Menu;

public class Constants {
	public static final String DISTANCE = "distance";
	public static final String NAME = "name";
	public static final String LAST_NAME = "last_name";
	public static final String FIRST_NAME = "first_name";
	public static final String COMPANY = "company";
	public static final String COMPANYNAME = "company_name";
	public static final String COMPANY_WEBSITE = "company_website";
	public static final String COMPANY_DESCRIPTION = "company_description";
	public static final String UPGRADE = "upgrade";
	public static final String TITLE = "title";
	public static final String TAKANON = "accepted";
	public static final String TAKANON2 = "accepted2";
	public static final String TAKANONSTRING = "takanonstring";

	public static final String COMPANYID = "company_id";
	public static final String MANPOWER = "manpower";
	public static final String METER = "meter";
	public static final String CITIES = "cities";
	public static final String RADIUS = "radius";
	public static final String ITEM = "item";
	public static final String PLACE = "place";
	public static final String REQUIRE_CV = "require_cv";
	public static final String CITYNAME = "city_name";

	public static final String ADDRESS = "address";
	public static final String PHONE = "phone";
	public static final String MAIL = "mail";
	public static final String EMAIL = "email";
	//public static final String CVEXIST = "life_story_and";
	public static final String LIFE_STORY = "life_story";

	public static final String MYEMAIL = "myemail";
	public static final String REQUIRE = "require";
	public static final String LOGO = "logo";
	public static final String DESC = "desc";
	public static final String DESCRIPTION = "description";
	public static final String WEBSITE = "website";
	public static final String AREA_NAME = "area_name";
	public static final String AREA = "area";
	public static final String AREA_CITY = "area_city";

	public static final String PHOTO = "photo";
	public static final String LINK = "link";
	public static final String FAV = "fav";
	public static final String FAVMODE = "favmode";

	public static final String ID = "id";
	public static final String SiteID="SiteID";
	public static final String MSG = "msg";
	public static final int SIDEMENU_ID = Menu.FIRST-1;
	public static final int ABOUT_ID = Menu.FIRST;
	public static final int NEW_ID = Menu.FIRST+1;
	public static final int FAVOUR_ID = Menu.FIRST+2;
	public static final int VISITED_ID = Menu.FIRST+3;
	public static final int CONTACT_ID = Menu.FIRST+4;
	public static final int EXPLAIN_ID = Menu.FIRST+5;
	public static final int TAKANON_ID = Menu.FIRST+6;
	public static final int SITE_ID = Menu.FIRST+7;
	public static final int RTL_ID = Menu.FIRST+8;
	public static final int HELP_ID = Menu.FIRST+9;
	public static final int REGISTER_ID = Menu.FIRST+10;
	public static final String TAG = "debug jobkarov";
	public static final String SPECIALITY = "speciality";
	public static final String JOBNAME = "jobname";
	public static final String ROLENAME = "role_name";
	public static final String FACEBOOKNAME = "facebookname";
	public static final String FACEBOOKID = "700352396677077";
	public static final String TOKEN = "model_token";
	public static final String ROLE = "role";
	public static final String SIZE = "size";
	public static final String MODELTYPE = "model_type";
	public static final String MODELNAME = "model_name";	
	public static final String PROPERTY_REG_ID = "registration_id";
	public static final String PROPERTY_APP_VERSION = "appVersion";
	public static final String ROWS = "rows";
	public static final String SECONDTIME = "secondtime";
	public static final String TIME = "time";
	public static final String TIMEOUT = "timeout";
	public static final String COLOR = "color";
	public static final String CONF = "conf";
	public static final String UNIQUE = "unique";


	public static final int NUM_OF_COLUMNS = 3;
	 
    // Gridview image padding
    public static final int GRID_PADDING = 8; // in dp
 
	public static final String VAR = "var";
	public static final String RANGE = "range";
	public static final String X = "x";
	public static final String Y = "y";
	public static final String MYX = "xx";
	public static final String MYY = "yy";
	public static final int SEARCH = 0;
	public static final String HEBREW = "hebrew";
	public static final String HEBREWCHECKED = "hebrewchecked";
	public static final String PASSWORD = "password";
	public static final String MY_DESCRIPTION = "my_description";
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String TYPE = "type";
    public static final String UPDATE = "update";
    public static final String MESSAGES = "messages";
    public static final String QUERY = "query";
    public static final String ORDER = "order";
	public static final String YES = "1";
	public static final String NO = "0";

    public final static String baseUrl="http://www.jobkarov.com";//home.a7.org";///php/";//"http://close2me.co.il/php/";
    //public final static String url1=baseUrl+"/App_2_android/";//"close2me.home.a7.org/php/";//"http://close2me.co.il/php/";
	public final static String urlCommand1="GetWorkJ?id=";//"getwork_JoinWeb.php?id=";
	public final static String urlCommandTitles="GetTitles3";//"gettitles.php";
	public static final String urlCommand2="SendEmail3?email=";//"sendtomail_JoinWeb.php?email=";
	public final static String urlCommand3="GetWorkList3";//"getworklist_JoinWeb.php";
	public static final String urlCommand4="FromApp?AppID=1&SiteID=";//"user_send_from_app.aspx?AppID=1&SiteID=";
	public final static String urlCommandSmartAgent="UpdateSearch";
	public final static String urlCommandCheckEmail="CheckEmail";
	public final static String urlCommandAllSmartAgent="SearchList";
	//public final static String urlCommand5="GetWorkList";//"getworklist_JoinWeb.php";
	public final static String urlCommandForgot="Forgot";
	public final static String urlCommandRegisterFacebook="FBLogin";
	public final static String urlCommandLogin="Login";
	public final static String urlCommandGCM="ModelLogin";
	public final static String urlCommandModelLogout="ModelLogout";
	public final static String urlCommandDeleteAgent="DeleteSearch";
	public final static String urlCommandCount="Count";
	public final static String urlCommandByURL="ByURL";
	public final static String URLMSG=baseUrl+"/App/Msg/?ids=";
	public final static String  LAST_ADDRESS="last_address";
	public final static String  LASTSEARCHES="lastsearches";
	public final static String  UPDATEDATE="update_date";
	public final static String  STATUS="status";
	public final static String  RESULT="result";
	public final static String  ERROR="error";
	public final static String USERCODE="UserCode";
	public final static String USERID="UserID";
	public final static String urlCommandContactUs="ContactUs";//"contactus.php";
	public final static String urlCommandRegisterNative="Register";//"contactus.php";
	public final static String urlCompany=baseUrl+"/Company";
	public static final String DONTSHOW = "dontshow";
	public static final String TAB = "tab";
    //public static final int VISITED_SIZE=10;
	public final static String IMAGE_URL=baseUrl+"/Uploads/pictures/";
	public final static String IMAGE_SURRFIX=".jpg?width=180&height=180";
	public static final String DATA_FETCHED="karov.shemi.oz.DATA_FETCHED";
	public static final String WIDGET_ITEM="karov.shemi.oz.WIDGET_ITEM";
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final int MAX_LAST_SEARCHES = 14;

	public static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
	public static final String SENDER_ID = "959131861493";//"973174535231";//new for job karov:959131861493
	//public static final String API_KEY = "AIzaSyAmuK6baAIexE1-tP_1Smq_T-tf6z2qiic";
	public static final String API_KEY_DIRECTIONS = "AIzaSyA62oA16mRYC72qurpmuQF2-SBreO7DI1c";


}
