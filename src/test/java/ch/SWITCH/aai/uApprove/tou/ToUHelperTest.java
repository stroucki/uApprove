/*
 * Copyright (c) 2011, SWITCH
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of SWITCH nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SWITCH BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ch.SWITCH.aai.uApprove.tou;

import org.joda.time.DateTime;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * ToU Helper.
 */
public class ToUHelperTest {

    /** ToU. */
    private ToU tou1a;

    /** ToU. */
    private ToU tou1b;

    /** ToU. */
    private ToU tou2;

    /** Before class. */
    @BeforeClass
    public void initialize() {
        tou1a = new ToU();
        tou1a.setVersion("1.0");
        tou1a.setResource(new ClassPathResource("examples/terms-of-use.html"));
        tou1a.initialize();

        tou1b = new ToU();
        tou1b.setVersion("1.0");
        tou1b.setResource(new ByteArrayResource("some other text".getBytes()));
        tou1b.initialize();

        tou2 = new ToU();
        tou2.setVersion("2.0");
        tou2.setResource(new ClassPathResource("examples/terms-of-use.html"));
        tou2.initialize();
    }

    /** Test. */
    @Test
    public void testAcceptedToU() {
        Assert.assertFalse(ToUHelper.acceptedToU(tou1a, null, false));
        final ToUAcceptance touAcceptance = new ToUAcceptance(tou1a, new DateTime());

        Assert.assertTrue(ToUHelper.acceptedToU(tou1a, touAcceptance, false));
        Assert.assertTrue(ToUHelper.acceptedToU(tou1a, touAcceptance, true));

        Assert.assertTrue(ToUHelper.acceptedToU(tou1b, touAcceptance, false));
        Assert.assertFalse(ToUHelper.acceptedToU(tou1b, touAcceptance, true));

        Assert.assertFalse(ToUHelper.acceptedToU(tou2, touAcceptance, false));

    }

}
