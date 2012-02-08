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

import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.EntryTypeGeolocation;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.business.ResourceHistory;
import fr.paris.lutece.plugins.workflow.business.ResourceHistoryHome;
import fr.paris.lutece.plugins.workflow.business.ResourceWorkflow;
import fr.paris.lutece.plugins.workflow.business.ResourceWorkflowHome;
import fr.paris.lutece.plugins.workflow.business.task.Task;
import fr.paris.lutece.plugins.workflow.modules.taskassignment.business.AssignmentHistory;
import fr.paris.lutece.plugins.workflow.modules.taskassignment.business.AssignmentHistoryHome;
import fr.paris.lutece.plugins.workflow.modules.taskassignment.business.WorkgroupConfig;
import fr.paris.lutece.plugins.workflow.modules.taskassignment.business.WorkgroupConfigHome;
import fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.service.AutomaticAssignmentPlugin;
import fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.service.AutomaticAssignmentService;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.business.mailinglist.MailingList;
import fr.paris.lutece.portal.business.mailinglist.MailingListHome;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.business.workgroup.AdminWorkgroup;
import fr.paris.lutece.portal.business.workgroup.AdminWorkgroupHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.mail.FileAttachment;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * TaskAutomaticAssignment
 *
 */
public class TaskAutomaticAssignment extends Task
{
    // Templates
    private static final String TEMPLATE_TASK_AUTO_ASSIGNMENT_CONFIG = "admin/plugins/workflow/modules/taskautomaticassignment/task_config.html";
    private static final String TEMPLATE_TASK_NOTIFICATION_MAIL = "admin/plugins/workflow/modules/tasknotification/task_notification_mail.html";

    //Markers
    private static final String MARK_CONFIG = "config";
    private static final String MARK_WORKGROUP_LIST = "workgroup_list";
    private static final String MARK_ITEM = "item";
    private static final String MARK_MESSAGE = "message";
    private static final String MARK_MAILING_LIST = "mailing_list";
    private static final String MARK_DIRECTORY_LIST = "directory_list";
    private static final String MARK_ENTRY_MARKER = "entry_";
    private static final String MARK_FULL_ENTRY_LIST = "full_entry_list";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_LINK_VIEW_RECORD = "link_view_record";
    private static final String MARK_LIST_ENTRIES_FILE = "list_entries_file";

    // Parameters
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_WORKGROUPS = "workgroups";
    private static final String PARAMETER_ID_MAILING_LIST = "id_mailing_list";
    private static final String PARAMETER_MESSAGE = "message";
    private static final String PARAMETER_IS_NOTIFICATION = "is_notify";
    private static final String PARAMETER_SUBJECT = "subject";
    private static final String PARAMETER_SENDER_NAME = "sender_name";
    private static final String PARAMETER_DIRECTORY = "directory";
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String PARAMETER_ID_TASK = "id_task";
    private static final String PARAMETER_VIEW_RECORD = "view_record";
    private static final String PARAMETER_LABEL_LINK_VIEW_RECORD = "label_link_view_record";
    private static final String PARAMETER_RECIPIENTS_CC = "recipients_cc";
    private static final String PARAMETER_RECIPIENTS_BCC = "recipients_bcc";
    private static final String PARAMETER_LIST_POSITION_ENTRY_FILE_CHECKED = "list_position_entry_file_checked";

    // Properties
    private static final String FIELD_TITLE = "module.workflow.taskautomaticassignment.task_config.label_title";
    private static final String FIELD_MAILINGLIST_SUBJECT = "module.workflow.taskautomaticassignment.task_config.label_mailinglist_subject";
    private static final String FIELD_MAILINGLIST_MESSAGE = "module.workflow.taskautomaticassignment.task_config.label_mailinglist_message";
    private static final String FIELD_MAILINGLIST_SENDER_NAME = "module.workflow.taskautomaticassignment.task_config.label_mailinglist_sender_name";
    private static final String FIELD_LABEL_LINK_VIEW_RECORD = "module.workflow.taskautomaticassignment.task_config.label_label_link_view_record";
    private static final String PROPERTY_IS_SELECTABLE_ENTRY_CHECKBOX = "workflow-automatic-assignment.selectable_entry.checkbox";
    private static final String PROPERTY_IS_SELECTABLE_ENTRY_RADIO = "workflow-automatic-assignment.selectable_entry.radio";
    private static final String PROPERTY_IS_SELECTABLE_ENTRY_SELECT = "workflow-automatic-assignment.selectable_entry.select";
    private static final String PROPERTY_LUTECE_ADMIN_PROD_URL = "lutece.admin.prod.url";
    private static final String PROPERTY_LUTECE_BASE_URL = "lutece.base.url";
    private static final String PROPERTY_LUTECE_PROD_URL = "lutece.prod.url";

