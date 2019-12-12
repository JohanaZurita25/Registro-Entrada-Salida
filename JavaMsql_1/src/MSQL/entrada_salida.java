/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MSQL;

import static MSQL.usuarios.buildTableModel;
import com.mysql.jdbc.Connection;
import com.panamahitek.ArduinoException;
import com.panamahitek.PanamaHitek_Arduino;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 *
 * @author Johana
 */
public class entrada_salida extends javax.swing.JDialog {
    
    private Connection conn;
    private String driver="com.mysql.jdbc.Driver";
    private String user="root";
    private String pass="";
    private String url="jdbc:mysql://localhost:3306/proyecto_rfid"; 
    private String RFID = "";
    private int lastActivity = -1;
    
    String msg;
    PanamaHitek_Arduino ino = new PanamaHitek_Arduino();
    
    SerialPortEventListener listener = new SerialPortEventListener() {
    @Override
    public void serialEvent(SerialPortEvent spe) {
        
        try {
            if ( ino.isMessageAvailable() == true ){
             msg= ino.printMessage();
            //
            
            if (lastActivity > 0 ){
                RFID = msg.trim();
                ino.flushSerialPort();
                registrarActividad(lastActivity);
            }
            
            
            
                
                
            }
                } catch (SerialPortException ex) {
           // Logger.getLogger(pote.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ArduinoException ex) {
           // Logger.getLogger(pote.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    };
    
    
    public void startScanningRFID(){
          try {
        ino.arduinoRXTX("COM5", 9600, listener);
        } catch (ArduinoException ex) {
            Logger.getLogger(registro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void conector() {
        // Reseteamos a null la conexion a la bd
        conn=null;
        try{
            Class.forName(driver);
            // Nos conectamos a la bd
            conn= (Connection) DriverManager.getConnection(url, user, pass);
            // Si la conexion fue exitosa mostramos un mensaje de conexion exitosa
            if (conn!=null){
               System.out.println("Conexion establecida");
            }
        }
        // Si la conexion NO fue exitosa mostramos un mensaje de error
        catch (ClassNotFoundException | SQLException e){
             System.out.println("Error de conexión");
        }
    }
    
    public void cargarListaActividades()
    {
        
        DefaultTableModel model = new DefaultTableModel();
        
        String consulta = "SELECT tipos_de_actividades.nombre as actividad, CONCAT(registro_usuarios.nombre, ' ', registro_usuarios.apellido_paterno, ' ', registro_usuarios.apellido_materno) as nombre, actividades.fecha FROM actividades INNER JOIN tipos_de_actividades ON actividades.tipo = tipos_de_actividades.id INNER JOIN registro_usuarios ON actividades.user_id = registro_usuarios.id ORDER BY actividades.fecha DESC ";
        
        Statement resultado;
        try {
            resultado = conn.createStatement();
            ResultSet rs = resultado.executeQuery(consulta); 
            tablaActividades.setModel(buildTableModel(rs));
            System.out.println("Lista de actividades cargada!");
            rs.close();
              
           
        } catch (SQLException ex) {
            Logger.getLogger(MSQL.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }

    /**
     * Creates new form entrada_salida
     */
    public entrada_salida(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        conector();
        cargarListaActividades();
        startScanningRFID();
    }

    public void registrarActividad(int id_actividad){
           Statement comando; 
           try {
               
                    Date fechaActual;
                    fechaActual = new Date();
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String fechaFormateada = sdf.format(fechaActual);

                    comando = conn.createStatement(); 
                    int userId = -1;
                    
                    String consulta = "SELECT * FROM registro_usuarios WHERE rfid = '" + RFID  +"'";
        
                        Statement resultado;
                        try {
                            resultado = conn.createStatement();
                            ResultSet rs = resultado.executeQuery(consulta); 
                            
                            while(rs.next()){
                                userId = rs.getInt("id");
                            }
                            
                            rs.close();
                            estado.setText("En espera...");
                           } catch (SQLException ex) {
                            Logger.getLogger(MSQL.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    /* END OF RFID READ*/
                    
                    if (userId > -1){
                            System.out.println("ENCONTRE AL RFID!");
                           String sql = "INSERT INTO actividades (tipo, fecha, user_id) VALUES (" + id_actividad  +", '" + fechaFormateada + "', " + userId  + ")";
                           System.out.print(sql);
                           
                           comando.executeUpdate(sql);
                            JOptionPane.showMessageDialog(null, "Se ha registrado a  exitosamente en la base de datos", "Registro de actividad", JOptionPane.INFORMATION_MESSAGE);
                             cargarListaActividades();
                    }
           
                } catch (SQLException ex){
                   JOptionPane.showMessageDialog(null, "Ha ocurrido un error al guardar la actividad en la base de datos", "Error", JOptionPane.INFORMATION_MESSAGE);

                    Logger.getLogger(MSQL.class.getName()).log(Level.SEVERE, null, ex);

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

        jScrollPane1 = new javax.swing.JScrollPane();
        tablaActividades = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        estado = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tablaActividades.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tablaActividades);

        jButton1.setText("Registrar entrada");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Registar salida");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel1.setText("Estado:");

        estado.setText("En espera...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jButton1)
                            .addGap(155, 155, 155)
                            .addComponent(jButton2))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(estado, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(estado))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        
        
         this.lastActivity = 1;
         this.estado.setText("Por favor acerca tu tarjeta al lector...");
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.lastActivity = 3;
         this.estado.setText("Por favor acerca tu tarjeta al lector...");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            // TODO add your handling code here:
            ino.killArduinoConnection();
        } catch (ArduinoException ex) {
            Logger.getLogger(entrada_salida.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

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
            java.util.logging.Logger.getLogger(entrada_salida.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(entrada_salida.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(entrada_salida.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(entrada_salida.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                entrada_salida dialog = new entrada_salida(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel estado;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaActividades;
    // End of variables declaration//GEN-END:variables
}
