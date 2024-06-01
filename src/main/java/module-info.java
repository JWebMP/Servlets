import com.guicedee.guicedinjection.interfaces.IGuiceModule;
import com.jwebmp.core.services.IDynamicRenderingServlet;
import com.jwebmp.servlets.implementations.JWebMPDynamicScriptRenderer;
import com.jwebmp.servlets.implementations.JWebMPJavaScriptDynamicScriptRenderer;
import com.jwebmp.servlets.implementations.JWebMPSiteBinder;

module com.jwebmp.servlets {
    requires transitive com.jwebmp.client;

    requires static lombok;
    requires com.google.guice.extensions.servlet;
    requires com.guicedee.guicedservlets;

    requires org.apache.commons.lang3;
    requires org.jboss.logging;

    opens com.jwebmp.servlets to com.google.guice, com.fasterxml.jackson.databind;
    opens com.jwebmp.servlets.options to com.google.guice, com.fasterxml.jackson.databind;
    opens com.jwebmp.servlets.implementations to com.google.guice, com.fasterxml.jackson.databind;

    provides IGuiceModule with JWebMPSiteBinder;
    provides IDynamicRenderingServlet with JWebMPDynamicScriptRenderer, JWebMPJavaScriptDynamicScriptRenderer;
}