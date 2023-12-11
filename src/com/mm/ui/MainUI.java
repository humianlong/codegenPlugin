package com.mm.ui;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.intellij.icons.AllIcons;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.IconButton;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.mm.base.CodeFile;
import com.mm.gen.DefaultTableModel;
import com.mm.gen.ItemData;
import com.mm.utils.CodeGenUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.swing.*;
import javax.swing.plaf.TableUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * description:
 * @author: humian49712
 * @date: 2023/12/1
 */
public class MainUI {
    private JLabel name;
    private JTextField nameField;
    private JLabel path;
    private JTextField pathField;
    private JLabel content;
    private JTextArea contentArea;
    private JScrollPane contentScroll;
    private JScrollPane tableScroll;
    private JTable table;
    private JLabel templatePath;
    private JTextField templatePathField;
    private JButton gen;
    private JPanel mainPanel;

    private String genPath;

    private Project project;

    private JBPopup jBPopup;

    private JCheckBox headerCheckBox;

    private VirtualFile virtualFile;

    public MainUI(Project project, VirtualFile virtualFile) {
        this.project = project;
        this.genPath = virtualFile.getPath();
        this.virtualFile = virtualFile;
        templatePathField.setText(PropertiesComponent.getInstance().getValue("codegenPluginTemplatePath"));
        pathField.setText(genPath);
        renderTable();

        templatePathField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                jBPopup.setUiVisible(false);
                FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false , false, false, false);
                VirtualFile[] virtualFiles = FileChooser.chooseFiles(descriptor, project, null);
                jBPopup.setUiVisible(true);
                if (virtualFiles == null || virtualFiles.length < 1) {
                    return;
                }
                templatePathField.setText(virtualFiles[0].getPath());
                renderTable();
                PropertiesComponent.getInstance().setValue("codegenPluginTemplatePath", virtualFiles[0].getPath());
            }
        });

        mainPanel.setPreferredSize(new Dimension(600, 500));
    }

    public void renderTable() {
        List<ItemData> list = new ArrayList<>();
        String chooseTemplatePath = templatePathField.getText();
        if (StringUtils.isBlank(chooseTemplatePath)) {
            return;
        }
        File directory = new File(chooseTemplatePath);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    System.out.println(fileName);
                    if (fileName.endsWith(".ftl")) {
                        fileName = fileName.substring(0, fileName.indexOf(".ftl"));
                        String[] split = fileName.split("_");
                        if (split != null && split.length == 2) {
                            list.add(new ItemData(false, split[0], split[1]));
                        }
                    }
                }
            }
            String[] header = new String[] {"", "模板", "类型", "操作"};
            String[] column = new String[] {"check", "templateName", "type", ""};
            DefaultTableModel defaultTableModel = new DefaultTableModel(header, column, list, ItemData.class);
            table.setModel(defaultTableModel);
            table.setRowHeight(30);

            headerCheckBox = new JCheckBox();
            defaultTableModel.setMainUI(this);
            table.getTableHeader().addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 0) {
                        // 获得选中列
                        int selectColumn = table.getTableHeader().columnAtPoint(e.getPoint());
                        if (selectColumn == 0) {
                            boolean value = !headerCheckBox.isSelected();
                            headerRepaint(value);
                            defaultTableModel.selectAllOrNull(value);
                        }
                    }
                }
            });

            TableColumn column0 = table.getColumnModel().getColumn(0);
            column0.setCellEditor(table.getDefaultEditor(Boolean.class));
            column0.setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    JCheckBox checkBox = new JCheckBox();
                    if (value instanceof Boolean) {
                        checkBox.setSelected(((Boolean)value).booleanValue());
                        checkBox.setForeground(table.getForeground());
                        checkBox.setBackground(table.getBackground());
                        checkBox.setOpaque(false);
                        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                    }
                    return checkBox;
                }
            });

            TableColumn column3 = table.getColumnModel().getColumn(3);
            column3.setCellRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    JButton jButton = new JButton();
                    jButton.setText("预览");
                    return jButton;
                }
            });

            table.getTableHeader().setDefaultRenderer(new TableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    String valueStr = (String) value;
                    JLabel label = new JLabel(valueStr);
                    label.setHorizontalAlignment(SwingConstants.CENTER); // 表头标签居中
                    headerCheckBox.setHorizontalAlignment(SwingConstants.CENTER);// 表头标签居中
                    headerCheckBox.setBorderPainted(true);
                    JComponent component = (column == 0) ? headerCheckBox : label;
                    component.setForeground(table.getTableHeader().getForeground());
                    component.setBackground(table.getTableHeader().getBackground());
                    component.setFont(table.getTableHeader().getFont());
                    component.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                    return component;
                }
            });
            table.getTableHeader().setReorderingAllowed(false);

            table.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int row = table.rowAtPoint(e.getPoint());
                    int column = table.columnAtPoint(e.getPoint());
                    if (column == 3) {
                        ItemData itemData = list.get(row);
                        String content = genContent(itemData, templatePathField.getText(), contentArea.getText());
                        if (StringUtils.isBlank(content)) {
                            return;
                        }
                        JScrollPane jScrollPane = new JScrollPane();
                        JTextArea jTextArea = new JTextArea();
                        jTextArea.setLineWrap(true);
                        jScrollPane.setViewportView(jTextArea);
                        jTextArea.setText(content);

                        jTextArea.addFocusListener(new FocusAdapter() {
                            @Override
                            public void focusLost(FocusEvent e) {
                                String content = jTextArea.getText();
                                table.setValueAt(content, row, column);
                            }
                        });
                        JBPopup jBPopupTable = JBPopupFactory.getInstance().createComponentPopupBuilder(jScrollPane, null)
                                .setMovable(true).setResizable(true).setFocusable(true).setMinSize(new Dimension(600, 500))
                                .setCancelOnClickOutside(true).createPopup();
                        jBPopupTable.setRequestFocus(true);
                        jBPopupTable.showInCenterOf(table);
                    }
                }
            });

            gen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (StringUtils.isBlank(nameField.getText())) {
                        //Messages.showMessageDialog("名称不能为空", "错误", Messages.getInformationIcon());
                        CodeGenUtil.popMessage("错误", "名称不能为空", mainPanel);
                        return;
                    }
                    List<ItemData> collect = list.stream().filter(ItemData::getCheck).collect(Collectors.toList());
                    if (collect == null || collect.size() == 0) {
                        //Messages.showMessageDialog("请选择模板", "错误", Messages.getInformationIcon());
                        CodeGenUtil.popMessage("错误", "请选择模板", mainPanel);
                        return;
                    }
                    List<CodeFile> list = new ArrayList<>();
                    for (int i = 0; i < collect.size(); i++) {
                        CodeFile codeFile = new CodeFile();
                        codeFile.setName(CodeGenUtil.upper(nameField.getText()) + CodeGenUtil.upper(collect.get(i).getTemplateName()) + "." + collect.get(i).getType());
                        codeFile.setPath(CodeGenUtil.lower(collect.get(i).getTemplateName()));
                        codeFile.setContent(genContent(collect.get(i), templatePathField.getText(), contentArea.getText()));
                        list.add(codeFile);
                        try {
                            CodeGenUtil.writeCodeFile(genPath, list);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                    CodeGenUtil.popMessage("信息", "生成完成", mainPanel);
                    virtualFile.refresh(true, true);
                }
            });
        }
    }

    private String genContent(ItemData itemData, String templatePath, String contentStr) {
        FreeMarkerConfigurer f = new FreeMarkerConfigurer();
        Properties p = new Properties();
        p.setProperty("template_update_delay", "0");
        p.setProperty("default_encoding", "UTF-8");
        p.setProperty("number_format", "0.##########");
        p.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        p.setProperty("classic_compatible", "true");
        p.setProperty("template_exception_handler", "ignore");
        f.setFreemarkerSettings(p);
        f.setTemplateLoaderPath(templatePath);
        try {
            Configuration configuration = f.createConfiguration();
            configuration.setDirectoryForTemplateLoading(new File(templatePath));
            f.setConfiguration(configuration);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (TemplateException templateException) {
            templateException.printStackTrace();
        }
        Template template = null;

        try {
            template = f.getConfiguration().getTemplate(itemData.getTemplateName() + "_" + itemData.getType() + ".ftl");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        JSONObject context = null;
        try {
            context = JSON.parseObject(contentStr);
        } catch (Exception e) {
            //Messages.showMessageDialog("内容解析失败", "错误", Messages.getInformationIcon());
            CodeGenUtil.popMessage("错误", "内容解析失败", mainPanel);
            return "";
        }
        String content = CodeGenUtil.genContentByTemplate(template, context);
        return content;
        /*List<CodeFile> list = new ArrayList<>();
        CodeFile codeFile = new CodeFile();
        codeFile.setName("User.java");
        codeFile.setPath("user");
        codeFile.setContent(content);
        list.add(codeFile);
        try {
            CodeGenUtil.writeCodeFile(pathField.getText(), list);
        } catch (Exception exception) {
            exception.printStackTrace();
        }*/
    }

    public void headerRepaint(boolean selected) {
        headerCheckBox.setSelected(selected);
        table.getTableHeader().repaint();
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    public JBPopup getjBPopup() {
        return jBPopup;
    }

    public void setjBPopup(JBPopup jBPopup) {
        this.jBPopup = jBPopup;
    }
}
