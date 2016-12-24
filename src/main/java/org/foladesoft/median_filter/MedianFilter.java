/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foladesoft.median_filter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;

/**
 *
 * @author Vladislav
 */
public class MedianFilter {

	public static BufferedImage apply(BufferedImage source, int adjacentPixels) {
		if (adjacentPixels <= 0) {
			throw new IllegalArgumentException("adjacentPixels value must be positive");
		}
		try {
			return new MedianFilter(adjacentPixels, source).applyFilter();
		} catch (InterruptedException ex) {
			Logger.getLogger(MedianFilter.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}
	}

	private final int adjacentPixels;
	private final BufferedImage source;

	private final ExecutorService es = Executors.newCachedThreadPool();

	public MedianFilter(int adjacentPixels, BufferedImage source) {
		this.adjacentPixels = adjacentPixels;
		this.source = source;
	}

	private BufferedImage applyFilter() throws InterruptedException {
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < width - adjacentPixels; x++) {
			for (int y = adjacentPixels; y < height - adjacentPixels; y++) {
				if (((x >= adjacentPixels) && (x < width - adjacentPixels))
						&& ((y >= adjacentPixels) && (y < height - adjacentPixels))) {
					// Set median color for pixel in separate thread
					es.execute(new MedianColorPicker(source, res, x, y, adjacentPixels));
				} else {
					// Copy unaffected border 
					synchronized (res) {
						res.setRGB(x, y, source.getRGB(x, y));
					}
				}
			}
		}
		es.shutdown();
		while (!es.awaitTermination(1000, TimeUnit.MILLISECONDS)) { }
		return res;
	}

}
