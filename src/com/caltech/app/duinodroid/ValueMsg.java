/**
 * code from the book Arduino + Android Projects for the Evil Genius
 * <br>Copyright 2011 Simon Monk
 *
 * <p>This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation (see COPYING).
 * 
 * <p>This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.caltech.app.duinodroid;


public class ValueMsg {
	private long time;
	private float t;
    private float p;
    private float h;
    private float optical;
    private float AH;
    private float AV;
    private float photoval;
    private float CH4;
    private float LPG;
    private float CO;
    private float H2;
    private float gAvgGap;
    private float gRate;
    private float gStd;

	public ValueMsg(float[] sensorValues) {
		time = System.currentTimeMillis();
		t = sensorValues[1];
		p = sensorValues[2];
		h = sensorValues[3];
		optical = sensorValues[4];
		AH = sensorValues[5];
		AV = sensorValues[6];
		photoval = sensorValues[7];
		CH4 = sensorValues[8];
		LPG = sensorValues[9];
		CO = sensorValues[10];
		H2 = sensorValues[11];
		gAvgGap = sensorValues[12];
		gRate = sensorValues[13];
		gStd = sensorValues[14];
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public float getT() {
		return t;
	}

	public void setT(float t) {
		this.t = t;
	}

	public float getP() {
		return p;
	}

	public void setP(float p) {
		this.p = p;
	}

	public float getH() {
		return h;
	}

	public void setH(float h) {
		this.h = h;
	}

	public float getOptical() {
		return optical;
	}

	public void setOptical(float optical) {
		this.optical = optical;
	}

	public float getAH() {
		return AH;
	}

	public void setAH(float aH) {
		AH = aH;
	}

	public float getAV() {
		return AV;
	}

	public void setAV(float aV) {
		AV = aV;
	}

	public float getPhotoval() {
		return photoval;
	}

	public void setPhotoval(float photoval) {
		this.photoval = photoval;
	}

	public float getCH4() {
		return CH4;
	}

	public void setCH4(float cH4) {
		CH4 = cH4;
	}

	public float getLPG() {
		return LPG;
	}

	public void setLPG(float lPG) {
		LPG = lPG;
	}

	public float getCO() {
		return CO;
	}

	public void setCO(float cO) {
		CO = cO;
	}

	public float getH2() {
		return H2;
	}

	public void setH2(float h2) {
		H2 = h2;
	}

	public float getgAvgGap() {
		return gAvgGap;
	}

	public void setgAvgGap(float gAvgGap) {
		this.gAvgGap = gAvgGap;
	}

	public float getgRate() {
		return gRate;
	}

	public void setgRate(float gRate) {
		this.gRate = gRate;
	}

	public float getgStd() {
		return gStd;
	}

	public void setgStd(float gStd) {
		this.gStd = gStd;
	}

	
}
