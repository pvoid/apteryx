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

package org.pvoid.apteryx.data.terminals;

import org.pvoid.apteryx.annotations.MachineStateLevel;

public class MachineState {

    public static final int LEVEL_INFO = 0;
    public static final int LEVEL_WARN = 1;
    public static final int LEVEL_ERROR = 2;

    @MachineStateLevel
    public final int level;
    public final int text;
    public final int flag;

    public MachineState(int level, int flag, int text) {
        this.level = level;
        this.text = text;
        this.flag = flag;
    }
}
