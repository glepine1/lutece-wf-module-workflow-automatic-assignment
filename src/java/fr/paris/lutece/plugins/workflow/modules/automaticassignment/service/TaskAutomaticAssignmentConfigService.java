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

import fr.paris.lutece.plugins.workflow.modules.assignment.business.WorkgroupConfig;
import fr.paris.lutece.plugins.workflow.modules.assignment.service.IWorkgroupConfigService;
import fr.paris.lutece.plugins.workflow.modules.automaticassignment.business.ITaskAutomaticAssignmentConfigDAO;
import fr.paris.lutece.plugins.workflow.modules.automaticassignment.business.TaskAutomaticAssignmentConfig;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import javax.inject.Inject;


/**
 *
 * TaskAutomaticAssignmentConfigService
 *
 */
public class TaskAutomaticAssignmentConfigService implements ITaskAutomaticAssignmentConfigService
{
    public static final String BEAN_SERVICE = "workflow-automaticassignment.taskAutomaticAssignmentConfigService";
    @Inject
    private ITaskAutomaticAssignmentConfigDAO _taskAutomaticAssignmentDAO;
    @Inject
    private IWorkgroupConfigService _workgroupConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow-automaticassignment.transactionManager" )
    public void create( TaskAutomaticAssignmentConfig config, Plugin autoAssignmentPlugin, Plugin workflowPlugin )
    {
        _taskAutomaticAssignmentDAO.insert( config, autoAssignmentPlugin );

        List<WorkgroupConfig> listWorkgroups = config.getWorkgroups(  );

        if ( listWorkgroups != null )
        {
            for ( WorkgroupConfig workgroupConfig : listWorkgroups )
            {
                _workgroupConfigService.create( workgroupConfig, workflowPlugin );
            }
        }

        if ( config.getListPositionsEntryFile(  ) != null )
        {
            for ( Integer nPositionEntryFile : config.getListPositionsEntryFile(  ) )
            {
                _taskAutomaticAssignmentDAO.insertListPositionsEntryFile( config.getIdTask(  ), nPositionEntryFile,
                    autoAssignmentPlugin );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow-automaticassignment.transactionManager" )
    public void update( TaskAutomaticAssignmentConfig config, Plugin autoAssignmentPlugin, Plugin workflowPlugin )
    {
        _taskAutomaticAssignmentDAO.store( config, autoAssignmentPlugin );

        // Update workgroups
        _workgroupConfigService.removeByTask( config.getIdTask(  ), workflowPlugin );

        List<WorkgroupConfig> listWorkgroups = config.getWorkgroups(  );

        if ( listWorkgroups != null )
        {
            for ( WorkgroupConfig workgroupConfig : listWorkgroups )
            {
                _workgroupConfigService.create( workgroupConfig, workflowPlugin );
            }
        }

        _taskAutomaticAssignmentDAO.deleteListPositionsEntryFile( config.getIdTask(  ), autoAssignmentPlugin );

        if ( config.getListPositionsEntryFile(  ) != null )
        {
            for ( Integer nPositionEntryFile : config.getListPositionsEntryFile(  ) )
            {
                _taskAutomaticAssignmentDAO.insertListPositionsEntryFile( config.getIdTask(  ), nPositionEntryFile,
                    autoAssignmentPlugin );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow-automaticassignment.transactionManager" )
    public void remove( int nIdTask, Plugin autoAssignmentPlugin, Plugin workflowPlugin )
    {
        _workgroupConfigService.removeByTask( nIdTask, workflowPlugin );
        _taskAutomaticAssignmentDAO.deleteListPositionsEntryFile( nIdTask, autoAssignmentPlugin );
        _taskAutomaticAssignmentDAO.delete( nIdTask, autoAssignmentPlugin );
    }

    // Finders

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow-automaticassignment.transactionManager" )
    public TaskAutomaticAssignmentConfig findByPrimaryKey( int nIdTask, Plugin autoAssignmentPlugin,
        Plugin workflowPlugin )
    {
        TaskAutomaticAssignmentConfig config = _taskAutomaticAssignmentDAO.load( nIdTask, autoAssignmentPlugin );

        if ( config != null )
        {
            config.setWorkgroups( _workgroupConfigService.getListByConfig( nIdTask, workflowPlugin ) );
            config.setListPositionsEntryFile( _taskAutomaticAssignmentDAO.loadListPositionsEntryFile( nIdTask,
                    autoAssignmentPlugin ) );
        }

        return config;
    }
}