    // Messages
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.taskautomaticassignment.message.mandatory.field";
    private static final String MESSAGE_NO_MAILINGLIST_FOR_WORKGROUP = "module.workflow.taskassignment.task_assignment_config.message.no_mailinglist_for_workgroup";
    private static final String PROPERTY_MAIL_SENDER_NAME = "module.workflow.taskassignment.task_assignment_config.mailSenderName";
    private static final String MESSAGE_CONFIRM_DIRECTORY_UPDATE = "module.workflow.taskautomaticassignment.message.task_config.confirm_directory_update";

    // JSP
    private static final String JSP_DO_UPDATE_DIRECTORY = "jsp/admin/plugins/workflow/modules/taskautomaticassignment/DoUpdateDirectory.jsp";
    private static final String JSP_DO_VISUALISATION_RECORD = "jsp/admin/plugins/directory/DoVisualisationRecord.jsp";

    // TAGS
    private static final String TAG_A = "a";

    // ATTRIBUTES
    private static final String ATTRIBUTE_HREF = "href";

    // Constants
    private static final int CONSTANT_ID_TYPE_RADIO = 1;
    private static final int CONSTANT_ID_TYPE_CHECKBOX = 2;
    private static final int CONSTANT_ID_TYPE_SELECT = 5;
    private static final String CONSTANT_COMMA = ", ";

    //    private static final String CONSTANT_FREEMARKER_BEGIN = "${";
    //    private static final String CONSTANT_FREEMARKER_END = "}";
    //    private static final String CONSTANT_FREEMARKER_REGEXP_BEGIN = "(\\$\\{";
    //    private static final String CONSTANT_FREEMARKER_REGEXP_END = "\\})";
    private static final String CONSTANT_SLASH = "/";

    /**
     * {@inheritDoc}
     */
    public void init(  )
    {
    }

    /**
     * {@inheritDoc}
     */
    public String doSaveConfig( HttpServletRequest request, Locale locale, Plugin workflowPlugin )
    {
        String strError = WorkflowUtils.EMPTY_STRING;

        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strIsNotification = request.getParameter( PARAMETER_IS_NOTIFICATION );
        String strSenderName = request.getParameter( PARAMETER_SENDER_NAME );
        String strMessage = request.getParameter( PARAMETER_MESSAGE );
        String strSubject = request.getParameter( PARAMETER_SUBJECT );
        String strIdDirectory = request.getParameter( PARAMETER_DIRECTORY );
        String[] tabWorkgroups = request.getParameterValues( PARAMETER_WORKGROUPS );
        String strViewRecord = request.getParameter( PARAMETER_VIEW_RECORD );
        String strLabelLinkViewRecord = request.getParameter( PARAMETER_LABEL_LINK_VIEW_RECORD );
        String strRecipientsCc = request.getParameter( PARAMETER_RECIPIENTS_CC );
        String strRecipientsBcc = request.getParameter( PARAMETER_RECIPIENTS_BCC );
        String[] tabSelectedPositionsEntryFile = request.getParameterValues( PARAMETER_LIST_POSITION_ENTRY_FILE_CHECKED );
        int nIdDirectory = -1;

        Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( WorkflowUtils.EMPTY_STRING ) )
        {
            strError = FIELD_TITLE;
        }

        if ( ( strIsNotification != null ) && ( ( strSubject == null ) || strSubject.equals( "" ) ) )
        {
            strError = FIELD_MAILINGLIST_SUBJECT;
        }

        if ( ( strIsNotification != null ) && ( ( strMessage == null ) || strMessage.equals( "" ) ) )
        {
            strError = FIELD_MAILINGLIST_MESSAGE;
        }

