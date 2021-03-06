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
package org.apache.jsieve.mailet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.james.mime4j.dom.address.AddressList;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.dom.address.MailboxList;
import org.apache.james.mime4j.dom.field.ParseException;
import org.apache.james.mime4j.field.address.AddressBuilder;
import org.apache.jsieve.SieveContext;
import org.apache.jsieve.exception.InternetAddressException;
import org.apache.jsieve.exception.SieveException;
import org.apache.jsieve.mail.Action;
import org.apache.jsieve.mail.AddressImpl;
import org.apache.jsieve.mail.MailAdapter;
import org.apache.jsieve.mail.MailUtils;
import org.apache.jsieve.mail.SieveMailException;
import org.apache.jsieve.mail.optional.EnvelopeAccessors;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.MailetContext;
/**
 * <p>
 * Class <code>SieveMailAdapter</code> implements a <code>MailAdapter</code>
 * for use in a Mailet environment.
 * </p>
 */
public class SieveMailAdapter implements MailAdapter, EnvelopeAccessors, ActionContext
{
    private static final Log LOG = LogFactory.getLog(SieveMailAdapter.class);
    
    private Log log = LOG;
    
    /**
     * The Mail being adapted.
     */
    private Mail fieldMail;
    /**
     * The MailetContext.
     */
    private MailetContext fieldMailetContext;
    /**
     * List of Actions to perform.
     */
    private List<Action> fieldActions;
    
    private final ActionDispatcher dispatcher;
    
    private final Poster poster;

    private String contentText;
    
    /**
     * Constructor for SieveMailAdapter.
     * 
     * @param aMail
     * @param aMailetContext
     */
    public SieveMailAdapter(final Mail aMail, final MailetContext aMailetContext, final ActionDispatcher dispatcher, final Poster poster)
    {
        this.poster = poster;
        this.dispatcher = dispatcher;
        setMail(aMail);
        setMailetContext(aMailetContext);
    }
    
  
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * Returns the message.
     * 
     * @return MimeMessage
     */
    protected MimeMessage getMessage() throws MessagingException
    {
        return getMail().getMessage();
    }
    /**
     * Returns the List of actions.
     * 
     * @return List
     */
    public List<Action> getActions()
    {
        List<Action> actions = null;
        if (null == (actions = getActionsBasic()))
        {
            updateActions();
            return getActions();
        }
        return actions;
    }
    /**
     * Returns a new List of actions.
     * 
     * @return List
     */
    protected List<Action> computeActions()
    {
        return new ArrayList<Action>();
    }
    /**
     * Returns the List of actions.
     * 
     * @return List
     */
    private List<Action> getActionsBasic()
    {
        return fieldActions;
    }
    /**
     * Adds an Action.
     * 
     * @param action The action to set
     */
    public void addAction(Action action)
    {
        getActions().add(action);
    }
    /**
     * @see org.apache.jsieve.mail.MailAdapter#executeActions()
     */
    public void executeActions() throws SieveException
    {
        final List<Action> actions = getActions();
        for (final Action action: actions) {
            getMailetContext().log("Executing action: " + action.toString());
            try
            {
                dispatcher.execute(action, getMail(), this);
            }
            catch (MessagingException e)
            {
                throw new SieveException(e);
            }
        }
    }
    /**
     * Sets the actions.
     * 
     * @param actions The actions to set
     */
    protected void setActions(List<Action> actions)
    {
        fieldActions = actions;
    }
    
    /**
     * Updates the actions.
     */
    protected void updateActions()
    {
        setActions(computeActions());
    }

    /**
     * @see org.apache.jsieve.mail.MailAdapter#getHeader(String)
     */
    public List<String> getHeader(String name) throws SieveMailException
    {
        try
        {
            String[] headers = getMessage().getHeader(name);
            return (headers == null ? new ArrayList<String>(0) : Arrays.asList(headers));
        }
        catch (MessagingException ex)
        {
            throw new SieveMailException(ex);
        }
    }
    
    /**
     * @see org.apache.jsieve.mail.MailAdapter#getHeaderNames()
     */
    public List<String> getHeaderNames() throws SieveMailException
    {
        Set<String> headerNames = new HashSet<String>();
        try
        {
            Enumeration allHeaders = getMessage().getAllHeaders();
            while (allHeaders.hasMoreElements())
            {
                headerNames.add(((Header) allHeaders.nextElement()).getName());
            }
            return new ArrayList<String>(headerNames);
        }
        catch (MessagingException ex)
        {
            throw new SieveMailException(ex);
        }
    }
    
    /**
     * @see org.apache.jsieve.mail.MailAdapter#getMatchingHeader(String)
     */
    public List<String> getMatchingHeader(String name) throws SieveMailException
    {
        return MailUtils.getMatchingHeader(this, name);
    }
    
    /**
     * @see org.apache.jsieve.mail.MailAdapter#getSize()
     */
    public int getSize() throws SieveMailException
    {
        try
        {
            return getMessage().getSize();
        }
        catch (MessagingException ex)
        {
            throw new SieveMailException(ex);
        }
    }
    
