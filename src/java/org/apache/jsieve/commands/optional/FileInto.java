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

package org.apache.jsieve.commands.optional;

import java.util.List;
import java.util.ListIterator;

import org.apache.jsieve.Arguments;
import org.apache.jsieve.Block;
import org.apache.jsieve.SieveException;
import org.apache.jsieve.StringListArgument;
import org.apache.jsieve.SyntaxException;
import org.apache.jsieve.commands.AbstractActionCommand;
import org.apache.jsieve.mail.Action;
import org.apache.jsieve.mail.ActionFileInto;
import org.apache.jsieve.mail.MailAdapter;

/**
 * Class FileInto implements the FileInto Command as defined in RFC 3028, section 
 * 4.2.
 */
public class FileInto extends AbstractActionCommand
{

    /**
     * Constructor for Require.
     * @param children
     */
    public FileInto()
    {
        super();
    }

    /**
     * <p>Add an ActionFileInto to the List of Actions to be performed passing the
     * sole StringList argument as the destination. RFC 3028 mandates that there
     * should be only one FileInto per destination. If this is a duplicate, this
     * Command is silently ignored. 
     * </p>
     * <p>Also,
     * @see org.apache.jsieve.commands.AbstractCommand#executeBasic(MailAdapter, Arguments, Block)
     * </p>
     */
    protected Object executeBasic(
        MailAdapter mail,
        Arguments arguments,
        Block block)
        throws SieveException
    {
        String destination =
            (String) ((StringListArgument) arguments.getArgumentList().get(0))
                .getList()
                .get(
                0);

        // Only one fileinto per destination allowed, others should be
        // discarded            
        ListIterator actionsIter = mail.getActionsIterator();
        boolean isDuplicate = false;
        while (actionsIter.hasNext())
        {
            Action action = (Action) actionsIter.next();
            isDuplicate =
                (action instanceof ActionFileInto)
                    && (((ActionFileInto) action)
                        .getDestination()
                        .equals(destination));
        }

        if (!isDuplicate)
            mail.addAction(new ActionFileInto(destination));

        return null;
    }
    
    /**
     * @see org.apache.jsieve.commands.AbstractCommand#validateArguments(Arguments)
     */
    protected void validateArguments(Arguments arguments) throws SieveException
    {
        List args = arguments.getArgumentList();
        if (args.size() != 1)
            throw new SyntaxException(
                "Exactly 1 argument permitted. Found " + args.size());

        Object argument = args.get(0);
        if (!(argument instanceof StringListArgument))
            throw new SyntaxException("Expecting a string-list");

        if (1 != ((StringListArgument) argument).getList().size())
            throw new SyntaxException("Expecting exactly one argument");
    }
    

    

}