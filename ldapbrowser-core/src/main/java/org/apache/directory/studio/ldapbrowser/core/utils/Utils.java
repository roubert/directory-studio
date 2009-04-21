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

package org.apache.directory.studio.ldapbrowser.core.utils;


import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.directory.shared.ldap.name.AttributeTypeAndValue;
import org.apache.directory.shared.ldap.name.LdapDN;
import org.apache.directory.shared.ldap.name.Rdn;
import org.apache.directory.shared.ldap.util.LdapURL;
import org.apache.directory.studio.connection.core.ConnectionParameter.EncryptionMethod;
import org.apache.directory.studio.ldapbrowser.core.BrowserCoreConstants;
import org.apache.directory.studio.ldapbrowser.core.BrowserCorePlugin;
import org.apache.directory.studio.ldapbrowser.core.model.IAttribute;
import org.apache.directory.studio.ldapbrowser.core.model.IBrowserConnection;
import org.apache.directory.studio.ldapbrowser.core.model.IEntry;
import org.apache.directory.studio.ldapbrowser.core.model.ISearch;
import org.apache.directory.studio.ldapbrowser.core.model.IValue;
import org.apache.directory.studio.ldapbrowser.core.model.schema.Schema;
import org.apache.directory.studio.ldifparser.LdifFormatParameters;
import org.apache.directory.studio.ldifparser.LdifUtils;
import org.apache.directory.studio.ldifparser.model.container.LdifChangeModifyRecord;
import org.apache.directory.studio.ldifparser.model.container.LdifModSpec;
import org.apache.directory.studio.ldifparser.model.lines.LdifAttrValLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifModSpecSepLine;
import org.apache.directory.studio.ldifparser.model.lines.LdifSepLine;
import org.eclipse.core.runtime.Preferences;


public class Utils
{

