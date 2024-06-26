/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.apache.ofbiz.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Sides;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.GeneralException;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.webapp.view.ApacheFopWorker;
import org.apache.ofbiz.widget.model.ThemeFactory;
import org.apache.ofbiz.widget.renderer.ScreenRenderer;
import org.apache.ofbiz.widget.renderer.ScreenStringRenderer;
import org.apache.ofbiz.widget.renderer.VisualTheme;
import org.apache.ofbiz.widget.renderer.macro.MacroScreenRenderer;
import org.xml.sax.SAXException;

import freemarker.template.TemplateException;

public class BusinessPrintServices {
    private static final String MODULE = BusinessPrintServices.class.getName();
    private static final String RESOURCE = "BusinessUiLabels";

    public static Map<String, Object> printReportPdf(DispatchContext dctx, Map<String, ? extends Object> context) {
        Locale locale = (Locale) context.get("locale");
        String screenLocation = "component://business/widget/business/BusinessReportScreens.xml";
        String reportScreenName = "BusinessReport";
        VisualTheme visualTheme = (VisualTheme) context.get("visualTheme");
        if (visualTheme == null) {
            visualTheme = ThemeFactory.resolveVisualTheme(null);
        }
        if (visualTheme == null) visualTheme = ThemeFactory.resolveVisualTheme(null);
        Map<String, Object> workContext = new HashMap<>();
        workContext.putAll(context);

        // render a screen to get the XML document
        Writer reportWriter = new StringWriter();
        ScreenStringRenderer screenStringRenderer = null;
        try {
            screenStringRenderer = new MacroScreenRenderer("screen", visualTheme.getModelTheme().getScreenRendererLocation("screen"));
        } catch (TemplateException e) {
            String errMsg = UtilProperties.getMessage(RESOURCE, "BusinessGeneralErrorRenderingScreen", UtilMisc.toMap("errorString", e.toString()),
                    locale);
            Debug.logError(e, errMsg, MODULE);
            return ServiceUtil.returnError(errMsg);
        } catch (IOException e) {
            String errMsg = UtilProperties.getMessage(RESOURCE, "BusinessIOErrorRenderingScreen", UtilMisc.toMap("errorString", e.toString()), locale);
            Debug.logError(e, errMsg, MODULE);
            return ServiceUtil.returnError(errMsg);
        }

        ScreenRenderer reportScreenRenderer = new ScreenRenderer(reportWriter, null, screenStringRenderer);
        reportScreenRenderer.populateContextForService(dctx, workContext);

        // put the businessId in the screen context, is a parameter coming into the service
        //reportScreenRenderer.getContext().put("businessId", context.get("businessId"));

        try {
            reportScreenRenderer.render(screenLocation, reportScreenName);
        } catch (GeneralException e) {
            String errMsg = UtilProperties.getMessage(RESOURCE, "BusinessGeneralErrorRenderingScreen", UtilMisc.toMap("errorString", e.toString()),
                    locale);
            Debug.logError(e, errMsg, MODULE);
            return ServiceUtil.returnError(errMsg);
        } catch (IOException e) {
            String errMsg = UtilProperties.getMessage(RESOURCE, "BusinessIOErrorRenderingScreen", UtilMisc.toMap("errorString", e.toString()), locale);
            Debug.logError(e, errMsg, MODULE);
            return ServiceUtil.returnError(errMsg);
        } catch (SAXException e) {
            String errMsg = UtilProperties.getMessage(RESOURCE, "BusinessSAXErrorRenderingScreen", UtilMisc.toMap("errorString", e.toString()),
                    locale);
            Debug.logError(e, errMsg, MODULE);
            return ServiceUtil.returnError(errMsg);
        } catch (ParserConfigurationException e) {
            String errMsg = UtilProperties.getMessage(RESOURCE, "BusinessParserConfigurationErrorRenderingScreen", UtilMisc.toMap("errorString",
                    e.toString()), locale);
            Debug.logError(e, errMsg, MODULE);
            return ServiceUtil.returnError(errMsg);
        }

        // set the input source (XSL-FO) and generate the PDF
        StreamSource src = new StreamSource(new StringReader(reportWriter.toString()));

        // create the output stream for the generation
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Fop fop = ApacheFopWorker.createFopInstance(out, MimeConstants.MIME_PDF);
            ApacheFopWorker.transform(src, null, fop);
        } catch (FOPException e) {
            Debug.logError(e, MODULE);
        }
        /*
        // set the content type and length
        response.setContentType("application/pdf");
        response.setContentLength(out.size());

        // write to the browser
        try {
            out.writeTo(response.getOutputStream());
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new ViewHandlerException("Unable write to browser OutputStream", e);
        }
        */

        DocFlavor docFlavor = DocFlavor.BYTE_ARRAY.PDF;
        Doc myDoc = new SimpleDoc(out.toByteArray(), docFlavor, null);
        PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
        aset.add(new Copies(1));
        //aset.add(MediaSize.A4);
        aset.add(Sides.ONE_SIDED);

        PrintService[] services = PrintServiceLookup.lookupPrintServices(docFlavor, aset);
        if (services.length > 0) {
            DocPrintJob job = services[0].createPrintJob();
            try {
                job.print(myDoc, aset);
            } catch (PrintException pe) {
                String errMsg = UtilProperties.getMessage(RESOURCE, "BusinessUnableToPrintPDFFromXSL-FO", UtilMisc.toMap("errorString",
                        pe.toString()), locale);
                Debug.logError(pe, errMsg, MODULE);
                return ServiceUtil.returnError(errMsg);
            }
        }

        return ServiceUtil.returnSuccess();
    }
}
