package com.dil.buildacuquarium;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
	int numberOfFish = 85;
	int sleepTime = 110;
	Vector<Fish> fishes = new Vector<Fish>();
	boolean runOK = true;
	Point bubblePoint = new Point();
	int bubbleRounds = 8;
	
	// TODO: devide by 2 is not correct exacly if bubbleRounds not even, have to check
	Image[] bubbleImages = new Image[bubbleRounds]; 
	int currentBubbleRound = 0;
	boolean isBubbleActivated = false;

	public Aquarium() {
		setTitle("The Aquarium");
		// Tracks if there are any problem while loading the images.
		tracker = new MediaTracker(this);

		loadBubbleImages(tracker);

		fishImages[0] = Toolkit.getDefaultToolkit().getImage("leftFish.png");
		tracker.addImage(fishImages[0], 0);

		fishImages[1] = Toolkit.getDefaultToolkit().getImage("rightFish.png");
		tracker.addImage(fishImages[1], 0);

		aquariumImage = Toolkit.getDefaultToolkit().getImage(
				"fishTankBackground.gif");
		tracker.addImage(aquariumImage, 0);

		try {
			// Starts loading all images tracked by this media tracker with the
			// specified identifier. This method waits until all the images with
			// the specified identifier have finished loading.
			tracker.waitForID(0);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}

		// Creates the window component as per the widths and heights of the
		// background image.
		setSize(aquariumImage.getWidth(this), aquariumImage.getHeight(this));
		setResizable(true);
		setVisible(true);

		// This is the entire image drawn @ different times (after each sleep
		// time). This image will be drawn. it includes everything (Fish +
		// background). This is an illusion of images keep redrawing after a
		// small time delay.
		memoryImage = createImage(getSize().width, getSize().height);
		memoryGraphics = memoryImage.getGraphics();

		thread = new Thread(this);
		thread.start();

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				runOK = false;
				System.exit(0);
			}
		});

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent clickEvent) {

				isBubbleActivated = true;
				bubblePoint = new Point(clickEvent.getX(), clickEvent.getY());
				currentBubbleRound = bubbleRounds;

			}
		});

	}

	/**
	 * @param tracker
	 * Populates bubble picutres first in ascending and then descending order.
	 * (oredr of display). Note there can be logic holes need to check extremes :) code in a hurry
	 */
	private void loadBubbleImages(MediaTracker tracker) {
		for (int i = 0; i <= bubbleRounds / 2 ; i++) {
			bubbleImages[i] = Toolkit.getDefaultToolkit().getImage("bubble" + String.valueOf(i + 1) + ".png");
			bubbleImages[bubbleRounds - (i + 2)] = Toolkit.getDefaultToolkit().getImage("bubble" + String.valueOf(i + 1) + ".png");
			tracker.addImage(bubbleImages[i], 0);
			tracker.addImage(bubbleImages[bubbleRounds - (i + 2)], 0);
		}
		

	}

	/*
	 * public void temp(int x, int y){ System.out.println(x + ":" + y);
	 * memoryGraphics.drawImage(fishImages[0], x, y, this);
	 * this.getGraphics().drawImage(memoryImage, 0, 0, this); try {
	 * thread.sleep(1000); } catch (InterruptedException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 */
	public static void main(String[] args) {
		new Aquarium();

	}

	@Override
	public void run() {

		// This thread will create the fishes and keep them in a vector.
		for (int i = 0; i < numberOfFish; i++) {
			// Gets the actual rectangle to play with fish, where all the border
			// thickness is being removed here using Insets.
			// Creates the inner rectangle(x, y, width, height)
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

		// Now all the fish will be given their behavior.
		// Here all the calculations will be done and will assign all the values
		// to each fish.
		// So those values will be used later in repaint(update method - by
		// updating the memory image with all the fish details)
		Fish fish;
		while (runOK) {
			for (int i = 0; i < numberOfFish; i++) {
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
	 *            Repaint method calls this update method.
	 */
	public void update(Graphics g) {
		// In the Aquarium application, the code starts by drawing the
		// background image using the Graphics object corresponding to the
		// memory image, not to the main window. So it avoids the flickering.
		memoryGraphics.drawImage(aquariumImage, 0, 0, this);
		for (int i = 0; i < numberOfFish; i++) {
			((Fish) fishes.elementAt(i)).drawFishImage(memoryGraphics);
		}

		if (isBubbleActivated) {
			// Reduces the current bubbleRound by 1 first soon after it's being checked: ds line is 4 u :D
			if (currentBubbleRound-- > 0) {
				memoryGraphics.drawImage(bubbleImages[bubbleRounds - (currentBubbleRound + 1)], bubblePoint.x - 75,
						bubblePoint.y - 75, this);
			} else {
				isBubbleActivated = false;
			}
		}

		g.drawImage(memoryImage, 0, 0, this);

	}

}
