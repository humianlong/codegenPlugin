package com.mm.action;
/**
 * description:
 *
 * @author: humian49712
 * @date: 2023/4/4
 */

import a.d.F;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.mm.base.CodeFile;
import com.mm.ui.MainUI;
import com.mm.utils.CodeGenUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.swing.plaf.TableUI;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class PopAction extends AnAction {
    public PopAction() {
        super("CodeGen");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile data = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String codeGenDir = "";
        if (data.isDirectory()) {
            JBPopupFactory instance = JBPopupFactory.getInstance();
            MainUI mainUI = new MainUI(project, data);
            JBPopup jBPopupTable = instance.createComponentPopupBuilder(mainUI.getComponent(), null)
                    .setTitle("配置")
                    .setMovable(true)
                    .setResizable(true)
                    .setFocusable(true)
                    .setCancelOnClickOutside(false).setCancelButton(new IconButton("close", AllIcons.Actions.Cancel))
                    .createPopup();
            mainUI.setjBPopup(jBPopupTable);
            jBPopupTable.setRequestFocus(true);
            jBPopupTable.showCenteredInCurrentWindow(project);
        }
    }

    public void gen(AnActionEvent e) {
        Project project = e.getProject();
        VirtualFile data = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String codeGenDir = "";
        if (data.isDirectory()) {
            codeGenDir = data.getPath();
            FreeMarkerConfigurer f = new FreeMarkerConfigurer();
            Properties p = new Properties();
            p.setProperty("template_update_delay", "0");
            p.setProperty("default_encoding", "UTF-8");
            p.setProperty("number_format", "0.##########");
            p.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
            p.setProperty("classic_compatible", "true");
            p.setProperty("template_exception_handler", "ignore");
            f.setFreemarkerSettings(p);
            f.setTemplateLoaderPath("E:/hundsun/configs/");
            try {
                Configuration configuration = f.createConfiguration();
                configuration.setDirectoryForTemplateLoading(new File("E:/hundsun/configs/"));
                f.setConfiguration(configuration);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (TemplateException templateException) {
                templateException.printStackTrace();
            }
            Template template = null;
            try {
                template = f.getConfiguration().getTemplate("entity.ftl");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            JSONObject context = JSON.parseObject("{\n" +
                    "\t\"entity\": {\n" +
                    "\t\t\"name\": \"user\",\n" +
                    "\t\t\"columns\": [\n" +
                    "\t\t\t{\n" +
                    "\t\t\t\t\"name\": \"id\",\n" +
                    "\t\t\t\t\"type\": \"int\"\n" +
                    "\t\t\t}\n" +
                    "\t\t]\n" +
                    "\t},\n" +
                    "\t\"config\": {\n" +
                    "\t\t\"basepkg\": \"com.mm.test\"\n" +
                    "\t}\n" +
                    "}");
            String content = CodeGenUtil.genContentByTemplate(template, context);
            List<CodeFile> list = new ArrayList<>();
            CodeFile codeFile = new CodeFile();
            codeFile.setName("User.java");
            codeFile.setPath("user");
            codeFile.setContent(content);
            list.add(codeFile);
            try {
                CodeGenUtil.writeCodeFile(codeGenDir, list);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

}
