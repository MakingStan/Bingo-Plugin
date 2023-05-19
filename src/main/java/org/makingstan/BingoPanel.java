package org.makingstan;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import net.runelite.client.ui.PluginPanel;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class BingoPanel extends PluginPanel {

    JButton copyAccountHash = new JButton("Copy account hash");
    JButton refreshButton = new JButton("Refresh");

    @Inject
    public BingoPanel(BingoPlugin plugin)
    {
        copyAccountHash.addActionListener(e -> {
            StringSelection selection = new StringSelection(String.valueOf(plugin.accountHash));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        });

        refreshButton.addActionListener(e -> {
            plugin.updateGroupInfo();
        });

        this.add(copyAccountHash);
        this.add(refreshButton);
    }
}
