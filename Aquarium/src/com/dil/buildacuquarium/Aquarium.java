package com.dil.buildacuquarium;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

public class Aquarium extends Frame implements Runnable {

	private static final long serialVersionUID = 1L;
	Image aquariumImage, memoryImage;
	Graphics memoryGraphics;
	Image[] fishImages = new Image[2];
	MediaTracker tracker;
	Thread thread;
	int numberFish = 85;
	int sleepTime = 110;
	Vector<Fish> fishes = new Vector<Fish>();
	boolean runOK = true;

	public Aquarium() {
		setTitle("The Aquarium");
		// Tracks if there are any problem while loading the images
		tracker = new MediaTracker(this);
		fishImages[0] = Toolkit.getDefaultToolkit().getImage("leftFish.png");
		tracker.addImage(fishImages[0], 0);

		fishImages[1] = Toolkit.getDefaultToolkit().getImage("rightFish.png");
		tracker.addImage(fishImages[1], 0);

		aquariumImage = Toolkit.getDefaultToolkit().getImage(
				"fishTankBackground.gif");
		tracker.addImage(aquariumImage, 0);

		try {
			tracker.waitForID(0);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}

		thread = new Thread(this);
		thread.start();

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				runOK = false;
				System.exit(0);
			}
		});

		setSize(aquariumImage.getWidth(this), aquariumImage.getHeight(this));
		setResizable(true);
		setVisible(true);

		memoryImage = createImage(getSize().width, getSize().height);
		memoryGraphics = memoryImage.getGraphics();

	}

	public static void main(String[] args) {
		new Aquarium().setVisible(true);

	}

	@Override
	public void run() {
		for (int i = 0; i < numberFish; i++) {
			Rectangle edges = new Rectangle(0 + getInsets().left,
					0 + getInsets().top, getSize().width
							- (getInsets().left + getInsets().right),
					getSize().height - (getInsets().top + getInsets().bottom));

			fishes.add(new Fish(fishImages[0], fishImages[1], edges, this));
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}

		Fish fish;
		while (runOK) {
			for (int i = 0; i < numberFish; i++) {
				fish = (Fish) fishes.elementAt(i);
				fish.swim();
			}

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			repaint();
		}

	}

	/**
	 * @param g
	 *            Repaint method calls the update method.
	 */
	public void update(Graphics g) {
		// In the Aquarium application, the code starts by drawing the
		// background image using the Graphics object corresponding to the
		// memory image, not to the main window. So it avoids the flickering.
		memoryGraphics.drawImage(aquariumImage, 0, 0, this);
		for (int i = 0; i < numberFish; i++) {
			((Fish)fishes.elementAt(i)).drawFishImage(memoryGraphics);	
		}
		g.drawImage(memoryImage, 0, 0, this);
		
	}

}
