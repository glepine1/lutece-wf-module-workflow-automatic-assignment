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

import fr.paris.lutece.plugins.workflow.modules.taskassignment.business.WorkgroupConfig;
import fr.paris.lutece.plugins.workflow.modules.taskassignment.business.WorkgroupConfigHome;
import fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.service.AutomaticAssignmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for TaskAutomaticAssignmentConfigHome objects
 */
public final class TaskAutomaticAssignmentConfigHome
{
    // Static variable pointed at the DAO instance
    private static ITaskAutomaticAssignmentConfigDAO _dao = (ITaskAutomaticAssignmentConfigDAO) SpringContextService.getPluginBean( AutomaticAssignmentPlugin.PLUGIN_NAME,
            "taskAutomaticAssignmentConfigDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TaskAutomaticAssignmentConfigHome(  )
    {
    }

    /**
     * Creation of an instance of config
     * @param config The instance of task which contains the informations to store
     * @param autoAssignmentPlugin the auto assignment plugin
     * @param workflowPlugin the workflow plugin
     */
    public static void create( TaskAutomaticAssignmentConfig config, Plugin autoAssignmentPlugin, Plugin workflowPlugin )
    {
        _dao.insert( config, autoAssignmentPlugin );

        List<WorkgroupConfig> listWorkgroups = config.getWorkgroups(  );

        if ( listWorkgroups != null )
        {
            for ( WorkgroupConfig workgroupConfig : listWorkgroups )
            {
                WorkgroupConfigHome.create( workgroupConfig, workflowPlugin );
            }
        }
    }

    /**
     * Update of task which is specified in parameter
     * @param  config The instance of config which contains the informations to update
     * @param autoAssignmentPlugin the auto assignment plugin
     * @param workflowPlugin the workflow plugin
     */
    public static void update( TaskAutomaticAssignmentConfig config, Plugin autoAssignmentPlugin, Plugin workflowPlugin )
    {
        _dao.store( config, autoAssignmentPlugin );

        //update workgroups
        WorkgroupConfigHome.removeByTask( config.getIdTask(  ), workflowPlugin );

        List<WorkgroupConfig> listWorkgroups = config.getWorkgroups(  );

        if ( listWorkgroups != null )
        {
            for ( WorkgroupConfig workgroupConfig : listWorkgroups )
            {
                WorkgroupConfigHome.create( workgroupConfig, workflowPlugin );
            }
        }
    }

    /**
     *  remove config associated to the task which is specified in parameter
     *
     * @param nIdTask The task key
     * @param autoAssignmentPlugin the auto assignment plugin
     * @param workflowPlugin the workflow plugin
     */
    public static void remove( int nIdTask, Plugin autoAssignmentPlugin, Plugin workflowPlugin )
    {
        WorkgroupConfigHome.removeByTask( nIdTask, workflowPlugin );
        _dao.delete( nIdTask, autoAssignmentPlugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the Config Object
     * @param nIdTask the task id
     * @param autoAssignmentPlugin the auto assignment plugin
     * @param workflowPlugin the workflow plugin
     * @return the Config Object
     */
    public static TaskAutomaticAssignmentConfig findByPrimaryKey( int nIdTask, Plugin autoAssignmentPlugin,
        Plugin workflowPlugin )
    {
        TaskAutomaticAssignmentConfig config = _dao.load( nIdTask, autoAssignmentPlugin );

        if ( config != null )
        {
            config.setWorkgroups( WorkgroupConfigHome.getListByConfig( nIdTask, workflowPlugin ) );
        }

        return config;
    }
}
