/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.cfo.common.utils;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.cfo.common.session.HttpSessionWrapper;
import com.cfo.common.session.SessionFactory;
import com.jrj.common.utils.DateUtil;

/**
 * TokenHelper
 *
 */
public class TokenHelper {

    /**
     * The default name to map the token value
     */
    public static final String DEFAULT_TOKEN_NAME = "xjb.token";

    /**
     * The name of the field which will hold the token name
     */
    public static final String TOKEN_NAME_FIELD = "xjb.token.name";
    private static final Logger LOG = Logger.getLogger(TokenHelper.class);
    private static final Random RANDOM = new Random();


    /**
     * Sets a transaction token into the session using the default token name.
     *
     * @return the token string
     */
    public static String setToken(HttpServletRequest request, HttpServletResponse response) {
        return setToken(request,response,DEFAULT_TOKEN_NAME);
    }

    /**
     * Sets a transaction token into the session using the provided token name.
     *
     * @param tokenName the name to store into the session with the token as the value
     * @return the token string
     */
    public static String setToken(HttpServletRequest request, HttpServletResponse response,String tokenName) {
        HttpSessionWrapper session = SessionFactory.getInstance(request, response).getSession();
        String token = generateGUID();
        try {
        	System.out.println(tokenName+"-put--"+token);
            session.putValue(tokenName, token,DateUtil.getAddMin(new Date(), 10));
        }
        catch(IllegalStateException e) {
            // WW-1182 explain to user what the problem is
            String msg = "Error creating HttpSession due response is commited to client. You can use the CreateSessionInterceptor or create the HttpSession from your action before the result is rendered to the client: " + e.getMessage();
            LOG.error(msg, e);
            throw new IllegalArgumentException(msg);
        }

        return token;
    }


    /**
     * Gets a transaction token into the session using the default token name.
     *
     * @return token
     */
    public static String getToken(HttpServletRequest request) {
        return getToken(request,DEFAULT_TOKEN_NAME);
    }

    /**
     * Gets the Token value from the params in the ServletActionContext using the given name
     *
     * @param tokenName the name of the parameter which holds the token value
     * @return the token String or null, if the token could not be found
     */
    public static String getToken(HttpServletRequest request,String tokenName) {
        if (tokenName == null ) {
            return null;
        }
        Map params = request.getParameterMap();
        String[] tokens = (String[]) params.get(tokenName);
        String token;

        if ((tokens == null) || (tokens.length < 1)) {
            LOG.warn("Could not find token mapped to token name " + tokenName);

            return null;
        }

        token = tokens[0];
        System.out.println(tokenName+"--get-"+token);
        return token;
    }

    /**
     * Gets the token name from the Parameters in the ServletActionContext
     *
     * @return the token name found in the params, or null if it could not be found
     */
    public static String getTokenName(HttpServletRequest request) {
        Map params = request.getParameterMap();

        if (!params.containsKey(TOKEN_NAME_FIELD)) {
            LOG.warn("Could not find token name in params.");

            return null;
        }

        String[] tokenNames = (String[]) params.get(TOKEN_NAME_FIELD);
        String tokenName;

        if ((tokenNames == null) || (tokenNames.length < 1)) {
            LOG.warn("Got a null or empty token name.");

            return null;
        }

        tokenName = tokenNames[0];

        return tokenName;
    }

    /**
     * Checks for a valid transaction token in the current request params. If a valid token is found, it is
     * removed so the it is not valid again.
     *
     * @return false if there was no token set into the params (check by looking for {@link #TOKEN_NAME_FIELD}), true if a valid token is found
     */
    public static boolean validToken(HttpServletRequest request, HttpServletResponse response) {
        String tokenName = getTokenName(request);

        if (tokenName == null) {
            if (LOG.isDebugEnabled())
                LOG.debug("no token name found -> Invalid token ");
            return false;
        }

        String token = getToken(request,tokenName);

        if (token == null) {
            if (LOG.isDebugEnabled())
                LOG.debug("no token found for token name "+tokenName+" -> Invalid token ");
            return false;
        }
        HttpSessionWrapper session=SessionFactory.getInstance(request, response).getSession();
//        Map session = ActionContext.getContext().getSession();
        String sessionToken = (String) session.getValue(tokenName);
        System.out.println(tokenName+"===check=="+sessionToken);
        if (!token.equals(sessionToken)) {
            return false;
        }

        // remove the token so it won't be used again
        session.removeValue(tokenName);

        return true;
    }

    public static String generateGUID() {
        return new BigInteger(165, RANDOM).toString(36).toUpperCase();
    }
}
