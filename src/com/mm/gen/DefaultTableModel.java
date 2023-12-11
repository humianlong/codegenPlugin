package com.mm.gen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import com.alibaba.fastjson2.JSON;
import com.intellij.ide.util.PropertiesComponent;
import com.mm.ui.MainUI;

public class DefaultTableModel extends AbstractTableModel {

    private String[] headerRecord;

    private String[] columnRecord;

    private List dataList;

    private Class clazz;

    private MainUI mainUI;

    public DefaultTableModel(String[] headerRecord, String[] columnRecord, List dataList, Class clazz) {
        this.headerRecord = headerRecord;
        this.columnRecord = columnRecord;
        if (dataList == null) {
            dataList = new ArrayList();
        }
        this.dataList = dataList;
        this.clazz = clazz;
    }

    /**
     * 返回一共有多少行
     * @return
     */
    @Override
    public int getRowCount() {
        return dataList.size();
    }

    /**
     * 返回一共有多少列
     * @return
     */
    @Override
    public int getColumnCount() {
        return headerRecord.length;
    }

    /**
     * 返回一共有多少列
     * @param columnIndex
     * @return
     */
    @Override
    public String getColumnName(int columnIndex) {
        return headerRecord[columnIndex];
    }

    /**
     * 单元格是否可以修改
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return true;
        }
        return false;
    }

    /**
     * 每一个单元格里的值
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 3) {
            return null;
        }
        try {
            Field field = clazz.getDeclaredField(columnRecord[columnIndex]);
            field.setAccessible(true);
            return field.get(dataList.get(rowIndex));
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        try {
            Field field = clazz.getDeclaredField(columnRecord[col]);
            field.setAccessible(true);
            field.set(dataList.get(row), value);
            if (col == 0) {
                if (Boolean.FALSE == value) {
                    mainUI.headerRepaint(Boolean.FALSE);
                }
            }
        } catch (Exception e) {

        }
        fireTableCellUpdated(row, col);
    }

    public void selectAllOrNull(boolean value) {
        for (int i = 0; i < getRowCount(); i++) {
            this.setValueAt(value, i, 0);
        }
    }

    public MainUI getMainUI() {
        return mainUI;
    }

    public void setMainUI(MainUI mainUI) {
        this.mainUI = mainUI;
    }

    public List getDataList() {
        return dataList;
    }

    public Class getClazz() {
        return clazz;
    }

}
