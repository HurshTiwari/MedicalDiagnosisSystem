/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package UI;
import DB.DBconnect;
import java.awt.Font;
import java.awt.List;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author KETAN1
 */

class Disease{

    int intensity;
    int dis_id;
    float hf;
    
    public Disease() {
        
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int getDis_id() {
        return dis_id;
    }

    public float getHf() {
        return hf;
    }

    public void setHf(float hf) {
        this.hf = hf;
    }

    public void setDis_id(int dis_id) {
        this.dis_id = dis_id;
    }
    
}

class Dis_Set{

    public Set<Disease> D;
    
    public Dis_Set() {
        this.D = new HashSet<Disease>();
    }
}

class  Diagnosis{
    
    int dis_id;
    float cf;

    public int getDis_id() {
        return dis_id;
    }

    public void setDis_id(int dis_id) {
        this.dis_id = dis_id;
    }

    public float getCf() {
        return cf;
    }

    public void setCf(float cf) {
        this.cf = cf;
    }
    
}

public class Diagnosis_Page extends javax.swing.JFrame {

    /**
     * Creates new form Diagnosis_Page
     */
    private int[] id1 = new int[5];
    private int[] intensity1 = new int[5];
    private int[] id2 = new int[5];
    private int[] intensity2 = new int[5];
    private int no_of_used_symptoms;
    private String diagnoses ;     // this will store the diagnosis names
    private String symptoms ;      // this will store the symptom descriptions
    private int cf;
    
    private int[] final_diagnoses = new int[40];
    private float[] final_cf = new float[40];
    private int no_of_final_diagnoses;
    
