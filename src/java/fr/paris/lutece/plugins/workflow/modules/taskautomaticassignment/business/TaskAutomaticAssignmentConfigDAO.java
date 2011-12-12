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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * TaskAutomaticAssignmentConfigDAO
 *
 */
public class TaskAutomaticAssignmentConfigDAO implements ITaskAutomaticAssignmentConfigDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_task, id_directory, title,is_notify,message,subject,sender_name,is_view_record,label_link_view_record,recipients_cc,recipients_bcc " +
        " FROM workflow_auto_assignment_cf WHERE id_task=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_auto_assignment_cf  " +
        "(id_task,id_directory, title, is_notify,message,subject,sender_name,is_view_record,label_link_view_record,recipients_cc,recipients_bcc)VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_auto_assignment_cf " +
        "SET id_task=?,id_directory=?, title=?, is_notify=?, message = ?, subject = ?, sender_name = ?, is_view_record = ?, label_link_view_record = ?, recipients_cc = ?, recipients_bcc = ? " +
        " WHERE id_task=? ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_auto_assignment_cf  WHERE id_task=? ";
    private static final String SQL_QUERY_DELETE_POSITION_ENTRY_FILE = " DELETE FROM workflow_auto_assignment_ef where id_task= ? ";
    private static final String SQL_QUERY_INSERT_POSITION_ENTRY_FILE = " INSERT INTO workflow_auto_assignment_ef( " +
        " id_task,position_directory_entry_file) VALUES ( ?,? ) ";
    private static final String SQL_QUERY_FIND_LIST_POSITION_ENTRY_FILE = "SELECT position_directory_entry_file " +
        " FROM workflow_auto_assignment_ef WHERE id_task = ? ";

    /**
     * {@inheritDoc}
     */
    public synchronized void insert( TaskAutomaticAssignmentConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.setInt( ++nPos, config.getIdDirectory(  ) );
        daoUtil.setString( ++nPos, config.getTitle(  ) );
        daoUtil.setBoolean( ++nPos, config.isNotify(  ) );
        daoUtil.setString( ++nPos, config.getMessage(  ) );
        daoUtil.setString( ++nPos, config.getSubject(  ) );
        daoUtil.setString( ++nPos, config.getSenderName(  ) );
        daoUtil.setBoolean( ++nPos, config.isViewRecord(  ) );
        daoUtil.setString( ++nPos, config.getLabelLinkViewRecord(  ) );
        daoUtil.setString( ++nPos, config.getRecipientsCc(  ) );
        daoUtil.setString( ++nPos, config.getRecipientsBcc(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public void store( TaskAutomaticAssignmentConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );
        daoUtil.setInt( ++nPos, config.getIdDirectory(  ) );
        daoUtil.setString( ++nPos, config.getTitle(  ) );
        daoUtil.setBoolean( ++nPos, config.isNotify(  ) );
        daoUtil.setString( ++nPos, config.getMessage(  ) );
        daoUtil.setString( ++nPos, config.getSubject(  ) );
        daoUtil.setString( ++nPos, config.getSenderName(  ) );
        daoUtil.setBoolean( ++nPos, config.isViewRecord(  ) );
        daoUtil.setString( ++nPos, config.getLabelLinkViewRecord(  ) );
        daoUtil.setString( ++nPos, config.getRecipientsCc(  ) );
        daoUtil.setString( ++nPos, config.getRecipientsBcc(  ) );

        daoUtil.setInt( ++nPos, config.getIdTask(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public TaskAutomaticAssignmentConfig load( int nIdTask, Plugin plugin )
    {
        TaskAutomaticAssignmentConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setInt( 1, nIdTask );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            config = new TaskAutomaticAssignmentConfig(  );
            config.setIdTask( daoUtil.getInt( ++nPos ) );
            config.setIdDirectory( daoUtil.getInt( ++nPos ) );
            config.setTitle( daoUtil.getString( ++nPos ) );
            config.setNotify( daoUtil.getBoolean( ++nPos ) );
            config.setMessage( daoUtil.getString( ++nPos ) );
            config.setSubject( daoUtil.getString( ++nPos ) );
            config.setSenderName( daoUtil.getString( ++nPos ) );
            config.setViewRecord( daoUtil.getBoolean( ++nPos ) );
            config.setLabelLinkViewRecord( daoUtil.getString( ++nPos ) );
            config.setRecipientsCc( daoUtil.getString( ++nPos ) );
            config.setRecipientsBcc( daoUtil.getString( ++nPos ) );
        }

        daoUtil.free(  );

        return config;
    }

    /**
     * {@inheritDoc}
     */
    public void delete( int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> loadListPositionsEntryFile( int nIdTask, Plugin plugin )
    {
        List<Integer> listIntegerPositionEntryFile = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_LIST_POSITION_ENTRY_FILE, plugin );
        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listIntegerPositionEntryFile.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return listIntegerPositionEntryFile;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteListPositionsEntryFile( int nIdTask, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_POSITION_ENTRY_FILE, plugin );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    public void insertListPositionsEntryFile( int nIdTask, Integer nPositionEntryFile, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_POSITION_ENTRY_FILE, plugin );

        daoUtil.setInt( 1, nIdTask );
        daoUtil.setInt( 2, nPositionEntryFile );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
