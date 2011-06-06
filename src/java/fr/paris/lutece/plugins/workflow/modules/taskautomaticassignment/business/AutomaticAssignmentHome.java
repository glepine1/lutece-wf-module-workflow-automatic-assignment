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

import fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.service.AutomaticAssignmentPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for AutomaticAssignment objects
 */
public final class AutomaticAssignmentHome
{
    // Static variable pointed at the DAO instance
    private static IAutomaticAssignmentDAO _dao = (IAutomaticAssignmentDAO) SpringContextService.getPluginBean( AutomaticAssignmentPlugin.PLUGIN_NAME,
            "automaticAssignmentDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private AutomaticAssignmentHome(  )
    {
    }

    /**
     * Creation of an instance of assign
     *
     * @param assign The instance of AutomaticAssignment
     * @param plugin the plugin
     *
     */
    public static void create( AutomaticAssignment assign, Plugin plugin )
    {
        _dao.insert( assign, plugin );
    }

    /**
     *  remove assign which is specified in parameter
     *
     * @param assign The AutomaticAssignment
     * @param plugin the Plugin
     *
     */
    public static void remove( AutomaticAssignment assign, Plugin plugin )
    {
        _dao.delete( assign, plugin );
    }

    /**
     *  remove all assign linked to the task
     *
     * @param nIdTask The id of the task
     * @param plugin the Plugin
     *
     */
    public static void removeByTask( int nIdTask, Plugin plugin )
    {
        _dao.deleteByTask( nIdTask, plugin );
    }

    /**
     * Check if an assignment already exist
     * @param assign the assignment to check
     * @param plugin the plugin
     * @return true if exists
     */
    public static boolean checkExist( AutomaticAssignment assign, Plugin plugin )
    {
        return _dao.checkExist( assign, plugin );
    }

    /**
     * Return a list of assignment with the same task and entry
     * @param nIdTask the task id
     * @param nIdEntry the entry id
     * @param plugin the plugin
     * @return assignmentList the list of automatic assignment
     */
    public static List<AutomaticAssignment> findByTaskByEntry( int nIdTask, int nIdEntry, Plugin plugin )
    {
        return _dao.loadByTaskByEntry( nIdTask, nIdEntry, plugin );
    }

    /**
     * Return a list of assignment with the same task
     * @param nIdTask the task id
     * @param plugin the plugin
     * @return assignmentList the list of automatic assignment
     */
    public static List<AutomaticAssignment> findByTask( int nIdTask, Plugin plugin )
    {
        return _dao.loadByTask( nIdTask, plugin );
    }

    /**
     * Return a list of id from entries with the same task
     * @param nIdTask the task id
     * @param plugin the plugin
     * @return idEntriesList the list of entries id
     */
    public static List<Integer> findAllIdEntriesByTask( int nIdTask, Plugin plugin )
    {
        return _dao.getIdEntriesListByTask( nIdTask, plugin );
    }
}
