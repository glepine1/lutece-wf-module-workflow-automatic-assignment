/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.service;

import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.PhysicalFileHome;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.business.TaskAutomaticAssignmentConfig;
import fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.business.TaskAutomaticAssignmentConfigHome;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.mail.FileAttachment;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 *
 * AutomaticAssignmentService
 *
 */
public final class AutomaticAssignmentService
{
    private static final String BEAN_AUTOMATIC_ASSIGNMENT_SERVICE = "workflow-automatic-assignment.automaticAssignmentService";

    // CONSTANTS
    private static final String COMMA = ",";

    // PROPERTIES
    private static final String PROPERTY_ACCEPTED_DIRECTORY_ENTRY_TYPE_FILE = "workflow-automatic-assignment.acceptedDirectoryEntryTypesFile";

    // PRIVATE VARIABLES
    private List<Integer> _listAcceptedEntryTypesFile;

    /**
     * Private constructor
     */
    private AutomaticAssignmentService(  )
    {
        // Init list accepted entry types for file
        _listAcceptedEntryTypesFile = fillListEntryTypes( PROPERTY_ACCEPTED_DIRECTORY_ENTRY_TYPE_FILE );
    }

    /**
     * Get the service
     * @return the service
     */
    public static AutomaticAssignmentService getService(  )
    {
        return (AutomaticAssignmentService) SpringContextService.getPluginBean( AutomaticAssignmentPlugin.PLUGIN_NAME,
            BEAN_AUTOMATIC_ASSIGNMENT_SERVICE );
    }

    // CHECKS

    /**
    * Check if the given entry type id is accepted for file
    * @param nIdEntryType the id entry type
    * @return true if it is accepted, false otherwise
    */
    public boolean isEntryTypeFileAccepted( int nIdEntryType )
    {
        boolean bIsAccepted = false;

        if ( ( _listAcceptedEntryTypesFile != null ) && !_listAcceptedEntryTypesFile.isEmpty(  ) )
        {
            bIsAccepted = _listAcceptedEntryTypesFile.contains( nIdEntryType );
        }

        return bIsAccepted;
    }

    // GET

    /**
     * Get the list of entries from a given id task
     * @param nIdTask the id task
     * @return a list of IEntry
     */
    public List<IEntry> getListEntries( int nIdTask )
    {
        Plugin pluginAutomaticAssignment = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        Plugin pluginWorkflow = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );

        TaskAutomaticAssignmentConfig config = TaskAutomaticAssignmentConfigHome.findByPrimaryKey( nIdTask,
                pluginAutomaticAssignment, pluginWorkflow );

        List<IEntry> listEntries = new ArrayList<IEntry>(  );

        if ( config != null )
        {
            EntryFilter entryFilter = new EntryFilter(  );
            entryFilter.setIdDirectory( config.getIdDirectory(  ) );

            listEntries = EntryHome.getEntryList( entryFilter, pluginDirectory );
        }

