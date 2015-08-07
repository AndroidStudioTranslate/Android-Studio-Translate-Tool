/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package translation.view;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import javax.security.auth.login.Configuration;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import test.examples.org.apache.http.examples.nio.client.QuickStart;
import translation.*;

/**
 *
 * @author v
 */
public class TAS extends javax.swing.JFrame {

    /**
     * Creates new form TAS
     */
    public TAS() {
        initComponents();
        jTable_kv.setModel(dataModel);
        for (int i = 0; i < ColumnNames.length; i++) {
            if (i == 0) {
                tc_id = jTable_kv.getColumn(ColumnNames[i]);
                tc_id.setPreferredWidth(50);
            } else if (i == 1) {
                tc_key = jTable_kv.getColumn(ColumnNames[i]);
                tc_key.setPreferredWidth(170);
            } else if (i == 2) {
                tc_value_origin = jTable_kv.getColumn(ColumnNames[i]);
                tc_value_origin.setPreferredWidth(300);
            } else if (i == 3) {
                tc_value_new = jTable_kv.getColumn(ColumnNames[i]);
                tc_value_new.setPreferredWidth(300);
            }
        }
        jTextField_file.setText("/home/swtf/AndroidStudio_jar/resources_en.jar");
        jTable_kv.setShowVerticalLines(false);
        jTable_kv.setShowHorizontalLines(false);
        jTable_kv.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cmodel = jTable_kv.getColumnModel();
        jTable_kv.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseMoved(MouseEvent e) {
                int row = jTable_kv.rowAtPoint(e.getPoint());
                int col = jTable_kv.columnAtPoint(e.getPoint());
                if (col != 0) {
                    String ttt = jTable_kv.getValueAt(row, col).toString();
                    if (ttt.length() != 0 && ttt != null) {
                        jTable_kv.setToolTipText(ttt);
                    } else {

                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
            }
        });
//        dataModel.addTableModelListener(new TableModelListener() {
//
//            @Override
//            public void tableChanged(TableModelEvent e) {
//                if (!valueNewList.get(jTable_kv.getSelectedRow()).equals(tc_value_new.getCellEditor().getCellEditorValue().toString())) {
//                    valueNewList.add(jTable_kv.getSelectedRow(), tc_value_new.getCellEditor().getCellEditorValue().toString());
//                }
//                System.out.println("-----------"+tc_value_new.getCellEditor().getCellEditorValue().toString());
//                jTable_kv.updateUI();
//            }
//
//        });
    }

    /**
     * 翻译异步请求
     *
     * @param origin 译文
     * @param index 索引
     */
    private void translate(List<String> list, int index_list) {

        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        HttpEntity entity = null;
        String jsonContent = "";
        try {
            // Start the client
            httpclient.start();
            final int count = list.size();
            for (int i = 0; i < count; i++) {
                int index = i;
                if (count == 1) {
                    index = index_list;
                }
                final int index_final = index;
                String origin = list.get(i);
                // One most likely would want to use a callback for operation result
                final CountDownLatch latch = new CountDownLatch(1);
                System.out.println(String.format(translate_site_baidu, URLEncoder.encode(origin, "UTF-8")));
                final HttpGet request = new HttpGet(String.format(translate_site_baidu, URLEncoder.encode(origin, "UTF-8")));
                httpclient.execute(request, new FutureCallback<HttpResponse>() {

                    @Override
                    public void completed(final HttpResponse response2) {
                        try {
                            latch.countDown();
//                            System.out.println(request.getRequestLine() + "->" + response2.getStatusLine());
                            HttpEntity entity = response2.getEntity();
                            String jsonContent = EntityUtils.toString(entity, "UTF-8");
                            System.out.println("jsonContent:" + jsonContent.replace("{ ", "{").replace(" }", "}"));
                            if (valueNewList.size() > index_final) {
                                valueNewList.remove(index_final);
                            }
                            valueNewList.add(index_final, jsonContent.replace("{ ", "{").replace(" }", "}"));
                            jTable_kv.updateUI();
                            jButton_saveProperties.setEnabled(true);
                            jLabel1.setText((index_final + 1) + "/" + valueNewList.size());
                            jProgressBar1.setValue((index_final + 1));
                            jTable_kv.setRowSelectionInterval(index_final, index_final);
                            Rectangle rect = jTable_kv.getCellRect(index_final, 0, true);
                            jTable_kv.scrollRectToVisible(rect);
                        } catch (IOException ex) {
                            Logger.getLogger(QuickStart.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ParseException ex) {
                            Logger.getLogger(QuickStart.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    @Override
                    public void failed(final Exception ex) {
                        latch.countDown();
                        System.out.println(request.getRequestLine() + "->" + ex);
                    }

                    @Override
                    public void cancelled() {
                        latch.countDown();
                        System.out.println(request.getRequestLine() + " cancelled");
                    }

                });
                latch.await();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                httpclient.close();
            } catch (IOException ex) {
                Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
            }
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

        jDialog1 = new javax.swing.JDialog();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new JTA();
        jButton2 = new javax.swing.JButton();
        jDialog2 = new javax.swing.JDialog();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem_translate = new javax.swing.JMenuItem();
        jDialog_ra = new javax.swing.JDialog();
        jTextField_newfile = new javax.swing.JTextField();
        jButton_new = new javax.swing.JButton();
        jTextField_oldfile = new javax.swing.JTextField();
        jButton_old = new javax.swing.JButton();
        jButton_compare = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea_list = new javax.swing.JTextArea();
        jTextField_file = new javax.swing.JTextField();
        jButton_fileview1 = new javax.swing.JButton();
        jButton_getJarContext = new javax.swing.JButton();
        jTextField_bak = new javax.swing.JTextField();
        jButton_fileview2 = new javax.swing.JButton();
        jButton_saveProperties = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable_kv = new javax.swing.JTable();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton3 = new javax.swing.JButton();
        jButton_replaceAll = new javax.swing.JButton();

        jDialog1.setAlwaysOnTop(true);
        jDialog1.setLocationByPlatform(true);
        jDialog1.setMinimumSize(new java.awt.Dimension(500, 400));

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setRows(5);
        jScrollPane3.setViewportView(jTextArea1);

        jButton2.setText("修                             改");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
            .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jDialog1Layout.createSequentialGroup()
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                    .addGap(39, 39, 39)))
            .addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDialog1Layout.createSequentialGroup()
                    .addGap(0, 264, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jDialog2.setAlwaysOnTop(true);
        jDialog2.setMinimumSize(new java.awt.Dimension(300, 120));

        jLabel2.setText("替换");

        jLabel3.setText("为");

        jButton4.setText("全 部 替 换");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });

        jButton5.setText("取  消");
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton5MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jDialog2Layout = new javax.swing.GroupLayout(jDialog2.getContentPane());
        jDialog2.getContentPane().setLayout(jDialog2Layout);
        jDialog2Layout.setHorizontalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog2Layout.createSequentialGroup()
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jDialog2Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jButton5))
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jDialog2Layout.setVerticalGroup(
            jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jDialog2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap())
        );

