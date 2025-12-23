package com.dic;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vp4nda
 * @date 2025-12-23
 */

public class Mainindex {
    private static final String CONFIG_FILE = "config.ini";

    private JTextField username;
    private JPasswordField password;
    private JButton loginButton;
    private JLabel usernamelabel;
    private JPanel form;
    private JTextArea textA;
    private JTextArea textB;
    private JTextArea textC;
    private JTextField textplan;
    private JButton saveButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("ComboPassDict");
        frame.setContentPane(new Mainindex().form);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        //居中
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private List<String> splitToList(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }
        // 支持空格、换行、逗号、分号等多种分隔符
        return Arrays.stream(text.split("[,;\\s\n]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public Mainindex() {
        loadConfig();
        saveButton.addActionListener(e -> saveConfig());

        loginButton.addActionListener(e -> {

            String plan = textplan.getText().toString().trim().toUpperCase();
            String text1 = textA.getText().toString().trim();
            String text2 = textB.getText().toString().trim();
            String text3 = textC.getText().toString().trim();

            // 解析三个基础列表（支持空格、换行、逗号分隔）
            List<String> listA = splitToList(text1);
            List<String> listB = splitToList(text2);
            List<String> listC = splitToList(text3);
            // 去除空元素
            listA.removeIf(String::isEmpty);
            listB.removeIf(String::isEmpty);
            listC.removeIf(String::isEmpty);
            // 解析 plan，支持多种分隔符：逗号、空格、换行等
            List<String> plans = Arrays.stream(plan.split("[,\\s]+"))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            // 存储所有最终组合
            List<String> results = new ArrayList<>();

            // 根据每个 plan 执行对应的笛卡尔积
            for (String p : plans) {
                switch (p) {
                    case "AB":
                        for (String a : listA) {
                            for (String b : listB) {
                                results.add(a + b);
                            }
                        }
                        break;

                    case "AC":
                        for (String a : listA) {
                            for (String c : listC) {
                                results.add(a + c);
                            }
                        }
                        break;

                    case "BC":
                        for (String b : listB) {
                            for (String c : listC) {
                                results.add(b + c);
                            }
                        }
                        break;

                    case "ABC":
                        for (String a : listA) {
                            for (String b : listB) {
                                for (String c : listC) {
                                    results.add(a + b + c);
                                }
                            }
                        }
                        break;

                    // 可扩展：A、B、C 单独
                    case "A":
                        for (String a : listA) results.add(a);
                        break;
                    case "B":
                        for (String b : listB) results.add(b);
                        break;
                    case "C":
                        for (String c : listC) results.add(c);
                        break;

                    default:
                        // 忽略无效指令
                        break;
                }
            }
            //去重
            results = results.stream().distinct().collect(Collectors.toList());
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String filename = "dic" + timestamp + ".txt";

            try (PrintWriter writer = new PrintWriter(filename, String.valueOf(StandardCharsets.UTF_8))) {
                for (String line : results) {
                    writer.println(line);
                }
                JOptionPane.showMessageDialog(
                        form,
                        "生成完成！共 " + results.size() + " 条组合" + "\n" + new File(filename).getAbsolutePath(),
                        "成功",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        form,
                        "保存文件失败: " + ex.getMessage(),
                        "失败",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }

    // 保存配置
    private void saveConfig() {
        Properties props = new Properties();
        props.setProperty("PLAN", textplan.getText().trim().toUpperCase());
        props.setProperty("TEXT1", textA.getText().trim());
        props.setProperty("TEXT2", textB.getText().trim());
        props.setProperty("TEXT3", textC.getText().trim());

        try (OutputStream out = Files.newOutputStream(Paths.get(CONFIG_FILE))) {

            // 使用 UTF-8 保存，支持中文不乱码
            props.store(new OutputStreamWriter(out, StandardCharsets.UTF_8), "配置文件 - 最后保存时间: " + new Date());
            JOptionPane.showMessageDialog(
                    form,
                    "配置已保存到: " + new File(CONFIG_FILE).getAbsolutePath(),
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    form,
                    "保存配置失败: " + e.getMessage(),
                    "失败",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // 加载配置
    private void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            Properties props = new Properties();
            try (InputStream in = Files.newInputStream(Paths.get(CONFIG_FILE))) {
                props.load(new InputStreamReader(in, StandardCharsets.UTF_8));

                textplan.setText(props.getProperty("PLAN", ""));
                textA.setText(props.getProperty("TEXT1", ""));
                textB.setText(props.getProperty("TEXT2", ""));
                textC.setText(props.getProperty("TEXT3", ""));

                System.out.println("配置已加载: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        form = new JPanel();
        form.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        form.setMinimumSize(new Dimension(458, 142));
        form.setPreferredSize(new Dimension(680, 400));
        form.setRequestFocusEnabled(true);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 4, new Insets(8, 8, 8, 8), -1, -1));
        form.add(panel1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 32), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("  生成方案：");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(65, 25), null, 0, false));
        textplan = new JTextField();
        textplan.setText("AB,AC,ABC");
        panel1.add(textplan, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(110, 25), null, 0, false));
        saveButton = new JButton();
        saveButton.setText("保存方案");
        panel1.add(saveButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(34, 25), null, 0, false));
        loginButton = new JButton();
        loginButton.setText("生成字典");
        panel1.add(loginButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(86, 25), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 3, new Insets(8, 8, 8, 8), -1, -1));
        panel1.add(panel2, new GridConstraints(1, 0, 1, 4, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 300), null, 0, false));
        usernamelabel = new JLabel();
        usernamelabel.setText("A列：");
        panel2.add(usernamelabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textA = new JTextArea();
        textA.setColumns(1);
        textA.setText("Admin\nadmin");
        panel2.add(textA, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 50), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("B列：");
        panel2.add(label2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textB = new JTextArea();
        textB.setColumns(1);
        textB.setText("@\n#\n!\n$\n.\n!@#\n-\n_");
        panel2.add(textB, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 50), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("C列：");
        panel2.add(label3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textC = new JTextArea();
        textC.setColumns(1);
        textC.setText("123\n2025\n2024\n2023\n2022\n2021\n888\n123456\n12345\n000\n111");
        panel2.add(textC, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(200, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return form;
    }
    // 可选：清空所有输入框
//    private void clearAllFields() {
//        textplan.setText("");
//        textA.setText("");
//        textB.setText("");
//        textC.setText("");
//    }
}
