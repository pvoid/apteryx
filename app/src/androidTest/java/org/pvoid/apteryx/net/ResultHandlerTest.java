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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ResultHandlerTest {
    @Test
    public void listenerCheck() throws Exception {
        ResultReceiver receiver = Mockito.mock(ResultReceiver.class);
        OsmpResponse response = Mockito.mock(OsmpResponse.class);
        ResultHandler handler = new ResultHandler(receiver);

        handler.onError();
        Mockito.verify(receiver, Mockito.only()).onError();
        Mockito.verify(receiver, Mockito.never()).onResponse(Mockito.any(OsmpResponse.class));
        Mockito.reset(receiver);
        handler.onSuccess(response);
        Mockito.verify(receiver, Mockito.never()).onError();
        Mockito.verify(receiver, Mockito.only()).onResponse(Mockito.same(response));
        Mockito.reset(receiver);

        handler.cancel();
        handler.onError();
        Mockito.verify(receiver, Mockito.never()).onError();
        Mockito.verify(receiver, Mockito.never()).onResponse(Mockito.any(OsmpResponse.class));
        Mockito.reset(receiver);
        handler.onSuccess(response);
        Mockito.verify(receiver, Mockito.never()).onError();
        Mockito.verify(receiver, Mockito.never()).onResponse(Mockito.any(OsmpResponse.class));
    }

    @Test
    public void cancelCheck() throws Exception {
        ResultHandler handler = new ResultHandler(null);
        Assert.assertFalse(handler.isCanceled());
        handler.cancel();
        Assert.assertTrue(handler.isCanceled());
    }

    @Test
    public void pendingStateCheck() throws Exception {
        ResultHandler handler = new ResultHandler(null);
        Assert.assertFalse(handler.isPending());
        handler.markPending();
        Assert.assertTrue(handler.isPending());
        handler.cancel();
        Assert.assertFalse(handler.isPending());
        handler.markPending();
        handler.onError();
        Assert.assertFalse(handler.isPending());
        handler.markPending();
        handler.onSuccess(null);
        Assert.assertFalse(handler.isPending());
    }
}
