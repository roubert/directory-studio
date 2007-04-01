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

package org.apache.directory.ldapstudio.browser.ui.dialogs;


import java.util.Arrays;

import org.apache.directory.ldapstudio.browser.core.model.schema.Schema;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIConstants;
import org.apache.directory.ldapstudio.browser.ui.BrowserUIPlugin;
import org.apache.directory.ldapstudio.browser.ui.widgets.BaseWidgetUtils;
import org.apache.directory.ldapstudio.browser.ui.widgets.ListContentProposalProvider;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.DecoratedField;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


/**
 * This class provides a dialog to enter or select an object class.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class ObjectClassDialog extends Dialog
{

    /** The dialog title */
    public static final String DIALOG_TITLE = "Object Class Editor";

    /** The schema. */
    private Schema schema;

    /** The initial value. */
    private String initialValue;

    /** The object class combo field. */
    private DecoratedField objectClassComboField;

    /** The object class combo. */
    private Combo objectClassCombo;

    /** The object class content proposal adapter */
    private ContentProposalAdapter objectClassCPA;

    /** The return value. */
    private String returnValue;


    /**
     * Creates a new instance of ObjectClassDialog.
     * 
     * @param parentShell the parent shell
     * @param schema the schema
     * @param initialValue the initial value
     */
    public ObjectClassDialog( Shell parentShell, Schema schema, String initialValue )
    {
        super( parentShell );
        super.setShellStyle( super.getShellStyle() | SWT.RESIZE );
        this.initialValue = initialValue;
        this.schema = schema;
        this.returnValue = null;
    }


    /**
     * {@inheritDoc}
     */
    protected void configureShell( Shell shell )
    {
        super.configureShell( shell );
        shell.setText( DIALOG_TITLE );
        shell.setImage( BrowserUIPlugin.getDefault().getImage( BrowserUIConstants.IMG_OCD ) );
    }


    /**
     * {@inheritDoc}
     */
    protected void createButtonsForButtonBar( Composite parent )
    {
        super.createButtonsForButtonBar( parent );
    }


    /**
     * {@inheritDoc}
     */
    protected void okPressed()
    {
        returnValue = objectClassCombo.getText();
        super.okPressed();
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea( Composite parent )
    {
        // create composite
        Composite composite = ( Composite ) super.createDialogArea( parent );
        GridData gd = new GridData( GridData.FILL_BOTH );
        gd.widthHint = convertHorizontalDLUsToPixels( IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH );
        composite.setLayoutData( gd );

        // combo widget
        String[] allOcNames = schema.getObjectClassDescriptionNames();
        Arrays.sort( allOcNames );

        final FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
            FieldDecorationRegistry.DEC_CONTENT_PROPOSAL );
        objectClassComboField = new DecoratedField( composite, SWT.NONE, new IControlCreator()
        {
            public Control createControl( Composite parent, int style )
            {
                Combo combo = BaseWidgetUtils.createCombo( parent, new String[0], -1, 1 );
                combo.setVisibleItemCount( 20 );
                return combo;
            }
        } );
        objectClassComboField.addFieldDecoration( fieldDecoration, SWT.TOP | SWT.LEFT, true );
        objectClassComboField.getLayoutControl().setLayoutData(
            new GridData( SWT.FILL, SWT.CENTER, true, false ) );
        objectClassCombo = ( Combo ) objectClassComboField.getControl();
        objectClassCombo.setItems( allOcNames );
        objectClassCombo.setText( initialValue );

        // content proposal adapter
        objectClassCPA = new ContentProposalAdapter (objectClassCombo, new ComboContentAdapter(),
            new ListContentProposalProvider( objectClassCombo.getItems() ), null, null );
        objectClassCPA.setFilterStyle( ContentProposalAdapter.FILTER_NONE );
        objectClassCPA.setProposalAcceptanceStyle( ContentProposalAdapter.PROPOSAL_REPLACE ); 
        
        applyDialogFont( composite );
        return composite;
    }


    /**
     * Gets the object class.
     * 
     * @return the object class, null if canceled
     */
    public String getObjectClass()
    {
        return returnValue;
    }
}
