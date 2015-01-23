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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.pvoid.apteryx.data.agents.Agent;

public interface PersonsManager {
    public static final String ACTION_PERSON_VERIFIED = "org.pvoid.apteryx.data.persons.ACTION_PERSON_VERIFIED";
    public static final String ACTION_PERSONS_CHANGED = "org.pvoid.apteryx.data.persons.ACTION_PERSONS_CHANGED";
    public static final String ACTION_CURRENT_PERSON_CHANGED = "org.pvoid.apteryx.data.persons.ACTION_CURRENT_PERSON_CHANGED";
    public static final String ACTION_AGENTS_CHANGED = "org.pvoid.apteryx.data.persons.ACTION_AGENTS_CHANGED";
    public static final String ACTION_CURRENT_AGENT_CHANGED = "org.pvoid.apteryx.data.persons.ACTION_CURRENT_AGENT_CHANGED";

    public static final String EXTRA_PERSON = "person";
    public static final String EXTRA_STATE = "state";

    boolean add(@NonNull Person person);
    void verify(@NonNull Person person);
    @NonNull Person[] getPersons();
    @Nullable Person getPerson(@NonNull String login);
    @Nullable Person getCurrentPerson();
    void setCurrentPerson(@NonNull String login);
    @Nullable Agent getCurrentAgent();
    void setCurrentAgent(@NonNull String agentId);
    @Nullable Agent[] getAgents(@NonNull String login);
}
