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
package org.apache.directory.studio.apacheds.model;


/**
 * This enum represents the version of an Apache DS server.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum ServerVersion
{
    /** Version 1.5.6 */
    VERSION_1_5_6
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return "1.5.6"; //$NON-NLS-1$
        }
    },
    /** Version 1.5.5 */
    VERSION_1_5_5
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return "1.5.5"; //$NON-NLS-1$
        }
    },
    /** Version 1.5.4 */
    VERSION_1_5_4
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return "1.5.4"; //$NON-NLS-1$
        }
    },

    /** Version 1.5.3 */
    VERSION_1_5_3
    {
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString()
        {
            return "1.5.3"; //$NON-NLS-1$
        }
    }
}