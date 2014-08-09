package com.lyndir.lhunath.masterpassword;

import static com.lyndir.lhunath.opal.system.util.StringUtils.strf;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;


/**
 * @author lhunath, 2014-06-08
 */
public class PasswordFrame extends JFrame  {
       
    public PasswordFrame(final User user)
            throws HeadlessException {
        super( "Master Password" );        

        //Read a list of user names if they exist
        Vector<SiteInfo> siteList = SiteInfo.readFromFile(this);
        
        //Have a vertical centered layout.
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        JPanel content = new JPanel() {
            {
                setBorder( new EmptyBorder( 20, 20, 20, 20 ) );
            }
        };
        content.setLayout(new BorderLayout() );        
        setContentPane( content );

        // Print the username at the top.
        JLabel label;
        content.add( label = new JLabel( strf( "Generating passwords for: %s", user.getName() ) ), BorderLayout.NORTH );
        label.setAlignmentX( Component.CENTER_ALIGNMENT );

        //Create a panel in the middle for the site list
        final JPanel listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        this.add(new JScrollPane(listContainer), BorderLayout.CENTER);
                
        //Add the panels for each site.        
        for ( SiteInfo s : siteList ) {
        	listContainer.add( new PasswordPanel(user, s) );
        }
        
        //Now add a button at the bottom to add a new site
        JButton button = new JButton("Add");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JPanel newPanel = new PasswordPanel(user,SiteInfo.getNewSiteInfo());
                listContainer.add(newPanel);
                listContainer.revalidate();
                // Scroll down to last added panel
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        newPanel.scrollRectToVisible(newPanel.getBounds());
                    }
                });
            }
        });
        
        content.add(button, BorderLayout.SOUTH);

        pack();        
        setPreferredSize( new Dimension( 600, 400 ) );
        pack();

        setLocationByPlatform( true );
        setLocationRelativeTo( null );
    }



    interface PasswordCallback {

        void passwordGenerated(SiteInfo info, String sitePassword);
    }
}
