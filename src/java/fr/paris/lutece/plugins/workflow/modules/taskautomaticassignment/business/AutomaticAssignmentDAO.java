/*
 * Copyright (c) 2002-2009, Mairie de Paris
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

import fr.paris.lutece.plugins.directory.business.Entry;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 *class   TaskAssignmentConfigDAO
 *
 */
public class AutomaticAssignmentDAO implements IAutomaticAssignmentDAO
{
    private static final String SQL_QUERY_FIND_BY_TASK_BY_ENTRY = "SELECT value, workgroup_key " +
        " FROM workflow_auto_assignment WHERE id_task = ? AND id_entry = ?";
    private static final String SQL_QUERY_FIND_BY_TASK = "SELECT value, workgroup_key, id_entry " +
        " FROM workflow_auto_assignment WHERE id_task = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_auto_assignment  " +
        "(id_task,id_entry, value, workgroup_key)VALUES(?,?,?,?)";
    private static final String SQL_QUERY_CHECK_EXIST = "SELECT  COUNT(*) AS result_nb FROM workflow_auto_assignment  " +
        "WHERE id_task = ? AND id_entry = ? AND value = ? AND workgroup_key = ?";
    private static final String SQL_QUERY_FIND_ID_ENTRY_BY_TASK = "SELECT DISTINCT id_entry FROM workflow_auto_assignment " +
        " WHERE id_task = ? ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_auto_assignment  " +
        "WHERE id_task = ? AND id_entry = ? AND value = ? AND workgroup_key = ?";
    private static final String SQL_QUERY_DELETE_BY_TASK = "DELETE FROM workflow_auto_assignment  " +
        "WHERE id_task = ?";
    private static final String CONSTANT_VALUE = "value";
    private static final String CONSTANT_WORKGROUP = "workgroup_key";
    private static final String CONSTANT_ID_ENTRY = "id_entry";

    /**
     * Check if an assignment already exist
     * @param assign the assignment to check
     * @param plugin the plugin
     * @return true if exists
     */
    public boolean checkExist( AutomaticAssignment assign, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_EXIST, plugin );

        daoUtil.setInt( 1, assign.getIdTask(  ) );
        daoUtil.setInt( 2, assign.getEntry(  ).getIdEntry(  ) );
        daoUtil.setInt( 3, assign.getIdField(  ) );
        daoUtil.setString( 4, assign.getWorkgroupKey(  ) );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            if ( daoUtil.getInt( 1 ) > 0 )
            {
                daoUtil.free(  );

                return true;
            }
        }

        daoUtil.free(  );

        return false;
    }

    /**
     * Delete an automatic assignment
     * @param assign the assignment to delete
     * @param plugin the plugin
     *
     */
    public void delete( AutomaticAssignment assign, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        daoUtil.setInt( 1, assign.getIdTask(  ) );
        daoUtil.setInt( 2, assign.getEntry(  ).getIdEntry(  ) );
        daoUtil.setInt( 3, assign.getIdField(  ) );
        daoUtil.setString( 4, assign.getWorkgroupKey(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete all automatic assignment linked to a task
     * @param nIdTask the task id
     * @param plugin the plugin
     *
     */
    public void deleteByTask( int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_TASK, plugin );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Create a new automatic assignment
     * @param assign the assignment to create
     * @param plugin the plugin
     *
     */
    public void insert( AutomaticAssignment assign, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, assign.getIdTask(  ) );
        daoUtil.setInt( ++nPos, assign.getEntry(  ).getIdEntry(  ) );
        daoUtil.setInt( ++nPos, assign.getIdField(  ) );
        daoUtil.setString( ++nPos, assign.getWorkgroupKey(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Return a list of assignment with the same task and entry
     * @param nIdTask the task id
     * @param nIdEntry the entry id
     * @param plugin the plugin
     * @return assignmentList the list of automatic assignment
     */
    public List<AutomaticAssignment> loadByTaskByEntry( int nIdTask, int nIdEntry, Plugin plugin )
    {
        List<AutomaticAssignment> assignmentList = new ArrayList<AutomaticAssignment>(  );
        AutomaticAssignment assignment = null;
        Entry entry = new Entry(  );
        entry.setIdEntry( nIdEntry );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_TASK_BY_ENTRY, plugin );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.setInt( 2, nIdEntry );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            assignment = new AutomaticAssignment(  );
            assignment.setIdTask( nIdTask );
            assignment.setEntry( entry );
            assignment.setIdField( daoUtil.getInt( CONSTANT_VALUE ) );
            assignment.setWorkgroupKey( daoUtil.getString( CONSTANT_WORKGROUP ) );
            assignmentList.add( assignment );
        }

        daoUtil.free(  );

        return assignmentList;
    }

    /**
     * Return a list of assignment with the same task
     * @param nIdTask the task id
     * @param plugin the plugin
     * @return assignmentList the list of automatic assignment
     */
    public List<AutomaticAssignment> loadByTask( int nIdTask, Plugin plugin )
    {
        List<AutomaticAssignment> assignmentList = new ArrayList<AutomaticAssignment>(  );
        AutomaticAssignment assignment = null;
        Entry entry = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_TASK, plugin );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            assignment = new AutomaticAssignment(  );
            assignment.setIdTask( nIdTask );
            entry = new Entry(  );
            entry.setIdEntry( daoUtil.getInt( CONSTANT_ID_ENTRY ) );
            assignment.setEntry( entry );
            assignment.setIdField( daoUtil.getInt( CONSTANT_VALUE ) );
            assignment.setWorkgroupKey( daoUtil.getString( CONSTANT_WORKGROUP ) );
            assignmentList.add( assignment );
        }

        daoUtil.free(  );

        return assignmentList;
    }

    /**
     * Return a list of id from entries with the same task
     * @param nIdTask the task id
     * @param plugin the plugin
     * @return idEntriesList the list of entries id
     */
    public List<Integer> getIdEntriesListByTask( int nIdTask, Plugin plugin )
    {
        List<Integer> idEntriesList = new ArrayList<Integer>(  );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ID_ENTRY_BY_TASK, plugin );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            idEntriesList.add( daoUtil.getInt( CONSTANT_ID_ENTRY ) );
        }

        daoUtil.free(  );

        return idEntriesList;
    }
}
