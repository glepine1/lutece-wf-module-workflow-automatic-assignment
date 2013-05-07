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
package fr.paris.lutece.plugins.workflow.modules.automaticassignment.service;

import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.workflow.modules.automaticassignment.business.AutomaticAssignment;
import fr.paris.lutece.plugins.workflow.modules.automaticassignment.business.TaskAutomaticAssignmentConfig;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.mail.FileAttachment;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * IAutomaticAssignmentService
 *
 */
public interface IAutomaticAssignmentService
{
    /**
    * Creation of an instance of assign
    * @param assign The instance of AutomaticAssignment
    * @param plugin the plugin
    */
    @Transactional( AutomaticAssignmentPlugin.BEAN_TRANSACTION_MANAGER )
    void create( AutomaticAssignment assign, Plugin plugin );

    /**
     * Remove assign which is specified in parameter
     * @param assign The AutomaticAssignment
     * @param plugin the plugin
     */
    @Transactional( AutomaticAssignmentPlugin.BEAN_TRANSACTION_MANAGER )
    void remove( AutomaticAssignment assign, Plugin plugin );

    /**
     * Remove all assign linked to the task
     * @param nIdTask The id of the task
     * @param plugin the plugin
     */
    @Transactional( AutomaticAssignmentPlugin.BEAN_TRANSACTION_MANAGER )
    void removeByTask( int nIdTask, Plugin plugin );

    // CHECKS

    /**
     * Check if an assignment already exist
     * @param assign the assignment to check
     * @param plugin the plugin
     * @return true if exists
     */
    boolean checkExist( AutomaticAssignment assign, Plugin plugin );

    /**
     * Check if the given entry type id is accepted for file
     * @param nIdEntryType the id entry type
     * @return true if it is accepted, false otherwise
     */
    boolean isEntryTypeFileAccepted( int nIdEntryType );

    // GET

    /**
     * Return a list of assignment with the same task and entry
     * @param nIdTask the task id
     * @param nIdEntry the entry id
     * @param plugin the plugin
     * @return assignmentList the list of automatic assignment
     */
    List<AutomaticAssignment> findByTaskByEntry( int nIdTask, int nIdEntry, Plugin plugin );

    /**
     * Return a list of assignment with the same task
     * @param nIdTask the task id
     * @param plugin the plugin
     * @return assignmentList the list of automatic assignment
     */
    List<AutomaticAssignment> findByTask( int nIdTask, Plugin plugin );

    /**
     * Return a list of id from entries with the same task
     * @param nIdTask the task id
     * @param plugin the plugin
     * @return idEntriesList the list of entries id
     */
    List<Integer> findAllIdEntriesByTask( int nIdTask, Plugin plugin );

    /**
     * Get the list of entries from a given id task
     * @param nIdTask the id task
     * @return a list of IEntry
     */
    List<IEntry> getListEntries( int nIdTask );

    /**
     * Get the list of entries that have the accepted type for file
     * @param nIdTask the id task
     * @param locale the Locale
     * @return a List of entries
     */
    List<IEntry> getListEntriesFile( int nIdTask, Locale locale );

    /**
     * the files Attachments to insert in the mail
     * @param config the configuration
     * @param nIdRecord the record id
     * @param nIdDirectory the  directory id
     * @return the files Attachments to insert in the mail
     */
    List<FileAttachment> getFilesAttachment( TaskAutomaticAssignmentConfig config, int nIdRecord, int nIdDirectory );

    /**
     * Notify the mailing list associated to the list of workgroups
     * @param config the config
     * @param listWorkgroup the list of workgroups
     * @param resourceHistory the resource history
     * @param request the HTTP request
     * @param locale the {@link Locale}
     * @param task the task
     */
    void notify( TaskAutomaticAssignmentConfig config, List<String> listWorkgroup, ResourceHistory resourceHistory,
        HttpServletRequest request, Locale locale, ITask task );
}
