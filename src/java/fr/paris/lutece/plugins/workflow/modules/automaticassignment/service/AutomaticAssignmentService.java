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
package fr.paris.lutece.plugins.workflow.modules.automaticassignment.service;

import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation;
import fr.paris.lutece.plugins.directory.business.File;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.PhysicalFileHome;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.assignment.business.AssignmentHistory;
import fr.paris.lutece.plugins.workflow.modules.assignment.business.WorkgroupConfig;
import fr.paris.lutece.plugins.workflow.modules.assignment.service.IAssignmentHistoryService;
import fr.paris.lutece.plugins.workflow.modules.assignment.service.IWorkgroupConfigService;
import fr.paris.lutece.plugins.workflow.modules.automaticassignment.business.AutomaticAssignment;
import fr.paris.lutece.plugins.workflow.modules.automaticassignment.business.IAutomaticAssignmentDAO;
import fr.paris.lutece.plugins.workflow.modules.automaticassignment.business.TaskAutomaticAssignmentConfig;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.mail.FileAttachment;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.lang.StringUtils;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * AutomaticAssignmentService
 *
 */
public final class AutomaticAssignmentService implements IAutomaticAssignmentService
{
    public static final String BEAN_SERVICE = "workflow-automaticassignment.automaticAssignmentService";

    // TEMPLATE
    private static final String TEMPLATE_TASK_NOTIFICATION_MAIL = "admin/plugins/workflow/modules/notification/task_notification_mail.html";

    // MARKS
    private static final String MARK_MESSAGE = "message";
    private static final String MARK_ENTRY_MARKER = "entry_";
    private static final String MARK_LINK_VIEW_RECORD = "link_view_record";

    // PROPERTIES
    private static final String PROPERTY_LUTECE_ADMIN_PROD_URL = "lutece.admin.prod.url";
    private static final String PROPERTY_LUTECE_BASE_URL = "lutece.base.url";
    private static final String PROPERTY_LUTECE_PROD_URL = "lutece.prod.url";
    private static final String PROPERTY_ACCEPTED_DIRECTORY_ENTRY_TYPE_FILE = "workflow-automatic-assignment.acceptedDirectoryEntryTypesFile";

    // MESSAGES
    private static final String PROPERTY_MAIL_SENDER_NAME = "module.workflow.assignment.task_assignment_config.mailSenderName";

    // JSP
    private static final String JSP_DO_VISUALISATION_RECORD = "jsp/admin/plugins/directory/DoVisualisationRecord.jsp";

    // TAGS
    private static final String TAG_A = "a";

    // ATTRIBUTES
    private static final String ATTRIBUTE_HREF = "href";

    // CONSTANTS
    private static final String COMMA = ",";

    // CONSTANTS
    private static final String CONSTANT_COMMA = ", ";
    private static final String CONSTANT_SLASH = "/";

