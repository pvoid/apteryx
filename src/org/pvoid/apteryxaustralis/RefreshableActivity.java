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

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.database.DataSetObserver;
import android.graphics.PixelFormat;
import android.graphics.drawable.LevelListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuCompat;
import android.view.*;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
import org.pvoid.apteryxaustralis.net.ContentLoader;
import org.pvoid.apteryxaustralis.preference.CommonSettings;

public class RefreshableActivity extends FragmentActivity implements View.OnClickListener,
                                                                     DialogInterface.OnClickListener,
                                                                     WrappedActionBar.OnNavigationListener
{
  private final static int MENU_REFRESH  = 0;
  private final static int MENU_SETTINGS = 1;
  private final static int ANIMATION_INTERVAL = 100;

  private final boolean     _mIsEmulated       = Build.VERSION.SDK_INT<=Build.VERSION_CODES.HONEYCOMB;
  private LevelListDrawable _mProgressDrawable = null;
  private CursorAdapter     _mNavigatorAdapter = null;
  private SpinnerDialog     _mDialog           = null;
  private int               _mSelectedNavigatorItem = 0;
  private final Handler     _mHandler          = new Handler();
  private final Runnable    _mProgressRunnable = new Runnable()
  {
    @Override
    public void run()
    {
      if(_mProgressDrawable==null)
        return;
      ///////
      int level = (_mProgressDrawable.getLevel() + 1) % 6;
      _mProgressDrawable.setLevel(level);
      _mHandler.postDelayed(this,ANIMATION_INTERVAL);
    }
  };
  private final DataSetObserver _mObserver = new DataSetObserver()
  {
    @Override
    public void onChanged()
    {
      setSelectedNavigationItem(_mSelectedNavigatorItem);
    }

    @Override
    public void onInvalidated()
    {
      setSelectedNavigationItem(_mSelectedNavigatorItem);
    }
  };

  private final BroadcastReceiver _mLoadingStateReceiver = new BroadcastReceiver()
  {
    @Override
    public void onReceive(Context context, Intent intent)
    {
      final String action = intent.getAction();
      if(ContentLoader.LOADING_STARTED.equals(action))
      {
        showRefreshProgress(true);
      }
      else if(ContentLoader.LOADING_FINISHED.equals(action))
      {
        showRefreshProgress(false);
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    try
    {
      getWindow().setFormat(PixelFormat.RGBA_8888);
    }
    catch(Exception e)
    {
      // nope
    }
    if(_mIsEmulated)
    {
      setTheme(R.style.EmulatedActionBar);
      requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    }
    else
    {
      setTheme(android.R.style.Theme_Holo);
      requestWindowFeature(Window.FEATURE_ACTION_BAR);
    }
    super.onCreate(savedInstanceState);
    ////////
    IntentFilter filter = new IntentFilter(ContentLoader.LOADING_STARTED);
    filter.addAction(ContentLoader.LOADING_FINISHED);
    registerReceiver(_mLoadingStateReceiver,filter);
  }

  @Override
  public void setContentView(int layoutResID)
  {
    super.setContentView(layoutResID);
    if(_mIsEmulated)
    {
      final Window window = getWindow();
      window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.emulated_actionbar);
////// Установим обработчик нажатия на кнопку обновить
      final ImageView button = (ImageView) window.findViewById(R.id.refresh_button);
      if(button!=null)
      {
        button.setOnClickListener(this);
        _mProgressDrawable = (LevelListDrawable)button.getDrawable();
      }
////// Установим обработчик нажатия на селектор
      final View spinner = window.findViewById(R.id.selector);
      if(spinner!=null)
        spinner.setOnClickListener(this);
    }
    else
    {
      WrappedActionBar bar = new WrappedActionBar(this);
      bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.top_bar));
      /*
      if(bar!=null)
      {
        bar.setDisplayUseLogoEnabled(false);
      }*/
    }
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
    unregisterReceiver(_mLoadingStateReceiver);
    if(_mIsEmulated && _mNavigatorAdapter!=null)
      _mNavigatorAdapter.unregisterDataSetObserver(_mObserver);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuItem item;
    if(!_mIsEmulated)
    {
      item = menu.add(Menu.NONE, MENU_REFRESH, Menu.FIRST, R.string.refresh);
      _mProgressDrawable = (LevelListDrawable) getResources().getDrawable(R.drawable.ic_menu_refresh);
      item.setIcon(_mProgressDrawable);
      MenuCompat.setShowAsAction(item,MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
    }
    item = menu.add(Menu.NONE, MENU_SETTINGS, Menu.FIRST, R.string.settings);
    item.setIcon(android.R.drawable.ic_menu_preferences);
////////
    showRefreshProgress(ContentLoader.isLoading());
////////
    return super.onCreateOptionsMenu(menu);
  }

  protected void showRefreshProgress(boolean inProgress)
  {
    if(_mProgressDrawable==null)
      return;
////////
    _mHandler.removeCallbacks(_mProgressRunnable);
    _mProgressDrawable.setLevel(0);
////////
    if(inProgress)
      _mHandler.postDelayed(_mProgressRunnable,ANIMATION_INTERVAL);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch(item.getItemId())
    {
      case MENU_REFRESH:
        refreshInfo();
        return true;
      case MENU_SETTINGS:
        startActivity(new Intent(this, CommonSettings.class));
    }
    return super.onOptionsItemSelected(item);
  }

  /*protected boolean getAccountData(long accountId, Bundle bundle)
  {
    Cursor cursor = managedQuery(AccountsProvider.Accounts.CONTENT_URI,
                                 new String[] {AccountsProvider.Accounts.COLUMN_LOGIN,
                                               AccountsProvider.Accounts.COLUMN_PASSWORD,
                                               AccountsProvider.Accounts.COLUMN_CUSTOM1},
                                 AccountsProvider.Accounts.COLUMN_ID+"=?",new String[] {Long.toString(accountId)},
                                 null);
    try
    {
      if(cursor.moveToFirst())
      {
        bundle.putString(OsmpRequest.ACCOUNT_ID,Long.toString(accountId));
        bundle.putString(OsmpRequest.LOGIN,cursor.getString(0));
        bundle.putString(OsmpRequest.PASSWORD,cursor.getString(1));
        bundle.putString(OsmpRequest.TERMINAL,cursor.getString(2));
        return true;
      }
    }
    finally
    {
      if(cursor!=null)
        cursor.close();
    }
    return false;
  }*/

  private void refreshInfo()
  {
    ContentLoader.refresh(this);
  }

  @Override
  public void onClick(View view)
  {
    switch(view.getId())
    {
      case R.id.refresh_button:
        refreshInfo();
        break;
      case R.id.selector:
        showSelectorList();
        break;
    }
  }

  private void showSelectorList()
  {
    if(_mDialog==null)
      _mDialog = new SpinnerDialog(this, _mNavigatorAdapter, this);
    _mDialog.show(getSupportFragmentManager(),null);
  }

  protected void setListNavigationMode(CursorAdapter adapter, int selectedIndex)
  {
    if(_mIsEmulated)
    {
      if(_mNavigatorAdapter!=null)
        _mNavigatorAdapter.unregisterDataSetObserver(_mObserver);
      _mNavigatorAdapter = adapter;
      _mDialog = null;
      adapter.registerDataSetObserver(_mObserver);
      final ViewGroup spinner = (ViewGroup) getWindow().findViewById(R.id.selector);
      if(spinner!=null)
      {
        View child = adapter.getView(selectedIndex,null,spinner);
        spinner.removeAllViews();
        if(child!=null)
          spinner.addView(child);
        spinner.setVisibility(View.VISIBLE);
        _mSelectedNavigatorItem = selectedIndex;
      }
    }
    else
    {
      WrappedActionBar bar = new WrappedActionBar(this);
      bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
      bar.setListNavigationCallbacks(adapter, this);
      /*
      bar.setSelectedNavigationItem(selectedIndex);*/
    }
  }

  @Override
  public void onClick(DialogInterface dialogInterface, int index)
  {
    long id = _mNavigatorAdapter.getItemId(index);
    dialogInterface.dismiss();
    if(onNavigationItemSelected(index,id))
      setSelectedNavigationItem(index);
  }

  protected void setSelectedNavigationItem(int index)
  {
    if(!_mIsEmulated)
    {
      /*ActionBar bar = getActionBar();
      bar.setSelectedNavigationItem(index);
      return;*/
    }
/////////
    if(_mNavigatorAdapter==null)
      return;
/////////
    final ViewGroup spinner = (ViewGroup) getWindow().findViewById(R.id.selector);
    if(spinner!=null)
    {
      View child = _mNavigatorAdapter.getView(index,null,spinner);
      spinner.removeAllViews();
      if(child!=null)
        spinner.addView(child);
      _mSelectedNavigatorItem = index;
    }
  }
  
  public boolean onNavigationItemSelected(int itemPosition, long itemId)
  {
    return false;
  }

  private static class SpinnerDialog extends DialogFragment
  {
    private final Context                         _mContext;
    private final NavigationAdapter               _mAdapter;
    private final DialogInterface.OnClickListener _mListener;

    private SpinnerDialog(Context context, CursorAdapter listAdapter, DialogInterface.OnClickListener listener)
    {
      _mContext = new ContextThemeWrapper(context,android.R.style.Theme_Light);
      _mAdapter = new NavigationAdapter(listAdapter);
      _mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
      AlertDialog.Builder builder = new AlertDialog.Builder(_mContext);
      builder.setAdapter(_mAdapter, _mListener);
      builder.setCancelable(true);
      final Dialog dialog = builder.create();
      dialog.setCanceledOnTouchOutside(true);
      return dialog;
    }
  }

  private static class NavigationAdapter implements WrapperListAdapter
  {
    private final CursorAdapter _mAdapter;
    public NavigationAdapter(CursorAdapter adapter)
    {
      _mAdapter = adapter;
    }

    @Override
    public ListAdapter getWrappedAdapter()
    {
      return _mAdapter;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
      return _mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int index)
    {
      return _mAdapter.isEnabled(index);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver)
    {
      _mAdapter.registerDataSetObserver(dataSetObserver);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver)
    {
      _mAdapter.unregisterDataSetObserver(dataSetObserver);
    }

    @Override
    public int getCount()
    {
      return _mAdapter.getCount();
    }

    @Override
    public Object getItem(int index)
    {
      return _mAdapter.getItem(index);
    }

    @Override
    public long getItemId(int index)
    {
      return _mAdapter.getItemId(index);
    }

    @Override
    public boolean hasStableIds()
    {
      return _mAdapter.hasStableIds();
    }

    @Override
    public View getView(int index, View view, ViewGroup viewGroup)
    {
      return _mAdapter.getDropDownView(index,view,viewGroup);
    }

    @Override
    public int getItemViewType(int index)
    {
      return _mAdapter.getItemViewType(index);
    }

    @Override
    public int getViewTypeCount()
    {
      return _mAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty()
    {
      return _mAdapter.isEmpty();
    }
  }
}
