package scenario.s2014.demo.gui;

import java.util.Vector;

import scenario.s2014.demo.gui.Demo.PDAComponents;
import tatami.core.agent.visualization.AgentGui.InputListener;

public class DemoAgentOutputInput extends javax.swing.JFrame {

	private static final long serialVersionUID = -2811079879336761997L;

	public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextArea jTextArea1;
    
    public InputListener	joinListener	= null;
	public javax.swing.JButton jButton1;
	
    public DemoAgentOutputInput() {
        initComponents();
    }
    
    private void initComponents() {

    	jButton1 = new javax.swing.JButton();
        
        jButton1.setText("Attends SmartTV");
        jButton1.setActionCommand("");
        jButton1.setMaximumSize(new java.awt.Dimension(70, 23));
        jButton1.setPreferredSize(new java.awt.Dimension(70, 23));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonJoinActionPerformed(evt);
            }
        });
        
    	jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(522, 232));
        setTitle("Attendee ");
        setBounds(1100, 700, 522, 232);
        
        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setHorizontalScrollBar(null);

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Times New Roman", 1, 14));
        jTextArea1.setForeground(new java.awt.Color(0, 102, 0));
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);
       
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
                    )
                    .addGroup(layout.createSequentialGroup()
                    	.addContainerGap()
                    	.addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            )
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                )
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                	.addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                )
                .addGap(18, 18, 18)
            )
        );
        
        pack();
    }

    public void jButtonJoinActionPerformed(java.awt.event.ActionEvent evt) {                                         

    	if(joinListener != null){
    		System.out.println("chair pushed .... lalala  ");
			Vector<Object> args = new Vector<Object>(2);
			args.add("lalala");
			joinListener.receiveInput("attend", args);
		}
		else
			System.out.println("nobody to receive the input");
    }
}
