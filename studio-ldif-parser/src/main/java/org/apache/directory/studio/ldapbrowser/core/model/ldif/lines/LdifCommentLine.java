/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */

package org.apache.directory.studio.ldapbrowser.core.model.ldif.lines;


import org.apache.directory.studio.ldifparser.LdifParserConstants;


public class LdifCommentLine extends LdifNonEmptyLineBase
{

    private static final long serialVersionUID = -4810053047583328865L;


    protected LdifCommentLine()
    {
    }


    public LdifCommentLine( int offset, String rawComment, String rawNewLine )
    {
        super( offset, rawComment, rawNewLine );
    }


    public String getRawComment()
    {
        return super.getRawLineStart();
    }


    public String getUnfoldedComment()
    {
        return super.getUnfoldedLineStart();
    }


    public String toRawString()
    {
        return super.toRawString();
    }


    public boolean isValid()
    {
        return super.isValid();
    }


    public String getInvalidString()
    {
        return super.getInvalidString();
    }


    public static LdifCommentLine create( String comment )
    {
        return new LdifCommentLine( 0, comment, LdifParserConstants.LINE_SEPARATOR );
    }

}
