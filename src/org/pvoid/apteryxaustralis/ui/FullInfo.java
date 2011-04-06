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

package org.pvoid.apteryxaustralis.ui;

import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.accounts.Terminal;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class FullInfo extends Activity
{
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    Terminal terminal = getIntent().getParcelableExtra("terminal");
    if(terminal!=null)
    {
      setTitle(terminal.Address());
    }
    setContentView(R.layout.fullinfo);
//////
    TextView view = (TextView) findViewById(R.id.fullinfo_tid);
    if(view!=null)
      view.setText(terminal.id());
    
    view = (TextView) findViewById(R.id.fullinfo_aid);
    if(view!=null)
      view.setText(Long.toString(terminal.agentId));
    
    view = (TextView) findViewById(R.id.fullinfo_an);
    if(view!=null)
      view.setText(terminal.agentName);
    
    view = (TextView) findViewById(R.id.fullinfo_cash);
    if(view!=null)
      view.setText(Html.fromHtml("<b>"+getString(R.string.fullinfo_cash) + "</b> " + Integer.toString(terminal.cash)));
    
    view = (TextView) findViewById(R.id.fullinfo_bonds);
    if(view!=null)
      view.setText(Html.fromHtml("<b>"+getString(R.string.fullinfo_bonds)+"</b> " + 
                                 Integer.toString(terminal.bondsCount)));
    
    view = (TextView) findViewById(R.id.fullinfo_last_activity);
    if(view!=null)
      view.setText(terminal.lastActivity);
    
    view = (TextView) findViewById(R.id.fullinfo_last_payment);
    if(view!=null)
      view.setText(terminal.lastPayment);
    
    view = (TextView) findViewById(R.id.fullinfo_signal_level);
    if(view!=null)
      view.setText(Html.fromHtml("<b>"+getString(R.string.fullinfo_signal_level)+"</b> "
                                 +Integer.toString(terminal.signalLevel)));
    
    view = (TextView) findViewById(R.id.fullinfo_balance);
    if(view!=null)
      view.setText(Html.fromHtml("<b>"+getString(R.string.fullinfo_balance)+"</b> "+terminal.balance));
    
    view = (TextView) findViewById(R.id.fullinfo_soft_version);
    if(view!=null)
      view.setText(terminal.softVersion);
    
    view = (TextView) findViewById(R.id.fullinfo_printer);
    if(view!=null)
      view.setText(terminal.printerModel);
    
    view = (TextView) findViewById(R.id.fullinfo_cashbin);
    if(view!=null)
      view.setText(terminal.cashbinModel);
    
    view = (TextView) findViewById(R.id.fullinfo_bonds10);
    if(view!=null)
      view.setText(Integer.toString(terminal.bonds10count));
    
    view = (TextView) findViewById(R.id.fullinfo_bonds50);
    if(view!=null)
      view.setText(Integer.toString(terminal.bonds50count));
    
    view = (TextView) findViewById(R.id.fullinfo_bonds100);
    if(view!=null)
      view.setText(Integer.toString(terminal.bonds100count));
    
    view = (TextView) findViewById(R.id.fullinfo_bonds500);
    if(view!=null)
      view.setText(Integer.toString(terminal.bonds500count));
    
    view = (TextView) findViewById(R.id.fullinfo_bonds1000);
    if(view!=null)
      view.setText(Integer.toString(terminal.bonds1000count));
    
    view = (TextView) findViewById(R.id.fullinfo_bonds5000);
    if(view!=null)
      view.setText(Integer.toString(terminal.bonds5000count));
    
    view = (TextView) findViewById(R.id.fullinfo_pays_per_hour);
    if(view!=null)
      view.setText(Html.fromHtml("<b>"+getString(R.string.fullinfo_pays_per_hour)+"</b> "+terminal.paysPerHour));
  }
}
