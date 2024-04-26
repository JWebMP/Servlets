package com.jwebmp.servlets.implementations;

import com.google.common.base.Strings;
import com.guicedee.services.jsonrepresentation.json.StaticStrings;
import com.jwebmp.core.base.html.attributes.*;
import com.jwebmp.core.base.html.interfaces.GlobalFeatures;
import com.jwebmp.core.base.interfaces.IComponentBase;
import com.jwebmp.core.base.interfaces.IComponentFeatureBase;
import com.jwebmp.core.base.interfaces.IComponentHTMLAttributeBase;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;
import com.jwebmp.core.services.*;
import com.jwebmp.interception.services.ScriptProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.jboss.logmanager.Level;

import java.util.ServiceLoader;

import static com.guicedee.client.IGuiceContext.*;


public class JWebMPJavaScriptDynamicScriptRenderer implements IDynamicRenderingServlet<JWebMPJavaScriptDynamicScriptRenderer>
{
    public static boolean renderJavascript = true;

    @Override
    public String getScriptLocation(IPage<?> page)
    {
        String queryParams = "";
        try
        {
            HttpServletRequest hsr = get(HttpServletRequest.class);
            queryParams = hsr.getQueryString();
        }
        catch (Throwable T)
        {
            log.log(Level.SEVERE, "Are you in a servlet request scope? Cannot get the query parameters from HTTPServletRequest", T);
        }
        return JWebMPSiteBinder
                .getJavaScriptLocation()
                .replaceAll(StaticStrings.STRING_FORWARD_SLASH, StaticStrings.STRING_EMPTY) + "?" + Strings.nullToEmpty(queryParams);
    }

    @Override
    public IComponentHierarchyBase<?, ?> renderScript(IPage<?> page)
    {
        return getJavascriptScript(page);
    }

    public IComponentHierarchyBase<?, ?> newScript(String contents)
    {
        ServiceLoader<ScriptProvider> load = ServiceLoader.load(ScriptProvider.class);
        for (ScriptProvider scriptProvider : load)
        {
            IComponentHierarchyBase<?, ?> iComponentHierarchyBase = scriptProvider.produceScript();
            ((IComponentHTMLAttributeBase) iComponentHierarchyBase).addAttribute(ScriptAttributes.Type, StaticStrings.HTML_HEADER_JAVASCRIPT);
            ((IComponentBase<?>) iComponentHierarchyBase).setText(contents);
            return iComponentHierarchyBase;
        }
        log.warning("No script provider was found to produce the script tags for servlet access");
        return null;
    }

    /**
     * Method getJavascriptScript ...
     *
     * @return Script
     */
    private IComponentHierarchyBase<?, ?> getJavascriptScript(IPage<?> pager)
    {
        if (renderJavascript)
        {
            StringBuilder js = ((IComponentFeatureBase<GlobalFeatures, ?>) pager).renderJavascript();
            return newScript(((IComponentBase<?>) pager).getNewLine() + js);
        }
        return null;
    }

    /**
     * Default Sort Order INTEGER.MAX - 900
     *
     * @return Default Sort Order INTEGER.MAX - 900
     */
    @Override
    public Integer sortOrder()
    {
        return Integer.MAX_VALUE - 900;
    }

    @Override
    public boolean enabled()
    {
        return false;
    }
}
