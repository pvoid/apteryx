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

package org.pvoid.apteryx.data.persons;

import android.content.Context;

import android.support.annotation.NonNull;
import org.pvoid.apteryx.data.Storage;
import org.pvoid.apteryx.data.terminals.TerminalsManager;
import org.pvoid.apteryx.net.RequestExecutor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {PersonsManager.class}, complete = false)
public class PersonsModule {
    @Provides
    @Singleton
    public PersonsManager provideManager(@NonNull Context context, @NonNull Storage storage,
                                         @NonNull TerminalsManager terminalsManager,
                                         @NonNull RequestExecutor executor) {
        return new OsmpPersonsManager(context, storage, terminalsManager, executor);
    }
}
