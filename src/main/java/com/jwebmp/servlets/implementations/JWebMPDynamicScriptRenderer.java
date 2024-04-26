package com.jwebmp.servlets.implementations;

import com.google.common.base.Strings;
import com.google.inject.*;
import com.guicedee.client.*;
import com.guicedee.services.jsonrepresentation.json.StaticStrings;
import com.jwebmp.core.base.html.attributes.*;
import com.jwebmp.core.base.interfaces.IComponentBase;
import com.jwebmp.core.base.interfaces.IComponentHTMLAttributeBase;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;
import com.jwebmp.core.generics.FileTemplates;
import com.jwebmp.core.services.*;
import com.jwebmp.interception.services.ScriptProvider;
import com.jwebmp.servlets.JWScriptServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ServiceLoader;

import static com.guicedee.client.IGuiceContext.*;

public class JWebMPDynamicScriptRenderer implements IDynamicRenderingServlet<JWebMPDynamicScriptRenderer>
{
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

        }
        return JWebMPSiteBinder
                .getJWScriptLocation()
                .replaceAll(StaticStrings.STRING_FORWARD_SLASH, StaticStrings.STRING_EMPTY) + "?" + Strings.nullToEmpty(queryParams);
    }

    @Override
    public IComponentHierarchyBase<?, ?> renderScript(IPage<?> page)
    {
        return getSiteLoaderScript();
    }

    private static final String[] HEADERS_TO_TRY = {"X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"};

    private String getClientIpAddress(HttpServletRequest request)
    {
        for (String header : HEADERS_TO_TRY)
        {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip))
            {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * Method getSiteLoaderScript returns the siteLoaderScript of this ScriptsDynamicPageConfigurator object.
     *
     * @return the siteLoaderScript (type Script<?,?>) of this ScriptsDynamicPageConfigurator object.
     */
    private IComponentHierarchyBase<?, ?> getSiteLoaderScript()
    {
        FileTemplates.getFileTemplate(JWScriptServlet.class, JWScriptServlet.FILE_TEMPLATE_NAME, "siteloader");
        
        FileTemplates
                .getTemplateVariables()
                .put("SITEADDRESSINSERT", new StringBuilder(SessionHelper.getServerPath()));
        FileTemplates
                .getTemplateVariables()
                .put("ROOTADDRESSINSERT", new StringBuilder(SessionHelper.getServerRootPath()));

        try
        {
            FileTemplates
                    .getTemplateVariables()
                    .put("PAGECLASS",
                         new StringBuilder(IGuiceContext
                                                   .get(IPage.class)
                                                   .getClass()
                                                   .getCanonicalName()));

        }
        catch (ProvisionException | OutOfScopeException e)
        {
            FileTemplates
                    .getTemplateVariables()
                    .put("PAGECLASS", new StringBuilder());
        }

        try
        {
            HttpServletRequest hsr = get(HttpServletRequest.class);
            FileTemplates
                    .getTemplateVariables()
                    .put("%USERAGENT%", new StringBuilder(hsr.getHeader("user-agent")));

            String ipAddress = getClientIpAddress(hsr);
            if ("[::1]".equals(ipAddress))
            {
                ipAddress = "localhost";
            }
            if ("127.0.0.1".equals(ipAddress))
            {
                ipAddress = "localhost";
            }
            if ("[0:0:0:0:0:0:0:1]".equals(ipAddress))
            {
                ipAddress = "localhost";
            }
            FileTemplates
                    .getTemplateVariables()
                    .put("%MYIP%", new StringBuilder(ipAddress));

            FileTemplates
                    .getTemplateVariables()
                    .put("%REFERER%", new StringBuilder(hsr.getHeader("referer")));
        }
        catch (Throwable T)
        {

        }

        StringBuilder jsScript = FileTemplates.renderTemplateScripts(JWScriptServlet.FILE_TEMPLATE_NAME);
        if (!jsScript
                .toString()
                .trim()
                .isEmpty())
        {
            return newScript(jsScript.toString());
        }
        return newScript("could not find site loader script");
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
     * Default Sort Order INTEGER.MAX - 1000
     *
     * @return INTEGER.MAX - 1000
     */
    @Override
    public Integer sortOrder()
    {
        return Integer.MAX_VALUE - 1000;
    }

    @Override
    public boolean enabled()
    {
        return false;
    }
}
