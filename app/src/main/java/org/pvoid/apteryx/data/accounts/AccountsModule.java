/*
 * Copyright (C) 2010-2015  Dmitry "PVOID" Petuhov
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

package org.pvoid.apteryx.data.accounts;

import android.content.Context;

import org.pvoid.apteryx.data.Storage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {AccountsManager.class}, complete = false)
public class AccountsModule {

    private final Context mContext;

    public AccountsModule(Context context) {
        mContext = context.getApplicationContext();
    }

    @Provides
    @Singleton
    public AccountsManager provideAccountManager(Storage storage) {
        return new OsmpAccountsManager(mContext, storage);
    }
}