        if ( ( strIsNotification != null ) && ( ( strSenderName == null ) || strSenderName.equals( "" ) ) )
        {
            strError = FIELD_MAILINGLIST_SENDER_NAME;
        }

        if ( StringUtils.isNotBlank( strViewRecord ) && StringUtils.isBlank( strLabelLinkViewRecord ) )
        {
            strError = FIELD_LABEL_LINK_VIEW_RECORD;
        }

        if ( !strError.equals( WorkflowUtils.EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        if ( strIdDirectory != null )
        {
            nIdDirectory = Integer.parseInt( strIdDirectory );
        }

        TaskAutomaticAssignmentConfig config = TaskAutomaticAssignmentConfigHome.findByPrimaryKey( this.getId(  ),
                autoAssignPlugin, workflowPlugin );
        Boolean bCreate = false;

        if ( config == null )
        {
            config = new TaskAutomaticAssignmentConfig(  );
            config.setIdTask( this.getId(  ) );
            bCreate = true;
        }

        //add workgroups
        List<WorkgroupConfig> listWorkgroupConfig = new ArrayList<WorkgroupConfig>(  );
        WorkgroupConfig workgroupConfig;

        if ( tabWorkgroups != null )
        {
            for ( int i = 0; i < tabWorkgroups.length; i++ )
            {
                workgroupConfig = new WorkgroupConfig(  );
                workgroupConfig.setIdTask( this.getId(  ) );
                workgroupConfig.setWorkgroupKey( tabWorkgroups[i] );

                if ( strIsNotification != null )
                {
                    if ( WorkflowUtils.convertStringToInt( request.getParameter( PARAMETER_ID_MAILING_LIST + "_" +
                                    tabWorkgroups[i] ) ) != -1 )
                    {
                        workgroupConfig.setIdMailingList( WorkflowUtils.convertStringToInt( request.getParameter( PARAMETER_ID_MAILING_LIST +
                                    "_" + tabWorkgroups[i] ) ) );
                    }
                    else
                    {
                        return AdminMessageService.getMessageUrl( request, MESSAGE_NO_MAILINGLIST_FOR_WORKGROUP,
                            AdminMessage.TYPE_STOP );
                    }
                }

                listWorkgroupConfig.add( workgroupConfig );
            }
        }

        config.setWorkgroups( listWorkgroupConfig );
        config.setTitle( strTitle );
        config.setNotify( strIsNotification != null );

        config.setMessage( strMessage );
        config.setSubject( strSubject );
        config.setSenderName( strSenderName );
        config.setViewRecord( strViewRecord != null );
        config.setLabelLinkViewRecord( strLabelLinkViewRecord );
        config.setRecipientsCc( StringUtils.isNotEmpty( strRecipientsCc ) ? strRecipientsCc : StringUtils.EMPTY );
        config.setRecipientsBcc( StringUtils.isNotEmpty( strRecipientsBcc ) ? strRecipientsBcc : StringUtils.EMPTY );

        if ( config.getIdDirectory(  ) != nIdDirectory )
        {
            config.setIdDirectory( nIdDirectory );

            if ( !bCreate )
            {
                UrlItem url = new UrlItem( JSP_DO_UPDATE_DIRECTORY );
                url.addParameter( PARAMETER_ID_DIRECTORY, nIdDirectory );
                url.addParameter( PARAMETER_ID_TASK, this.getId(  ) );

                return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_DIRECTORY_UPDATE, url.getUrl(  ),
                    AdminMessage.TYPE_CONFIRMATION );
            }
        }

        if ( ( tabSelectedPositionsEntryFile != null ) && ( tabSelectedPositionsEntryFile.length > 0 ) )
        {
            List<Integer> listSelectedPositionEntryFile = new ArrayList<Integer>(  );

            for ( int i = 0; i < tabSelectedPositionsEntryFile.length; i++ )
            {
                listSelectedPositionEntryFile.add( WorkflowUtils.convertStringToInt( tabSelectedPositionsEntryFile[i] ) );
            }

            config.setListPositionsEntryFile( listSelectedPositionEntryFile );
        }

        if ( bCreate )
        {
            TaskAutomaticAssignmentConfigHome.create( config, autoAssignPlugin, workflowPlugin );
        }
        else
        {
            TaskAutomaticAssignmentConfigHome.update( config, autoAssignPlugin, workflowPlugin );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        Plugin plugin )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayConfigForm( HttpServletRequest request, Plugin workflowPlugin, Locale locale )
    {
        AutomaticAssignmentService autoAssignService = AutomaticAssignmentService.getService(  );
        Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );
        Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        ReferenceList directoryRefList = DirectoryHome.getDirectoryList( directoryPlugin );
        List<HashMap<String, Object>> listWorkgroups = new ArrayList<HashMap<String, Object>>(  );
        String strNothing = StringUtils.EMPTY;

        TaskAutomaticAssignmentConfig config = TaskAutomaticAssignmentConfigHome.findByPrimaryKey( this.getId(  ),
                autoAssignPlugin, workflowPlugin );

        if ( config == null )
        {
            config = new TaskAutomaticAssignmentConfig(  );
            config.setIdTask( this.getId(  ) );
        }

        for ( AdminWorkgroup workgroup : AdminWorkgroupHome.findAll(  ) )
        {
            HashMap<String, Object> workgroupsItem = new HashMap<String, Object>(  );
            workgroupsItem.put( MARK_ITEM, workgroup );

            if ( ( config != null ) && ( config.getWorkgroups(  ) != null ) )
            {
                for ( WorkgroupConfig workgroupSelected : config.getWorkgroups(  ) )
                {
                    if ( workgroup.getKey(  ).equals( workgroupSelected.getWorkgroupKey(  ) ) )
                    {
                        workgroupsItem.put( MARK_CONFIG, workgroupSelected );

                        break;
                    }
                }
            }

            listWorkgroups.add( workgroupsItem );
        }

        EntryFilter filter = new EntryFilter(  );
        filter.setIdDirectory( config.getIdDirectory(  ) );
        filter.setIsGroup( EntryFilter.FILTER_FALSE );

        List<Integer> typeEntryList = new ArrayList<Integer>(  );

        if ( Boolean.valueOf( AppPropertiesService.getProperty( PROPERTY_IS_SELECTABLE_ENTRY_RADIO, "true" ) ) )
        {
            typeEntryList.add( CONSTANT_ID_TYPE_RADIO );
        }

        if ( Boolean.valueOf( AppPropertiesService.getProperty( PROPERTY_IS_SELECTABLE_ENTRY_CHECKBOX, "true" ) ) )
        {
            typeEntryList.add( CONSTANT_ID_TYPE_CHECKBOX );
        }

        if ( Boolean.valueOf( AppPropertiesService.getProperty( PROPERTY_IS_SELECTABLE_ENTRY_SELECT, "true" ) ) )
        {
            typeEntryList.add( CONSTANT_ID_TYPE_SELECT );
        }

        //customizable entry list
        List<IEntry> entryList = EntryHome.getEntryListByTypeList( typeEntryList, filter, directoryPlugin );
        config.setEntryList( entryList );

        //full entry list
        List<IEntry> fullEntryList = EntryHome.getEntryList( filter, directoryPlugin );

        ReferenceList refMailingList = new ReferenceList(  );
        refMailingList.addItem( WorkflowUtils.CONSTANT_ID_NULL, strNothing );

        ReferenceList refMailList = new ReferenceList(  );

        for ( MailingList mailingList : MailingListHome.findAll(  ) )
        {
            refMailList.addItem( mailingList.getId(  ), mailingList.getName(  ) );
        }

        refMailingList.addAll( refMailList );

        model.put( MARK_WORKGROUP_LIST, listWorkgroups );
        model.put( MARK_DIRECTORY_LIST, directoryRefList );
        model.put( MARK_CONFIG, config );
        model.put( MARK_FULL_ENTRY_LIST, fullEntryList );
        model.put( MARK_MAILING_LIST, refMailingList );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, locale );
        model.put( MARK_LIST_ENTRIES_FILE, autoAssignService.getListEntriesFile( getId(  ), locale ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_AUTO_ASSIGNMENT_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Plugin plugin, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Plugin workflowPlugin, Locale locale )
    {
        Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );
        Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        ResourceHistory resourceHistory = ResourceHistoryHome.findByPrimaryKey( nIdResourceHistory, workflowPlugin );
        List<String> listWorkgroup = new ArrayList<String>(  );

        List<AutomaticAssignment> listAssignment = AutomaticAssignmentHome.findByTask( this.getId(  ), autoAssignPlugin );

        List<Integer> idEntryList = AutomaticAssignmentHome.findAllIdEntriesByTask( this.getId(  ), autoAssignPlugin );

        List<RecordField> recordFields = RecordFieldHome.getRecordFieldSpecificList( idEntryList,
                resourceHistory.getIdResource(  ), directoryPlugin );

        for ( RecordField recordField : recordFields )
        {
            if ( recordField.getField(  ) != null )
            {
                int nIdField = recordField.getField(  ).getIdField(  );

                for ( AutomaticAssignment assignment : listAssignment )
                {
                    if ( assignment.getIdField(  ) == nIdField )
                    {
                        listWorkgroup.add( assignment.getWorkgroupKey(  ) );
                    }
                }
            }
        }

        TaskAutomaticAssignmentConfig config = TaskAutomaticAssignmentConfigHome.findByPrimaryKey( this.getId(  ),
                autoAssignPlugin, workflowPlugin );

        if ( ( config != null ) && config.isNotify(  ) )
        {
            notify( config, listWorkgroup, resourceHistory, request, locale );
        }

        //update resource workflow 
        ResourceWorkflow resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( resourceHistory.getIdResource(  ),
                resourceHistory.getResourceType(  ), resourceHistory.getWorkflow(  ).getId(  ), workflowPlugin );
        resourceWorkflow.setWorkgroups( listWorkgroup );
        ResourceWorkflowHome.update( resourceWorkflow, workflowPlugin );
    }

