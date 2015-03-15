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

package org.slf4j.impl;

import org.pvoid.apteryx.util.log.ApteryxLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder {

    public static String REQUESTED_API_VERSION = "1.6.99";
    private final ILoggerFactory mLoggerFactory;

    private StaticLoggerBinder() {
        mLoggerFactory = new ApteryxLoggerFactory();
    }

    @Override
    public ILoggerFactory getLoggerFactory() {
        return mLoggerFactory;
    }

    @Override
    public String getLoggerFactoryClassStr() {
        return ApteryxLoggerFactory.class.getName();
    }

    public static StaticLoggerBinder getSingleton() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final StaticLoggerBinder INSTANCE = new StaticLoggerBinder();
    }
}
