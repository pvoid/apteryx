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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.net.results.ResponseTag;
import org.pvoid.apteryx.net.results.ResultFactory;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OsmpResultFactoriesTest {
    @Test
    public void factoryRegistrationCheck() throws Exception {
        OsmpResultFactories factories = new OsmpResultFactories();
        ResultFactory factory = Mockito.mock(ResultFactory.class);
        factories.register("requestLogs", factory);
        ResponseTag tag = Mockito.mock(ResponseTag.class);
        Mockito.when(tag.getName()).thenReturn("setPublicKey");
        factories.build(tag);
        Mockito.verify(factory, Mockito.never()).create(Mockito.any(ResponseTag.class));
        Mockito.when(tag.getName()).thenReturn("requestLogs");
        factories.build(tag);
        Mockito.verify(factory, Mockito.only()).create(Mockito.same(tag));
    }
}
