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

public interface PersonsManager {
    public static final String ACTION_VERIFIED = "org.pvoid.apteryx.data.persons.ACTION_VERIFIED";
    public static final String ACTION_CHANGED = "org.pvoid.apteryx.data.persons.ACTION_CHANGED";

    public static final String EXTRA_PERSON = "person";

    boolean add(@NonNull Person person);
    void verify(@NonNull Person person);
    @NonNull Person[] getPersons();
}
