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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;


/**
 *
 *TaskComment
 */
public class TaskAutomaticAssignment extends Task
{
    //templates
    private static final String TEMPLATE_TASK_AUTO_ASSIGNMENT_CONFIG = "admin/plugins/workflow/modules/taskautomaticassignment/task_config.html";
    private static final String TEMPLATE_TASK_NOTIFICATION_MAIL = "admin/plugins/workflow/modules/tasknotification/task_notification_mail.html";

    //	Markers
    private static final String MARK_CONFIG = "config";
    private static final String MARK_WORKGROUP_LIST = "workgroup_list";
    private static final String MARK_ITEM = "item";
    private static final String MARK_MESSAGE = "message";
    private static final String MARK_MAILING_LIST = "mailing_list";
    private static final String MARK_DIRECTORY_LIST = "directory_list";
    private static final String MARK_ENTRY_MARKER = "entry_";
    private static final String MARK_FULL_ENTRY_LIST = "full_entry_list";

    //Parameters
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
   
    //Properties
    private static final String FIELD_TITLE = "module.workflow.taskautomaticassignment.task_config.label_title";
    private static final String FIELD_MAILINGLIST_SUBJECT = "module.workflow.taskautomaticassignment.task_config.label_mailinglist_subject";
    private static final String FIELD_MAILINGLIST_MESSAGE = "module.workflow.taskautomaticassignment.task_config.label_mailinglist_message";
    private static final String FIELD_MAILINGLIST_SENDER_NAME = "module.workflow.taskautomaticassignment.task_config.label_mailinglist_sender_name";
    private static final String PROPERTY_IS_SELECTABLE_ENTRY_CHECKBOX = "workflow-automatic-assignment.selectable_entry.checkbox";
    private static final String PROPERTY_IS_SELECTABLE_ENTRY_RADIO = "workflow-automatic-assignment.selectable_entry.radio";
    private static final String PROPERTY_IS_SELECTABLE_ENTRY_SELECT = "workflow-automatic-assignment.selectable_entry.select";
    private static final String PROPERTY_ENTRY_TYPE_GEOLOCATION = "directory.entry_type.geolocation";

    //Messages
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.taskautomaticassignment.message.mandatory.field";
    private static final String MESSAGE_NO_MAILINGLIST_FOR_WORKGROUP = "module.workflow.taskassignment.task_assignment_config.message.no_mailinglist_for_workgroup";
    private static final String PROPERTY_MAIL_SENDER_NAME = "module.workflow.taskassignment.task_assignment_config.mailSenderName";
    private static final String MESSAGE_CONFIRM_DIRECTORY_UPDATE = "module.workflow.taskautomaticassignment.message.task_config.confirm_directory_update";

    //JSP
    private static final String JSP_DO_UPDATE_DIRECTORY = "jsp/admin/plugins/workflow/modules/taskautomaticassignment/DoUpdateDirectory.jsp";