        jMenuItem_translate.setText("翻译");
        jMenuItem_translate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_translateActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem_translate);

        jDialog_ra.setTitle("比较新旧版");
        jDialog_ra.setMinimumSize(new java.awt.Dimension(500, 300));

        jTextField_newfile.setColumns(30);
        jTextField_newfile.setText("G:\\as汉化\\resources_en20140221.jar");

        jButton_new.setText("新");
        jButton_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_newActionPerformed(evt);
            }
        });

        jTextField_oldfile.setColumns(30);
        jTextField_oldfile.setText("G:\\as汉化\\old\\AndroidStudio源文件备份.jar");

        jButton_old.setText("旧");
        jButton_old.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_oldActionPerformed(evt);
            }
        });

        jButton_compare.setText("比  较");
        jButton_compare.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_compareMouseClicked(evt);
            }
        });

        bar_compare = jScrollPane5.getVerticalScrollBar();

        jTextArea_list.setColumns(20);
        jTextArea_list.setEditable(false);
        jTextArea_list.setRows(5);
        jScrollPane5.setViewportView(jTextArea_list);

        javax.swing.GroupLayout jDialog_raLayout = new javax.swing.GroupLayout(jDialog_ra.getContentPane());
        jDialog_ra.getContentPane().setLayout(jDialog_raLayout);
        jDialog_raLayout.setHorizontalGroup(
            jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_raLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jDialog_raLayout.createSequentialGroup()
                        .addGroup(jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jDialog_raLayout.createSequentialGroup()
                                .addComponent(jTextField_newfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_new))
                            .addGroup(jDialog_raLayout.createSequentialGroup()
                                .addComponent(jTextField_oldfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_old)))
                        .addGap(18, 18, 18)
                        .addComponent(jButton_compare, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jDialog_raLayout.setVerticalGroup(
            jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jDialog_raLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton_compare, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jDialog_raLayout.createSequentialGroup()
                        .addGroup(jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_newfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton_new))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jDialog_raLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField_oldfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton_old))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("翻译属性文件工具");

        jTextField_file.setToolTipText("原始属性文件");
        new DropTarget(jTextField_file, DnDConstants.ACTION_COPY_OR_MOVE, new MyDropTargetListener(this));

        jButton_fileview1.setText("浏览");
        jButton_fileview1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_fileview1ActionPerformed(evt);
            }
        });

        jButton_getJarContext.setText("获取jar包目录");
        jButton_getJarContext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_getJarContextActionPerformed(evt);
            }
        });

        jTextField_bak.setToolTipText("保存属性文件");
        jTextField_bak.setVisible(false);

        jButton_fileview2.setText("浏览");
        jButton_fileview2.setVisible(false);
        jButton_fileview2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_fileview2ActionPerformed(evt);
            }
        });

        jButton_saveProperties.setText("保存属性值文件");
        jButton_saveProperties.setEnabled(false);
        jButton_saveProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_savePropertiesActionPerformed(evt);
            }
        });

        listmousedoubleclicklistener();
        jScrollPane2.setViewportView(jList1);
        jList1.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                jTextField1.setText(jList1.getSelectedValue().toString());
            }
        });

        jTable_kv.setCellSelectionEnabled(true);
        jTable_kv.setSelectionBackground(new java.awt.Color(0, 49, 255));
        jTable_kv.setShowHorizontalLines(false);
        jTable_kv.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTable_kvMouseReleased(evt);
            }
        });
        jScrollPane4.setViewportView(jTable_kv);

        jTextField1.setText("");

        jButton1.setText("查看");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });
        jButton1.setVisible(false);

        jCheckBox1.setText("翻译");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jButton3.setText("替换");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });

        jButton_replaceAll.setText("比较新旧版本");
        jButton_replaceAll.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_replaceAllMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField_file, javax.swing.GroupLayout.DEFAULT_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField_bak, javax.swing.GroupLayout.DEFAULT_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton_fileview2)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton_fileview1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton_getJarContext)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jButton_saveProperties, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jButton_replaceAll)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3))))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField_file, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_fileview1)
                        .addComponent(jButton_getJarContext)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1))
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField_bak, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton_fileview2))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton3)
                        .addComponent(jButton_replaceAll)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(37, 37, 37)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(jButton_saveProperties))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_fileview1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_fileview1ActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc = new JFileChooser();
        jfc.setFileSelectionMode(0);//设置选择文件模式0为选择文件，1为目录,2为目录或文件
        jfc.setDialogTitle("请选择原始属性文件");//标题
        if (jTextField_file.getText().toString() != null) {
            jfc.setCurrentDirectory(new File(jTextField_file.getText()).getParentFile());
        }
        int result = jfc.showDialog(null, null);
        if (result == 0) {
            f = jfc.getSelectedFile();
            String file = f.getAbsolutePath();
            jTextField_file.setText(file);
        }
    }//GEN-LAST:event_jButton_fileview1ActionPerformed

    private void jButton_fileview2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_fileview2ActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(0);//设置选择文件模式0为选择文件，1为目录,2为目录或文件
        jfc.setDialogTitle("请选择原始属性文件");//标题
        if (jTextField_file.getText().toString() != null) {
            jfc.setCurrentDirectory(new File(jTextField_file.getText()).getParentFile());
        }
        int result = jfc.showDialog(null, null);
        if (result == 0) {
            f1 = jfc.getSelectedFile();
            String file = f1.getAbsolutePath();
            jTextField_bak.setText(file);
        }
    }//GEN-LAST:event_jButton_fileview2ActionPerformed

    private void jButton_getJarContextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_getJarContextActionPerformed
