package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ShoeStoreApp extends JFrame {
    protected List<Product> productList;
    protected JPanel leftPanel;
    protected JPanel rightPanel;

    private JPanel selectedCard = null;

    private JLabel lblLargeImage, lblLargeName, lblLargePrice;
    private JTextArea txtLargeDesc;

    private float alpha = 1.0f;
    private Timer fadeTimer;

    static class Product {
        String name, price, brand, desc, imagePath;
        public Product(String name, String price, String brand, String desc, String imagePath) {
            this.name = name; this.price = price; this.brand = brand; this.desc = desc; this.imagePath = imagePath;
        }
    }

    public ShoeStoreApp() {
        setTitle("Shoe Store Interface - Connected to SQLite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        initData();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(320);
        splitPane.setDividerSize(1);

        // Trái
        leftPanel = new JPanel();
        leftPanel.setBackground(Color.WHITE);
        setupLeftPanel();

        // Phải
        rightPanel = new JPanel();
        rightPanel.setBackground(new Color(245, 245, 245));
        setupRightPanel();

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane);
    }

    // --- Lấy dữ liệu từ file ---
    private void initData() {
        productList = ProductDAO.getAllProducts();

        if (productList.isEmpty()) {
            System.out.println("Danh sách sản phẩm từ database đang rỗng!");
        }
    }

    private void setupLeftPanel() {
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 20, 30));

        lblLargeImage = new JLabel();
        lblLargeImage.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblLargeName = new JLabel();
        lblLargeName.setFont(new Font("Arial", Font.BOLD, 20));
        lblLargeName.setForeground(new Color(50, 50, 50));
        lblLargeName.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblLargePrice = new JLabel();
        lblLargePrice.setFont(new Font("Arial", Font.BOLD, 18));
        lblLargePrice.setForeground(new Color(50, 50, 50));
        lblLargePrice.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtLargeDesc = new JTextArea();
        txtLargeDesc.setFont(new Font("Arial", Font.PLAIN, 13));
        txtLargeDesc.setForeground(Color.GRAY);
        txtLargeDesc.setLineWrap(true);
        txtLargeDesc.setWrapStyleWord(true);
        txtLargeDesc.setEditable(false);
        txtLargeDesc.setBackground(Color.WHITE);
        txtLargeDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(lblLargeImage);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        leftPanel.add(lblLargeName);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        leftPanel.add(lblLargePrice);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        leftPanel.add(txtLargeDesc);

        if(!productList.isEmpty()) updateLeftPanel(productList.get(0));
    }

    public void updateLeftPanel(Product p) {
        lblLargeName.setText(p.name);
        lblLargePrice.setText(p.price);
        txtLargeDesc.setText(p.brand + "\n\n" + p.desc);
        ImageIcon icon = new ImageIcon(p.imagePath);
        if (icon.getIconWidth() > 0) {
            Image scaled = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            lblLargeImage.setIcon(new ImageIcon(scaled));
        } else {
            lblLargeImage.setText("[ Big Image ]");
            lblLargeImage.setIcon(null);
        }
    }

    private void setupRightPanel() {
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        JPanel gridCardPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        gridCardPanel.setBackground(Color.WHITE);
        gridCardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (Product p : productList) {
            JPanel card = createProductCard(p);
            gridCardPanel.add(card);
        }
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(gridCardPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));

        rightPanel.add(scrollPane, BorderLayout.CENTER);

        rightPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int currentWidth = rightPanel.getWidth() - 82;
                int idealCardWidth = 180;
                int gap = 15;

                int calculatedCols = (currentWidth + gap) / (idealCardWidth + gap);
                if (calculatedCols < 1) calculatedCols = 1;

                GridLayout layout = (GridLayout) gridCardPanel.getLayout();
                if (layout.getColumns() != calculatedCols) {
                    layout.setColumns(calculatedCols);
                    gridCardPanel.removeAll();
                    for (Product p : productList) {
                        JPanel card = createProductCard(p);
                        gridCardPanel.add(card);

                        if (lblLargeName != null && p.name.equals(lblLargeName.getText())) {
                            selectedCard = card;
                        }
                    }
                    gridCardPanel.revalidate();
                    gridCardPanel.repaint();
                }
            }
        });
    }

    private JPanel createProductCard(Product p) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(242, 242, 242));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                if (this == selectedCard) {
                    g2d.setColor(new Color(100, 160, 240));
                    g2d.setStroke(new BasicStroke(2.0f));
                    g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);
                }
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setPreferredSize(new Dimension(180, 250));

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        topPanel.setOpaque(false);

        JLabel lblName = new JLabel(p.name);
        lblName.setFont(new Font("Arial", Font.BOLD, 14));
        lblName.setForeground(new Color(60, 60, 60));

        String shortDesc = p.desc.length() > 28 ? p.desc.substring(0, 26) + "..." : p.desc;
        JLabel lblSub = new JLabel(shortDesc);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 11));
        lblSub.setForeground(Color.LIGHT_GRAY);

        topPanel.add(lblName);
        topPanel.add(lblSub);

        JPanel imgWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        imgWrapper.setOpaque(false);

        JLabel lblImg = new JLabel();
        ImageIcon icon = new ImageIcon(p.imagePath);
        if (icon.getIconWidth() > 0) {
            Image scaled = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            lblImg.setIcon(new ImageIcon(scaled));
        } else {
            lblImg.setText("[ " + p.name + " ]");
        }
        imgWrapper.add(lblImg);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JLabel lblBrand = new JLabel(p.brand);
        lblBrand.setFont(new Font("Arial", Font.PLAIN, 12));
        lblBrand.setForeground(Color.GRAY);

        JLabel lblPrice = new JLabel(p.price);
        lblPrice.setFont(new Font("Arial", Font.BOLD, 15));
        lblPrice.setForeground(new Color(40, 40, 40));

        bottomPanel.add(lblBrand, BorderLayout.WEST);
        bottomPanel.add(lblPrice, BorderLayout.EAST);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(imgWrapper, BorderLayout.CENTER);
        card.add(bottomPanel, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (selectedCard == card) return;

                JPanel previousCard = selectedCard;
                selectedCard = card;

                startFadeInAnimation(p);

                if (previousCard != null) {
                    previousCard.repaint();
                }
                card.repaint();
            }
        });
        return card;
    }

    private void startFadeInAnimation(Product p) {
        if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
        alpha = 0.0f;
        updateLeftPanel(p);

        fadeTimer = new Timer(15, e -> {
            alpha += 0.05f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                fadeTimer.stop();
            }
            leftPanel.repaint();
        });
        fadeTimer.start();
    }

    public static void main(String[] args) {
        ProductDAO.initDatabase();
        SwingUtilities.invokeLater(() -> {
            new ShoeStoreApp().setVisible(true);
        });
    }
}
