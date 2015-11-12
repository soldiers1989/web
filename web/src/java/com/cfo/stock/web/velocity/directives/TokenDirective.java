package com.cfo.stock.web.velocity.directives;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.tools.view.context.ChainedContext;

import com.cfo.common.utils.TokenHelper;
import com.jrj.common.velocity.directives.AbstractDirective;
@SuppressWarnings("deprecation")
public class TokenDirective extends AbstractDirective {
	private final String TEMPLATE="template/vm/cfoToken.vm";

	@Override
	protected boolean doRender(InternalContextAdapter arg0,
			ChainedContext context, Writer writer, Node arg3) throws IOException,
			ResourceNotFoundException, ParseErrorException,
			MethodInvocationException {
		HttpServletRequest request=context.getRequest();
		HttpServletResponse response=context.getResponse();
		VelocityContext ctx = new VelocityContext();
		String name=TokenHelper.DEFAULT_TOKEN_NAME;
		ctx.put("name", name);
		ctx.put("token", TokenHelper.setToken(request,response,name));
		ctx.put("tokenNameField", TokenHelper.TOKEN_NAME_FIELD);
		Template  template=context.getVelocityEngine().getTemplate(TEMPLATE);
		template.merge(ctx, writer);
		writer.flush();
		return false;
	}

	@Override
	public String getName() {
		return "token";
	}

	@Override
	public int getType() {
		return LINE;
	}

}
