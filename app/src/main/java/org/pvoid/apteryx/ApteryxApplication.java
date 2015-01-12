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

package org.pvoid.apteryx;

import android.app.Application;

import org.pvoid.apteryx.data.DataModule;
import org.pvoid.apteryx.data.persons.PersonsModule;
import org.pvoid.apteryx.data.terminals.TerminalsModule;
import org.pvoid.apteryx.net.NetworkModule;

import dagger.ObjectGraph;

public class ApteryxApplication extends Application implements GraphHolder {

    private ObjectGraph mGraph;

    @Override
    public void onCreate() {
        createGraph();
        super.onCreate();
    }

    protected void createGraph() {
        mGraph = ObjectGraph.create(new NetworkModule(), new DataModule(this),
                new PersonsModule(this), new TerminalsModule(this));
    }

    @Override
    public ObjectGraph getGraph() {
        return mGraph;
    }
}
