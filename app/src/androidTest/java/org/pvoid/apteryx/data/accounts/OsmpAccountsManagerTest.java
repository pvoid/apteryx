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

package org.pvoid.apteryx.data.accounts;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.data.Storage;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class OsmpAccountsManagerTest {
    @Test
    public void addAccountCheck() throws Exception {
        Storage storage = Mockito.mock(Storage.class);
        Account account = new Account("LOGIN", "PASSWORD", "TERMINAL");
        OsmpAccountsManager manager = new OsmpAccountsManager(Robolectric.application, storage);
        Assert.assertTrue(manager.add(account));
        Mockito.verify(storage).storeAccount(Mockito.same(account));
        Mockito.reset(storage);
        account = new Account("LOGIN", "PASSWORD", "TERMINAL");
        Assert.assertFalse(manager.add(account));
        Mockito.verify(storage, Mockito.never()).storeAccount(Mockito.any(Account.class));
    }
}
