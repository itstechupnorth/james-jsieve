/***********************************************************************
 * Copyright (c) 2003-2004 The Apache Software Foundation.             *
 * All rights reserved.                                                *
 * ------------------------------------------------------------------- *
 * Licensed under the Apache License, Version 2.0 (the "License"); you *
 * may not use this file except in compliance with the License. You    *
 * may obtain a copy of the License at:                                *
 *                                                                     *
 *     http://www.apache.org/licenses/LICENSE-2.0                      *
 *                                                                     *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS,   *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or     *
 * implied.  See the License for the specific language governing       *
 * permissions and limitations under the License.                      *
 ***********************************************************************/

package org.apache.jsieve.junit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.SieveConfigurationException;

import junit.framework.TestCase;

/**
 * Class ConfigurationManagerTest
 */
public class ConfigurationManagerTest extends TestCase
{

    /**
     * Constructor for ConfigurationManagerTest.
     * @param arg0
     */
    public ConfigurationManagerTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(ConfigurationManagerTest.class);
    }

    /**
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    /**
     * Test the CommandMap maps 'MUST' and 'SHOULD' be supported commands to the 
     * correct classes.
     */
    public void testCommandMap()
    {
        Map map = new HashMap();
        // Condition Commands
        // RFC3082 - Implementations MUST support these:
        map.put("if", "org.apache.jsieve.commands.If");
        map.put("else", "org.apache.jsieve.commands.Else");
        map.put("elsif", "org.apache.jsieve.commands.Elsif");
        map.put("require", "org.apache.jsieve.commands.Require");
        map.put("stop", "org.apache.jsieve.commands.Stop");

        // Action Commands        
        // RFC3082 - Implementations MUST support these:
        map.put("keep", "org.apache.jsieve.commands.Keep");
        map.put("discard", "org.apache.jsieve.commands.Discard");
        map.put("redirect", "org.apache.jsieve.commands.Redirect");
        // RFC3082 - Implementations SHOULD support these:       
        map.put("reject", "org.apache.jsieve.commands.optional.Reject");
        map.put("fileinto", "org.apache.jsieve.commands.optional.FileInto");

        boolean isTestPassed = false;
        try
        {
            Map commandMap = ConfigurationManager.getInstance().getCommandMap();

            Iterator mapIter = map.entrySet().iterator();
            while (mapIter.hasNext())
            {
                Map.Entry entry = (Map.Entry) mapIter.next();
                assertTrue(
                    "Key: " + entry.getKey(),
                    commandMap.containsKey(entry.getKey()));
                assertTrue(
                    "Value: " + entry.getValue(),
                    commandMap.get(entry.getKey()).equals(entry.getValue()));
            }
            isTestPassed = true;
        }
        catch (SieveConfigurationException e)
        {
        }
        assertTrue(isTestPassed);
    }
    
    /**
     * Test the TestMap maps 'MUST' and 'SHOULD' be supported tests to the 
     * correct classes.
     */
    public void testTestMap()
    {
        Map map = new HashMap();

        // RFC3082 - Implementations MUST support these tests:
        map.put("address", "org.apache.jsieve.tests.Address");
        map.put("allof", "org.apache.jsieve.tests.AllOf");
        map.put("anyof", "org.apache.jsieve.tests.AnyOf");
        map.put("exists", "org.apache.jsieve.tests.Exists");
        map.put("false", "org.apache.jsieve.tests.False");
        map.put("header", "org.apache.jsieve.tests.Header");
        map.put("not", "org.apache.jsieve.tests.Not");
        map.put("size", "org.apache.jsieve.tests.Size");
        map.put("true", "org.apache.jsieve.tests.True");

        // RFC3082 - Implementations SHOULD support the "envelope" test.
        map.put("envelope", "org.apache.jsieve.tests.optional.Envelope");

        boolean isTestPassed = false;
        try
        {
            Map testMap = ConfigurationManager.getInstance().getTestMap();

            Iterator mapIter = map.entrySet().iterator();
            while (mapIter.hasNext())
            {
                Map.Entry entry = (Map.Entry) mapIter.next();
                assertTrue(
                    "Key: " + entry.getKey(),
                    testMap.containsKey(entry.getKey()));
                assertTrue(
                    "Value: " + entry.getValue(),
                    testMap.get(entry.getKey()).equals(entry.getValue()));
            }
            isTestPassed = true;
        }
        catch (SieveConfigurationException e)
        {
        }
        assertTrue(isTestPassed);
    } 
    
    /**
     * Test the CommparatorMap maps 'MUST' and 'SHOULD' be supported comparators to
     * the correct classes.
     */
    public void testComparatorMap()
    {
        Map map = new HashMap();

        // RFC3082 - Required Comparators
        map.put("i;octet", "org.apache.jsieve.comparators.Octet");
        map.put("i;ascii-casemap", "org.apache.jsieve.comparators.AsciiCasemap");

        boolean isTestPassed = false;
        try
        {
            Map comparatorMap = ConfigurationManager.getInstance().getComparatorMap();

            Iterator mapIter = map.entrySet().iterator();
            while (mapIter.hasNext())
            {
                Map.Entry entry = (Map.Entry) mapIter.next();
                assertTrue(
                    "Key: " + entry.getKey(),
                    comparatorMap.containsKey(entry.getKey()));
                assertTrue(
                    "Value: " + entry.getValue(),
                    comparatorMap.get(entry.getKey()).equals(entry.getValue()));
            }
            isTestPassed = true;
        }
        catch (SieveConfigurationException e)
        {
        }
        assertTrue(isTestPassed);
    }    
       


        

}