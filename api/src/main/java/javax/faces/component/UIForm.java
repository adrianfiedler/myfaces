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
package javax.faces.component;

import org.apache.myfaces.core.api.shared._ComponentUtils;
import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFComponent;
import org.apache.myfaces.buildtools.maven2.plugin.builder.annotation.JSFProperty;

import javax.faces.FacesException;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.PostValidateEvent;
import javax.faces.event.PreValidateEvent;
import javax.faces.view.Location;

import java.util.Collection;

/**
 * see Javadoc of <a href="http://java.sun.com/javaee/javaserverfaces/1.2/docs/api/index.html">JSF Specification</a>
 */
@JSFComponent(type = "javax.faces.Form", family = "javax.faces.Form")
public class UIForm extends UIComponentBase implements NamingContainer, UniqueIdVendor
{

    /**
     * 
     * {@inheritDoc}
     * 
     * @since 2.0
     */
    @Override
    public String createUniqueId(FacesContext context, String seed)
    {
        StringBuilder bld = null;
        
        // When prependId is set to false, it is necessary to append an unique
        // prefix to ensure the generated ids are unique, but that's only necessary
        // when no seed is provided. If a seed is provided, that one is already unique
        // for all the view, so the following logic is not necessary.
        if (!isPrependId() && seed == null)
        {
            bld = new StringBuilder();
            UniqueIdVendor parentUniqueIdVendor = _ComponentUtils.closest(UniqueIdVendor.class, this);
            if (parentUniqueIdVendor == null)
            {
                UIViewRoot viewRoot = context.getViewRoot();
                if (viewRoot != null)
                {
                    bld.append(viewRoot.createUniqueId());
                    bld.append('_');
                }
                else
                {
                    // The RI throws a NPE
                    String location = getComponentLocation(this);
                    throw new FacesException("Cannot create clientId. No id is assigned for component"
                            + " to create an id and UIViewRoot is not defined: "
                            + _ComponentUtils.getPathToComponent(this)
                            + (location != null ? " created from: " + location : ""));
                }
            }
            else
            {
                bld.append(parentUniqueIdVendor.createUniqueId(context, null));
                bld.append('_');
            }
        }
        else
        {
            bld = _getSharedStringBuilder(context);
        }

        // Generate an identifier for a component. The identifier will be prefixed with
        // UNIQUE_ID_PREFIX, and will be unique within this UIViewRoot.
        if(seed == null)
        {
            Integer uniqueIdCounter = (Integer) getStateHelper().get(PropertyKeys.uniqueIdCounter);
            uniqueIdCounter = (uniqueIdCounter == null) ? 0 : uniqueIdCounter;
            getStateHelper().put(PropertyKeys.uniqueIdCounter, (uniqueIdCounter+1));
            return bld.append(UIViewRoot.UNIQUE_ID_PREFIX).append(uniqueIdCounter).toString();    
        }
        // Optionally, a unique seed value can be supplied by component creators
        // which should be included in the generated unique id.
        else
        {
            return bld.append(UIViewRoot.UNIQUE_ID_PREFIX).append(seed).toString();
        }
    }
    
    public boolean isSubmitted()
    {
        return (Boolean) getTransientStateHelper().getTransient(PropertyKeys.submitted, false);
    }

    public void setSubmitted(boolean submitted)
    {
        getTransientStateHelper().putTransient(PropertyKeys.submitted, submitted);
    }

    @Override
    public void processDecodes(FacesContext context)
    {
        if (context == null)
        {
            throw new NullPointerException("context");
        }
        try
        {
            setCachedFacesContext(context);
            try
            {
                pushComponentToEL(context, this);
                
                decode(context);
                
                if (!isSubmitted())
                {
                    return;
                }

                if (getFacetCount() > 0)
                {
                    for (UIComponent facet : getFacets().values())
                    {
                        facet.processDecodes(context);
                    }
                }
                
                for (int i = 0, childCount = getChildCount(); i < childCount; i++)
                {
                    UIComponent child = getChildren().get(i);
                    child.processDecodes(context);
                }
            }
            finally
            {
                popComponentFromEL(context);
            }
        }
        finally
        {
            setCachedFacesContext(null);
        }
    }

    @Override
    public void processValidators(FacesContext context)
    {
        if (context == null)
        {
            throw new NullPointerException("context");
        }
        
        try
        {
            setCachedFacesContext(context);
            try
            {
                pushComponentToEL(context, this);
                // SF issue #1050022: a form used within a datatable will loose it's submitted state
                // as UIForm is no EditableValueHolder and therefore it's state is not saved/restored by UIData
                // to restore the submitted state we call decode here again
                if (!isSubmitted())
                {
                    decode(context);
                }
                if (!isSubmitted())
                {
                    return;
                }

                //Pre validation event dispatch for component
                context.getApplication().publishEvent(context, PreValidateEvent.class, getClass(), this);

                if (getFacetCount() > 0)
                {
                    for (UIComponent facet : getFacets().values())
                    {
                        facet.processValidators(context);
                    }
                }
                
                for (int i = 0, childCount = getChildCount(); i < childCount; i++)
                {
                    UIComponent child = getChildren().get(i);
                    child.processValidators(context);
                }
                
            }
            finally
            {
                context.getApplication().publishEvent(context,  PostValidateEvent.class, getClass(), this);
                popComponentFromEL(context);
            }
        }
        finally
        {
            setCachedFacesContext(null);
        }
    }

