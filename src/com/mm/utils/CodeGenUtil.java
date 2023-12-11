package com.mm.utils;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.mm.base.CodeFile;
import com.mm.ui.MainUI;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * description:
 * @author: humian49712
 * @date: 2023/11/28
 */
public class CodeGenUtil {

    public static String genContentByTemplate(Template template, Object context) {
        StringWriter out = new StringWriter();
        try {
            template.process(context, out);
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    public static void writeCodeFile(String rootPath, List<CodeFile> filelist) throws Exception {
        File root = new File(rootPath);
        if (!root.exists()) {
            root.mkdir();
        }
        for (CodeFile file : filelist) {
            String path = file.getPath().replace(".", "/");
            path = rootPath + "/" + path;
            File pathFile = new File(path);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            path += "/" + file.getName();
            OutputStream os = new FileOutputStream(new File(path));
            OutputStreamWriter oStreamWriter = new OutputStreamWriter(os, "utf-8");
            oStreamWriter.append(file.getContent());
            oStreamWriter.close();
        }
    }

    public static String upper(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String lower(String str) {
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public static void popMessage(String title, String content, Component component) {
        JBPopupFactory instance = JBPopupFactory.getInstance();
        JLabel jLabel = new JLabel(content);
        jLabel.setPreferredSize(new Dimension(150, 50));
        jLabel.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel.setVerticalAlignment(SwingConstants.CENTER);
        JBPopup jBPopupTable = instance.createComponentPopupBuilder(jLabel, null)
                .setTitle(title)
                .setMovable(true)
                .setResizable(false)
                .setFocusable(true)
                .setCancelOnClickOutside(true)
                .setMinSize(new Dimension(150, 50))
                .createPopup();
        jBPopupTable.setRequestFocus(true);
        jBPopupTable.showInCenterOf(component);
    }
}
