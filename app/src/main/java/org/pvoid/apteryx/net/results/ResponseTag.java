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

package org.pvoid.apteryx.net.results;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface ResponseTag {
    @NonNull String getName();
    @Nullable String getText();
    @Nullable String getAttribute(@NonNull String name);
    @Nullable ResponseTag nextChild() throws TagReadExceptin;

    public class TagReadExceptin extends Exception {

        public TagReadExceptin(String detailMessage) {
            super(detailMessage);
        }

        public TagReadExceptin(Throwable throwable) {
            super(throwable);
        }
    }
}
