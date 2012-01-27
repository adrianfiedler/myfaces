/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.view.facelets.compiler;

/**
 * 
 * @author Leonardo Uribe
 * @since 2.1.0
 */
public final class FaceletsProcessingInstructions
{
    public static final String PROCESS_AS_JSPX = "jspx";
    public static final String PROCESS_AS_XHTML = "xhtml";
    public static final String PROCESS_AS_XML = "xml";
    
    private static final FaceletsProcessingInstructions FACELETS_PROCESSING_XHTML =
        new FaceletsProcessingInstructions(
                false, false, false, false, true, false, true);

    private static final FaceletsProcessingInstructions FACELETS_PROCESSING_XML =
        new FaceletsProcessingInstructions(
                true, true, true, true, true, true, true);

    private static final FaceletsProcessingInstructions FACELETS_PROCESSING_JSPX =
        new FaceletsProcessingInstructions(
                true, true, true, true, false, true, false);

    private final boolean consumeXmlDocType;
    
    private final boolean consumeXmlDeclaration;
    
    private final boolean consumeProcessingInstructions;
    
    private final boolean consumeCDataSections;
    
    private final boolean escapeInlineText;
    
    private final boolean consumeXMLComments;
    
    private final boolean swallowCDataContent;
    
    public final static FaceletsProcessingInstructions getProcessingInstructions(String processAs)
    {
        if (processAs == null)
        {
            return FACELETS_PROCESSING_XHTML;
        }
        else if (PROCESS_AS_XHTML.equals(processAs))
        {
            return FACELETS_PROCESSING_XHTML;
        }
        else if (PROCESS_AS_XML.equals(processAs))
        {
            return FACELETS_PROCESSING_XML;
        }
        else if (PROCESS_AS_JSPX.equals(processAs))
        {
            return FACELETS_PROCESSING_JSPX;
        }
        else
        {
            return FACELETS_PROCESSING_XHTML;
        }
    }
    
    public FaceletsProcessingInstructions(
            boolean consumeXmlDocType,
            boolean consumeXmlDeclaration,
            boolean consumeProcessingInstructions,
            boolean consumeCDataSections, 
            boolean escapeInlineText,
            boolean consumeXMLComments,
            boolean swallowCDataContent)
    {
        super();
        this.consumeXmlDocType = consumeXmlDocType;
        this.consumeXmlDeclaration = consumeXmlDeclaration;
        this.consumeProcessingInstructions = consumeProcessingInstructions;
        this.consumeCDataSections = consumeCDataSections;
        this.escapeInlineText = escapeInlineText;
        this.consumeXMLComments = consumeXMLComments;
        this.swallowCDataContent = swallowCDataContent;
    }

    public boolean isConsumeXmlDocType()
    {
        return consumeXmlDocType;
    }

    public boolean isConsumeXmlDeclaration()
    {
        return consumeXmlDeclaration;
    }

    public boolean isConsumeProcessingInstructions()
    {
        return consumeProcessingInstructions;
    }

    public boolean isConsumeCDataSections()
    {
        return consumeCDataSections;
    }

    public boolean isEscapeInlineText()
    {
        return escapeInlineText;
    }

    public boolean isConsumeXMLComments()
    {
        return consumeXMLComments;
    }

    public boolean isSwallowCDataContent()
    {
        return swallowCDataContent;
    }

}
