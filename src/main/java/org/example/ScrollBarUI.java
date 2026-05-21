package org.example;

import javax.swing.*;
import java.awt.*;

public class ScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isDragging) {
            g2d.setColor(new Color(130, 130, 130, 180));
        } else if (isThumbRollover()) {
            g2d.setColor(new Color(160, 160, 160, 150));
        } else {
            g2d.setColor(new Color(200, 200, 200, 100));
        }

        g2d.fillRoundRect(thumbBounds.x + 4, thumbBounds.y, thumbBounds.width - 6, thumbBounds.height, 8, 8);
        g2d.dispose();
    }
}
