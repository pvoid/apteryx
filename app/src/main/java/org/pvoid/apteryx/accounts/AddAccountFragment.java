/*
 * Copyright (C) 2010-2014  Dmitry "PVOID" Petuhov
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

package org.pvoid.apteryx.accounts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import org.pvoid.apteryx.R;

public class AddAccountFragment extends Fragment {
    private static final String ARG_LOGIN = "login";
    private static final String ARG_TERMINAL = "terminal";

    @Nullable private String mLogin;
    @Nullable private String mTerminal;
    @Nullable private AddAccuntListener mListener;

    public static AddAccountFragment newInstance(@Nullable String login, @Nullable String terminal) {
        AddAccountFragment fragment = new AddAccountFragment();
        Bundle args = new Bundle();
        args.putString(ARG_LOGIN, login);
        args.putString(ARG_TERMINAL, terminal);
        fragment.setArguments(args);
        return fragment;
    }

    public AddAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            mLogin = arguments.getString(ARG_LOGIN);
            mTerminal = arguments.getString(ARG_TERMINAL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_acount_info, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            if (!TextUtils.isEmpty(mLogin)) {
                EditText login = (EditText) view.findViewById(R.id.account_login);
                login.setText(mLogin);
            }
            if (!TextUtils.isEmpty(mTerminal)) {
                EditText login = (EditText) view.findViewById(R.id.account_terminal);
                login.setText(mTerminal);
            }
        }

        Button button = (Button) view.findViewById(R.id.account_next);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mListener != null) {
                    mListener.onAddAccount(null, null, null);
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AddAccuntListener) getActivity();
        } catch (ClassCastException e) {
            throw new IllegalStateException("Host activity should implement " + AddAccuntListener.class.getName());
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface AddAccuntListener {
        void onAddAccount(@NonNull String login, @NonNull String password, @NonNull String terminal);
    }
}
