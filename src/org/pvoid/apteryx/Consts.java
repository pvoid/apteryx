package org.pvoid.apteryx;
public class Consts
{
  public static final String APTERYX_PREFS = "apteryx-settings";
  public static final String PREF_INTERVAL = "apteryx.update-interval";
  public static final String PREF_AUTOCHECK = "apteryx.autoupdate";
  public static final String PREF_LASTUPDATE = "apteryx.lastupdate";
  public static final String URL = "http://xml1.osmp.ru/term2/xml.jsp";
  
  public static final String TAB_ACCOUNTS = "tab_account";
  public static final String TAB_PREFERENCES = "tab_preferences";
  
  public static final int ACTIVITY_ADD_ACCOUNT = 1;
  public static final int ACTIVITY_EDIT_ACCOUNT = 2;
  
  public static final String DB_NAME = "apteryx";
  public static final int DB_VERSION = 2;
  public static final String ACCOUNTS_TABLE = "accounts";
  public static final String TERMINALS_TABLE = "terminals";
  public static final String BALANCES_TABLE = "balances";
  public static final String COLUMN_ID = "id";
  public static final String COLUMN_TITLE = "title";
  public static final String COLUMN_LOGIN = "login";
  public static final String COLUMN_PASSWORD = "password";
  public static final String COLUMN_TERMINAL = "terminal";
  
  public static final String COLUMN_ADDRESS = "address";
  public static final String COLUMN_STATE = "state";
  public static final String COLUMN_PRINTERSTATE = "printer_state";
  public static final String COLUMN_CASHBINSTATE = "cashbin_state";
  public static final String COLUMN_LPD = "lpd";
  public static final String COLUMN_CASH = "cash";
  public static final String COLUMN_LASTACTIVITY = "last_activity";
  public static final String COLUMN_LASTPAYMENT = "last_payment";
  public static final String COLUMN_BONDS = "bonds_count";
  public static final String COLUMN_BALANCE = "balance";
  public static final String COLUMN_SIGNALLEVEL = "signal_level";
  public static final String COLUMN_SOFTVERSION = "soft_version";
  public static final String COLUMN_PRINTERMODEL = "printer_model";
  public static final String COLUMN_CASHBINMODEL = "cashbin_model";
  public static final String COLUMN_BONDS10 = "bonds_10";
  public static final String COLUMN_BONDS50 = "bonds_50";
  public static final String COLUMN_BONDS100 = "bonds_100";
  public static final String COLUMN_BONDS500 = "bonds_500";
  public static final String COLUMN_BONDS1000 = "bonds_1000";
  public static final String COLUMN_BONDS5000 = "bonds_5000";
  public static final String COLUMN_BONDS10000 = "bonds_10000";
  public static final String COLUMN_PAYSPERHOUR = "pays_per_hour";
  public static final String COLUMN_AGENTID = "agent_id";
  public static final String COLUMN_AGENTNAME = "agent_name";
  
  public static final String COLUMN_OVERDRAFT = "overdraft";
  
  public static final int MENU_DELETE = 1;
  public static final int MENU_EDIT = 2;
  
  public static final int[] INTERVALS = new int[] {900000,1800000,3600000,10800000,21600000,43200000,86400000};
  
  public static final int NOTIFICATION_ICON = 1;
}
