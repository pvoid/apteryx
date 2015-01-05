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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class AccountTest {
    @Test
    public void constructorCheck() throws Exception {
        Account account = new Account("LOGIN", "PASS_HASH", "TERMINAL");
        Assert.assertEquals("LOGIN", account.getLogin());
        Assert.assertEquals("PASS_HASH", account.getPasswordHash());
        Assert.assertEquals("TERMINAL", account.getTerminal());
        Assert.assertNull(account.getTitle());
        Assert.assertNull(account.getAgentId());
        Assert.assertFalse(account.isVerified());

        Account verified = account.cloneVerified("TITLE", "ID");
        Assert.assertNotSame(account, verified);
        Assert.assertEquals("LOGIN", verified.getLogin());
        Assert.assertEquals("PASS_HASH", verified.getPasswordHash());
        Assert.assertEquals("TERMINAL", verified.getTerminal());
        Assert.assertEquals("TERMINAL", verified.getTerminal());
        Assert.assertEquals("ID", verified.getAgentId());
        Assert.assertTrue(verified.isVerified());
    }

    @Test
    public void equalsCheck() throws Exception {
        Account account = new Account("LOGIN", "PASS_HASH", "TERMINAL");

        Assert.assertEquals("LOGIN".hashCode(), account.hashCode());
        Assert.assertTrue(account.equals(account));
        Assert.assertFalse(account.equals(null));
        Assert.assertFalse(account.equals("Some object"));
        Assert.assertTrue(account.equals(account.cloneVerified("TITLE", "ID")));
        Assert.assertTrue(account.equals(new Account("LOGIN", "", "")));
        Assert.assertFalse(account.equals(new Account("LOGIN2", "PASS_HASH", "TERMINAL")));
    }
}
