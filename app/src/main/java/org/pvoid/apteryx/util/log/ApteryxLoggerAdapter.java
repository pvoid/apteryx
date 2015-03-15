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

package org.pvoid.apteryx.util.log;

import android.support.annotation.NonNull;
import android.util.Log;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/* package */ class ApteryxLoggerAdapter extends MarkerIgnoringBase {

    private static volatile int sLogLevel = Log.INFO;

    @NonNull
    private final String mTag;

    /* package */ ApteryxLoggerAdapter(@NonNull String tag) {
        mTag = tag;
    }

    /* package */ static void setLogLevel(int level) {
        sLogLevel = level;
    }

    @Override
    public boolean isTraceEnabled() {
        return isLoggable(Log.VERBOSE);
    }

    @Override
    public void trace(String msg) {
        log(Log.VERBOSE, msg, null);
    }

    @Override
    public void trace(String format, Object arg) {
        formatAndLog(Log.VERBOSE, format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        formatAndLog(Log.VERBOSE, format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        formatAndLog(Log.VERBOSE, format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        log(Log.VERBOSE, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return isLoggable(Log.DEBUG);
    }

    @Override
    public void debug(String msg) {
        log(Log.DEBUG, msg, null);
    }

    @Override
    public void debug(String format, Object arg) {
        formatAndLog(Log.DEBUG, format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        formatAndLog(Log.DEBUG, format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        formatAndLog(Log.DEBUG, format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        log(Log.DEBUG, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return isLoggable(Log.INFO);
    }

    @Override
    public void info(String msg) {
        log(Log.INFO, msg, null);
    }

    @Override
    public void info(String format, Object arg) {
        formatAndLog(Log.INFO, format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        formatAndLog(Log.INFO, format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        formatAndLog(Log.INFO, format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        log(Log.INFO, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return isLoggable(Log.WARN);
    }

    @Override
    public void warn(String msg) {
        log(Log.WARN, msg, null);
    }

    @Override
    public void warn(String format, Object arg) {
        formatAndLog(Log.WARN, format, arg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        formatAndLog(Log.WARN, format, arguments);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        formatAndLog(Log.WARN, format, arg1, arg2);
    }

    @Override
    public void warn(String msg, Throwable t) {
        log(Log.WARN, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return isLoggable(Log.ERROR);
    }

    @Override
    public void error(String msg) {
        log(Log.ERROR, msg, null);
    }

    @Override
    public void error(String format, Object arg) {
        formatAndLog(Log.ERROR, format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        formatAndLog(Log.ERROR, format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        formatAndLog(Log.ERROR, format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        log(Log.ERROR, msg, t);
    }

    private boolean isLoggable(int level) {
        return level >= sLogLevel;
    }

    private void formatAndLog(int level, @NonNull String format, Object... args) {
        if (!isLoggable(level)) {
            return;
        }
        FormattingTuple ft = MessageFormatter.arrayFormat(format, args);
        log(level, ft.getMessage(), ft.getThrowable());
    }

    private void log(int level, @NonNull String message, @Nullable Throwable exception) {
        if (!isLoggable(level)) {
            return;
        }
        if (exception != null) {
            message += "\n" + Log.getStackTraceString(exception);
        }
        Log.println(level, mTag, message);
    }
}
