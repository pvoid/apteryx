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

package org.pvoid.apteryxaustralis.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import org.pvoid.apteryxaustralis.R;

public class AddAccountDialog extends Dialog implements View.OnClickListener
{
  private OnClickListener _mListener;
  private final Handler   _mHandler = new Handler();
  private final Runnable  _mAddRunnable = new Runnable()
  {
    @Override
    public void run()
    {
      if(_mListener!=null)
        _mListener.onClick(AddAccountDialog.this, DialogInterface.BUTTON_POSITIVE);
    }
  };

  public AddAccountDialog(Context context)
  {
    super(context);
    final View content = View.inflate(context,R.layout.account,null);
    View view = content.findViewById(R.id.wiki_link);
    if(view!=null)
      view.setOnClickListener(this);
    view = content.findViewById(R.id.add);
    if(view!=null)
      view.setOnClickListener(this);
    setContentView(content);
    setTitle(R.string.add_account);
    getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
  }

  public void howClicked()
  {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    intent.setData(Uri.parse("http://kiosks.ru/wiki/index.php?title=Apteryx_australis#.D0.A1.D0.BE.D0.B7.D0.B4.D0.B0.D0.BD.D0.B8.D0.B5_.D1.82.D0.B5.D1.80.D0.BC.D0.B8.D0.BD.D0.B0.D0.BB.D0.B0"));
    getContext().startActivity(intent);
  }

  @Override
  public void onClick(View view)
  {
    switch(view.getId())
    {
      case R.id.wiki_link:
        howClicked();
        break;
      case R.id.add:
        _mHandler.post(_mAddRunnable);
        dismiss();
        break;
    }
  }

  public void setOnAddClickListener(OnClickListener listener)
  {
    _mListener = listener;
  }
}