    public static ResourceBundle oidDescriptions = null;
    // Load RessourceBundle with OID descriptions
    static
    {
        try
        {
            oidDescriptions = ResourceBundle.getBundle( "org.apache.directory.studio.ldapbrowser.core.OIDDescriptions" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }


    /**
     * Gets the textual OID description for the given numeric OID.
     * 
     * @param oid the numeric OID
     * 
     * @return the OID description, null if the numeric OID is unknown
     */
    public static String getOidDescription( String oid )
    {
        if ( oidDescriptions != null )
        {
            try
            {
                String description = oidDescriptions.getString( oid );
                return description;
            }
            catch ( MissingResourceException ignored )
            {
            }
        }
        return null;
    }


    /**
     * Transforms the given DN into a normalized String, usable by the schema cache.
     * The following transformations are performed:
     * <ul>
     *   <li>The attribute type is replaced by the OID
     *   <li>The attribute value is trimmed and lowercased
     * </ul> 
     * Example: the surname=Bar will be transformed to
     * 2.5.4.4=bar
     * 
     * 
     * @param dn the DN
     * @param schema the schema
     * 
     * @return the oid string
     */
    public static String getNormalizedOidString( LdapDN dn, Schema schema )
    {
        StringBuffer sb = new StringBuffer();

        Iterator<Rdn> it = dn.getRdns().iterator();
        while ( it.hasNext() )
        {
            Rdn rdn = it.next();
            sb.append( getOidString( rdn, schema ) );
            if ( it.hasNext() )
            {
                sb.append( ',' );
            }
        }

        return sb.toString();
    }


    private static String getOidString( Rdn rdn, Schema schema )
    {
        StringBuffer sb = new StringBuffer();

        Iterator<AttributeTypeAndValue> it = rdn.iterator();
        while ( it.hasNext() )
        {
            AttributeTypeAndValue atav = it.next();
            sb.append( getOidString( atav, schema ) );
            if ( it.hasNext() )
            {
                sb.append( '+' );
            }
        }

        return sb.toString();
    }


    private static String getOidString( AttributeTypeAndValue atav, Schema schema )
    {
        String oid = schema != null ? schema.getAttributeTypeDescription( atav.getNormType() ).getNumericOid() : atav
            .getNormType();
        return oid.trim().toLowerCase() + "=" + ( ( String ) atav.getUpValue() ).trim().toLowerCase(); //$NON-NLS-1$
    }


    public static String arrayToString( String[] array )
    {
        if ( array == null || array.length == 0 )
        {
            return "";
        }
        else
        {
            StringBuffer sb = new StringBuffer( array[0] );
            for ( int i = 1; i < array.length; i++ )
            {
                sb.append( ", " );
                sb.append( array[i] );
            }
            return sb.toString();
        }
    }


    public static boolean equals( byte[] data1, byte[] data2 )
    {
        if ( data1 == data2 )
            return true;
        if ( data1 == null || data2 == null )
            return false;
        if ( data1.length != data2.length )
            return false;
        for ( int i = 0; i < data1.length; i++ )
        {
            if ( data1[i] != data2[i] )
                return false;
        }
        return true;
    }


    public static String getShortenedString( String value, int length )
    {

        if ( value == null )
            return "";

        if ( value.length() > length )
        {
            value = value.substring( 0, length ) + "...";
        }

        return value;
    }


    public static String serialize( Object o )
    {
        Thread.currentThread().setContextClassLoader( Utils.class.getClassLoader() );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder( baos );
        encoder.writeObject( o );
        encoder.close();
        String s = LdifUtils.utf8decode( baos.toByteArray() );
        return s;
    }


    public static Object deserialize( String s )
    {
        Thread.currentThread().setContextClassLoader( Utils.class.getClassLoader() );
        ByteArrayInputStream bais = new ByteArrayInputStream( LdifUtils.utf8encode( s ) );
        XMLDecoder decoder = new XMLDecoder( bais );
        Object o = decoder.readObject();
        decoder.close();
        return o;
    }


    public static String getNonNullString( Object o )
    {
        return o == null ? "-" : o.toString();
    }


    public static String formatBytes( long bytes )
    {
        String size = "";
        if ( bytes > 1024 * 1024 )
            size += ( bytes / 1024 / 1024 ) + " MB (" + bytes + " Bytes)";
        else if ( bytes > 1024 )
            size += ( bytes / 1024 ) + " KB (" + bytes + " Bytes)";
        else if ( bytes > 1 )
            size += bytes + " Bytes";
        else
            size += bytes + " Byte";
        return size;
    }


    public static boolean containsIgnoreCase( Collection<String> c, String s )
    {
        if ( c == null || s == null )
        {
            return false;
        }

        for ( String string : c )
        {
            if ( string.equalsIgnoreCase( s ) )
            {
                return true;
            }
        }

        return false;
    }


    public static LdifFormatParameters getLdifFormatParameters()
    {
        Preferences store = BrowserCorePlugin.getDefault().getPluginPreferences();
        boolean spaceAfterColon = store.getBoolean( BrowserCoreConstants.PREFERENCE_LDIF_SPACE_AFTER_COLON );
        int lineWidth = store.getInt( BrowserCoreConstants.PREFERENCE_LDIF_LINE_WIDTH );
        String lineSeparator = store.getString( BrowserCoreConstants.PREFERENCE_LDIF_LINE_SEPARATOR );
        LdifFormatParameters ldifFormatParameters = new LdifFormatParameters( spaceAfterColon, lineWidth, lineSeparator );
        return ldifFormatParameters;
    }


    /**
     * Transforms an IBrowserConnection to an LdapURL. The following parameters are
     * used to create the LDAP URL:
     * <ul>
     * <li>scheme
     * <li>host
     * <li>port
     * </ul>
     *
     * @param entry the entry
     * @return the LDAP URL
     */
    public static LdapURL getLdapURL( IBrowserConnection browserConnection )
    {
        LdapURL url = new LdapURL();
        if ( browserConnection.getConnection() != null )
        {
            if ( browserConnection.getConnection().getEncryptionMethod() == EncryptionMethod.LDAPS )
            {
                url.setScheme( LdapURL.LDAPS_SCHEME );
            }
            else
            {
                url.setScheme( LdapURL.LDAP_SCHEME );
            }
            url.setHost( browserConnection.getConnection().getHost() );
            url.setPort( browserConnection.getConnection().getPort() );
        }
        return url;
    }


    /**
     * Transforms an IEntry to an LdapURL. The following parameters are
     * used to create the LDAP URL:
     * <ul>
     * <li>scheme
     * <li>host
     * <li>port
     * <li>dn
     * </ul>
     *
     * @param entry the entry
     * @return the LDAP URL
     */
    public static LdapURL getLdapURL( IEntry entry )
    {
        LdapURL url = getLdapURL( entry.getBrowserConnection() );
        url.setDn( entry.getDn() );
        return url;
    }


    /**
     * Transforms an ISearch to an LdapURL. The following search parameters are
     * used to create the LDAP URL:
     * <ul>
     * <li>scheme
     * <li>host
     * <li>port
     * <li>search base
     * <li>returning attributes
     * <li>scope
     * <li>filter
     * </ul>
     *
     * @param search the search
     * @return the LDAP URL
     */
    public static LdapURL getLdapURL( ISearch search )
    {
        LdapURL url = getLdapURL( search.getBrowserConnection() );
        url.setDn( search.getSearchBase() );
        if ( search.getReturningAttributes() != null )
        {
            url.setAttributes( Arrays.asList( search.getReturningAttributes() ) );
        }
        url.setScope( search.getScope().getOrdinal() );
        url.setFilter( search.getFilter() );
        return url;
    }


    /**
     * Computes the difference between the old and the new entry
     * and returns an LDIF that could be applied to the old entry
     * to get new entry.
     *
     * @param t0 the old entry
     * @param t1 the new entry
     * @return the change modify record or null if there is no difference
     *         between the two entries
     */
    public static LdifChangeModifyRecord computeDiff( IEntry t0, IEntry t1 )
    {
        LdifChangeModifyRecord record = LdifChangeModifyRecord.create( t0.getDn().getUpName() );

        // check which attributes/values must be deleted
        for ( IAttribute oldAttr : t0.getAttributes() )
        {
            String oldAttrDesc = oldAttr.getDescription();
            IAttribute newAttr = t1.getAttribute( oldAttrDesc );
            if ( newAttr == null )
            {
                // delete whole attribute
                LdifModSpec modSpec = LdifModSpec.createDelete( oldAttrDesc );
                modSpec.finish( LdifModSpecSepLine.create() );
                record.addModSpec( modSpec );
            }
            else if ( oldAttr.getValueSize() == 1 && newAttr.getValueSize() == 1 )
            {
                // check later: replace
            }
            else
            {
                Set<String> newValues = new HashSet<String>( Arrays.asList( newAttr.getStringValues() ) );
                for ( IValue oldValue : oldAttr.getValues() )
                {
                    if ( !newValues.contains( oldValue.getStringValue() ) && !oldValue.isEmpty() )
                    {
                        LdifModSpec modSpec = LdifModSpec.createDelete( oldAttrDesc );
                        if ( oldAttr.isBinary() )
                        {
                            modSpec.addAttrVal( LdifAttrValLine.create( oldAttrDesc, oldValue.getBinaryValue() ) );
                        }
                        else
                        {
                            modSpec.addAttrVal( LdifAttrValLine.create( oldAttrDesc, oldValue.getStringValue() ) );
                        }
                        modSpec.finish( LdifModSpecSepLine.create() );
                        record.addModSpec( modSpec );
                    }
                }
            }
        }

        // check which attributes/values must be added
        for ( IAttribute newAttr : t1.getAttributes() )
        {
            String newAttrDesc = newAttr.getDescription();
            IAttribute oldAttr = t0.getAttribute( newAttrDesc );
            if ( oldAttr == null )
            {
                // add whole attribute
                LdifModSpec modSpec = LdifModSpec.createAdd( newAttrDesc );
                for ( IValue newValue : newAttr.getValues() )
                {
                    if ( !newValue.isEmpty() )
                    {
                        if ( newAttr.isBinary() )
                        {
                            modSpec.addAttrVal( LdifAttrValLine.create( newAttrDesc, newValue.getBinaryValue() ) );
                        }
                        else
                        {
                            modSpec.addAttrVal( LdifAttrValLine.create( newAttrDesc, newValue.getStringValue() ) );
                        }
                    }
                }
                modSpec.finish( LdifModSpecSepLine.create() );
                if ( modSpec.isValid() )
                {
                    record.addModSpec( modSpec );
                }
            }
            else if ( oldAttr.getValueSize() == 1 && newAttr.getValueSize() == 1 )
            {
                // check later: replace
            }
            else
            {
                Set<String> oldValues = new HashSet<String>( Arrays.asList( oldAttr.getStringValues() ) );
                for ( IValue newValue : newAttr.getValues() )
                {
                    if ( !oldValues.contains( newValue.getStringValue() ) && !newValue.isEmpty() )
                    {
                        LdifModSpec modSpec = LdifModSpec.createAdd( newAttrDesc );
                        if ( newAttr.isBinary() )
                        {
                            modSpec.addAttrVal( LdifAttrValLine.create( newAttrDesc, newValue.getBinaryValue() ) );
                        }
                        else
                        {
                            modSpec.addAttrVal( LdifAttrValLine.create( newAttrDesc, newValue.getStringValue() ) );
                        }
                        modSpec.finish( LdifModSpecSepLine.create() );
                        record.addModSpec( modSpec );
                    }
                }
            }
        }

        // check which attributes/values must be replaced
        for ( IAttribute newAttr : t1.getAttributes() )
        {
            String newAttrDesc = newAttr.getDescription();
            IAttribute oldAttr = t0.getAttribute( newAttrDesc );
            if ( oldAttr != null && oldAttr.getValueSize() == 1 && newAttr.getValueSize() == 1
                && !oldAttr.getValues()[0].getStringValue().equals( newAttr.getValues()[0].getStringValue() ) )
            {
                LdifModSpec modSpec = LdifModSpec.createReplace( newAttrDesc );
                if ( newAttr.isBinary() )
                {
                    modSpec.addAttrVal( LdifAttrValLine.create( newAttrDesc, newAttr.getValues()[0].getBinaryValue() ) );
                }
                else
                {
                    modSpec.addAttrVal( LdifAttrValLine.create( newAttrDesc, newAttr.getValues()[0].getStringValue() ) );
                }
                modSpec.finish( LdifModSpecSepLine.create() );
                record.addModSpec( modSpec );
            }
        }

        record.finish( LdifSepLine.create() );

        // check for changes: if there are no changes the record does not include
        // any modifications and so it is not valid.
        return record.isValid() ? record : null;
    }

}
