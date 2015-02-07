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

package org.pvoid.apteryx.views.terminals.filters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import org.pvoid.apteryx.data.terminals.Terminal;
import org.pvoid.apteryx.data.terminals.TerminalType;

import java.util.Arrays;
import java.util.Comparator;

public class BaseTerminalsFilter implements TerminalsFilter {

    @Nullable private final Comparator<Terminal> mComparator;
    @Nullable private Terminal mTerminals[] = null;
    private int mCount = 0;

    public BaseTerminalsFilter(@Nullable Comparator<Terminal> comparator) {
        mComparator = comparator;
    }

    public BaseTerminalsFilter() {
        this(null);
    }

    @Override
    public void fill(@NonNull Terminal[] terminals) {
        mTerminals = new Terminal[terminals.length];
        int index = 0;
        for (Terminal terminal : terminals) {
            if (!match(terminal)) {
                continue;
            }
            mTerminals[index++] = terminal;
        }
        mCount = index;
        if (mComparator != null) {
            Arrays.sort(mTerminals, 0, mCount, mComparator);
        }
    }

    @Override
    @NonNull public Terminal getAt(int position) {
        if (mTerminals != null && position > -1 && position < mCount) {
            return mTerminals[position];
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int count() {
        return mCount;
    }

    protected boolean match(@NonNull Terminal terminal) {
        return terminal.getType() == TerminalType.SelfService ||
                terminal.getType() == TerminalType.Linux;
    }
}