    //Constants
    private static final int CONSTANT_ID_TYPE_RADIO = 1;
    private static final int CONSTANT_ID_TYPE_CHECKBOX = 2;
    private static final int CONSTANT_ID_TYPE_SELECT = 5;
    private static final String CONSTANT_COMMA = ", ";
    private static final String CONSTANT_FREEMARKER_BEGIN = "${";
    private static final String CONSTANT_FREEMARKER_END = "}";
    private static final String CONSTANT_FREEMARKER_REGEXP_BEGIN = "(\\$\\{";
    private static final String CONSTANT_FREEMARKER_REGEXP_END = "\\})";

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#init()
     */
    public void init(  )
    {
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doSaveConfig(javax.servlet.http.HttpServletRequest, java.util.Locale, fr.paris.lutece.portal.service.plugin.Plugin)
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

        if( tabWorkgroups != null )
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
        config.setSenderName( strSenderName  );

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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doValidateTask(int, java.lang.String, javax.servlet.http.HttpServletRequest, java.util.Locale, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        Plugin plugin )
    {        
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getDisplayConfigForm(javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getDisplayConfigForm( HttpServletRequest request, Plugin workflowPlugin, Locale locale )
    {
        Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );
        Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        ReferenceList directoryRefList = DirectoryHome.getDirectoryList( directoryPlugin );
        List<HashMap<String, Object>> listWorkgroups = new ArrayList<HashMap<String, Object>>(  );
        String strNothing = "";

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
        for( MailingList mailingList : MailingListHome.findAll(  ) )
        {
        	refMailList.addItem( mailingList.getId(  ), mailingList.getName(  ) );
        }
        refMailingList.addAll( refMailList );
        
        model.put( MARK_WORKGROUP_LIST, listWorkgroups );       
        model.put( MARK_DIRECTORY_LIST, directoryRefList );
        model.put( MARK_CONFIG, config );
        model.put( MARK_FULL_ENTRY_LIST, fullEntryList );
        model.put( MARK_MAILING_LIST, refMailingList );      

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_AUTO_ASSIGNMENT_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getDisplayTaskForm(int, java.lang.String, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Plugin plugin, Locale locale )
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getDisplayTaskInformation(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {       
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#processTask(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Plugin workflowPlugin, Locale locale )
    {
        Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );
        Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        ResourceHistory resourceHistory = ResourceHistoryHome.findByPrimaryKey( nIdResourceHistory, workflowPlugin );
        List<String> listWorkgroup = new ArrayList<String>(  );

        TaskAutomaticAssignmentConfig config = TaskAutomaticAssignmentConfigHome.findByPrimaryKey( this.getId(  ),
                autoAssignPlugin, workflowPlugin );

        List<AutomaticAssignment> listAssignment = AutomaticAssignmentHome.findByTask( this.getId(  ), autoAssignPlugin );

        List<Integer> idEntryList = AutomaticAssignmentHome.findAllIdEntriesByTask( this.getId(  ), autoAssignPlugin );

        List<RecordField> recordFields = RecordFieldHome.getRecordFieldSpecificList( idEntryList, resourceHistory.getIdResource(  ),
                directoryPlugin );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        
        for ( RecordField recordField : recordFields )
        {
        	if( recordField.getField(  ) != null )
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
        
        //get values for markers that can be used in the message
        RecordFieldFilter filter = new RecordFieldFilter(  );
        filter.setIdDirectory( config.getIdDirectory(  ) );
        filter.setIdRecord(resourceHistory.getIdResource(  ) );
        List<RecordField> recordFieldsForMarkers = RecordFieldHome.getRecordFieldList( filter, directoryPlugin);
        
        for( RecordField recordField : recordFieldsForMarkers )
        {
        	String strKey = MARK_ENTRY_MARKER + recordField.getEntry(  ).getIdEntry(  );
        	String strOldValue = ( (String) model.get( strKey ) );
        	String strNewValue;        	
        	IEntry recordfielEntry = recordField.getEntry(  );
    	
        	strNewValue = recordField.getValue(  );
        	
    		if( ( recordfielEntry != null ) 
    				&& ( recordfielEntry.getEntryType(  ) != null )
    				&& ( recordfielEntry.getEntryType(  ).getIdType(  ) == 
    					AppPropertiesService.getPropertyInt( PROPERTY_ENTRY_TYPE_GEOLOCATION, 16 ) ) )
    		{
    			if( ( recordField.getField(  ) != null && recordField.getField(  ).getTitle(  ) != null ) 
    					&& ( ! recordField.getField(  ).getTitle(  ).equals( EntryTypeGeolocation.CONSTANT_ADDRESS  ) )
    					|| ( recordField.getField(  ) == null || recordField.getField(  ).getTitle(  ) == null ))
    			{
    				strNewValue = null ;
    			}    			
    		}        	
    		else if( recordField.getField(  ) != null && recordField.getField(  ).getTitle(  ) != null)
    		{
        		strNewValue = recordField.getField(  ).getTitle(  );
    		}
    		
        	//if it's a file
        	if( recordField.getFile(  ) != null )
        	{
        		strNewValue = recordField.getFile(  ).getTitle(  );
        	}
        	
        	if( ( strOldValue != null ) && ( strNewValue != null ) && ( ! strOldValue.equals( WorkflowUtils.EMPTY_STRING ) ) )
        	{
        		//add markers for message
                model.put( strKey, 
                		strNewValue + CONSTANT_COMMA + strOldValue );
        	}
        	else if( strNewValue != null )
        	{
        		 model.put( strKey, strNewValue ) ;
        	}        	
        	else
        	{
        		 model.put( strKey, WorkflowUtils.EMPTY_STRING ) ;
        	}
        	
        }
        
        for ( String workGroup : listWorkgroup )
        {
            //add history 
            AssignmentHistory history = new AssignmentHistory(  );
            history.setIdResourceHistory( nIdResourceHistory );
            history.setIdTask( this.getId(  ) );
            history.setWorkgroup( workGroup );
            AssignmentHistoryHome.create( history, workflowPlugin );

            if ( config.isNotify(  ) )
            {
                WorkgroupConfig workgroupConfig = WorkgroupConfigHome.findByPrimaryKey( this.getId(  ), workGroup,
                        workflowPlugin );

                if ( ( workgroupConfig != null ) &&
                        ( workgroupConfig.getIdMailingList(  ) != WorkflowUtils.CONSTANT_ID_NULL ) )
                {
                    Collection<Recipient> listRecipients = new ArrayList<Recipient>(  );
                    listRecipients = AdminMailingListService.getRecipients( workgroupConfig.getIdMailingList(  ) );

                    String strSenderEmail = MailService.getNoReplyEmail(  );
                    
                    model.put( MARK_MESSAGE, config.getMessage(  ) );

                    HtmlTemplate t = AppTemplateService.getTemplate( 
                    				TEMPLATE_TASK_NOTIFICATION_MAIL, locale, model ) ;
                    
                    String strSubject = config.getSubject(  );
                    
                    for( Entry<String, Object> entryModel : model.entrySet(  ) )
                    {
                    	String strCurrentFreemarker = CONSTANT_FREEMARKER_BEGIN + entryModel.getKey(  ) + CONSTANT_FREEMARKER_END;
                    	
                    	//substitute freemarkers in the message
                    	t.substitute( strCurrentFreemarker, entryModel.getValue(  ).toString(  ) );
                    	
                    	//substitute freemarkers in the subject
                    	strSubject = strSubject.replaceAll( CONSTANT_FREEMARKER_REGEXP_BEGIN
                    			+ entryModel.getKey(  ) +  CONSTANT_FREEMARKER_REGEXP_END, entryModel.getValue(  ).toString(  ) );
                    	
                    }
                    
                    String strSenderName = config.getSenderName(  );
                    if( strSenderName == null )
                    {
                    	strSenderName = I18nService.getLocalizedString( PROPERTY_MAIL_SENDER_NAME, locale );
                    }             

                    // Send Mail
                    for ( Recipient recipient : listRecipients )
                    {
                        // Build the mail message
                        MailService.sendMailHtml( recipient.getEmail(  ), strSenderName, strSenderEmail,
                        		strSubject, t.getHtml(  ) );
                    }
                }
            }
        }

        //update resource workflow 
       
        ResourceWorkflow resourceWorkflow = ResourceWorkflowHome.findByPrimaryKey( resourceHistory.getIdResource(  ),
                resourceHistory.getResourceType(  ), resourceHistory.getWorkflow(  ).getId(  ), workflowPlugin );
        resourceWorkflow.setWorkgroups( listWorkgroup );
        ResourceWorkflowHome.update( resourceWorkflow, workflowPlugin );
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doRemoveConfig(fr.paris.lutece.portal.service.plugin.Plugin)
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

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#isConfigRequire()
     */
    public boolean isConfigRequire(  )
    {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#isFormTaskRequire()
     */
    public boolean isFormTaskRequire(  )
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#doRemoveTaskInformation(int, fr.paris.lutece.portal.service.plugin.Plugin)
     */
    public void doRemoveTaskInformation( int nIdHistory, Plugin plugin )
    {
        AssignmentHistoryHome.removeByHistory( nIdHistory, this.getId(  ), plugin );
    }

    public void doRemoveTaskInformation( Plugin plugin )
    {
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTaskInformationXml(int, javax.servlet.http.HttpServletRequest, fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getTaskInformationXml( int idHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {        
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTitle(fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public String getTitle( Plugin workflowPlugin, Locale locale )
    {
    	Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );

    	TaskAutomaticAssignmentConfig config = TaskAutomaticAssignmentConfigHome.findByPrimaryKey( this.getId(  ), autoAssignPlugin, workflowPlugin );

    	if ( config != null )
    	{
    		return config.getTitle(  );
    	}

    	return WorkflowUtils.EMPTY_STRING;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#getTaskFormEntries(fr.paris.lutece.portal.service.plugin.Plugin, java.util.Locale)
     */
    public ReferenceList getTaskFormEntries( Plugin plugin, Locale locale )
    {        
        return null;
    }

    /*
     * (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.business.task.ITask#isTaskForActionAutomatic()
     */
    public boolean isTaskForActionAutomatic(  )
    {
        return true;
    }

}
