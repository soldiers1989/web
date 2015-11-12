package com.cfo.stock.web.velocity.data.toolbox;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/spring-app.xml" })
public class CommonDataToolboxTest {

	
	@Test
	public void testGetAccUserInfo() {
		CommonDataToolbox toolbox = new CommonDataToolbox();
		Map<String, Object> accUserInfo = toolbox.getAccUserInfo("141017010039806379", 100029L);
		
	}

	@Test
	public void testGetAccUserList() {
		fail("Not yet implemented");
	}

}
