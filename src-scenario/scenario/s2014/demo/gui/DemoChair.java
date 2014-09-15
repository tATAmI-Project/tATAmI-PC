package scenario.s2014.demo.gui;

import java.util.Vector;

import tatami.core.agent.visualization.AgentGui.InputListener;

public class DemoChair extends javax.swing.JFrame {

	private static final long serialVersionUID = -2811079879336761997L;

	public InputListener	joinListener	= null;
	
	public javax.swing.JButton jButton1;
	
	public DemoChair() {
        initComponents();
    }
    
    private void initComponents() {

    	jButton1 = new javax.swing.JButton();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(220, 80));
        setTitle("Chair GUI");
        setBounds(0, 700, 220, 80);
        
        jButton1.setText("Presentation Started");
        jButton1.setActionCommand("");
        jButton1.setMaximumSize(new java.awt.Dimension(70, 23));
        jButton1.setPreferredSize(new java.awt.Dimension(70, 23));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonJoinActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                    )
                )
                .addContainerGap())
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    )
                    .addGap(18, 18, 18)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pack();
    }

    public void setNimbus() {
    }
    
    public void jButtonJoinActionPerformed(java.awt.event.ActionEvent evt) {                                         

    	if(joinListener != null){
			Vector<Object> args = new Vector<Object>(2);
			args.add("started");
			joinListener.receiveInput(Demo.PDAComponents.JOIN.toString().toLowerCase(), args);
		}
		else
			System.out.println("nobody to receive the input");
    }
}
