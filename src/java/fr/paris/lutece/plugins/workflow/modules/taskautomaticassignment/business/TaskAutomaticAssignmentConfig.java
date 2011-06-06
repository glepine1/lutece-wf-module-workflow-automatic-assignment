/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.business;

import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.workflow.modules.taskassignment.business.WorkgroupConfig;

import java.util.List;


/**
 *
 *TaskCommentConfig
 *
 */
public class TaskAutomaticAssignmentConfig
{
    private int _nIdDirectory;
    private List<IEntry> _entryList;    
    private String _strMessage;
    private boolean _bNotify;
    private String _strSubject;
    private int _nIdTask;
    private String _strTitle;
    private String _strSenderName;
    private List<WorkgroupConfig> _workgroups;
    
    /**
     *
     * @return the task id
     */
    public int getIdTask(  )
    {
        return _nIdTask;
    }

    /**
     *
     * @param idTask the task id
     */
    public void setIdTask( int idTask )
    {
        _nIdTask = idTask;
    }

    /**
     *
     * @return the directory id
     */
    public int getIdDirectory(  )
    {
        return _nIdDirectory;
    }

    /**
     * 
     * @param nIdDirectory the id of the directory to set
     */
    public void setIdDirectory( int nIdDirectory )
    {
        _nIdDirectory = nIdDirectory;
    }
    
    /**
     *
     * @return the title of the field insert in tasks form
     */
    public String getTitle(  )
    {
    	return _strTitle;
    }

    /**
     * set  the title of the field insert in tasks form
     * @param title the title of the field insert in tasks form
     */
    public void setTitle( String title )
    {
    	_strTitle = title;
    }

    /**
     *
     * @return the entry list
     */
    public List<IEntry> getEntryList(  )
    {
        return _entryList;
    }

    /**
     * 
     * @param entryList the list of entry to set
     */
    public void setEntryList( List<IEntry> entryList )
    {
        _entryList = entryList;
    }


    /**
     * @return the Notification
     */
    public boolean isNotify(  )
    {
        return _bNotify;
    }

    /**
     * @param notification the Notification to set
     */
    public void setNotify( boolean notify )
    {
        _bNotify = notify;
    }

    /**
     * @return the strMessage
     */
    public String getMessage(  )
    {
        return _strMessage;
    }

    /**
     * @param message the message of the notification to set
     */
    public void setMessage( String message )
    {
        _strMessage = message;
    }

    /**
     * @return the strSubject
     */
    public String getSubject(  )
    {
        return _strSubject;
    }

    /**
     * @param subject the subject of notification to set
     */
    public void setSubject( String subject )
    {
        _strSubject = subject;
    }
    
    /**
     * @return the sender name
     */
    public String getSenderName(  )
    {
    	return _strSenderName;
    }

    /**
     * @param strSenderName the sender name to set
     */
    public void setSenderName( String strSenderName )
    {
    	_strSenderName = strSenderName;
    }
    
    /**
    *
    * @return a list wich contains the differents workgroups to displayed in task form
    */
    public List<WorkgroupConfig> getWorkgroups(  )
    {
        return _workgroups;
    }

    /**
     * set a list wich contains the differents workgroups to displayed in task form
     * @param worgroups the list of workgroups
     */
    public void setWorkgroups( List<WorkgroupConfig> worgroups )
    {
        _workgroups = worgroups;
    }
}
