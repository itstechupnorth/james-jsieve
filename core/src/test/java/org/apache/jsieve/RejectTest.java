/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.jsieve;

import org.apache.jsieve.exception.CommandException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.exception.SyntaxException;
import org.apache.jsieve.mail.ActionReject;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.parser.generated.ParseException;
import org.apache.jsieve.utils.JUnitUtils;
import org.junit.Assert;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Class RejectTest
 */
public class RejectTest {

    /**
     * Test for Command 'reject' with invalid arguments
     */
    @org.junit.Test
    public void testInvalidArguments() {
        boolean isTestPassed = false;
        String script = "reject 1 ;";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'reject' with an invalid block
     */
    @Test
    public void testInvalidBlock() {
        boolean isTestPassed = false;
        String script = "reject \"Spam not consumed here!\" {throwTestException;}";

        try {
            JUnitUtils.interpret(JUnitUtils.createMail(), script);
        } catch (SyntaxException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'reject'
     */
    @Test
    public void testReject() {
        boolean isTestPassed = false;
        String script = "reject \"Spam not consumed here!\";";

        try {
            MailAdapter mail = JUnitUtils.createMail();
            JUnitUtils.interpret(mail, script);
            Assert.assertTrue(mail.getActions().size() == 1);
            Assert.assertTrue(mail.getActions().get(0) instanceof ActionReject);
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Tests that the message is correctly passed
     */
    @Test
    public void testRejectMessage() throws Exception {
        String message = "Spam not consumed here!";
        String script = "reject \"" + message + "\";";
        ActionReject rejection = runRejectScript(script);
        Assert.assertEquals(message, rejection.getMessage());
    }

    private ActionReject runRejectScript(String script) throws SieveException, ParseException {
        MailAdapter mail = JUnitUtils.createMail();
        JUnitUtils.interpret(mail, script);
        assertTrue(mail.getActions().size() == 1);
        Object action = mail.getActions().get(0);
        assertTrue(action instanceof ActionReject);
        return (ActionReject) action;
    }

    /**
     * Test for Command 'reject'
     */
    @Test
    public void testRejectMissingMessage() {
        boolean isTestPassed = false;
        String script = "reject;";

        try {
            MailAdapter mail = JUnitUtils.createMail();
            JUnitUtils.interpret(mail, script);
            Assert.assertTrue(mail.getActions().size() == 1);
            Assert.assertTrue(mail.getActions().get(0) instanceof ActionReject);
        } catch (ParseException e) {
        } catch (SieveException e) {
            isTestPassed = true;
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for duplicate Command 'reject'
     */
    @Test
    public void testDuplicateReject() {
        boolean isTestPassed = false;
        String script = "reject \"Spam not consumed here!\"; reject \"Spam not consumed here!\";";

        try {
            MailAdapter mail = JUnitUtils.createMail();
            JUnitUtils.interpret(mail, script);
        } catch (CommandException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'reject' preceded by another command
     */
    @Test
    public void testRejectAndAPrecedingCommand() {
        boolean isTestPassed = false;
        String script = "keep; reject \"Spam not consumed here!\";";

        try {
            MailAdapter mail = JUnitUtils.createMail();
            JUnitUtils.interpret(mail, script);
        } catch (CommandException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

    /**
     * Test for Command 'reject' followed by another command
     */
    @Test
    public void testRejectAndAFollowingCommand() {
        boolean isTestPassed = false;
        String script = "reject \"Spam not consumed here!\"; keep;";

        try {
            MailAdapter mail = JUnitUtils.createMail();
            JUnitUtils.interpret(mail, script);
        } catch (CommandException e) {
            isTestPassed = true;
        } catch (ParseException e) {
        } catch (SieveException e) {
        }
        Assert.assertTrue(isTestPassed);
    }

}
