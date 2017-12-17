package io.github.voxelbuster.autools.ui;

import io.github.voxelbuster.autools.api.Globals;
import io.github.voxelbuster.autools.api.Task;
import se.vidstige.jadb.managers.PropertyManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class FlashPane extends JTabbedPane {
    public FlashPane(ParentWindow parent) {
        String codename = "";
        try {
            codename = new PropertyManager(Globals.selectedDevice).getprop().get("ro.product.model");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not get device codename. You will be unable to find roms automatically.", "build.prop error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel selectRom = new JPanel();
        selectRom.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        selectRom.setLayout(new GridLayout(0, 1));

        JPanel flashOpt = new JPanel();
        flashOpt.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        flashOpt.setLayout(new GridLayout(0, 1));

        this.addTab("Select Rom", selectRom);
        this.addTab("Flash Options", flashOpt);

        JList<String> romList = new JList<>();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        ArrayList<Task.Partition> partitions = new ArrayList<>();

        JPanel romButtons = new JPanel();

        JButton cancelButton = new JButton("Cancel");
        JButton addButton = new JButton("Add");
        JButton rmButton = new JButton("Remove");
        JButton nextButton = new JButton("Next >>");

        cancelButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setWindowState(ParentWindow.WindowState.HOME);
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

        addButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.addChoosableFileFilter(new FileNameExtensionFilter("Firmware ZIP", "zip"));
                chooser.addChoosableFileFilter(new FileNameExtensionFilter("Boot Image", "img"));
                chooser.showOpenDialog(null);
                String fname = chooser.getSelectedFile().getAbsolutePath();
                if (!(fname.endsWith(".zip") || fname.endsWith(".img"))) {
                    JOptionPane.showMessageDialog(null, "Invalid rom file.", "Invalid file", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                final boolean[] cancel = {false};
                if (fname.endsWith(".img")) {
                    JDialog partDlog = new JDialog();
                    partDlog.setLayout(new GridLayout(0, 1));
                    ButtonGroup bg = new ButtonGroup();
                    JRadioButton boot = new JRadioButton("Boot"), recovery = new JRadioButton("Recovery"), sys = new JRadioButton("System");
                    recovery.setSelected(true);
                    JButton confirm = new JButton("OK");
                    confirm.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (boot.isSelected()) {
                                partitions.add(Task.Partition.BOOT);
                            } else if (recovery.isSelected()) {
                                partitions.add(Task.Partition.RECOVERY);
                            } else if (sys.isSelected()) {
                                partitions.add(Task.Partition.SYSTEM);
                            } else {
                                partitions.add(Task.Partition.SIDELOAD);
                            }
                            partDlog.setVisible(false);
                            partDlog.dispose();
                        }
                    });
                    partDlog.setModal(true);
                    bg.add(boot);
                    bg.add(recovery);
                    bg.add(sys);
                    partDlog.add(new JLabel("Select partition to flash image"));
                    partDlog.add(boot);
                    partDlog.add(recovery);
                    partDlog.add(sys);
                    partDlog.add(confirm);
                    partDlog.pack();
                    partDlog.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {

                        }

                        @Override
                        public void windowClosing(WindowEvent e) {

                        }

                        @Override
                        public void windowClosed(WindowEvent e) {
                            cancel[0] = true;
                        }

                        @Override
                        public void windowIconified(WindowEvent e) {

                        }

                        @Override
                        public void windowDeiconified(WindowEvent e) {

                        }

                        @Override
                        public void windowActivated(WindowEvent e) {

                        }

                        @Override
                        public void windowDeactivated(WindowEvent e) {

                        }
                    });
                    partDlog.setVisible(true);
                } else {
                    partitions.add(Task.Partition.SIDELOAD);
                }
                if (!cancel[0]) {
                    listModel.addElement(fname);
                    romList.setModel(listModel);
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

        rmButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selected = -1;
                selected = romList.getSelectedIndex();
                if (selected > -1) {
                    listModel.remove(selected);
                    romList.setModel(listModel);
                    partitions.remove(selected);
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

        nextButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                FlashPane.this.setSelectedComponent(flashOpt);
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

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(addButton);
        buttonsPanel.add(rmButton);
        buttonsPanel.add(nextButton);

        JCheckBox backUp = new JCheckBox("Backup System partitions (boot, recovery, system)");
        JCheckBox buData = new JCheckBox("Backup Data partition");
        JCheckBox wipeCache = new JCheckBox("Wipe Cache (recommended)");
        wipeCache.setSelected(true);
        JCheckBox wipeDalvik = new JCheckBox("Wipe Dalvik (highly recommended)");
        wipeDalvik.setSelected(true);
        JCheckBox cleanAll = new JCheckBox("Clean Flash");

        JPanel flashOptButtons = new JPanel();
        JButton flashButton = new JButton("Flash");
        flashButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Globals.roms.clear();
                if (listModel.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No roms selected, please select a rom.", "No rom selected", JOptionPane.INFORMATION_MESSAGE);
                    FlashPane.this.setSelectedComponent(selectRom);
                    return;
                }
                if (cleanAll.isSelected()) {
                    int cont = JOptionPane.showConfirmDialog(null, "You have selected clean flash, which will wipe your cache, system, and data partitions. All apps and their data will be lost. Are you sure you want to do this?", "Clean Flash", JOptionPane.YES_NO_OPTION);
                    if (cont == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                for (int i = 0; i < listModel.size(); i++) {
                    Globals.roms.add(listModel.get(i));
                    Globals.partitions.add(partitions.get(i));
                }
                Globals.wipeCache = wipeCache.isSelected();
                Globals.wipeDalvik = wipeDalvik.isSelected();
                Globals.clean = cleanAll.isSelected();
                Globals.backupSystem = backUp.isSelected();
                Globals.backupData = buData.isSelected();
                parent.setWindowState(ParentWindow.WindowState.FLASH_PROGRESS);
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

        flashOptButtons.add(cancelButton);
        flashOptButtons.add(flashButton);

        selectRom.add(new JLabel("Add roms to flash queue -- Please note all boot images will be flashed first regardless of their order."));
        selectRom.add(romList);
        selectRom.add(buttonsPanel);

        flashOpt.add(backUp);
        flashOpt.add(buData);
        flashOpt.add(wipeCache);
        flashOpt.add(wipeDalvik);
        flashOpt.add(cleanAll);
        flashOpt.add(flashOptButtons);
    }
}
