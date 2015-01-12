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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import org.pvoid.apteryx.R;
import org.pvoid.apteryx.util.LogHelper;

public enum TerminalType {
    Unknown(0, R.string.terminal_type_0),
    Linudix(1, R.string.terminal_type_1),
    Web(2, R.string.terminal_type_2),
    Windows(3, R.string.terminal_type_3),
    SelfService(4, R.string.terminal_type_4),
    MySQL(5, R.string.terminal_type_5),
    Xml(6, R.string.terminal_type_6),
    Qiwi1C(7, R.string.terminal_type_7),
    Wap(8, R.string.terminal_type_8),
    Pda(9, R.string.terminal_type_9),
    Monitor(10, R.string.terminal_type_10),
    Abg(11, R.string.terminal_type_11),
    Nurit(12, R.string.terminal_type_12),
    Guard(13, R.string.terminal_type_13),
    Atm(15, R.string.terminal_type_15),
    Diller(16, R.string.terminal_type_16),
    iPhone(17, R.string.terminal_type_17),
    Linux(18, R.string.terminal_type_18),
    Light(19, R.string.terminal_type_19),
    Cashier(33, R.string.terminal_type_33),
    Java(52, R.string.terminal_type_52),
    CashierMobile(53, R.string.terminal_type_53),
    EportMobile(101, R.string.terminal_type_101),
    OpenWay(102, R.string.terminal_type_102),
    TSB(103, R.string.terminal_type_103),
    XML2(201, R.string.terminal_type_201),
    Ingenico(500, R.string.terminal_type_500),
    ShtrihMini(700, R.string.terminal_type_700),
    ShtrihMobile(701, R.string.terminal_type_701),
    Pax(800, R.string.terminal_type_800),
    UniPos(801, R.string.terminal_type_801),
    MobileMonitor(1300, R.string.terminal_type_1300);

    public final int id;
    @StringRes public final int nameRes;

    TerminalType(int id, int nameRes) {
        this.id = id;
        this.nameRes = nameRes;
    }

    @NonNull
    public static TerminalType fromId(int id) {
        for (TerminalType type : values()) {
            if (id == type.id) {
                return type;
            }
        }

        return Unknown;
    }

    @NonNull
    public static TerminalType fromString(@Nullable String value) {
        if (!TextUtils.isEmpty(value)) {
            try {
                return fromId(Integer.parseInt(value));
            } catch (NumberFormatException e) {
                LogHelper.error("TerminalType", "Can't convert id '%1$s' to type. Reason: '%2$s'", value, e.getMessage());
            }
        }
        return Unknown;
    }
}
