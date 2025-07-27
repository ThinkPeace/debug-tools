/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.idea.ui.tree;

import com.intellij.openapi.project.Project;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.idea.client.http.HttpClientUtils;
import io.github.future0923.debug.tools.idea.ui.tree.node.EmptyTreeNode;
import io.github.future0923.debug.tools.idea.ui.tree.node.ResultDebugTreeNode;
import io.github.future0923.debug.tools.idea.ui.tree.node.TreeNode;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * @author future0923
 */
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class ResultDebugTreePanel extends JBScrollPane {

    private final Tree tree;

    public ResultDebugTreePanel(Project project) {
        this(project, null);
    }

    public ResultDebugTreePanel(Project project, ResultDebugTreeNode root) {
        this.tree = new SimpleTree();
        // 可以拖动的Tree SimpleDnDAwareTree
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setBorder(new CustomLineBorder(JBUI.insetsTop(1)));
        tree.setCellRenderer(new ResultDebugCellRenderer());
        tree.addTreeWillExpandListener(new TreeWillExpandListener() {

            // 展开
            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                if (event.getPath().getLastPathComponent() instanceof TreeNode node) {
                    if (node.getChildCount() == 1 && node.getFirstChild() instanceof EmptyTreeNode) {
                        node.removeAllChildren();
                        List<RunResultDTO> runResultDTOList = HttpClientUtils.resultDetail(project, ((RunResultDTO)node.getUserObject()).getFiledOffset());
                        for (RunResultDTO runResultDTO : runResultDTOList) {
                            node.add(new ResultDebugTreeNode(runResultDTO, runResultDTO.getLeaf()));
                        }
                        ((DefaultTreeModel) tree.getModel()).reload(node);
                    }
                }
            }

            // 折叠
            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {

            }
        });
        // 创建右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyName = new JMenuItem("Copy Name");
        JMenuItem copyValue = new JMenuItem("Copy Value");
        popupMenu.add(copyName);
        popupMenu.add(copyValue);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tree.getClosestRowForLocation(e.getX(), e.getY());
                    tree.setSelectionRow(row);
                    TreePath path = tree.getPathForRow(row);

                    if (path != null) {
                        // 显示右键菜单
                        popupMenu.show(tree, e.getX(), e.getY());
                    }
                }
            }
        });
        copyName.addActionListener(e -> copy(false));
        copyValue.addActionListener(e -> copy(true));
        boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
        int modifierKey = isMac ? KeyEvent.META_DOWN_MASK : KeyEvent.CTRL_DOWN_MASK;
        copyValue.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, modifierKey));
        Action copyValueAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                copy(true);
            }
        };
        tree.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, modifierKey), "copyValue");
        tree.getActionMap().put("copyValue", copyValueAction);
        if (root != null) {
            setRoot(root);
        }
    }

    private void copy(boolean value) {
        TreePath selectedPath = tree.getSelectionPath();
        if (selectedPath != null) {
            TreeNode<RunResultDTO> selectedNode = (TreeNode<RunResultDTO>) selectedPath.getLastPathComponent();
            RunResultDTO runResultDTO = selectedNode.getUserObject();
            if (runResultDTO == null) {
                return;
            }
            StringSelection stringSelection = new StringSelection(value ? runResultDTO.getValue() == null ? "null" : runResultDTO.getValue() : runResultDTO.getName() == null ? "null" : runResultDTO.getName());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        }
    }

    public void setRoot(ResultDebugTreeNode root) {
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
        model.setRoot(root);
        ((DefaultTreeModel) tree.getModel()).setRoot(root);
        // 获取根节点的TreePath
        TreePath treePath = new TreePath(tree.getModel().getRoot());
        // 折叠根节点
        tree.collapsePath(treePath);
        this.setViewportView(tree);
    }
}
