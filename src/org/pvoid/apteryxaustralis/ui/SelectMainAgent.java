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

import java.util.ArrayList;

import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.Consts;
import org.pvoid.apteryxaustralis.accounts.Agent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectMainAgent extends Activity
{
  private ArrayList<Agent> _Agents;
  private ListView _List;
  
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    Bundle extras = getIntent().getExtras();
    _Agents = extras.getParcelableArrayList(Consts.EXTRA_AGENTS);
    setContentView(R.layout.selectmainagent);
    _List = (ListView)findViewById(R.id.agents_list);
    _List.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    _List.setAdapter(new ArrayAdapter<Agent>(this, android.R.layout.simple_list_item_single_choice,_Agents));
  }
  
  public void Select(View view)
  {
    int position = _List.getCheckedItemPosition();
    if(position==ListView.INVALID_POSITION)
    {
      return;
    }
    Agent agent = _Agents.get(position);
    Intent result = new Intent();
    result.putExtra(Consts.EXTRA_SELECTED_AGENT, agent);
    setResult(RESULT_OK,result);
    finish();
  }
}
