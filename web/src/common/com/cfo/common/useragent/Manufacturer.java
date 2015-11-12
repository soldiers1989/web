package com.cfo.common.useragent;

/**
 * Enum constants representing manufacturers of operating systems and client software. 
 * Manufacturer could be used for specific handling of browser requests.
 * @author harald
 */

public enum Manufacturer {
	
	/**
	 * Unknow or rare manufacturer
	 */
	OTHER(1, "Other"),
	/**
	 * Microsoft Corporation
	 */
	MICROSOFT(2, "Microsoft Corporation"),
	/**
	 * Apple Inc.
	 */
	APPLE(3, "Apple Inc."),
	/**
	 * Sun Microsystems, Inc.
	 */
	SUN(4, "Sun Microsystems, Inc."),
	/**
	 * Symbian Ltd.
	 */
	SYMBIAN(5, "Symbian Ltd."),
	/**
	 * Nokia Corporation
	 */
	NOKIA(6, "Nokia Corporation"),
	/**
	 * Research In Motion Limited
	 */
	BLACKBERRY(7, "Research In Motion Limited"),	
	/**
	 * Palm, Inc.
	 */
	PALM(8, "Palm, Inc. "),
	/**
	 * Sony Ericsson Mobile Communications AB
	 */
	SONY_ERICSSON(9, "Sony Ericsson Mobile Communications AB"),
	/**
	 * Sony Computer Entertainment, Inc.
	 */
	SONY(10, "Sony Computer Entertainment, Inc."),
	/**
	 * Nintendo
	 */
	NINTENDO(11, "Nintendo"),
	/**
	 * Opera Software ASA
	 */
	OPERA(12, "Opera Software ASA"),
	/**
	 * Mozilla Foundation
	 */
	MOZILLA(13, "Mozilla Foundation"),
	/**
	 * Google Inc.
	 */
	GOOGLE(15, "Google Inc."),
	/**
	 * CompuServe Interactive Services, Inc. 
	 */
	COMPUSERVE(16, "CompuServe Interactive Services, Inc."),
	/**
	 * Yahoo Inc.
	 */
	YAHOO(17, "Yahoo Inc."),
	/**
	 * AOL LLC.
	 */
	AOL(18, "AOL LLC."),
	/**
	 * Mail.com Media Corporation
	 */
	MMC(19, "Mail.com Media Corporation")
	;
	
	
	private final byte id;
	private final String name;
	
	private Manufacturer(int id, String name) {
		this.id = (byte) id;
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public byte getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
