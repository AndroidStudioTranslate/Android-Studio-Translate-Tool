package translation.view;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Java右键菜单实现文本组件内容的的复制、粘贴、剪切功能
 *
 * @author 五斗米 <如转载请保留作者和出处> @blog http://blog.csdn.net/mq612
 */
public class JTA extends JTextArea implements MouseListener {

    private static final long serialVersionUID = -2308615404205560110L;
    private JPopupMenu pop = null; // 弹出菜单
    private JMenuItem copy = null, paste = null, cut = null, delete = null; // 四个功能菜单

    public JTA() {
        super();
        init();
    }

    private void init() {
        this.addMouseListener(this);
        pop = new JPopupMenu();
        pop.add(copy = new JMenuItem("复制"));
        pop.add(paste = new JMenuItem("粘贴"));
        pop.add(cut = new JMenuItem("剪切"));
        pop.add(delete = new JMenuItem("删除"));
        copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
        paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));
        cut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));
        delete.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        copy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        paste.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        cut.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        delete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    Robot robot = new Robot();
                    robot.keyPress(KeyEvent.VK_DELETE);
                    robot.keyRelease(KeyEvent.VK_DELETE);
                } catch (AWTException ex) {
                    Logger.getLogger(JTA.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        this.add(pop);
    }

    /**
     * 菜单动作
     *
     * @param e
     */
    public void action(ActionEvent e) {
        String str = e.getActionCommand();
        if (str.equals(copy.getText())) { // 复制
            this.copy();
        } else if (str.equals(paste.getText())) { // 粘贴
            this.paste();
        } else if (str.equals(cut.getText())) { // 剪切
            this.cut();
        }
    }

    public JPopupMenu getPop() {
        return pop;
    }

    public void setPop(JPopupMenu pop) {
        this.pop = pop;
    }

    /**
     * 剪切板中是否有文本数据可供粘贴
     *
     * @return true为有文本数据
     */
    public boolean isClipboardString() {
        boolean b = false;
        Clipboard clipboard = this.getToolkit().getSystemClipboard();
        Transferable content = clipboard.getContents(this);
        try {
            if (content.getTransferData(DataFlavor.stringFlavor) instanceof String) {
                b = true;
            }
        } catch (Exception e) {
        }
        return b;
    }

    /**
     * 文本组件中是否具备复制的条件
     *
     * @return true为具备
     */
    public boolean isCanCopy() {
        boolean b = false;
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        if (start != end) {
            b = true;
        }
        return b;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3) {
            copy.setEnabled(isCanCopy());
            paste.setEnabled(isClipboardString());
            cut.setEnabled(isCanCopy());
            pop.show(this, e.getX(), e.getY());
        }
    }

    public void mouseReleased(MouseEvent e) {
    }
}
