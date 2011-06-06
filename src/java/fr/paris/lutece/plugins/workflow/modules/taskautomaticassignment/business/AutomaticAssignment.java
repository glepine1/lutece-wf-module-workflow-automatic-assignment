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

import fr.paris.lutece.plugins.directory.business.Entry;


public class AutomaticAssignment
{
    private int _nIdTask;
    private Entry _entry;
    private int _nIdField;
    private String _strFieldValue;
    private String _strWorkgroupKey;
    private String _strWorkgroupDescription;

    /**
    *
    * @return the task id
    */
    public int getIdTask(  )
    {
        return _nIdTask;
    }

    /**
    *
    * @param the task id
    */
    public void setIdTask( int nIdTask )
    {
        _nIdTask = nIdTask;
    }

    /**
    *
    * @return the entry
    */
    public Entry getEntry(  )
    {
        return _entry;
    }

    /**
    *
    * @param the entry
    */
    public void setEntry( Entry entry )
    {
        _entry = entry;
    }

    /**
    *
    * @return the field id
    */
    public int getIdField(  )
    {
        return _nIdField;
    }

    /**
    *
    * @param the field id
    */
    public void setIdField( int nIdField )
    {
        _nIdField = nIdField;
    }

    /**
    *
    * @return the workgroup id
    */
    public String getWorkgroupKey(  )
    {
        return _strWorkgroupKey;
    }

    /**
    *
    * @param the workgroup id
    */
    public void setWorkgroupKey( String strWorkgroupKey )
    {
        _strWorkgroupKey = strWorkgroupKey;
    }

    /**
    *
    * @return the field id
    */
    public String getFieldValue(  )
    {
        return _strFieldValue;
    }

    /**
    *
    * @param the field id
    */
    public void setFieldValue( String strFieldValue )
    {
        _strFieldValue = strFieldValue;
    }

    /**
    *
    * @return the workgroup description
    */
    public String getWorkgroupDescription(  )
    {
        return _strWorkgroupDescription;
    }

    /**
    *
    * @param the workgroup description
    */
    public void setWorkgroupDescription( String strWorkgroupDescription )
    {
        _strWorkgroupDescription = strWorkgroupDescription;
    }
}
