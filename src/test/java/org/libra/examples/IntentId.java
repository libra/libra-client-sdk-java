// Copyright (c) The Libra Core Contributors
// SPDX-License-Identifier: Apache-2.0

package org.libra.examples;

import org.junit.Assert;
import org.junit.Test;
import org.libra.AccountIdentifier;
import org.libra.IntentIdentifier;
import org.libra.LocalAccount;
import org.libra.Testnet;

public class IntentId {
    @Test
    public void encodeAndDecode() {
        LocalAccount merchant = LocalAccount.generate();
        long amount = 5_000_000;

        AccountIdentifier merchantAccountId = new AccountIdentifier(
                AccountIdentifier.NetworkPrefix.TestnetPrefix,
                merchant.address
        );

        IntentIdentifier intentId = new IntentIdentifier(merchantAccountId, Testnet.COIN1, amount);
        String encodeIntent = intentId.encode();
        System.out.println("encoded intent identifier: " + encodeIntent);

        // payer receives encode intent, decode for creating payment transaction
        IntentIdentifier decodedIntent = IntentIdentifier.decode(AccountIdentifier.NetworkPrefix.TestnetPrefix, encodeIntent);

        Assert.assertEquals(merchantAccountId, decodedIntent.getAccountIdentifier());
        Assert.assertEquals(Testnet.COIN1, decodedIntent.getCurrency());
        Assert.assertEquals(amount, decodedIntent.getAmount());
    }
}