//        JarFile jarFile = null;
//        if (chooseConfigPath != null) {
//            try {
//                jarFile = new JarFile(jTextField_file.getText());
//                ZipEntry entry = jarFile.getEntry(chooseConfigPath);
//                if (entry == null) {
//                    System.out.println(chooseConfigPath + "路径所代表的文件不存在!读取失败~");
//                    // 安全起见，将 jarFile 置为 null，这样在关闭窗口的时候将不会执行收尾操作~  
//                }
//
//                //获取到inputstream了 就相当简单了  
//                InputStream is = jarFile.getInputStream(entry);
//                byte[] bytes = PropertiesControl.inputStream2byteArray(is);
//                String cfgStr = new String(bytes);
//                jTextArea_show.setText(cfgStr);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            String content[] = pc.getValues(jTextField_file.getText());
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < content.length; i++) {
//                if (content[i].equals("")) {
//                    sb.append(newline);
//                } else {
//                    if (i == content.length - 1) {
//                        sb.append(content[i]);
//                    } else {
//                        sb.append(content[i] + newline);
//                    }
//                }
//            }
//            jTextArea_show.setText(sb.toString());
//        } catch (Exception e) {
//            jTextField_file.setText("请选择原始属性文件");
//            jButton_fileview1.doClick();
//        }

        jList1.setModel(new javax.swing.AbstractListModel() {

            String[] strings = PropertiesControl.getJarFileContent(new java.io.File(jTextField_file.getText().toString()));

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        jList1.updateUI();
        jButton1.setVisible(true);
        jList1.setSelectedIndex(0);
    }//GEN-LAST:event_jButton_getJarContextActionPerformed

    private void jButton_savePropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_savePropertiesActionPerformed

        InputStream is = null, is1 = null;
        OutputStream fos = null;
        File tmpFile = null;
        try {
            //        String[] jtaValues = jTextArea_show.getText().split(newline);
//        StringBuffer sb = new StringBuffer();
            File jarFilePath = new File(jTextField_file.getText().toString());
//        //System.out.println(keys.length + "   " + jtaValues.length);
//        for (int i = 0; i < keys.length; i++) {
//            if (jtaValues[i].contains("@n@")) {
//                sb.append(keys[i] + "=" + jtaValues[i].replace("@n@", "\\n\\"));
//            } else {
//                if (keys[i].equals("")) {
//                    sb.append(newline);
//                } else if (keys[i].contains("#")) {
//                    sb.append(keys[i] + newline);
//                } else {
//                    if (i == keys.length - 1) {
//                        sb.append(keys[i] + "=" + wt.utf8ToUnicode(jtaValues[i]));
//                    } else {
//                        sb.append(keys[i] + "=" + wt.utf8ToUnicode(jtaValues[i]) + newline);
//                    }
//                }
//            }
            //System.out.println((i + 1) + "   " + keys[i] + "=" + jtaValues[i]);
//        }
            //System.out.println(jTextField_file.getText().toString() + "    " + chooseConfigPath + "     " + sb.toString().getBytes());
//        pc.write2JarFile(jarFilePath, chooseConfigPath, sb.toString().replace("{ ", "{").replace(" }", "}").getBytes());
//        try {
//            File f = new File(jarFilePath.getParent(), "已翻译文件路径记录.txt");
//            if (!f.exists()) {
//                f.createNewFile();
//            }
//            FileOutputStream out = new FileOutputStream(f, true);//第二参数为true时，从文件结尾添加
//            out.write((newline + chooseConfigPath).getBytes());
//            out.flush();
//            out.close();
//        } catch (IOException ex) {
//            Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        jTextField_file.setText(jTextField_file.getText().toString().substring(0, jTextField_file.getText().toString().length() - 4) + "_temp.jar");
//        jButton_getJarContext.doClick();
//        jList1.setSelectedIndex(index);
//        pc.put(jTextField_bak.getText(), pc.getKeys(jTextField_file.getText()), content1);

            tmpFile = File.createTempFile("tmp", ".properties");
            Properties prop = new Properties();
            is = new FileInputStream(tmpFile);
            prop.load(is);
            fos = new FileOutputStream(tmpFile);
            int count = keyList.size();
            for (int i = 0; i < count; i++) {
                prop.put(keyList.get(i), wt.utf8ToUnicode(valueNewList.get(i)));
            }
            prop.store(fos, "Update:" + chooseConfigPath);
            is1 = new FileInputStream(tmpFile);
            pc2.write2JarFile(jarFilePath, chooseConfigPath, pc2.inputStream2byteArray(is1));
            jButton_saveProperties.setEnabled(false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
                is1.close();
                fos.close();
//                tmpFile.deleteOnExit();
            } catch (IOException ex) {
                Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jButton_savePropertiesActionPerformed

    private void saveProperties(String jarFilePathStr, List<String> kList, List<String> vList, String choosePropertiesPath) {

        InputStream is = null, is1 = null;
        OutputStream fos = null;
        File tmpFile = null;
        try {
            File jarFilePath = new File(jarFilePathStr);
            tmpFile = File.createTempFile("tmp", ".properties");
            Properties prop = new Properties();
            is = new FileInputStream(tmpFile);
            prop.load(is);
            fos = new FileOutputStream(tmpFile);
            int count = kList.size();
            for (int i = 0; i < count; i++) {
                prop.put(kList.get(i), wt.utf8ToUnicode(vList.get(i)));
            }
            prop.store(fos, "Update:" + choosePropertiesPath);
            is1 = new FileInputStream(tmpFile);
            pc2.write2JarFile(jarFilePath, choosePropertiesPath, pc2.inputStream2byteArray(is1));
            jButton_saveProperties.setEnabled(false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
                is1.close();
                fos.close();
                tmpFile.deleteOnExit();
            } catch (IOException ex) {
                Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        System.out.println(jTextField1.getText().toString());
        if (jTextField1.getText().toString() != null || !jTextField1.getText().toString().equals("")) {
            jDialog1.show();
            jButton2.setText("修                             改");
            jTextArea1.setEditable(false);
            jDialog1.setTitle("预览属性文件（不可编辑）");
            final MouseEvent e = evt;
            new Thread() {

                @Override
                public void run() {
                    chooseConfigPath = jTextField1.getText().toString();
                    JarFile jarFile = null;
                    try {
                        jarFile = new JarFile(jTextField_file.getText().toString());
                        int index = jList1.locationToIndex(e.getPoint());

                        ZipEntry entry = jarFile.getEntry(chooseConfigPath);
                        if (entry == null) {
                            System.out.println(chooseConfigPath + "路径所代表的文件不存在!读取失败~");
                            // 安全起见，将 jarFile 置为 null，这样在关闭窗口的时候将不会执行收尾操作~  
                        }

                        //获取到inputstream了 就相当简单了  
                        InputStream is = jarFile.getInputStream(entry);
                        byte[] bytes = pc.inputStream2byteArray(is);
                        String cfgStr = new String(bytes);
                        String[] values = pc.getBytesValue(bytes);
                        String temp = "";
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < values.length; i++) {
                            sb.append(wt.UnicodeToGBK(values[i]) + newline);
                        }
                        jDialog1.setTitle("预览属性文件（不可编辑）    " + values.length + "行");
                        jTextArea1.setText(sb.toString());
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }.start();
        }
    }//GEN-LAST:event_jButton1MouseClicked

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        if (jTextArea1.isEditable()) {
            String[] kv = jTextArea1.getText().split("\n");
            StringBuffer sb = new StringBuffer();
            File jarFilePath = new File(jTextField_file.getText().toString());
            //System.out.println(keys.length + "   " + jtaValues.length);
            for (int i = 0; i < kv.length; i++) {
                if (i == 0) {
                    sb.append(wt.utf8ToUnicode(kv[i]));
                } else {
                    sb.append(newline + wt.utf8ToUnicode(kv[i]));
                }
                //System.out.println((i + 1) + "   " + keys[i] + "=" + jtaValues[i]);
            }
            //System.out.println(jTextField_file.getText().toString() + "    " + chooseConfigPath + "     " + sb.toString().getBytes());
            pc.write2JarFile(jarFilePath, chooseConfigPath, sb.toString().replace("{ ", "{").replace(" }", "}").getBytes());
            jDialog1.hide();
            jTextField_file.setText(jTextField_file.getText().toString().substring(0, jTextField_file.getText().toString().length() - 4) + "_temp.jar");
            jButton_getJarContext.doClick();
            jList1.setSelectedIndex(index);
            jButton2.setText("修                             改");
            jTextArea1.setEditable(false);
        } else {
            jButton2.setText("保                             存");
            jTextArea1.setEditable(true);
            jDialog1.setTitle(jDialog1.getTitle().replace("不可编辑", "可编辑"));
        }
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        jDialog2.show();
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
//        jTextArea_show.setText(jTextArea_show.getText().replaceAll(jTextField2.getText().toString(), jTextField3.getText().toString()));
        jDialog2.hide();
    }//GEN-LAST:event_jButton4MouseClicked

    private void jButton5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseClicked
        jDialog2.hide();
    }//GEN-LAST:event_jButton5MouseClicked

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jMenuItem_translateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_translateActionPerformed
        // TODO add your handling code here:
        System.out.println("点击翻译菜单1");
        List<String> list = new ArrayList<String>();
        list.clear();
        list.add(valueList.get(rightClickIndex));
        translate(list, rightClickIndex);
    }//GEN-LAST:event_jMenuItem_translateActionPerformed

    private void jTable_kvMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable_kvMouseReleased
        // TODO add your handling code here:
        if (evt.getButton() == MouseEvent.BUTTON3) {
            rightClickIndex = jTable_kv.rowAtPoint(evt.getPoint());
            jPopupMenu1.show(jTable_kv, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_jTable_kvMouseReleased

    private void jButton_replaceAllMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_replaceAllMouseClicked
        //比较新旧版本
        jDialog_ra.show();
    }//GEN-LAST:event_jButton_replaceAllMouseClicked

    private void jButton_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_newActionPerformed
        if (!isReplaceStart) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(0);//设置选择文件模式0为选择文件，1为目录,2为目录或文件
            jfc.setDialogTitle("请选择新版属性文件");//标题
            if (jTextField_newfile.getText().toString() != null) {
                jfc.setCurrentDirectory(new File(jTextField_newfile.getText()).getParentFile());
            }
            int result = jfc.showDialog(null, null);
            if (result == 0) {
                fnew = jfc.getSelectedFile();
                String file = fnew.getAbsolutePath();
                jTextField_newfile.setText(file);
                newfiles = PropertiesControl.getJarFileContent(new java.io.File(jTextField_newfile.getText().toString()));
                System.out.println("newfiles count : " + newfiles.length);

            }
        } else {
            show("替换正在进行中，请稍候。。。", "提示");
        }
    }//GEN-LAST:event_jButton_newActionPerformed

    private void jButton_oldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_oldActionPerformed
        if (!isReplaceStart) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(0);//设置选择文件模式0为选择文件，1为目录,2为目录或文件
            jfc.setDialogTitle("请选择旧版属性文件");//标题
            if (jTextField_oldfile.getText().toString() != null) {
                jfc.setCurrentDirectory(new File(jTextField_oldfile.getText()).getParentFile());
            }
            int result = jfc.showDialog(null, null);
            if (result == 0) {
                fold = jfc.getSelectedFile();
                String file = fold.getAbsolutePath();
                jTextField_oldfile.setText(file);
                oldfiles = PropertiesControl.getJarFileContent(new java.io.File(jTextField_oldfile.getText().toString()));
                System.out.println("oldfiles count : " + oldfiles.length);
            }
        } else {
            show("替换正在进行中，请稍候。。。", "提示");
        }
    }//GEN-LAST:event_jButton_oldActionPerformed

    private void jButton_compareMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_compareMouseClicked
        if (!isReplaceStart) {
            isReplaceStart = true;
            new Thread(new Runnable() {

                @Override
                public void run() {

                    JarFile newjarFile = null, oldjarFile = null;
                    try {
                        newjarFile = new JarFile(jTextField_newfile.getText().toString());
                        oldjarFile = new JarFile(jTextField_oldfile.getText().toString());
                        for (int i = 0; i < newfiles.length; i++) {
                            //System.out.println("Newfile:" + newfiles[i] + "  Oldfile:" + oldfiles[i]);
                            ZipEntry entrynew = newjarFile.getEntry(newfiles[i]);
                            ZipEntry entryold;

                            if (arryContains(oldfiles, newfiles[i])) {
                                entryold = oldjarFile.getEntry(newfiles[i]);
                            } else {
                                String listtemp = "";
                                if (i == 0) {
                                    listtemp = newfiles[i];
                                } else {
                                    listtemp = newline + "新版中增加的属性文件" + newfiles[i];
                                }
                                System.out.println(i + "--新版中增加的属性文件-->" + listtemp);
                                jTextArea_list.append(listtemp);
                                continue;
                            }
                            InputStream isnew = newjarFile.getInputStream(entrynew);
                            InputStream isold = oldjarFile.getInputStream(entryold);
                            PropertiesControl2 pc2new = new PropertiesControl2(isnew);
                            PropertiesControl2 pc2old = new PropertiesControl2(isold);
                            Map<String, String> kvMapNew = pc2new.getKeysAndValuesMap();
                            Map<String, String> kvMapOld = pc2old.getKeysAndValuesMap();
                            Iterator<Map.Entry<String, String>> itold = kvMapOld.entrySet().iterator();
                            while (itold.hasNext()) {
                                Map.Entry<String, String> entry = itold.next();
                                if (kvMapNew.containsKey(entry.getKey())) {
                                    kvMapNew.put(entry.getKey(), entry.getValue());
//                                System.out.printf("key:%s==>value:%s", entry.getKey(), entry.getValue());
//                                System.out.println();
                                }
                            }
                            Iterator<Map.Entry<String, String>> itnew = kvMapNew.entrySet().iterator();
                            InputStream is = null, is1 = null;
                            OutputStream fos = null;
                            File tmpFile = null;
                            try {
                                File jarFilePath = new File(jTextField_newfile.getText().toString());

                                tmpFile = File.createTempFile("tmp", ".properties");
                                Properties prop = new Properties();
                                is = new FileInputStream(tmpFile);
                                prop.load(is);
                                fos = new FileOutputStream(tmpFile);
//                            System.out.println("============以下为新数据============");
                                while (itnew.hasNext()) {
                                    Map.Entry<String, String> entry = itnew.next();
                                    prop.put(entry.getKey(), wt.utf8ToUnicode(entry.getValue()));
//                                    prop.put(entry.getKey(), entry.getValue());
//                                System.out.printf("key:%s==>value:%s", entry.getKey(), entry.getValue());
//                                System.out.println();
//                                System.out.printf("new key:%s==>value:%s", entry.getKey(), prop.getProperty(entry.getKey()));
//                                System.out.println();
                                }
                                prop.store(fos, "Update:" + newfiles[i]);
                                is1 = new FileInputStream(tmpFile);
                                if (i == 0) {
                                    pc2new.write2JarFile(jarFilePath, "temp" + jarFilePath.getName(), newfiles[i], pc2new.inputStream2byteArray(is1));
                                } else {
                                    pc2new.write2JarFile(new File(jarFilePath.getParent(), "temp" + jarFilePath.getName() + ".jar"), null, newfiles[i], pc2new.inputStream2byteArray(is1), 2);
                                }
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
                            } finally {
                                try {
                                    is.close();
                                    is1.close();
                                    fos.close();
//                tmpFile.deleteOnExit();
                                } catch (IOException ex) {
                                    Logger.getLogger(TAS.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
//                        byte[] bytesnew = pc.inputStream2byteArray(isnew);
//                        byte[] bytesold = pc.inputStream2byteArray(isold);
//                        newvalues = pc.getBytesValue(bytesnew);
//                        newkeys = new String[newvalues.length];
//                        oldvalues = pc.getBytesValue(bytesold);
//                        oldkeys = new String[oldvalues.length];
//                        for (int j = 0; j < newvalues.length; j++) {
//                            if (!newvalues[j].contains("=")) {
//                                newkeys[j] = newvalues[j];
//                            } else {
//                                newkeys[j] = newvalues[j].substring(0, newvalues[j].indexOf("="));
//                            }
//                            //System.out.println(i+"  new :"+newkeys[j]);
//                        }
//                        //System.out.println("test1111");
//                        for (int j = 0; j < oldvalues.length; j++) {
//                            if (!oldvalues[j].contains("=")) {
//                                oldkeys[j] = oldvalues[j];
//                            } else {
//                                oldkeys[j] = oldvalues[j].substring(0, oldvalues[j].indexOf("="));
//                            }
//                            System.out.println(j + "  old :" + oldvalues[j]);
//                        }
//                        //System.out.println("test11112222");
//                        int same = 0;
//                        StringBuffer sb = new StringBuffer();
//                        if (newvalues.length > oldvalues.length) {
//                            //System.out.println("test1");
//                            valuestemp = new String[newvalues.length];
//                            for (int j = 0; j < newvalues.length; j++) {
//                                for (int k = 0; k < oldvalues.length; k++) {
//                                    if (oldvalues[k].equals(newvalues[j])) {
//                                        valuestemp[j] = oldvalues[k];
//                                        same++;
//                                    }
//                                }
//                                if (same == 0) {
//                                    valuestemp[j] = newvalues[j];
//                                }
//                                System.out.println(j + "   >>>" + valuestemp[j]);
//                            }
//                            for (int k = 0; k < newvalues.length; k++) {
//                                if (k == 0) {
//                                    sb.append(valuestemp[k]);
//                                } else {
//                                    sb.append(newline + valuestemp[k]);
//                                }
//                            }
//                        } else if (newvalues.length == oldvalues.length) {
//                            for (int k = 0; k < newvalues.length; k++) {
//                                if (k == 0) {
//                                    sb.append(oldvalues[k]);
//                                } else {
//                                    sb.append(newline + oldvalues[k]);
//                                }
//                            }
//                        } else {
//                            // System.out.println("test2");
//                            valuestemp = new String[oldvalues.length];
//                            for (int j = 0; j < oldvalues.length; j++) {
//                                for (int k = 0; k < newvalues.length; k++) {
//                                    //System.out.println("test3");
//                                    if (oldvalues[j].equals(newvalues[k])) {
//                                        valuestemp[j] = oldvalues[j];
//                                        same++;
//                                    }
//                                }
//                                if (same == 0) {
//                                    valuestemp[j] = oldvalues[j];
//                                }
//                                System.out.println(j + "---->" + valuestemp[j]);
//                            }
//                            for (int k = 0; k < oldvalues.length; k++) {
//                                if (k == 0) {
//                                    sb.append(valuestemp[k]);
//                                } else {
//                                    sb.append(newline + valuestemp[k]);
//                                }
//                            }
//                            //System.out.println(sb.toString());
//                        }
//                        pc.write2JarFile(new File(jTextField_oldfile.getText().toString()), newfiles[i], sb.toString().getBytes());
                            String listtemp = "";
                            if (i == 0) {
                                listtemp = newfiles[i];
                            } else {
                                listtemp = newline + newfiles[i];
                            }
                            System.out.println(i + "---->" + listtemp);
                            jTextArea_list.append(listtemp);
                            bar_compare.setValue(bar_compare.getMaximum());
                            Thread.sleep(1000);
                        }

                    } catch (Exception e) {
                    }
                    for (String sold : oldfiles) {
                        if (arryContains(newfiles, sold)) {

                        } else {
                            String listtemp = "";
                            listtemp = newline + "新版中删除的属性文件" + sold;
                            System.out.println("--新版中删除的属性文件-->" + listtemp);
                            jTextArea_list.append(listtemp);
                        }
                    }
                    jTextArea_list.append(newline + "比较完毕！！！" + newline + newline + newline + newline + newline);
                    bar_compare.setValue(bar_compare.getMaximum());
                    isReplaceStart = false;
                }
            }).start();
        } else {
            show("替换正在进行中，请稍候。。。", "提示");
        }
    }//GEN-LAST:event_jButton_compareMouseClicked

    /**
     * 判断某个字符串是否存在于数组中
     *
     * @param stringArray 原数组
     * @param source 查找的字符串
     * @return 是否找到
     */
    private static boolean arryContains(String[] stringArray, String source) {
        if (stringArray != null) {
            for (String str : stringArray) {
                if (str.contains(source)) {
                    return true;
                }
            }
        }
        return false;
    }

//        if (evt.getButton() == MouseEvent.BUTTON3) {
//            rightClickIndex = jTable_kv.rowAtPoint(evt.getPoint());
//            jPopupMenu1.show(jTable_kv, evt.getX(), evt.getY());
//        }
    public void listmousedoubleclicklistener() {
        jList1.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2 & e.getButton() == MouseEvent.BUTTON1) {
                    new Thread() {

                        @Override
                        public void run() {
                            int sleeptime = 0;
                            chooseConfigPath = jList1.getSelectedValue().toString();
                            index = jList1.locationToIndex(e.getPoint());
                            JarFile jarFile = null;
                            try {
                                jarFile = new JarFile(jTextField_file.getText().toString());
                                int index = jList1.locationToIndex(e.getPoint());

                                ZipEntry entry = jarFile.getEntry(chooseConfigPath);
                                if (entry == null) {
                                    System.out.println(chooseConfigPath + "路径所代表的文件不存在!读取失败~");
                                    // 安全起见，将 jarFile 置为 null，这样在关闭窗口的时候将不会执行收尾操作~  
                                }

                                //获取到inputstream了 就相当简单了  
                                InputStream is = jarFile.getInputStream(entry);
                                if (pc2 != null) {
                                    pc2 = null;
                                }
                                pc2 = new PropertiesControl2(is);
                                if (keyList.size() > 0) {
                                    keyList.clear();
                                }
                                keyList = pc2.getKeyList();
                                if (valueList.size() > 0) {
                                    valueList.clear();
                                }
                                valueList = pc2.getValueList();

                                if (valueNewList.size() > 0) {
                                    valueNewList.clear();
                                }
                                for (String s : valueList) {
                                    valueNewList.add(s);
                                }
                                jProgressBar1.setMaximum(pc2.getCount());
                                jTable_kv.setRowSelectionInterval(0, 0);

                                jLabel1.setText(pc2.getCount() + "");

                                ((AbstractTableModel) jTable_kv.getModel()).fireTableDataChanged(); //更新数据
                                jTable_kv.setModel(dataModel);
                                if (jCheckBox1.isSelected()) {
                                    translate(valueList, 0);
                                } else {
                                    jTable_kv.updateUI();
                                }

//                                bar1.setValue(bar1.getMaximum());
//                                bar.setValue(bar1.getValue());
                            } catch (IOException ioe) {
                                ioe.printStackTrace();
                            }
//                            jButton_saveProperties.setEnabled(true);
//                            if (jCheckBox1.isSelected()) {
//                                jLabel1.setText(jLabel1.getText().toString() + "  翻译完成");
//                            } else {
//                                jLabel1.setText(jLabel1.getText().toString());
//                            }
                        }
                    }.start();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    private void show(String msg, String title) {
        show(msg, title, JOptionPane.CLOSED_OPTION, JOptionPane.WARNING_MESSAGE);
    }

    private void show(String msg, String title, int optionType, int msgType) {
        int option = JOptionPane.showConfirmDialog(null, msg, title, optionType, msgType, null);
//        switch (option) {
//            case JOptionPane.YES_NO_OPTION: {
//                saveAsFile();
//                break;
//            }
//            case JOptionPane.NO_OPTION:
//                System.exit(0);
//
//        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TAS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TAS().setVisible(true);
            }
        });
    }

    /**
     * @author Bruce Yang 拖拽监听~
     */
    class MyDropTargetListener extends DropTargetAdapter {

        private TAS tas;

        public MyDropTargetListener(TAS tas) {
            this.tas = tas;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void drop(DropTargetDropEvent event) {
            if (event.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

                DataFlavor df = DataFlavor.javaFileListFlavor;
                List<File> list = null;
                try {
                    list = (List<File>) (event.getTransferable().getTransferData(df));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Iterator<File> iterator = list.iterator();
                while (iterator.hasNext()) {
                    File file = iterator.next();
                    if (file.exists() && file.isFile()) {
                        String filePath = file.getAbsolutePath();
                        if (filePath == null || filePath.equals("")) {
                            System.out.println("文件名为 null 或为 \"\"~");
                            break;
                        }
                        if (!filePath.endsWith(".jar")) {
                            String str = "此工具专门为jar包设计，不通用!! 请注意!!";
                            JOptionPane.showMessageDialog(null, str);
                            break;
                        }
                        tas.jTextField_file.setText(filePath);
                        System.out.println("jarFilePath=" + filePath);

                    }
                    // 一次只能处理一个，要避免处理多个的情况，因此 break 跳出~  
                    break;
                }
                event.dropComplete(true);
            } else {
                event.rejectDrop();
            }
        }
    }

    TableModel dataModel = new AbstractTableModel() {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public int getColumnCount() {
            return ColumnNames.length;
        }

        public int getRowCount() {
            return keyList.size();
        }

        @Override
        public String getColumnName(int col) {
            String s = null;
            if (col == 0) {
                s = ColumnNames[0];
            } else if (col == 1) {
                s = ColumnNames[1];
            } else if (col == 2) {
                s = ColumnNames[2];
            } else if (col == 3) {
                s = ColumnNames[3];
            }
            return s;
        }

        public Object getValueAt(int row, int col) {
            String s = null;
            if (col == 0) {
                s = row + 1 + "";
            } else if (col == 1) {
                s = keyList.get(row);
            } else if (col == 2) {
                s = valueList.get(row);
            } else if (col == 3) {
                s = valueNewList.get(row);
            }
            cmodel.getColumn(col).setCellRenderer(multiLineHeaderRenderer);
            return s;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            super.setValueAt(aValue, rowIndex, columnIndex); //To change body of generated methods, choose Tools | Templates.
            if (!aValue.toString().equals(valueNewList.get(rowIndex))) {
                valueNewList.remove(rowIndex);
                valueNewList.add(rowIndex, aValue.toString());
                fireTableCellUpdated(rowIndex, columnIndex);
                jButton_saveProperties.setEnabled(true);
            } else {
                System.out.println("未编辑");
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 3) {
                return true;
            }
            return false; //To change body of generated methods, choose Tools | Templates.
        }

    };
    private java.io.File f, f1, fnew, fold;
    PropertiesControl pc = new PropertiesControl();
    PropertiesControl2 pc2;
    //private static String newline = System.getProperty("line.separator");
    private static String newline = "\r\n";
    private static String chooseConfigPath = null;
    String[] keys, newfiles, newkeys, newvalues, oldfiles, oldkeys, oldvalues, valuestemp;
    JScrollBar bar_compare;
    HttpDownloader hd = new HttpDownloader();
    WordsTransfer wt = new WordsTransfer();
    MSDN_Translate msdnt = new MSDN_Translate();
    Post post = new Post();
    String translate_site_baidu = "http://viphp.sinaapp.com/baidu/translate/translate.php?origin=%s";
    int index = 0;
    TableColumn tc_id, tc_key, tc_value_origin, tc_value_new;
    TableCellEditor tce_value_new;
    String[] ColumnNames = {"序号", "键名", "键值", "翻译"};
    List<String> keyList = new ArrayList<String>();
    List<String> valueList = new ArrayList<String>();
    List<String> valueNewList = new ArrayList<String>();
    MultiLineRowRenderer multiLineHeaderRenderer = new MultiLineRowRenderer();
    TableColumnModel cmodel = null;
    private int rightClickIndex = 0;
    private boolean isReplaceStart = false;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton_compare;
    private javax.swing.JButton jButton_fileview1;
    private javax.swing.JButton jButton_fileview2;
    private javax.swing.JButton jButton_getJarContext;
    private javax.swing.JButton jButton_new;
    private javax.swing.JButton jButton_old;
    private javax.swing.JButton jButton_replaceAll;
    private javax.swing.JButton jButton_saveProperties;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JDialog jDialog2;
    private javax.swing.JDialog jDialog_ra;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList jList1;
    private javax.swing.JMenuItem jMenuItem_translate;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable_kv;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea_list;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField_bak;
    private javax.swing.JTextField jTextField_file;
    private javax.swing.JTextField jTextField_newfile;
    private javax.swing.JTextField jTextField_oldfile;
    // End of variables declaration//GEN-END:variables
}