    /**
     * Method getEnvelopes.
     * 
     * @return Map
     */
    protected Map<String, String> getEnvelopes()
    {
        Map<String, String> envelopes = new HashMap<String, String>(2);
        if (null != getEnvelopeFrom())
            envelopes.put("From", getEnvelopeFrom());
        if (null != getEnvelopeTo())
            envelopes.put("To", getEnvelopeTo());
        return envelopes;
    }
    /**
     * @see org.apache.jsieve.mail.optional.EnvelopeAccessors#getEnvelope(String)
     */
    public List<String> getEnvelope(String name) throws SieveMailException
    {
        List<String> values = new ArrayList<String>(1);
        String value = getEnvelopes().get(name);
        if (null != value)
            values.add(value);
        return values;
    }
    
    /**
     * @see org.apache.jsieve.mail.optional.EnvelopeAccessors#getEnvelopeNames()
     */
    public List<String> getEnvelopeNames() throws SieveMailException
    {
        return new ArrayList<String>(getEnvelopes().keySet());
    }
    
    /**
     * @see org.apache.jsieve.mail.optional.EnvelopeAccessors#getMatchingEnvelope(String)
     */
    public List<String> getMatchingEnvelope(String name) throws SieveMailException
    {
        final List<String> matchedEnvelopeValues = new ArrayList<String>(32);
        for (String envelopeName: getEnvelopeNames()) {
            if (envelopeName.trim().equalsIgnoreCase(name))
                matchedEnvelopeValues.addAll(getEnvelope(envelopeName));
        }
        return matchedEnvelopeValues;
    }
    
    /**
     * Returns the from.
     * 
     * @return String
     */
    public String getEnvelopeFrom()
    {
        MailAddress sender = getMail().getSender(); 
        return (null == sender ? "" : sender.toString());
    }
    
    /**
     * Returns the sole recipient or null if there isn't one.
     * 
     * @return String
     */
    public String getEnvelopeTo()
    {
        String recipient = null;
        Iterator recipientIter = getMail().getRecipients().iterator();
        if (recipientIter.hasNext())
            recipient = recipientIter.next().toString();
        return recipient;
    }
    
    /**
     * Returns the mail.
     * 
     * @return Mail
     */
    public Mail getMail()
    {
        return fieldMail;
    }
    
    /**
     * Sets the mail.
     * 
     * @param mail The mail to set
     */
    protected void setMail(Mail mail)
    {
        fieldMail = mail;
        contentText = null;
    }
    
    /**
     * Returns the mailetContext.
     * 
     * @return MailetContext
     */
    public MailetContext getMailetContext()
    {
        return fieldMailetContext;
    }
    
    /**
     * Sets the mailetContext.
     * 
     * @param mailetContext The mailetContext to set
     */
    protected void setMailetContext(MailetContext mailetContext)
    {
        fieldMailetContext = mailetContext;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        String messageID = null;
        try
        {
            messageID = getMail().getMessage().getMessageID();
        }
        catch (MessagingException e)
        {
            messageID = "<" + e.getMessage() + ">";
        }
        return getClass().getName() + " Envelope From: "
                + (null == getEnvelopeFrom() ? "null" : getEnvelopeFrom())
                + " Envelope To: "
                + (null == getEnvelopeTo() ? "null" : getEnvelopeTo())
                + " Message ID: " + (null == messageID ? "null" : messageID);
    }
    
    public String getContentType() throws SieveMailException {
        try {
            return getMessage().getContentType();
        } catch (MessagingException e) {
            throw new SieveMailException(e);
        }
    }
    
    public Address[] parseAddresses(String arg) throws SieveMailException, InternetAddressException {
        try {
            final MailboxList list = new AddressList(AddressBuilder.DEFAULT.parseAddressList(arg), true).flatten();
            final int size = list.size();
            final Address[] results = new Address[size];
            for (int i=0;i<size;i++) {
                final Mailbox mailbox = list.get(i);
                results[i] = new AddressImpl(mailbox.getLocalPart(), mailbox.getDomain());
            }
            return null;
        } catch (ParseException e) {
            throw new InternetAddressException(e);
        }
    }

    public Log getLog() {
        return log;
    }
    
    public String getServerInfo() {
        return getMailetContext().getServerInfo();
    }
    public void post(String uri, MimeMessage mail) throws MessagingException {
        poster.post(uri, mail);
    }
    
    public void post(MailAddress sender, Collection recipients, MimeMessage mail) throws MessagingException {
        getMailetContext().sendMail(sender, recipients, mail);
    }


    public boolean isInBodyText(String phraseCaseInsensitive) throws SieveMailException {
        try {
            if (contentText == null) {
                contentText = getMessage().getContent().toString().toLowerCase();
            }
            return contentText.contains(phraseCaseInsensitive);
        } catch (MessagingException e) {
            throw new SieveMailException(e);
        } catch (IOException e) {
            throw new SieveMailException(e);
        }
    }

    public void setContext(SieveContext context) {}
}