    /**
     * {@inheritDoc}
     */
    public void doRemoveConfig( Plugin workflowPlugin )
    {
        Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );
        //remove config
        TaskAutomaticAssignmentConfigHome.remove( this.getId(  ), autoAssignPlugin, workflowPlugin );
        AutomaticAssignmentHome.removeByTask( this.getId(  ), autoAssignPlugin );
        //remove task information
        AssignmentHistoryHome.removeByTask( this.getId(  ), workflowPlugin );
        WorkgroupConfigHome.removeByTask( this.getId(  ), workflowPlugin );
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConfigRequire(  )
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFormTaskRequire(  )
    {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void doRemoveTaskInformation( int nIdHistory, Plugin plugin )
    {
        AssignmentHistoryHome.removeByHistory( nIdHistory, this.getId(  ), plugin );
    }

    /**
     * {@inheritDoc}
     */
    public void doRemoveTaskInformation( Plugin plugin )
    {
    }

    /**
     * {@inheritDoc}
     */
    public String getTaskInformationXml( int idHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getTitle( Plugin workflowPlugin, Locale locale )
    {
        Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );

        TaskAutomaticAssignmentConfig config = TaskAutomaticAssignmentConfigHome.findByPrimaryKey( this.getId(  ),
                autoAssignPlugin, workflowPlugin );

        if ( config != null )
        {
            return config.getTitle(  );
        }

        return WorkflowUtils.EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     */
    public ReferenceList getTaskFormEntries( Plugin plugin, Locale locale )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTaskForActionAutomatic(  )
    {
        return true;
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
     * Notify the mailing list associated to the list of workgroups
     * @param config the config
     * @param listWorkgroup the list of workgroups
     * @param resourceHistory the resource history
     * @param request the HTTP request
     * @param locale the {@link Locale}
     */
    private void notify( TaskAutomaticAssignmentConfig config, List<String> listWorkgroup,
        ResourceHistory resourceHistory, HttpServletRequest request, Locale locale )
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
            history.setIdTask( this.getId(  ) );
            history.setWorkgroup( workGroup );
            AssignmentHistoryHome.create( history, workflowPlugin );

            WorkgroupConfig workgroupConfig = WorkgroupConfigHome.findByPrimaryKey( this.getId(  ), workGroup,
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
        AutomaticAssignmentService autoAssignService = AutomaticAssignmentService.getService(  );

        return autoAssignService.getFilesAttachment( config, resourceHistory.getIdResource(  ),
            config.getIdDirectory(  ) );
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
