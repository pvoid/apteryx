package org.pvoid.apteryx;
public class Consts
{
  public static final String APTERYX_PREFS = "apteryx-settings";
  public static final String PREF_INTERVAL = "apteryx.update-interval";
  public static final String PREF_AUTOCHECK = "apteryx.autoupdate";
  public static final String URL = "http://xml1.osmp.ru/term2/xml.jsp";
  
  public static final String TAB_ACCOUNTS = "tab_account";
  public static final String TAB_PREFERENCES = "tab_preferences";
  
  public static final int ACTIVITY_ADD_ACCOUNT = 1;
  public static final int ACTIVITY_EDIT_ACCOUNT = 2;
  
  public static final String DB_NAME = "apteryx";
  public static final int DB_VERSION = 2;
  public static final String ACCOUNTS_TABLE = "accounts";
  public static final String TERMINALS_STATES_TABLE = "terminals_states";
  public static final String COLUMN_ID = "id";
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_LOGIN = "login";
  public static final String COLUMN_PASSWORD = "password";
  public static final String COLUMN_TERMINAL = "terminal";
  public static final String COLUMN_STATE = "state";
  
  public static final int MENU_DELETE = 1;
  public static final int MENU_EDIT = 2;
  
  public static int[] INTERVALS = new int[] {900000,1800000,3600000,10800000,21600000,43200000,86400000};
}
