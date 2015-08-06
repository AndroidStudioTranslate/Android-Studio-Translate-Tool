/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package translation;

import java.io.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 *
 * @author v
 */
public class PropertiesControl {

    private static String newline = System.getProperty("line.separator");

    public static void put(String newfilepath, String[] content,
            String[] content1) {
        try {
            PrintWriter pw = new PrintWriter(new File(newfilepath));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < content.length; i++) {
                if (content[i].equals("")) {
                    sb.append(newline);
                } else {
                    if (i == content.length - 1) {
                        sb.append(content[i] + "=" + content1[i]);
                    } else {
                        sb.append(content[i] + "=" + content1[i] + newline);
                    }
                }
            }
            pw.write(sb.toString());
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getValues(String filepath) {
        String[] kav = getKeysAndValues(filepath);
        String[] values = new String[kav.length];
        for (int i = 0; i < kav.length; i++) {
            values[i] = kav[i].substring(kav[i].indexOf("=") + 1);
        }
        return values;
    }

    public static String[] getKeys(String filepath) {
        String[] kav = getKeysAndValues(filepath);
        String[] keys = new String[kav.length];
        for (int i = 0; i < kav.length; i++) {
            if (kav[i].indexOf("=") == -1) {
                keys[i] = "";
            } else {
                keys[i] = kav[i].substring(0, kav[i].indexOf("="));
            }
        }
        return keys;
    }

    public static String[] getKeysAndValues(String filepath) {
        String[] keysAndValues = null;
        try {
            int count = getFileLineNumber(filepath);

            BufferedReader br = getBR(filepath);

            String line1 = br.readLine();
            keysAndValues = new String[count];
            int count1 = 0;
            while (null != line1) {
                keysAndValues[count1] = line1;
                line1 = br.readLine();
                count1++;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keysAndValues;
    }

    public static int getFileLineNumber(String filepath) {
        int count = 0;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(new File(filepath))));
            String line = br.readLine();
            while (null != line) {
                count++;
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

    public static BufferedReader getBR(String filepath) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(
                    new File(filepath))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return br;
    }

    /**
     * 写入jar文件的话会将 jar文件原来的内容统统抹掉!!切记!!~
     *
     * @param original 源jar包路径
     * @param configPath jar包内属性文件路径
     * @param values 属性文件内容
     * @param ctrl 0为另存为以"'原文件名'+'_temp.jar'"命名的文件，1为删除源文件，2为覆盖源文件。
     */
    public static void write2JarFile(File original, String configPath, byte[] values) {
        String originalPath = original.getAbsolutePath();
        /**
         * 创建一个临时文件来做暂存，待一切操作完毕之后会将该文件重命名为原文件的名称(原文件会被删除掉)~
         */
        String tempPath = originalPath.substring(0, originalPath.length() - 4) + "_temp.jar";
//        System.out.println(tempPath);

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

            /**
             * 删除原始文件，将新生成的文件重命名为原始文件的名称~
             */
            handled.renameTo(new File(originalPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getJarFileContent(File original) {
        JarFile originalJar = null;
        String[] jarFileContent = null;
        try {
            originalJar = new JarFile(original.getAbsoluteFile());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        List<JarEntry> lists = new LinkedList<JarEntry>();
        for (Enumeration<JarEntry> entrys = originalJar.entries(); entrys.hasMoreElements();) {
            JarEntry jarEntry = entrys.nextElement();
//            System.out.println(jarEntry.getName());  
            lists.add(jarEntry);
        }

        try {
            String[] temppath = new String[lists.size()];
            int i = 0;
            /**
             * 将源文件中的内容复制过来~ 可以利用循环将一个文件夹中的文件都写入jar包中 其实很简单
             */
            for (JarEntry je : lists) {
                if (je.getName().toString().endsWith(".properties")) {
                    temppath[i] = je.getName();
                    i++;
                }
            }
            jarFileContent = new String[i];
            for (int tempi = 0; tempi < i; tempi++) {
                jarFileContent[tempi] = temppath[tempi];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jarFileContent;
    }

    /**
     * InputStream 转 byte[]~
     *
     * @param is
     * @return
     */
    public static byte[] inputStream2byteArray(InputStream is) {
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

    public static String[] getBytesValue(byte[] bytes) {
        return new String(bytes).split("\n");
    }
}
