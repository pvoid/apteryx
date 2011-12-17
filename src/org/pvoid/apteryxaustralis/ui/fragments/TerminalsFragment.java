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

package org.pvoid.apteryxaustralis.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.pvoid.apteryxaustralis.R;
import org.pvoid.apteryxaustralis.net.ContentLoader;
import org.pvoid.apteryxaustralis.storage.osmp.OsmpContentProvider;
import org.pvoid.apteryxaustralis.ui.TerminalInfoActivity;
import org.pvoid.apteryxaustralis.ui.widgets.AgentHeader;

import java.util.ArrayList;
import java.util.List;

public class TerminalsFragment extends ListFragment implements AdapterView.OnItemLongClickListener,
                                                               DialogInterface.OnClickListener
{
  public static final String ARGUMENT_AGENT = "agent";

  private final TerminalsObserver _mTerminalsObserver = new TerminalsObserver(new Handler());
  private AgentHeader _mHeader;
  private ArrayList<OsmpContentProvider.TerminalAction> _mActions = new ArrayList<OsmpContentProvider.TerminalAction>();
  private long _mSelectedTerminalId;
  private CharSequence _mSelectedTerminalName;

  private long getGroupId()
  {
    final Bundle arguments = getArguments();
    if(arguments==null)
      return 0;
    return arguments.getLong(ARGUMENT_AGENT);
  }

  private String getWhereClause()
  {
    long id = getGroupId();
    if(id==0)
      return null;
    else
      return OsmpContentProvider.Terminals.COLUMN_AGENTID + "=" + Long.toString(id);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);
    final Activity activity = getActivity();
    final ListView list = getListView();
    _mHeader = new AgentHeader(activity);
    setListAdapter(null);
    _mHeader.loadAgentData(getGroupId());
    list.addHeaderView(_mHeader,null,false);
    setListAdapter(new TerminalsCursorAdapter(activity,getWhereClause(), R.layout.record_terminal));
    final ContentResolver resolver = activity.getContentResolver();
    resolver.registerContentObserver(OsmpContentProvider.Terminals.CONTENT_URI, true, _mTerminalsObserver);
//////////
    final ContentValues values = new ContentValues();
    values.put(OsmpContentProvider.Agents.COLUMN_SEEN,1);
    resolver.update(OsmpContentProvider.Agents.CONTENT_URI,
                    values,
                    OsmpContentProvider.Agents.COLUMN_AGENT+"=?",
                    new String[] {Long.toString(getGroupId())});
/////////
    OsmpContentProvider.getActions(getActivity(),_mActions);
    list.setOnItemLongClickListener(this);
  }

  @Override
  public void onDestroyView()
  {
    super.onDestroyView();
    getActivity().getContentResolver().unregisterContentObserver(_mTerminalsObserver);
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id)
  {
    Intent intent = new Intent(getActivity(), TerminalInfoActivity.class);
    intent.putExtra(TerminalInfoFragment.EXTRA_TERMINAL,id);
    intent.putExtra(TerminalInfoActivity.EXTRA_AGENT,getGroupId());
    startActivity(intent);
  }

  @Override
  public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long id)
  {
    _mSelectedTerminalName = "";
    _mSelectedTerminalId = id;
////////
    TextView text = (TextView) view.findViewById(R.id.list_title);
    if(text!=null)
      _mSelectedTerminalName = text.getText();
    ActionsDialog dialog = new ActionsDialog(getActivity(),_mSelectedTerminalName,_mActions,this);
    dialog.show(getFragmentManager(),null);
    return true;
  }

  @Override
  public void onClick(DialogInterface dialogInterface, int index)
  {
    OsmpContentProvider.TerminalAction action = _mActions.get(index);
    if(TextUtils.isEmpty(action.question))
    {
      return;
    }
    ConfirmDialog dialog = new ConfirmDialog(getActivity(),
                                             String.format(action.question,_mSelectedTerminalName),
                                             getGroupId(),
                                             action.id,
                                             _mSelectedTerminalId);
    dialog.show(getFragmentManager(),null);
  }

  private class TerminalsObserver extends ContentObserver
  {
    public TerminalsObserver(Handler handler)
    {
      super(handler);
    }

    @Override
    public void onChange(boolean selfChange)
    {
      super.onChange(selfChange);
      ((TerminalsCursorAdapter)getListAdapter()).refresh();
      _mHeader.loadAgentData(getGroupId());
    }
  }

  private static class ActionsDialog extends DialogFragment
  {
    private final ContextThemeWrapper _mContext;
    private final ArrayAdapter<String> _mActions;
    private final DialogInterface.OnClickListener _mListener;
    private final CharSequence _mTitle;

    public ActionsDialog(Context context, CharSequence title, List<OsmpContentProvider.TerminalAction> actions, DialogInterface.OnClickListener listener)
    {
      super();
      _mContext = new ContextThemeWrapper(context,android.R.style.Theme_Black);
      _mActions = new ArrayAdapter<String>(_mContext,android.R.layout.simple_dropdown_item_1line);
      for(OsmpContentProvider.TerminalAction action : actions)
        _mActions.add(action.title);
      _mTitle = title;
      _mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      final AlertDialog.Builder builder = new AlertDialog.Builder(_mContext);
      builder.setAdapter(_mActions,_mListener);
      builder.setTitle(_mTitle);
      builder.setCancelable(true);
      final Dialog dialog = builder.create();
      dialog.setCanceledOnTouchOutside(true);
      return dialog;
    }
  }

  private static class ConfirmDialog extends DialogFragment implements DialogInterface.OnClickListener
  {
    private final ContextThemeWrapper _mContext;
    private final String _mMessage;
    private final int _mAction;
    private final long _mTerminalId;
    private final long _mGroupId;
    
    public ConfirmDialog(Context context, String question, long agentId, int action, long terminalId)
    {
      super();
      _mContext = new ContextThemeWrapper(context,android.R.style.Theme_Black);
      _mMessage = question;
      _mAction = action;
      _mTerminalId = terminalId;
      _mGroupId = agentId;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      final AlertDialog.Builder builder = new AlertDialog.Builder(_mContext);
      builder.setMessage(_mMessage);
      builder.setCancelable(true);
      builder.setPositiveButton("Ok",this).setNegativeButton(R.string.cancel,this);
      return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int index)
    {
      if(index==DialogInterface.BUTTON_POSITIVE)
      {
        Bundle accountData = new Bundle();
        if(!ContentLoader.getAccountData(_mContext,_mGroupId,accountData))
          return;
        ContentLoader.rebootTerminal(_mContext,accountData,_mTerminalId);
      }
    }
  }
}
