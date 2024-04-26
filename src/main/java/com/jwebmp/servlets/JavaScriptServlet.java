/*
 * Copyright (C) 2017 GedMarc
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jwebmp.servlets;

import com.google.inject.Singleton;
import com.guicedee.client.*;
import com.jwebmp.core.base.interfaces.IComponentFeatureBase;
import com.jwebmp.core.generics.FileTemplates;
import com.jwebmp.interception.services.StaticStrings;

import com.jwebmp.core.base.ajax.AjaxCall;
import com.jwebmp.core.base.ajax.AjaxResponse;
import com.jwebmp.core.services.IPage;
import com.jwebmp.interception.services.AjaxCallIntercepter;

import static com.guicedee.client.IGuiceContext.get;
import static com.jwebmp.interception.services.JWebMPInterceptionBinder.AjaxCallInterceptorKey;
import static com.jwebmp.servlets.implementations.JWebMPJavaScriptDynamicScriptRenderer.renderJavascript;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * This Servlet supplies all the JavaScript for a given HTML Page
 *
 * @author GedMarc
 */
@Singleton
public class JavaScriptServlet
        extends JWDefaultServlet
{
    /**
     * Field scriptReplacement
     */
    private static final String scriptReplacement = "JW_JAVASCRIPT;";

    /**
     * When to perform any commands
     */
    @Override
    public void perform()
    {
        if (renderJavascript)
        {
            IPage<?> page = get(IPage.class);
            for (AjaxCallIntercepter<?> ajaxCallIntercepter : get(AjaxCallInterceptorKey))
            {

                ajaxCallIntercepter.intercept(IGuiceContext.get(AjaxCall.class), IGuiceContext.get(AjaxResponse.class));
            }
            // page.toString(0);
            FileTemplates.removeTemplate(JavaScriptServlet.scriptReplacement);
            FileTemplates.getFileTemplate(JavaScriptServlet.class, JavaScriptServlet.scriptReplacement, "javascriptScript");
            FileTemplates.getTemplateVariables()
                         .put(JavaScriptServlet.scriptReplacement, ((IComponentFeatureBase) page).renderJavascript());
            StringBuilder scripts = FileTemplates.renderTemplateScripts(JavaScriptServlet.scriptReplacement);
            writeOutput(scripts, StaticStrings.HTML_HEADER_JAVASCRIPT, UTF_8);
            FileTemplates.getTemplateVariables()
                         .remove(JavaScriptServlet.scriptReplacement);
        }
    }
}
