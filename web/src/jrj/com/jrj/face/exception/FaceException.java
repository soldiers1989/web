/**
 * 
 */
package com.jrj.face.exception;

import com.jrj.common.exception.JrjBaseException;

/**
 * 接口异常
 * @author coldwater
 *
 */
@SuppressWarnings("serial")
public class FaceException extends JrjBaseException {

	public FaceException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FaceException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public FaceException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public FaceException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
