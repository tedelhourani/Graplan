/*
 * Copyright (C) 2011 Theodore Elhourani - ECE 573 
 * Project: Graplan
 * 
 * constants class
 * 
 */

package com.project.graplan;

public class Constants {

	private Constants() {
	}

	final static public int MAIN_MENU_VIEW_REQUEST = 10;
	final static public int GRAPH_ACTIVITY_REQUEST = 11;
	final static public int WAIT_FOR_INPUT = 20;
	final static public int PLAYING = 21;

	final static public int QUIT = 0;
	final static public int PLAY = 1;
	final static public int PLANAR = 2;
	final static public int NON_PLANAR = 3;
	final static public int SAVE_AND_QUIT = 4;
	final static public int NEW_CHALLENGE = 5;
	final static public int SET_DIFFICULTY_LEVEL = 6;

	final static public String ACTION = "ACTION";

	final static public int WIDTH = 300;
	final static public int LENGTH = 470;
	final static public float TAP_MARGIN = 5;

	final static public int[] RESOURCES = { R.raw.state, R.raw.challenge_0,
			R.raw.challenge_1, R.raw.challenge_2, R.raw.challenge_3,
			R.raw.challenge_4, R.raw.challenge_5, R.raw.challenge_6,
			R.raw.challenge_7, R.raw.challenge_8, R.raw.challenge_9,
			R.raw.challenge_10, R.raw.challenge_11, R.raw.challenge_12,
			R.raw.challenge_13, R.raw.challenge_14, R.raw.challenge_15,
			R.raw.challenge_16, R.raw.challenge_17, R.raw.challenge_18,
			R.raw.challenge_19, R.raw.challenge_20, R.raw.challenge_21,
			R.raw.challenge_22, R.raw.challenge_23, R.raw.challenge_24,
			R.raw.challenge_25, R.raw.challenge_26, R.raw.challenge_27,
			R.raw.challenge_28, R.raw.challenge_29, R.raw.challenge_30,
			R.raw.challenge_31, R.raw.challenge_32, R.raw.challenge_33,
			R.raw.challenge_34, R.raw.challenge_35, R.raw.challenge_36,
			R.raw.challenge_37, R.raw.challenge_38 };

	final static public String[] RESOURCE_STRING = { "state.xml",
			"challenge-0.xml", "challenge-1.xml", "challenge-2.xml",
			"challenge-3.xml", "challenge-4.xml", "challenge-5.xml",
			"challenge-6.xml", "challenge-7.xml", "challenge-8.xml",
			"challenge-9.xml", "challenge-10.xml", "challenge-11.xml",
			"challenge-12.xml", "challenge-13.xml", "challenge-14.xml",
			"challenge-15.xml", "challenge-16.xml", "challenge-17.xml",
			"challenge-18.xml", "challenge-19.xml", "challenge-20.xml",
			"challenge-21.xml", "challenge-22.xml", "challenge-23.xml",
			"challenge-24.xml", "challenge-25.xml", "challenge-26.xml",
			"challenge-27.xml", "challenge-28.xml", "challenge-29.xml",
			"challenge-30.xml", "challenge-31.xml", "challenge-32.xml",
			"challenge-33.xml", "challenge-34.xml", "challenge-35.xml",
			"challenge-36.xml", "challenge-37.xml", "challenge-38.xml" };
}
