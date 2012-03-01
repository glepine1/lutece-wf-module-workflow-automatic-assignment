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
package fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.web;

import fr.paris.lutece.plugins.directory.business.Entry;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.FieldHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.business.AutomaticAssignment;
import fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.business.AutomaticAssignmentHome;
import fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.business.TaskAutomaticAssignmentConfig;
import fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.business.TaskAutomaticAssignmentConfigHome;
import fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.service.AutomaticAssignmentPlugin;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.portal.business.workgroup.AdminWorkgroup;
import fr.paris.lutece.portal.business.workgroup.AdminWorkgroupHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * AutomaticAssignmentJspBean
 *
 */
public class AutomaticAssignmentJspBean extends PluginAdminPageJspBean
{
    private static final String TEMPLATE_MODIFY_ENTRY_ASSIGNMENT = "admin/plugins/workflow/modules/taskautomaticassignment/modify_entry_assignment.html";
    private static final String JSP_MODIFY_TASK = "jsp/admin/plugins/workflow/ModifyTask.jsp";
    private static final String JSP_MODIFY_ENTRY_ASSIGNMENT = "jsp/admin/plugins/workflow/modules/taskautomaticassignment/ModifyEntryAssignment.jsp";
    private static final String PROPERTY_MODIFY_TASK_PAGE_TITLE = "workflow.modify_workflow.page_title";
    private static final String PARAMETER_ID_TASK = "id_task";
    private static final String PARAMETER_ID_ENTRY = "id_entry";
    private static final String PARAMETER_ENTRY = "entry";
    private static final String PARAMETER_URL = "url";
    private static final String PARAMETER_WORKGROUP_LIST = "workgroup_list";
    private static final String PARAMETER_WORKGROUP = "workgroup";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_ASSIGNMENT_LIST = "assignment_list";
    private static final String PARAMETER_ID_DIRECTORY = "id_directory";
    private static final String MESSAGE_ALREADY_EXIST = "module.workflow.taskautomaticassignment.message.modify_entry_assignment.already_exist";
    private static final String MESSAGE_ERROR_MISSING_FIELD = "module.workflow.taskautomaticassignment.message.missing_field";

    /**
     * Get the modify entry assignment page which allow the user to assign a workgroup to a field
     * @param request the request
     * @return the page
     */
    public String getModifyEntryAssignments( HttpServletRequest request )
    {
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        List<AutomaticAssignment> assignmentList;
        Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );
        Plugin directoryPlugin = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        int nIdTask = -1;
        int nIdEntry = -1;

        if ( strIdTask != null )
        {
            nIdTask = Integer.parseInt( strIdTask );
        }

        if ( strIdEntry != null )
        {
            nIdEntry = Integer.parseInt( strIdEntry );
        }

