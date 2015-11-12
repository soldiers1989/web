/**
 * 
 */
package com.cfo.stock.web.util;

import java.util.Observable;

import org.apache.velocity.app.VelocityEngine;

/**  
 *   
 *   
 * @history  
 * <PRE>  
 * ---------------------------------------------------------  
 * VERSION       DATE            BY       CHANGE/COMMENT  
 * ---------------------------------------------------------  
 * 1.0           2012-5-28    yuanlong.wang     create  
 * ---------------------------------------------------------  
 * </PRE>  
 *  
 */

public class VelocityObservable extends Observable {

	VelocityEngine engine;

	public VelocityObservable() {		
		addObservers();		
	}

	private void addObservers() {
		addObserver(VelocityUtils.getInstance());
	}

	public VelocityEngine getEngine() {
		return engine;
	}

	public void setEngine(VelocityEngine engine) {
		this.engine = engine;
		setChanged();
		notifyObservers(engine);
	}
}