        return listEntries;
    }

    /**
     * Get the list of entries that have the accepted type for file
     * @param nIdTask the id task
     * @param locale the Locale
     * @return a List of entries
     */
    public List<IEntry> getListEntriesFile( int nIdTask, Locale locale )
    {
        List<IEntry> listEntries = new ArrayList<IEntry>(  );

        for ( IEntry entry : getListEntries( nIdTask ) )
        {
            int nIdEntryType = entry.getEntryType(  ).getIdType(  );

            if ( isEntryTypeFileAccepted( nIdEntryType ) )
            {
                listEntries.add( entry );
            }
        }

        return listEntries;
    }

    /**
     * the files Attachments to insert in the mail
     * @param config the configuration
     * @param nIdRecord the record id
     * @param nIdDirectory the  directory id
     * @return the files Attachments to insert in the mail
     */
    public List<FileAttachment> getFilesAttachment( TaskAutomaticAssignmentConfig config, int nIdRecord,
        int nIdDirectory )
    {
        List<FileAttachment> listFileAttachment = null;

        if ( ( config.getListPositionsEntryFile(  ) != null ) && !config.getListPositionsEntryFile(  ).isEmpty(  ) )
        {
            listFileAttachment = new ArrayList<FileAttachment>(  );

            for ( Integer nPositionEntryFile : config.getListPositionsEntryFile(  ) )
            {
                List<File> listFiles = getFiles( nPositionEntryFile, nIdRecord, nIdDirectory );

                if ( ( listFiles != null ) && !listFiles.isEmpty(  ) )
                {
                    for ( File file : listFiles )
                    {
                        if ( ( file != null ) && ( file.getPhysicalFile(  ) != null ) )
                        {
                            FileAttachment fileAttachment = new FileAttachment( file.getTitle(  ),
                                    file.getPhysicalFile(  ).getValue(  ), file.getMimeType(  ) );
                            listFileAttachment.add( fileAttachment );
                        }
                    }
                }
            }
        }

        return listFileAttachment;
    }

    // PRIVATE METHODS

    /**
    * Fill the list of entry types
    * @param strPropertyEntryTypes the property containing the entry types
    * @return a list of integer
    */
    private static List<Integer> fillListEntryTypes( String strPropertyEntryTypes )
    {
        List<Integer> listEntryTypes = new ArrayList<Integer>(  );
        String strEntryTypes = AppPropertiesService.getProperty( strPropertyEntryTypes );

        if ( StringUtils.isNotBlank( strEntryTypes ) )
        {
            String[] listAcceptEntryTypesForIdDemand = strEntryTypes.split( COMMA );

            for ( String strAcceptEntryType : listAcceptEntryTypesForIdDemand )
            {
                if ( StringUtils.isNotBlank( strAcceptEntryType ) && StringUtils.isNumeric( strAcceptEntryType ) )
                {
                    int nAcceptedEntryType = Integer.parseInt( strAcceptEntryType );
                    listEntryTypes.add( nAcceptedEntryType );
                }
            }
        }

        return listEntryTypes;
    }

    /**
     * Get the directory files
     * @param nPosition the position of the entry
     * @param nIdRecord the id record
     * @param nIdDirectory the id directory
     * @return the directory file
     */
    private List<File> getFiles( int nPosition, int nIdRecord, int nIdDirectory )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        // RecordField
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setPosition( nPosition );
        entryFilter.setIdDirectory( nIdDirectory );

        List<IEntry> listEntries = EntryHome.getEntryList( entryFilter, pluginDirectory );

        if ( ( listEntries != null ) && !listEntries.isEmpty(  ) )
        {
            IEntry entry = listEntries.get( 0 );
            RecordFieldFilter recordFieldFilter = new RecordFieldFilter(  );
            recordFieldFilter.setIdDirectory( nIdDirectory );
            recordFieldFilter.setIdEntry( entry.getIdEntry(  ) );
            recordFieldFilter.setIdRecord( nIdRecord );

            List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( recordFieldFilter, pluginDirectory );

            if ( ( listRecordFields != null ) && !listRecordFields.isEmpty(  ) && ( listRecordFields.get( 0 ) != null ) )
            {
                List<File> listFiles = new ArrayList<File>(  );

                for ( RecordField recordField : listRecordFields )
                {
                    if ( entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeFile )
                    {
                        File file = recordField.getFile(  );

                        if ( ( file != null ) && ( file.getPhysicalFile(  ) != null ) )
                        {
                            file.setPhysicalFile( PhysicalFileHome.findByPrimaryKey( 
                                    file.getPhysicalFile(  ).getIdPhysicalFile(  ), pluginDirectory ) );
                            listFiles.add( file );
                        }
                    }
                    else if ( entry instanceof fr.paris.lutece.plugins.directory.business.EntryTypeDownloadUrl )
                    {
                        File file = DirectoryUtils.doDownloadFile( recordField.getValue(  ) );

                        if ( file != null )
                        {
                            listFiles.add( file );
                        }
                    }
                }

                return listFiles;
            }
        }

        return null;
    }
}
