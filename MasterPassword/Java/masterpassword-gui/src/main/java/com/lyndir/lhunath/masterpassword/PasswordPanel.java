package com.lyndir.lhunath.masterpassword;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.lyndir.lhunath.masterpassword.PasswordFrame.PasswordCallback;
import com.lyndir.lhunath.masterpassword.util.Components;


public class PasswordPanel extends JPanel implements DocumentListener {

	
	private final JTextField               siteNameField;
    private final JComboBox<MPElementType> siteTypeField;
    private final JSpinner                 siteCounterField;
    private final JLabel                   passwordLabel;
    private final User                     user;
    private final SiteInfo				   siteInfo;
    private final JButton				   closeBtn;
    private final JButton				   copyBtn;

	public PasswordPanel(User user, SiteInfo siteInfo)
	{
		
		this.user = user;
		this.siteInfo = siteInfo;		
		
		//Use a vertical centered layout
		this.setLayout(new BoxLayout( this, BoxLayout.PAGE_AXIS));
		
		// A label to indicate the site name
		JLabel label = new JLabel( "Site Name:" );                   
       
		//And create the field to update the site-name itself.
        siteNameField = new JTextField(siteInfo.getName()) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension( Integer.MAX_VALUE, getPreferredSize().height );
            }                        
        };
        siteNameField.setColumns(20);
                       
        siteNameField.getDocument().addDocumentListener( this );
        siteNameField.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                updatePassword( new PasswordPanelCallback() );
            }
        } );
        
        copyBtn = new JButton("Copy");
        copyBtn.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				copyToClippboard();
			}
        	
        });
        
        //Add the site label and the site next to each other using a flowlayout.
        this.add( Components.flowLayout(label, siteNameField, copyBtn));
        
        // Site Type & Counter
        siteTypeField = new JComboBox<>( MPElementType.valuesOfType(MPElementTypeClass.Generated) ); 
        siteTypeField.setSelectedItem( siteInfo.getType() );
        siteTypeField.addItemListener( new ItemListener() {
            @Override
            public void itemStateChanged(final ItemEvent e) {
            	updatePassword( new PasswordPanelCallback() );
            }
        } );

        siteCounterField = new JSpinner( new SpinnerNumberModel( siteInfo.getNumber(), 1, Integer.MAX_VALUE, 1 ) ) {
        	@Override
        	public Dimension getMaximumSize() {
        		return new Dimension( 20, getPreferredSize().height );
        	}
        };
        
        siteCounterField.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
            	updatePassword( PasswordPanelCallback.getInstance() );
            }
        } );
                
        //And the close button
        closeBtn = new JButton("Close");        
        closeBtn.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deleteMe();				
			}
        } );
        
        //Add the password type and counter next to each other.
        this.add( Components.flowLayout(siteTypeField, siteCounterField, closeBtn));

        // Password
        this.add( passwordLabel = new JLabel( "--" ) );
        passwordLabel.setFont( Res.sourceCodeProBlack().deriveFont( 40f ) );
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        //If we have a site name that is not empty, generate a password immediately
        if ( siteInfo.getName().length() > 0 ) {
        	this.updatePassword( PasswordPanelCallback.getInstance() );
        }
                
        //Make a pretty border around it all.
        this.setBorder( new CompoundBorder( new EtchedBorder( EtchedBorder.RAISED ), new EmptyBorder( 8, 8, 8, 8 ) ) );
        
        this.setMaximumSize(this.getPreferredSize());
	}
	
	private void deleteMe() {
		Container parent = this.getParent();
		SiteInfo.remove(this.siteInfo);
		parent.remove(this);
		parent.revalidate();
		parent.repaint();
		
	}
	
	protected void copyToClippboard() {
		//TODO: This is duplicate code from below in the callback object.
		StringSelection clipboardContents = new StringSelection( this.passwordLabel.getText() );
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( clipboardContents, null );		
	}

	
    private void updatePassword(final PasswordCallback callback) {
        this.siteInfo.setName( siteNameField.getText() ); 
        this.siteInfo.setNumber( (Integer) siteCounterField.getValue() );
        this.siteInfo.setType( (MPElementType) siteTypeField.getSelectedItem() );    	

        if ( this.siteInfo.getType().getTypeClass() != MPElementTypeClass.Generated ||
        		this.siteInfo.getName().isEmpty() ||
        		!user.hasKey() )  {
        	//generateContent does not like invalid data passed to it. So we check beforehand.
        	return;        
        }
        
        Res.execute( new Runnable() {
            @Override
            public void run() {
                final String sitePassword = MasterPassword.generateContent( 
                		siteInfo.getType(), siteInfo.getName(), user.getKey(), siteInfo.getNumber() );
                if (callback != null) {
                    callback.passwordGenerated( siteInfo, sitePassword );
                }

                SwingUtilities.invokeLater( new Runnable() {
                    @Override
                    public void run() {
                        passwordLabel.setText( sitePassword );
                    }
                } );
            }
        } );
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        updatePassword( PasswordPanelCallback.getInstance() );
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        updatePassword( PasswordPanelCallback.getInstance() );
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
        updatePassword( PasswordPanelCallback.getInstance() );
    }

}

class PasswordPanelCallback implements PasswordFrame.PasswordCallback {    
	
	//Private constructor and static member to make this a singleton class.	
	protected PasswordPanelCallback()	{ }	
	private static PasswordFrame.PasswordCallback obj;	
	
	public static PasswordFrame.PasswordCallback getInstance()
	{
		
		if ( obj == null ) {
			obj = new PasswordPanelCallback();
		}
		return obj;
	}
	
	
	//Now get on with the actual class
	
	
	@Override
    public void passwordGenerated(final SiteInfo info, final String sitePassword) {
    	//TODO: Have this function write the site info back to the file on disk as well.
    	
        StringSelection clipboardContents = new StringSelection( sitePassword );
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( clipboardContents, null );

        //And write back to the disk.
        SiteInfo.writeBackToFile();
        
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                //dispose();
            }
        } );
    }
}