        assignmentList = AutomaticAssignmentHome.findByTaskByEntry( nIdTask, nIdEntry, autoAssignPlugin );
        setAssignmentValues( assignmentList, directoryPlugin );

        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, directoryPlugin );

        model.put( PARAMETER_ENTRY, entry );

        UrlItem url = new UrlItem( JSP_MODIFY_TASK );
        url.addParameter( PARAMETER_ID_TASK, nIdTask );
        setPageTitleProperty( PROPERTY_MODIFY_TASK_PAGE_TITLE );
        model.put( PARAMETER_URL, url.getUrl(  ) );
        model.put( PARAMETER_ID_TASK, nIdTask );
        model.put( PARAMETER_WORKGROUP_LIST, AdminWorkgroupHome.findAll(  ) );
        model.put( PARAMETER_ASSIGNMENT_LIST, assignmentList );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_ENTRY_ASSIGNMENT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Fill a list of assignment with workgroupdescription
     * and field value for displaying purpose
     * @param assignmentList a list of AutomaticAssignment
     * @param directoryPlugin the directory plugin
     */
    private void setAssignmentValues( List<AutomaticAssignment> assignmentList, Plugin directoryPlugin )
    {
        for ( AutomaticAssignment assignment : assignmentList )
        {
            AdminWorkgroup workgroup = AdminWorkgroupHome.findByPrimaryKey( assignment.getWorkgroupKey(  ) );

            if ( workgroup != null )
            {
                assignment.setWorkgroupDescription( workgroup.getDescription(  ) );
            }

            Field field = FieldHome.findByPrimaryKey( assignment.getIdField(  ), directoryPlugin );
            assignment.setFieldValue( field.getTitle(  ) );
        }
    }

    /**
     * Assign the selected workgroup to the field
     * @param request the request
     * @return the modify assignment page url
     */
    public String doAddAssignment( HttpServletRequest request )
    {
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strValue = request.getParameter( PARAMETER_VALUE );
        Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );

        int nIdTask = -1;
        int nIdEntry = -1;
        int nIdField = -1;

        if ( strIdTask != null )
        {
            nIdTask = Integer.parseInt( strIdTask );
        }

        if ( strIdEntry != null )
        {
            nIdEntry = Integer.parseInt( strIdEntry );
        }

        if ( strValue != null )
        {
            nIdField = Integer.parseInt( strValue );
        }

        if ( ( strValue == null ) || ( strWorkgroup == null ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_MISSING_FIELD, AdminMessage.TYPE_STOP );
        }

        IEntry entry = new Entry(  );
        entry.setIdEntry( nIdEntry );

        AutomaticAssignment assign = new AutomaticAssignment(  );
        assign.setEntry( entry );
        assign.setIdTask( nIdTask );
        assign.setIdField( nIdField );
        assign.setWorkgroupKey( strWorkgroup );

        if ( !AutomaticAssignmentHome.checkExist( assign, autoAssignPlugin ) )
        {
            AutomaticAssignmentHome.create( assign, autoAssignPlugin );
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ALREADY_EXIST, AdminMessage.TYPE_STOP );
        }

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_ENTRY_ASSIGNMENT );

        url.addParameter( PARAMETER_ID_TASK, nIdTask );
        url.addParameter( PARAMETER_ID_ENTRY, nIdEntry );

        return url.getUrl(  );
    }

    /**
     * Delete an assignment
     * @param request the request
     * @return the modify assignment page url
     */
    public String doDeleteAssignment( HttpServletRequest request )
    {
        Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );
        AutomaticAssignment assignment = getAutomaticAssignment( request );
        AutomaticAssignmentHome.remove( assignment, autoAssignPlugin );

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_ENTRY_ASSIGNMENT );

        url.addParameter( PARAMETER_ID_TASK, assignment.getIdTask(  ) );
        url.addParameter( PARAMETER_ID_ENTRY, assignment.getEntry(  ).getIdEntry(  ) );

        return url.getUrl(  );
    }

    /**
     * Use request parameters to create a AutomaticAssignment object
     * @param request the request
     * @return  the AutomaticAssignment created
     */
    private AutomaticAssignment getAutomaticAssignment( HttpServletRequest request )
    {
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        String strIdEntry = request.getParameter( PARAMETER_ID_ENTRY );
        String strWorkgroup = request.getParameter( PARAMETER_WORKGROUP );
        String strValue = request.getParameter( PARAMETER_VALUE );

        int nIdTask = -1;
        int nIdEntry = -1;
        int nIdField = -1;

        if ( strIdTask != null )
        {
            nIdTask = Integer.parseInt( strIdTask );
        }

        if ( strIdEntry != null )
        {
            nIdEntry = Integer.parseInt( strIdEntry );
        }

        if ( strValue != null )
        {
            nIdField = Integer.parseInt( strValue );
        }

        IEntry entry = new Entry(  );
        entry.setIdEntry( nIdEntry );

        AutomaticAssignment assign = new AutomaticAssignment(  );
        assign.setEntry( entry );
        assign.setIdTask( nIdTask );
        assign.setIdField( nIdField );
        assign.setWorkgroupKey( strWorkgroup );

        return assign;
    }

    /**
     * Update the directory selected and reload the page
     * @param request the request
     * @return the modify autoassignment config page
     */
    public String doUpdateDirectory( HttpServletRequest request )
    {
        String strIdDirectory = request.getParameter( PARAMETER_ID_DIRECTORY );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        int nIdDirectory = -1;
        int nIdTask = -1;
        Plugin autoAssignPlugin = PluginService.getPlugin( AutomaticAssignmentPlugin.PLUGIN_NAME );
        Plugin workflowPlugin = PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME );

        if ( strIdDirectory != null )
        {
            nIdDirectory = Integer.parseInt( strIdDirectory );
        }

        if ( strIdTask != null )
        {
            nIdTask = Integer.parseInt( strIdTask );
        }

        TaskAutomaticAssignmentConfig config = TaskAutomaticAssignmentConfigHome.findByPrimaryKey( nIdTask,
                autoAssignPlugin, workflowPlugin );
        config.setIdDirectory( nIdDirectory );

        if ( nIdDirectory != -1 )
        {
            TaskAutomaticAssignmentConfigHome.update( config, autoAssignPlugin, workflowPlugin );
            AutomaticAssignmentHome.removeByTask( nIdTask, autoAssignPlugin );
        }

        UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MODIFY_TASK );
        url.addParameter( PARAMETER_ID_TASK, nIdTask );

        return url.getUrl(  );
    }
}
