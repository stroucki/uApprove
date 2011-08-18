/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.SWITCH.aai.uApprove;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ch.SWITCH.aai.uApprove.tou.ToUModule;

/**
 * uApprove request intercepter.
 */
public class Intercepter implements Filter {

    /** Class logger. */
    private final Logger logger = LoggerFactory.getLogger(Intercepter.class);

    private ServletContext servletContext;

    private ToUModule touModule;

    /** {@inheritDoc} */
    public void init(final FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
        final WebApplicationContext appContext =
                WebApplicationContextUtils.getRequiredWebApplicationContext(filterConfig.getServletContext());
        touModule = (ToUModule) appContext.getBean("uApprove.touModule", ToUModule.class);
        logger.debug("uApprove initialized.");
    }

    /** {@inheritDoc} */
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        Assert.isInstanceOf(HttpServletRequest.class, request, "Not an HttpServletRequest.");
        Assert.isInstanceOf(HttpServletResponse.class, response, "Not an HttpServletResponse.");
        intercept((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    public void
            intercept(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
                    throws IOException, ServletException {

        if (!LoginHelper.isAuthenticated(servletContext, request)) {
            logger.trace("Request is not authenticated.");
            chain.doFilter(request, response);
            return;
        }

        final String principalName = LoginHelper.getPrincipalName(servletContext, request);
        final String relyingPartyId = LoginHelper.getRelyingPartyId(servletContext, request);
        logger.debug("uApprove access from {} to {}.", principalName, relyingPartyId);

        if (!touModule.isToUAccepted(principalName)) {
            LoginHelper.redirectToServlet(request, response, "/uApprove/TermsOfUse");
            return;
        } else {
            chain.doFilter(request, response);
            return;
        }

    }

    /** {@inheritDoc} */
    public void destroy() {
        logger.debug("uApprove destroyed.");
    }

}