    @Override
    public void processUpdates(FacesContext context)
    {
        if (context == null)
        {
            throw new NullPointerException("context");
        }
        
        try
        {
            setCachedFacesContext(context);
            try
            {
                pushComponentToEL(context, this);
                // SF issue #1050022: a form used within a datatable will loose it's submitted state
                // as UIForm is no EditableValueHolder and therefore it's state is not saved/restored by UIData
                // to restore the submitted state we call decode here again
                if (!isSubmitted())
                {
                    decode(context);
                }
                if (!isSubmitted())
                {
                    return;
                }

                if (getFacetCount() > 0)
                {
                    for (UIComponent facet : getFacets().values())
                    {
                        facet.processUpdates(context);
                    }
                }
                
                for (int i = 0, childCount = getChildCount(); i < childCount; i++)
                {
                    UIComponent child = getChildren().get(i);
                    child.processUpdates(context);
                }

            }
            finally
            {
                popComponentFromEL(context);
            }
        }
        finally
        {
            setCachedFacesContext(null);
        }
    }

    enum PropertyKeys
    {
         prependId,
         uniqueIdCounter,
         submitted,
    }
    
    @Override
    public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback) throws FacesException {
        if (isPrependId()) {
            String baseClientId = getClientId(context);

            // skip if the component is not a children of the UIForm
            if (!clientId.startsWith(baseClientId)) {
                return false;
            }
        }

        return super.invokeOnComponent(context, clientId, callback);
    }
    
    @Override
    public boolean visitTree(VisitContext context, VisitCallback callback)
    {
        if (!isPrependId())
        {
            // Since the container client id will not be added to child clientId,
            // It is not possible to take advantage of NamingContainer interface
            // and prevent visit child nodes. Just do it as default.
            return super.visitTree(context, callback);
        }
        else
        {
            pushComponentToEL(context.getFacesContext(), this);
            boolean isCachedFacesContext = isCachedFacesContext();
            try
            {
                if (!isCachedFacesContext)
                {
                    setCachedFacesContext(context.getFacesContext());
                }

                if (!isVisitable(context))
                {
                    return false;
                }

                VisitResult res = context.invokeVisitCallback(this, callback);
                switch (res)
                {
                    //we are done nothing has to be processed anymore
                    case COMPLETE:
                        return true;

                    case REJECT:
                        return false;

                    //accept
                    default:
                        // Take advantage of the fact this is a NamingContainer
                        // and we can know if there are ids to visit inside it
                        Collection<String> subtreeIdsToVisit = context.getSubtreeIdsToVisit(this);

                        if (subtreeIdsToVisit != null && !subtreeIdsToVisit.isEmpty())
                        {
                            if (getFacetCount() > 0)
                            {
                                for (UIComponent facet : getFacets().values())
                                {
                                    if (facet.visitTree(context, callback))
                                    {
                                        return true;
                                    }
                                }
                            }
                            for (int i = 0, childCount = getChildCount(); i < childCount; i++)
                            {
                                UIComponent child = getChildren().get(i);
                                if (child.visitTree(context, callback))
                                {
                                    return true;
                                }
                            }
                        }
                        return false;
                }
            }
            finally
            {
                //all components must call popComponentFromEl after visiting is finished
                popComponentFromEL(context.getFacesContext());
                if (!isCachedFacesContext)
                {
                    setCachedFacesContext(null);
                }
            }
        }
    }

    // ------------------ GENERATED CODE BEGIN (do not modify!) --------------------

    public static final String COMPONENT_TYPE = "javax.faces.Form";
    public static final String COMPONENT_FAMILY = "javax.faces.Form";
    private static final String DEFAULT_RENDERER_TYPE = "javax.faces.Form";

    public UIForm()
    {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }

    @Override
    public String getFamily()
    {
        return COMPONENT_FAMILY;
    }

    private String getComponentLocation(UIComponent component)
    {
        Location location = (Location) component.getAttributes().get(UIComponent.VIEW_LOCATION_KEY);
        if (location != null)
        {
            return location.toString();
        }
        return null;
    }

    // ------------------ GENERATED CODE END ---------------------------------------

    @Override
    public String getContainerClientId(FacesContext ctx)
    {
        if (isPrependId())
        {
            return super.getContainerClientId(ctx);
        }

        UIComponent parentNamingContainer = _ComponentUtils.findParentNamingContainer(this, false);
        if (parentNamingContainer != null)
        {
            return parentNamingContainer.getContainerClientId(ctx);
        }

        return null;
    }

    @JSFProperty(defaultValue = "true")
    public boolean isPrependId()
    {
        return (Boolean) getStateHelper().eval(PropertyKeys.prependId, true);
    }

    public void setPrependId(boolean prependId)
    {
        getStateHelper().put(PropertyKeys.prependId, prependId ); 
    }
}