    DBconnect db = new DBconnect();
    Statement stmt = db.getStmt();
    ResultSet rs;  
    private int[] final_diag_id;
    
    
    public Set<Diagnosis> union(Set<Diagnosis> final_dia, Dis_Set d){
        
        for (Disease t1 : d.D) {
            
            int t1_id = t1.getDis_id();
            int insert = 1;
            
            for (Diagnosis t2 : final_dia) {
                
                if(t2.getDis_id() == t1_id){
                    float temp_cf = t2.getCf();
                    final_dia.remove(t2);
                    Diagnosis temp_d = new Diagnosis();
                    temp_d.setDis_id(t1_id);
                    temp_d.setCf(temp_cf + t1.getHf());
                    final_dia.add(temp_d);
                    insert = 0;
                    break;
                }
            }
            
            if(insert == 1){
                Diagnosis temp_d = new Diagnosis();
                temp_d.setDis_id(t1_id);
                temp_d.setCf(t1.getHf());
                final_dia.add(temp_d);
            }
        }
        
        return final_dia;
    }
    
    
    //Function returns number of final diagnosis
    public int searchAlgo(int[] id1 ,int[] id2 ,int[] intensity1, int[]intensity2){
    
        int[] id  = new int[10];
        
        for(int i=0; i <5; i++){
            id[i] = id1[i];
        }
        for(int i=5; i <10; i++){
            id[i] = id2[i-5];
        }
        
        int[] intensity  = new int[10];
        
        for(int i=0; i <5; i++){
            intensity[i] = intensity1[i];
        }
        for(int i=5; i <10; i++){
            intensity[i] = intensity2[i-5];
        }
        
        Dis_Set[] D = new Dis_Set[10];
        
        for(int i=0; i<10; i++){
            D[i] = new Dis_Set();
        }
        
        
        for(int i=0; i<5; i++){       // loop to extract all dis_symptom id pairs from symptom1 tables
            try 
            {
                int j = 0;    
                String query = "Select dis_id, intensity, hf_num, hf_den from mades.dis_sym1_rel where sym_id= " + id1[i];
                rs = db.getRS(query,stmt);
                while(rs.next()){
                    
                    Disease d = new Disease();
                    d.setDis_id(rs.getInt("dis_id"));
                    d.setIntensity(rs.getInt("intensity"));
                    d.setHf((float)rs.getInt("hf_num")/(float)rs.getInt("hf_den"));
                    //D[i].D.add(d); 
                    
                    int insert = 1;
                    
                    for (Disease t1 : D[i].D) {
            
                        int t1_id = t1.getDis_id();

                        if(d.getDis_id() == t1_id){
                            
                            insert = 0;
                            
                            if(t1.getHf()/Math.abs(t1.getIntensity()-intensity1[i]) < d.getHf()/Math.abs(d.getIntensity() - intensity1[i])){
                                D[i].D.remove(t1);
                                D[i].D.add(d);
                                break;
                            }
                            
                        }
                        
                    }
                    
                    if(insert == 1){
                        D[i].D.add(d);
                    }
                    
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Diagnosis_Page.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        for(int i=0;i<5;i++){       // loop to extract all dis_symptom id pairs from symptom2 tables
            try 
            {
                int j = 0;    
                String query = "Select dis_id, intensity, hf_num, hf_den from mades.dis_sym2_rel where sym_id= " + id2[i];
                rs = db.getRS(query,stmt);
                while(rs.next()){
                    Disease d = new Disease();
                    d.setDis_id(rs.getInt("dis_id"));
                    d.setIntensity(rs.getInt("intensity"));
                    d.setHf((float)rs.getInt("hf_num")/(float)rs.getInt("hf_den"));
                    
                    //D[i+5].D.add(d); 
                    
                    int insert = 1;
                    
                    for (Disease t1 : D[i+5].D) {
            
                        int t1_id = t1.getDis_id();

                        if(d.getDis_id() == t1_id){
                            
                            insert = 0;
                            
                            if(t1.getHf()/Math.abs(t1.getIntensity()-intensity2[i]) < d.getHf()/Math.abs(d.getIntensity() - intensity2[i])){
                                D[i+5].D.remove(t1);
                                D[i+5].D.add(d);
                                break;
                            }
                            
                        }
                        
                    }
                    
                    if(insert == 1){
                        D[i+5].D.add(d);
                    }
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Diagnosis_Page.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
                // now we have all diseases for symptom
            //--------------------------------step2 over ---------------//
        
        
        for (int i=0; i<10 ; i++)    // for ech set Di
        {
            Iterator j = D[i].D.iterator();
            
            while(j.hasNext()){
                
                Disease d = (Disease)j.next();
                
                if(d.getIntensity() != intensity[i]){
                    D[i].D.remove(d);
                    d.setHf(d.getHf()/(Math.abs(d.getIntensity() - intensity[i])));
                    D[i].D.add(d);
                }
                //System.out.println("DiseaseSet["+i+"]-> disease: hf = "+d.getHf());
                
            }
            
            
        }
        
            //--------------------------------step3 over ---------------//
        
        Set<Diagnosis> final_dia = new HashSet<>();
        
        for(int i=0; i<10; i++){
            final_dia = union(final_dia, D[i]);
        }
        
        int i = 0;
        
        for (Diagnosis t2 : final_dia) {
            
            final_diagnoses[i] = t2.getDis_id();
            final_cf[i++] = t2.getCf()/no_of_used_symptoms;
        
        }
        
        int no_of_final_diagnoses = i;
        
            // ------------------------------step4 over ----------------//
        
        
            // ------------------------------step5 over ----------------//
        
        
        
        //**********************************************************************************
        //                  STEP 6 => Sort diagnoses a/c to cf.
        //**********************************************************************************
        int temp;
        float tmp;
        
        for(int g=0; g<no_of_final_diagnoses; g++){
            for(int h=i; h<no_of_final_diagnoses-g-1; h++){
                if(final_cf[g] < final_cf[g+1]){
                    temp = final_diagnoses[g];
                    final_diagnoses[g] = final_diagnoses[g+1];
                    final_diagnoses[g+1] = temp;
                    tmp = final_cf[g];
                    final_cf[g] = final_cf[g+1];
                    final_cf[g+1] = tmp;
                }
            }
        }
        //////////////////////////////  STEP 6 OVER  ////////////////////////////////////////
        
        return no_of_final_diagnoses;
    }
    
    
    public void insertRow(String diagnosis, String symptoms, float cf){
        int index = jTable2.getRowCount();
        ((DefaultTableModel)jTable2.getModel()).addRow(new Object[]{index+1, diagnosis, symptoms, cf});
    }
    
    public void init_table(){
        jTable2.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 20));
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment( SwingConstants.LEFT );
        
        jTable2.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        jTable2.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        jTable2.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);
        jTable2.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);
        
        jTable2.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable2.getColumnModel().getColumn(1).setPreferredWidth(200);
        jTable2.getColumnModel().getColumn(2).setPreferredWidth(550);
        
        jTable2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    public Diagnosis_Page() {
        initComponents();
        init_table();

    }
    
    public Diagnosis_Page(int[] id1 ,int[] id2 ,int[] intensity1, int[]intensity2, int no_of_used_symptoms) {
        
        initComponents();
        
        init_table();
        
        this.id1 = id1 ;
        this.id2 = id2 ;
        this.intensity1 = intensity1 ;
        this.intensity2 = intensity2 ;
        this.no_of_used_symptoms = no_of_used_symptoms;
        
        no_of_final_diagnoses = searchAlgo(id1 ,id2 ,intensity1,intensity2);
        
        for(int i=0 ; i< no_of_final_diagnoses ; i++){          // loop to put all disease values in the displayed table
        
            //TODO: sql statements to fetch diagnosis name and symptoms and cf from respective tables
            
            String query = "Select name from mades.disease where id= " + final_diagnoses[i];
            rs = db.getRS(query,stmt);
            
            try {
                if(rs.next()){
                    insertRow(rs.getString("name"), "-", final_cf[i]);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Diagnosis_Page.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            insertRow("Other","-",0.0f);
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Calligraphy", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 0, 153));
        jLabel1.setText("Here Goes the Diagnosis..");

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 0, 153));
        jButton1.setText("BACK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 204, 51));
        jButton2.setText("RESET");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 153, 51));
        jButton3.setText("CONTINUE");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Sr.No.", "Diagnosis", "Symptoms Leading to Diagnosis", "CF"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setRowHeight(40);
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 340, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(0, 342, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 541, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if(jTable2.getSelectedRow() == -1){
            JOptionPane.showMessageDialog(null,"Select at least one row!","Error",JOptionPane.ERROR_MESSAGE);
        }
        else if(jTable2.getSelectedRow() == jTable2.getRowCount()-1){     // code for other option
            dispose();
            Learning_Page lp = new Learning_Page(id1 ,id2 ,intensity1,intensity2,5,5);
            lp.setVisible(true);
        }
        else{
            
            //********************************************************************************************//
            //********************  UPDATE HF in KB. INSERT NEW DIS-SYM RELATIONS.  **********************//
            //********************************************************************************************//
            
            for(int i=0; i<no_of_final_diagnoses; i++){
                
                
                if(i == jTable2.getSelectedRow()){
                    
                    for(int j=0; j<5; j++){

                        if(id1[j] != 0){

                            String query = "select * from mades.dis_sym1_rel where dis_id= " + final_diagnoses[i] + " and sym_id = " + id1[j] + " and intensity = " + intensity1[j];
                            rs = db.getRS(query,stmt);

                            try {

                                if(rs.next()){
                                    
                                    int hf_deno = rs.getInt("hf_den");
                                    int hf_num = rs.getInt("hf_num");
                                    
                                    String update_query = "update mades.dis_sym1_rel set hf_den = " + (hf_deno+1) + ", hf_num = " + (hf_num+1) +"where dis_id = " + 
                                                            final_diagnoses[i] + " and sym_id = " + id1[j] + " and  intensity = " + intensity1[i];

                                    rs = db.getRS(update_query,stmt);
                                }
                                
                                else{
                                    String insert_query = "insert into mades.dis_sym1_rel (`dis_id`, `sym_id`,`hf_num`, `hf_den`,`intensity`)\n" +
                                                          "VALUES (" + final_diagnoses[i] + ", " + id1[j] + ", 1, 1" + intensity1[i] + ")";

                                    rs = db.getRS(insert_query,stmt);
                                }

                            } catch (SQLException ex) {
                                Logger.getLogger(Diagnosis_Page.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }
                    
                    for(int j=0; j<5; j++){

                        if(id2[j] != 0){

                            String query = "select * from mades.dis_sym2_rel where dis_id= " + final_diagnoses[i] + " and sym_id = " + id2[j] + " and intensity = " + intensity2[j];
                            rs = db.getRS(query,stmt);

                            try {

                                if(rs.next()){
                                    
                                    int hf_deno = rs.getInt("hf_den");
                                    int hf_num = rs.getInt("hf_num");
                                    
                                    String update_query = "update mades.dis_sym2_rel set hf_den = " + (hf_deno+1) + ", hf_num = " + (hf_num+1) +"where dis_id = " + 
                                                            final_diagnoses[i] + " and sym_id = " + id2[j] + " and  intensity = " + intensity2[i];

                                    rs = db.getRS(update_query,stmt);
                                }
                                
                                else{
                                    String insert_query = "insert into mades.dis_sym2_rel (`dis_id`, `sym_id`,`hf_num`, `hf_den`,`intensity`)\n" +
                                                          "VALUES (" + final_diagnoses[i] + ", " + id2[j] + ", 1, 1" + intensity2[i] + ")";

                                    rs = db.getRS(insert_query,stmt);
                                }

                            } catch (SQLException ex) {
                                Logger.getLogger(Diagnosis_Page.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }
                }
                
                else{
                
                    for(int j=0; j<5; j++){

                        if(id1[j] != 0){

                            String query = "select * from mades.dis_sym1_rel where dis_id= " + final_diagnoses[i] + " and sym_id = " + id1[j] + " and intensity = " + intensity1[j];
                            rs = db.getRS(query,stmt);

                            try {

                                if(rs.next()){
                                    
                                    int hf_deno = rs.getInt("hf_den");
                                    
                                    String update_query = "update mades.dis_sym1_rel set hf_den = " + (hf_deno+1) + "where dis_id = " + 
                                                            final_diagnoses[i] + " and sym_id = " + id1[j] + " and  intensity = " + intensity1[i];

                                    rs = db.getRS(update_query,stmt);
                                }

                            } catch (SQLException ex) {
                                Logger.getLogger(Diagnosis_Page.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }
                    
                    for(int j=0; j<5; j++){

                        if(id2[j] != 0){

                            String query = "select * from mades.dis_sym2_rel where dis_id= " + final_diagnoses[i] + " and sym_id = " + id2[j] + " and intensity = " + intensity2[j];
                            rs = db.getRS(query,stmt);

                            try {

                                if(rs.next()){
                                    
                                    int hf_deno = rs.getInt("hf_den");
                                    
                                    String update_query = "update mades.dis_sym2_rel set hf_den = " + (hf_deno+1) + "where dis_id = " + 
                                                            final_diagnoses[i] + " and sym_id = " + id2[j] + " and  intensity = " + intensity2[i];

                                    rs = db.getRS(update_query,stmt);
                                }

                            } catch (SQLException ex) {
                                Logger.getLogger(Diagnosis_Page.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                    }
                }
                
            }
            
            
            //////////////////////////////////////  HF UPDATES DONE.  ////////////////////////////////////
            
            dispose();
            Treatment t = new Treatment();
            t.setVisible(true);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        dispose();
        Symptoms_Page sp = new Symptoms_Page();
        sp.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        jTable2.clearSelection();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Diagnosis_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Diagnosis_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Diagnosis_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Diagnosis_Page.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Diagnosis_Page().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables

}