    // SERVICES
    @Inject
    private IAutomaticAssignmentDAO _automaticAssignmentDAO;
    @Inject
    @Named( TaskAutomaticAssignmentConfigService.BEAN_SERVICE )
    private ITaskConfigService _taskAutomaticAssignmentConfigService;
    @Inject
    private IAssignmentHistoryService _assignmentHistoryService;
    @Inject
    private IWorkgroupConfigService _workgroupConfigService;

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
     * {@inheritDoc}
     */
    @Override
    @Transactional( AutomaticAssignmentPlugin.BEAN_TRANSACTION_MANAGER )
    public void create( AutomaticAssignment assign, Plugin plugin )
    {
        _automaticAssignmentDAO.insert( assign, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( AutomaticAssignmentPlugin.BEAN_TRANSACTION_MANAGER )
    public void remove( AutomaticAssignment assign, Plugin plugin )
    {
        _automaticAssignmentDAO.delete( assign, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( AutomaticAssignmentPlugin.BEAN_TRANSACTION_MANAGER )
    public void removeByTask( int nIdTask, Plugin plugin )
    {
        _automaticAssignmentDAO.deleteByTask( nIdTask, plugin );
    }

    // CHECKS

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkExist( AutomaticAssignment assign, Plugin plugin )
    {
        return _automaticAssignmentDAO.checkExist( assign, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public List<AutomaticAssignment> findByTaskByEntry( int nIdTask, int nIdEntry, Plugin plugin )
    {
        return _automaticAssignmentDAO.loadByTaskByEntry( nIdTask, nIdEntry, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AutomaticAssignment> findByTask( int nIdTask, Plugin plugin )
    {
        return _automaticAssignmentDAO.loadByTask( nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> findAllIdEntriesByTask( int nIdTask, Plugin plugin )
    {
        return _automaticAssignmentDAO.getIdEntriesListByTask( nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IEntry> getListEntries( int nIdTask )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        TaskAutomaticAssignmentConfig config = _taskAutomaticAssignmentConfigService.findByPrimaryKey( nIdTask );

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
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void notify( TaskAutomaticAssignmentConfig config, List<String> listWorkgroup,
        ResourceHistory resourceHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        Plugin workflowPlugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );

        String strSenderEmail = MailService.getNoReplyEmail(  );
        String strSenderName = config.getSenderName(  );

        if ( StringUtils.isBlank( strSenderName ) )
        {
            strSenderName = I18nService.getLocalizedString( PROPERTY_MAIL_SENDER_NAME, locale );
        }

        Map<String, Object> model = buildModel( config, resourceHistory, request, locale );
        String strEmailContent = buildMailHtml( model, locale );
        String strSubject = buildSubjectHtml( config, model, locale );

        List<FileAttachment> listFileAttachments = getListFileAttachments( config, resourceHistory );

        // Notify the mailings list associated to each workgroup
        for ( String workGroup : listWorkgroup )
        {
            //add history 
            AssignmentHistory history = new AssignmentHistory(  );
            history.setIdResourceHistory( resourceHistory.getId(  ) );
            history.setIdTask( task.getId(  ) );
            history.setWorkgroup( workGroup );
            _assignmentHistoryService.create( history, workflowPlugin );

            WorkgroupConfig workgroupConfig = _workgroupConfigService.findByPrimaryKey( task.getId(  ), workGroup,
                    workflowPlugin );

            if ( ( workgroupConfig != null ) &&
                    ( workgroupConfig.getIdMailingList(  ) != WorkflowUtils.CONSTANT_ID_NULL ) )
            {
                Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( workgroupConfig.getIdMailingList(  ) );

                // Send Mail
                for ( Recipient recipient : listRecipients )
                {
                    if ( ( listFileAttachments != null ) && !listFileAttachments.isEmpty(  ) )
                    {
                        MailService.sendMailMultipartHtml( recipient.getEmail(  ), null, null, strSenderName,
                            strSenderEmail, strSubject, strEmailContent, null, listFileAttachments );
                    }
                    else
                    {
                        // Build the mail message
                        MailService.sendMailHtml( recipient.getEmail(  ), strSenderName, strSenderEmail, strSubject,
                            strEmailContent );
                    }
                }
            }
        }

        // Notify recipients
        boolean bHasRecipients = ( StringUtils.isNotBlank( config.getRecipientsBcc(  ) ) ||
            StringUtils.isNotBlank( config.getRecipientsCc(  ) ) );

        if ( bHasRecipients )
        {
            if ( ( listFileAttachments != null ) && !listFileAttachments.isEmpty(  ) )
            {
                MailService.sendMailMultipartHtml( null, config.getRecipientsCc(  ), config.getRecipientsBcc(  ),
                    strSenderName, strSenderEmail, strSubject, strEmailContent, null, listFileAttachments );
            }
            else
            {
                MailService.sendMailHtml( null, config.getRecipientsCc(  ), config.getRecipientsBcc(  ),
                    config.getSenderName(  ), strSenderEmail, strSubject, strEmailContent );
            }
        }
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

    /**
     * Get the base url
     * @param request the HTTP request
     * @return the base url
     */
    private String getBaseUrl( HttpServletRequest request )
    {
        String strBaseUrl = StringUtils.EMPTY;

        if ( request != null )
        {
            strBaseUrl = AppPathService.getBaseUrl( request );
        }
        else
        {
            strBaseUrl = AppPropertiesService.getProperty( PROPERTY_LUTECE_ADMIN_PROD_URL );

            if ( StringUtils.isBlank( strBaseUrl ) )
            {
                strBaseUrl = AppPropertiesService.getProperty( PROPERTY_LUTECE_BASE_URL );

                if ( StringUtils.isBlank( strBaseUrl ) )
                {
                    strBaseUrl = AppPropertiesService.getProperty( PROPERTY_LUTECE_PROD_URL );
                }
            }
        }

        return strBaseUrl;
    }

    /**
     * Build the mail Html
     * @param model the model
     * @param locale the {@link Locale}
     * @return the mail HTML
     */
    private String buildMailHtml( Map<String, Object> model, Locale locale )
    {
        HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                    TEMPLATE_TASK_NOTIFICATION_MAIL, locale, model ).getHtml(  ), locale, model );

        return t.getHtml(  );
    }

    /**
     * Build the subject Html
     * @param config the config
     * @param model the model
     * @param locale the {@link Locale}
     * @return the subject
     */
    private String buildSubjectHtml( TaskAutomaticAssignmentConfig config, Map<String, Object> model, Locale locale )
    {
        return AppTemplateService.getTemplateFromStringFtl( config.getSubject(  ), locale, model ).getHtml(  );
    }

    /**
     * Get the list of file attachments
     * @param config the config
     * @param resourceHistory the resource history
     * @return a list of file attachments
     */
    private List<FileAttachment> getListFileAttachments( TaskAutomaticAssignmentConfig config,
        ResourceHistory resourceHistory )
    {
        return this.getFilesAttachment( config, resourceHistory.getIdResource(  ), config.getIdDirectory(  ) );
    }

    /**
     * Build the model for the mail content and for the subject
     * @param config the config
     * @param resourceHistory the resource history
     * @param request the HTTP request
     * @param locale the {@link Locale}
     * @return the model
     */
    private Map<String, Object> buildModel( TaskAutomaticAssignmentConfig config, ResourceHistory resourceHistory,
        HttpServletRequest request, Locale locale )
    {
        Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        Map<String, Object> model = new HashMap<String, Object>(  );

        // Get values for markers that can be used in the message
        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdDirectory( config.getIdDirectory(  ) );
        filter.setIdRecord( resourceHistory.getIdResource(  ) );

        List<RecordField> recordFieldsForMarkers = RecordFieldHome.getRecordFieldList( filter, directoryPlugin );

        for ( RecordField recordField : recordFieldsForMarkers )
        {
            String strNewValue = recordField.getEntry(  )
                                            .convertRecordFieldValueToString( recordField, locale, false, false );

            if ( recordField.getEntry(  ) instanceof fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation &&
                    !recordField.getField(  ).getTitle(  ).equals( EntryTypeGeolocation.CONSTANT_ADDRESS ) )
            {
                continue;
            }
            else if ( ( recordField.getField(  ) != null ) && ( recordField.getField(  ).getTitle(  ) != null ) &&
                    !( recordField.getEntry(  ) instanceof fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation ) )
            {
                strNewValue = recordField.getField(  ).getTitle(  );
            }
            else if ( recordField.getEntry(  ) instanceof fr.paris.lutece.plugins.directory.business.EntryTypeFile &&
                    ( recordField.getFile(  ) != null ) && ( recordField.getFile(  ).getTitle(  ) != null ) )
            {
                strNewValue = recordField.getFile(  ).getTitle(  );
            }

            recordField.setEntry( EntryHome.findByPrimaryKey( recordField.getEntry(  ).getIdEntry(  ), directoryPlugin ) );

            String strKey = MARK_ENTRY_MARKER + recordField.getEntry(  ).getIdEntry(  );
            String strOldValue = ( (String) model.get( strKey ) );

            if ( StringUtils.isNotBlank( strOldValue ) && StringUtils.isNotBlank( strNewValue ) )
            {
                // Add markers for message
                model.put( strKey, strNewValue + CONSTANT_COMMA + strOldValue );
            }
            else if ( strNewValue != null )
            {
                model.put( strKey, strNewValue );
            }
            else
            {
                model.put( strKey, WorkflowUtils.EMPTY_STRING );
            }
        }

        // Link View record
        String strLinkViewRecordHtml = DirectoryUtils.EMPTY_STRING;

        if ( config.isViewRecord(  ) )
        {
            StringBuilder sbBaseUrl = new StringBuilder( getBaseUrl( request ) );

            if ( ( sbBaseUrl.length(  ) > 0 ) && !sbBaseUrl.toString(  ).endsWith( CONSTANT_SLASH ) )
            {
                sbBaseUrl.append( CONSTANT_SLASH );
            }

            sbBaseUrl.append( JSP_DO_VISUALISATION_RECORD );

            UrlItem url = new UrlItem( sbBaseUrl.toString(  ) );
            url.addParameter( DirectoryUtils.PARAMETER_ID_DIRECTORY, config.getIdDirectory(  ) );
            url.addParameter( DirectoryUtils.PARAMETER_ID_DIRECTORY_RECORD, resourceHistory.getIdResource(  ) );

            StringBuffer sbLinkHtml = new StringBuffer(  );
            Map<String, String> mapParams = new HashMap<String, String>(  );
            mapParams.put( ATTRIBUTE_HREF, url.getUrl(  ) );
            XmlUtil.beginElement( sbLinkHtml, TAG_A, mapParams );
            sbLinkHtml.append( config.getLabelLinkViewRecord(  ) );
            XmlUtil.endElement( sbLinkHtml, TAG_A );

            Map<String, Object> modelTmp = new HashMap<String, Object>(  );
            modelTmp.put( MARK_LINK_VIEW_RECORD, url.getUrl(  ) );
            strLinkViewRecordHtml = AppTemplateService.getTemplateFromStringFtl( sbLinkHtml.toString(  ), locale,
                    modelTmp ).getHtml(  );
        }

        model.put( MARK_LINK_VIEW_RECORD, strLinkViewRecordHtml );
        model.put( MARK_MESSAGE, config.getMessage(  ) );

        return model;
    }
}
