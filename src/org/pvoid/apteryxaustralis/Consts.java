/*
 * Copyright (C) 2010-2011  Dmitry Petuhov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pvoid.apteryxaustralis;

import android.app.Activity;

public class Consts
{
  public static final String DB_NAME = "apteryx";
  public static final int DB_VERSION = 2;
  
  public static final String ACCOUNTS_TABLE = "accounts";
  public static final String TERMINALS_TABLE = "terminals";
  public static final String BALANCES_TABLE = "balances";
  public static final String AGENTS_TABLE = "agents";
  
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
  
  public static final String COLUMN_ACCOUNT = "account";
  
  public static final String COLUMN_OVERDRAFT = "overdraft";
  
  public static final String EXTRA_ACCOUNTID = "account_id";
  public static final String EXTRA_AGENTS = "agents";
  public static final String EXTRA_SELECTED_AGENT = "selected_agent";
  public static final String EXTRA_SELECTED_AGENTS = "selected_agents";
}
