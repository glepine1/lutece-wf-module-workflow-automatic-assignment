/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.automaticassignment.business;

import fr.paris.lutece.plugins.directory.business.IEntry;


/**
 *
 * AutomaticAssignment
 *
 */
public class AutomaticAssignment
{
    private int _nIdTask;
    private IEntry _entry;
    private int _nIdField;
    private String _strFieldValue;
    private String _strWorkgroupKey;
    private String _strWorkgroupDescription;

    /**
     * Get the id task
     * @return the task id
     */
    public int getIdTask(  )
    {
        return _nIdTask;
    }

    /**
     * Set the id task
     * @param nIdTask the task id
     */
    public void setIdTask( int nIdTask )
    {
        _nIdTask = nIdTask;
    }

    /**
     * Get the entry
     * @return the entry
     */
    public IEntry getEntry(  )
    {
        return _entry;
    }

    /**
     * Set the entry
     * @param entry the entry
     */
    public void setEntry( IEntry entry )
    {
        _entry = entry;
    }

    /**
     * Get the id field
     * @return the field id
     */
    public int getIdField(  )
    {
        return _nIdField;
    }

    /**
     * Set the id field
     * @param nIdField the field id
     */
    public void setIdField( int nIdField )
    {
        _nIdField = nIdField;
    }

    /**
     * Get the workgroup id
     * @return the workgroup id
     */
    public String getWorkgroupKey(  )
    {
        return _strWorkgroupKey;
    }

    /**
     * Set the workgroup id
     * @param strWorkgroupKey the workgroup id
     */
    public void setWorkgroupKey( String strWorkgroupKey )
    {
        _strWorkgroupKey = strWorkgroupKey;
    }

    /**
     * Get the field value
     * @return the field value
     */
    public String getFieldValue(  )
    {
        return _strFieldValue;
    }

    /**
     * Set the field value
     * @param strFieldValue the field value
     */
    public void setFieldValue( String strFieldValue )
    {
        _strFieldValue = strFieldValue;
    }

    /**
     * Get the workgroup description
     * @return the workgroup description
     */
    public String getWorkgroupDescription(  )
    {
        return _strWorkgroupDescription;
    }

    /**
     * Get the workgroup description
     * @param strWorkgroupDescription the workgroup description
     */
    public void setWorkgroupDescription( String strWorkgroupDescription )
    {
        _strWorkgroupDescription = strWorkgroupDescription;
    }
}
