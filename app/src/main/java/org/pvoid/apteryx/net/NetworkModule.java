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

import org.pvoid.apteryx.net.commands.GetAgentInfoCommand;
import org.pvoid.apteryx.net.commands.GetAgentsCommand;
import org.pvoid.apteryx.net.results.GetAgentInfoResult;
import org.pvoid.apteryx.net.results.GetAgentsResult;
import org.pvoid.apteryx.net.results.ResultFactory;

import dagger.Module;
import dagger.Provides;

@Module(injects = {RequestExecutor.class, ResultFactories.class})
public class NetworkModule {
    @Provides
    public ResultFactories provideFactories() {
        OsmpResultFactories factories = new OsmpResultFactories();
        // add all requests here
        factories.register(GetAgentInfoCommand.NAME, new GetAgentInfoResult.Factory());
        factories.register(GetAgentsCommand.NAME, new GetAgentsResult.Factory());

        return factories;
    }

    @Provides
    public RequestExecutor provideExecutor(ResultFactories factories) {
        return new OsmpRequestExecutor(factories);
    }
}