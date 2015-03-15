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

import org.slf4j.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ApteryxLoggerFactory implements ILoggerFactory {

    private final Map<String, Logger> mLoggers = new ConcurrentHashMap<>();

    @Override
    public Logger getLogger(String name) {
        Logger logger = mLoggers.get(name);
        if (logger == null) {
            synchronized (mLoggers) {
                if ((logger = mLoggers.get(name)) == null) {
                    logger = new ApteryxLoggerAdapter(name);
                    mLoggers.put(name, logger);
                }
            }
        }
        return logger;
    }
}
