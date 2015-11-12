package com.cfo.stock.web.velocity.directives;

import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.tools.view.context.ChainedContext;

import com.jrj.common.velocity.directives.AbstractDirective;

public class ConfigDirective extends AbstractDirective{
	Logger log=Logger.getLogger(getClass());
	private String key="base";
	private static final ResourceBundle RESOURCE_BUNDLE=ResourceBundle.getBundle("global");
	@Override
	protected boolean doRender(InternalContextAdapter adapter,
			ChainedContext context, Writer writer, Node node) throws IOException,
			ResourceNotFoundException, ParseErrorException,
			MethodInvocationException {
		try{
			SimpleNode keyParam=null;
			if(node.jjtGetNumChildren()>0){
				keyParam=(SimpleNode)node.jjtGetChild(0);
			}
			if(keyParam!=null){
				this.key=(String)keyParam.value(adapter);
			}
			if(key!=null&&!"".equals(key)){
			String value=RESOURCE_BUNDLE.getString(key);
			writer.write(value);
			writer.flush();
			}
		}catch(Exception e){
			log.error("key-- "+key+" -- 不存在。");
		}
		return true;
	}

	@Override
	public String getName() {
		return "C";
	}

	@Override
	public int getType() {
		return LINE;
	}

}
