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

package org.pvoid.apteryx.util;

import android.util.Log;

import org.pvoid.apteryxaustralis.BuildConfig;

public final class LogHelper {

    private static int sLogLevel = BuildConfig.DEBUG ? Log.DEBUG : Log.INFO;

    private LogHelper() {
    }

    public static void setLogLevel(int level) {
        sLogLevel = level;
    }

    public static void log(int level, String tag, String message) {
        if (level < sLogLevel) {
            return;
        }
        Log.println(level, tag, message);
    }

    public static void log(int level, String tag, String message, Object... data) {
        log(level, tag, String.format(message, data));
    }

    public static void debug(String tag, String message) {
        log(Log.DEBUG, tag, message);
    }

    public static void debug(String tag, String message, Object... data) {
        log(Log.DEBUG, tag, message, data);
    }

    public static void info(String tag, String message) {
        log(Log.INFO, tag, message);
    }

    public static void info(String tag, String message, Object... data) {
        log(Log.INFO, tag, message, data);
    }

    public static void warn(String tag, String message) {
        log(Log.WARN, tag, message);
    }

    public static void warn(String tag, String message, Object... data) {
        log(Log.WARN, tag, message, data);
    }

    public static void error(String tag, String message) {
        log(Log.ERROR, tag, message);
    }

    public static void error(String tag, String message, Object... data) {
        log(Log.ERROR, tag, message, data);
    }
}
