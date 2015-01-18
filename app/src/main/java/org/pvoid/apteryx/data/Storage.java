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

package org.pvoid.apteryx.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.pvoid.apteryx.data.agents.Agent;
import org.pvoid.apteryx.data.persons.Person;
import org.pvoid.apteryx.data.terminals.Terminal;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface Storage {
    void storePerson(@NonNull Person person);
    @Nullable Person[] getPersons() throws ExecutionException, InterruptedException;
    void storeAgents(@NonNull Agent... agents);
    void storeTerminals(@NonNull String personId, @NonNull Terminal... terminals);
    @Nullable Terminal[] getTerminals() throws ExecutionException, InterruptedException;
}
