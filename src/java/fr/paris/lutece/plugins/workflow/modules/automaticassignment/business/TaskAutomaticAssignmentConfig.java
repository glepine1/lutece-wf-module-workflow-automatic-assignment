/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.automaticassignment.business;

import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.workflow.modules.assignment.business.WorkgroupConfig;
import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


/**
 *
 * TaskAutomaticAssignmentConfig
 *
 */
public class TaskAutomaticAssignmentConfig extends TaskConfig
{
    @NotNull
    @Min( 1 )
    private int _nIdDirectory;
    private List<IEntry> _entryList;
    private String _strMessage;
    private boolean _bNotify;
    private String _strSubject;
    @NotNull
    private String _strTitle;
    private String _strSenderName;
    private List<WorkgroupConfig> _workgroups;
    private boolean _bViewRecord;
    private String _strLabelLinkViewRecord;
    private String _strRecipientsCc;
    private String _strRecipientsBcc;
    private List<Integer> _listPositionsEntryFile;
    private List<AutomaticAssignment> _listAutomaticAssignments;

    /**
     * Get the id directory
     * @return the directory id
     */
    public int getIdDirectory(  )
    {
        return _nIdDirectory;
    }

    /**
     * Set the id directory
     * @param nIdDirectory the id of the directory to set
     */
    public void setIdDirectory( int nIdDirectory )
    {
        _nIdDirectory = nIdDirectory;
    }

    /**
     * Get the title
     * @return the title of the field insert in tasks form
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * Set  the title of the field insert in tasks form
     * @param strTitle the title of the field insert in tasks form
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Get the list of entries
     * @return the entry list
     */
    public List<IEntry> getEntryList(  )
    {
        return _entryList;
    }

    /**
     * Set the list of entries
     * @param entryList the list of entry to set
     */
    public void setEntryList( List<IEntry> entryList )
    {
        _entryList = entryList;
    }

    /**
     * Check if the task must notify or not
     * @return true if it must notify, false otherwise
     */
    public boolean isNotify(  )
    {
        return _bNotify;
    }

    /**
     * Set true if it must notify, false otherwise
     * @param notify true if it must notify, false otherwise
     */
    public void setNotify( boolean notify )
    {
        _bNotify = notify;
    }

    /**
     * Get the message
     * @return the message
     */
    public String getMessage(  )
    {
        return _strMessage;
    }

    /**
     * Set the message
     * @param strMessage the message of the notification to set
     */
    public void setMessage( String strMessage )
    {
        _strMessage = strMessage;
    }

    /**
     * Get the subject
     * @return the subject
     */
    public String getSubject(  )
    {
        return _strSubject;
    }

    /**
     * Set the subject
     * @param strSubject the subject of notification to set
     */
    public void setSubject( String strSubject )
    {
        _strSubject = strSubject;
    }

    /**
     * Get the sender name
     * @return the sender name
     */
    public String getSenderName(  )
    {
        return _strSenderName;
    }

    /**
     * Set the sender name
     * @param strSenderName the sender name to set
     */
    public void setSenderName( String strSenderName )
    {
        _strSenderName = strSenderName;
    }

    /**
     * Get the list of workgroup config
     * @return a list wich contains the differents workgroups to displayed in task form
     */
    public List<WorkgroupConfig> getWorkgroups(  )
    {
        return _workgroups;
    }

    /**
     * Set a list wich contains the differents workgroups to displayed in task form
     * @param worgroups the list of workgroups
     */
    public void setWorkgroups( List<WorkgroupConfig> worgroups )
    {
        _workgroups = worgroups;
    }

    /**
     * Set the link view record
     * @param bViewRecord true if the email should include the link view record
     */
    public void setViewRecord( boolean bViewRecord )
    {
        _bViewRecord = bViewRecord;
    }

    /**
     * Check if the email should include the link view record
     * @return true if the email should include the link view record
     */
    public boolean isViewRecord(  )
    {
        return _bViewRecord;
    }

    /**
     * Set the label for the link view record
     * @param strLabelLinkViewRecord the label
     */
    public void setLabelLinkViewRecord( String strLabelLinkViewRecord )
    {
        _strLabelLinkViewRecord = strLabelLinkViewRecord;
    }

    /**
     * Get the label for the link view record
     * @return the label for the link view record
     */
    public String getLabelLinkViewRecord(  )
    {
        return _strLabelLinkViewRecord;
    }

    /**
     * Returns the Recipient
     * @return The Recipient
     */
    public String getRecipientsCc(  )
    {
        return _strRecipientsCc;
    }

    /**
     * Sets the Recipient
     * @param strRecipient The Recipient
     */
    public void setRecipientsCc( String strRecipient )
    {
        _strRecipientsCc = strRecipient;
    }

    /**
     * Returns the Recipient
     * @return The Recipient
     */
    public String getRecipientsBcc(  )
    {
        return _strRecipientsBcc;
    }

    /**
     * Sets the Recipient
     * @param strRecipient The Recipient
     */
    public void setRecipientsBcc( String strRecipient )
    {
        _strRecipientsBcc = strRecipient;
    }

    /**
     * Set the list of entry file which must be include in mail attachments
     * @param listPositionsEntryFile list of entry file which must be include in mail attachments
     */
    public void setListPositionsEntryFile( List<Integer> listPositionsEntryFile )
    {
        _listPositionsEntryFile = listPositionsEntryFile;
    }

    /**
     * Get the list of entry file which must be include in mail attachments
     * @return the list of entry file which must be include in mail attachments
     */
    public List<Integer> getListPositionsEntryFile(  )
    {
        return _listPositionsEntryFile;
    }

    /**
     * @return the listAutomaticAssignments
     */
    public List<AutomaticAssignment> getListAutomaticAssignments(  )
    {
        return _listAutomaticAssignments;
    }

    /**
     * @param listAutomaticAssignments the listAutomaticAssignments to set
     */
    public void setListAutomaticAssignments( List<AutomaticAssignment> listAutomaticAssignments )
    {
        _listAutomaticAssignments = listAutomaticAssignments;
    }
}
