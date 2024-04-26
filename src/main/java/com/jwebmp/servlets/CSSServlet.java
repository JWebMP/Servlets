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
import com.jwebmp.core.base.interfaces.IComponentStyleBase;
import com.jwebmp.interception.services.StaticStrings;
import com.jwebmp.core.services.IPage;

import java.nio.charset.StandardCharsets;

/**
 * This Servlet supplies all the JavaScript for a given HTML Page
 *
 * @author GedMarc
 */
@Singleton
public class CSSServlet
        extends JWDefaultServlet
{
    @Override
    public void perform()
    {
        @SuppressWarnings("MismatchedQueryAndUpdateOfStringBuilder")
        StringBuilder scripts = new StringBuilder();
        IPage<?> page = IGuiceContext.get(IPage.class);
        StringBuilder css = ((IComponentStyleBase) page.getBody())
                .renderCss(0);
        scripts.append(css);
        writeOutput(css, StaticStrings.HTML_HEADER_CSS, StandardCharsets.UTF_8);
    }
}
