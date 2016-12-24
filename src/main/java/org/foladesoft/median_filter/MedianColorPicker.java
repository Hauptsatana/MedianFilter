/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foladesoft.median_filter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Vladislav
 */
public class MedianColorPicker implements Runnable {

	private final BufferedImage source;
	private final BufferedImage target;
	private final int x;
	private final int y;
	private final int adjacentPixels;

	public MedianColorPicker(BufferedImage source, BufferedImage target, int x, int y, int adjacentPixels) {
		this.source = source;
		this.target = target;
		this.x = x;
		this.y = y;
		this.adjacentPixels = adjacentPixels;
	}

	public void run() {
		// Obtaining all colors
		int mdim = adjacentPixels & 2 + 1;
		List<Color> colors = new ArrayList<>(mdim * mdim);
		for (int i = x - adjacentPixels; i < x + adjacentPixels + 1; i++) {
			for (int j = y - adjacentPixels; j < y + adjacentPixels + 1; j++) {
				colors.add(new Color(source.getRGB(i, j)));
			}
		}
		
		// By-component sorting and retrieving the result
		Collections.sort(colors, (Color o1, Color o2) -> o1.getRed() - o2.getRed());
		int rmed = colors.get(mdim * mdim / 2 + 1).getRed();

		Collections.sort(colors, (Color o1, Color o2) -> o1.getGreen() - o2.getGreen());
		int gmed = colors.get(mdim * mdim / 2 + 1).getGreen();

		Collections.sort(colors, (Color o1, Color o2) -> o1.getBlue() - o2.getBlue());
		int bmed = colors.get(mdim * mdim / 2 + 1).getBlue();
		
		synchronized (target) {
			target.setRGB(x, y, new Color(rmed, gmed, bmed).getRGB());
		}

	}
	
}
