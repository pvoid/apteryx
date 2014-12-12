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

package org.pvoid.apteryx.net;

import android.support.annotation.NonNull;

import org.pvoid.apteryx.net.results.Result;
import org.pvoid.apteryx.net.results.ResponseTag;
import org.pvoid.apteryx.net.results.ResultFactory;

import java.util.HashMap;
import java.util.Map;

public class OsmpResultFactories implements ResultFactories {

    private final Map<String, ResultFactory> mFactories = new HashMap<>();

    @Override
    public void register(@NonNull String tagName, @NonNull ResultFactory factory) {
        mFactories.put(tagName, factory);
    }

    @Override
    public Result build(@NonNull ResponseTag tag) {
        ResultFactory factory = mFactories.get(tag.getName());
        if (factory != null) {
            return factory.create(tag);
        }
        return null;
    }
}
