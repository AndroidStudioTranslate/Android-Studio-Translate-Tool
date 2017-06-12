/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package translation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 *
 * @author swtf
 */
public class PropertiesControl2 {

    private Properties props = new Properties();
    private boolean isNull = false;

    private List<String> keyList = new ArrayList<String>();
    private List<String> valueList = new ArrayList<String>();

    private Map<String, String> kvMap = new HashMap<String, String>();

    public PropertiesControl2(String filepath) {
        try {
            InputStream is = new FileInputStream(new File(filepath));
            if (null != is) {
                props.load(is);
                is.close();
                setKeysAndValues();
            }
        } catch (FileNotFoundException e) {
            System.err.println("文件" + filepath + "不存在");
            isNull = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PropertiesControl2(InputStream is) {
        try {
            if (null != is) {
                props.load(is);
                is.close();
                setKeysAndValues();
            }
        } catch (FileNotFoundException e) {
            isNull = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getStringValue(String key) {
        if (!isNull) {
            return props.getProperty(key);
        } else {
            return null;
        }
    }

    public boolean getBooleanValue(String key) {
        if (!isNull) {
            String value = props.getProperty(key);
            if (value.equals("true")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int getCount() {
        return props.size();
    }

    /**
     * @param properties
     */
    private void setKeysAndValues() {
        Iterator<Entry<Object, Object>> it = props.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Object, Object> entry = it.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            keyList.add(key);
            valueList.add(value);
            kvMap.put(key, value);
        }
    }

    public List<String> getKeyList() {
        return keyList;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public Map<String, String> getKeysAndValuesMap() {
        return kvMap;
    }

    public Iterator<Entry<Object, Object>> getIterator() {
        return props.entrySet().iterator();
    }

    public void save(List<String> kList, List<String> vList) {
        int count = getCount();
        for (int i = 0; i < count; i++) {
            props.setProperty(kList.get(i), vList.get(i));
        }
    }

    /**
     * 写入jar文件的话会将 jar文件原来的内容统统抹掉!!切记!!~
     *
     * @param original 源jar包路径
     * @param configPath jar包内属性文件路径
     * @param values 属性文件内容
     */
    public void write2JarFile(File original, String configPath, byte[] values) {
        write2JarFile(original, null, configPath, values, 0);
    }

    /**
     * 写入jar文件的话会将 jar文件原来的内容统统抹掉!!切记!!~
     *
     * @param original 源jar包路径
     * @param tempFileName jar包名称
     * @param configPath jar包内属性文件路径
     * @param values 属性文件内容
     */
    public void write2JarFile(File original, String tempFileName, String configPath, byte[] values) {
        write2JarFile(original, tempFileName, configPath, values, 0);
    }

    /**
     * 写入jar文件的话会将 jar文件原来的内容统统抹掉!!切记!!~
     *
     * @param original 源jar包路径
     * @param tempFileName jar包名称
     * @param configPath jar包内属性文件路径
     * @param values 属性文件内容
     * @param ctrl 0为另存为以"'原文件名'+'_temp.jar'"命名的文件，1为删除源文件，2为覆盖源文件。
     */
    public void write2JarFile(File original, String tempFileName, String configPath, byte[] values, int ctrl) {
        String originalPath = original.getAbsolutePath();
        /**
         * 创建一个临时文件来做暂存，待一切操作完毕之后会将该文件重命名为原文件的名称(原文件会被删除掉)~
         */
        String tempPath = null;
        if (tempFileName == null) {
            tempPath = originalPath.substring(0, originalPath.lastIndexOf(".")) + "_temp" + originalPath.substring(originalPath.lastIndexOf("."));
        } else {
            tempPath = original.getParent() + File.separator + tempFileName;
        }

        System.out.println(tempPath);
        JarFile originalJar = null;
        try {
            originalJar = new JarFile(originalPath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        List<JarEntry> lists = new LinkedList<JarEntry>();
        for (Enumeration<JarEntry> entrys = originalJar.entries(); entrys.hasMoreElements();) {
            JarEntry jarEntry = entrys.nextElement();
//            System.out.println(jarEntry.getName());  
            lists.add(jarEntry);
        }

        // 定义一个 jaroutputstream 流  
        File handled = new File(tempPath);
        JarOutputStream jos = null;
        try {
            FileOutputStream fos = new FileOutputStream(handled);
            jos = new JarOutputStream(fos);

            /**
             * 将源文件中的内容复制过来~ 可以利用循环将一个文件夹中的文件都写入jar包中 其实很简单
             */
            for (JarEntry je : lists) {
                // jar 中的每一个文件夹 每一个文件 都是一个 jarEntry  
                JarEntry newEntry = new JarEntry(je.getName());

//              newEntry.setComment(je.getComment());  
//              newEntry.setCompressedSize(je.getCompressedSize());  
//              newEntry.setCrc(je.getCrc());  
//              newEntry.setExtra(je.getExtra());  
//              newEntry.setMethod(je.getMethod());  
//              newEntry.setTime(je.getTime());  
//              System.out.println(je.getAttributes());  
                /**
                 * 这句代码有问题，会导致将jar包重命名为zip包之后无法解压缩~
                 */
//              newEntry.setSize(je.getSize());  
                // 表示将该entry写入jar文件中 也就是创建该文件夹和文件  
                jos.putNextEntry(newEntry);
                //System.out.println(je.getName());
                /**
                 * 如果当前已经处理到属性文件了，那么将在 JTextArea 中编辑过的文本写入到该属性文件~
                 */
                if (je.getName().equals(configPath)) {
                    jos.write(values);
                    continue;
                }

                InputStream is = originalJar.getInputStream(je);
                byte[] bytes = inputStream2byteArray(is);
                is.close();

                // 然后就是往entry中的jj.txt文件中写入内容  
                jos.write(bytes);
            }
            // 最后不能忘记关闭流  
            jos.close();
            fos.close();

            switch (ctrl) {
                case 0:

                    break;
                case 1:
                    new File(originalPath).delete();
                    break;
                case 2:
                    /**
                     * 删除原始文件，将新生成的文件重命名为原始文件的名称~
                     */
                    System.out.println(originalPath);
//                    handled.renameTo(new File(originalPath));
                    copyFile(tempPath,originalPath);
                    handled.delete();
                    break;
                default:

                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * InputStream 转 byte[]~
     *
     * @param is
     * @return
     */
    public byte[] inputStream2byteArray(InputStream is) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        try {
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
    
    private static void copyFile(String oldPath,
        String newPath)
        throws Exception {

        int bytesum = 0;
        int byteread = 0;
        FileInputStream inPutStream = null;
        FileOutputStream outPutStream = null;

        try {

            // oldPath的文件copy到新的路径下，如果在新路径下有同名文件，则覆盖源文件
            inPutStream = new FileInputStream(oldPath);

            outPutStream = new FileOutputStream(newPath);
            byte[] buffer = new byte[4096];

            while ((byteread = inPutStream.read(buffer)) != -1) {

                // byte ファイル
                bytesum += byteread;
                outPutStream.write(buffer, 0, byteread);
            }
        } finally {

            // inPutStreamを关闭
            if (inPutStream != null) {
                inPutStream.close();
                inPutStream = null;
            }

            // inPutStream关闭
            if (outPutStream != null) {
                outPutStream.close();
                outPutStream = null;
            }

        }

    }
}
