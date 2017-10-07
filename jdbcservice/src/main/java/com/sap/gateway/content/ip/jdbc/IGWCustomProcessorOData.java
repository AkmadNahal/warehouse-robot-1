package com.sap.gateway.content.ip.jdbc;

import java.util.List;
import java.util.Map;
import org.apache.camel.Exchange;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.uri.NavigationSegment;
import org.apache.olingo.odata2.api.uri.UriInfo;
import com.sap.gateway.core.ip.component.commons.ODataExchangeHeaderProperty;
import com.sap.gateway.core.ip.component.commons.ODataMethod;
import com.sap.gateway.ip.core.customdev.api.IGWCustomProcessor;


public abstract class IGWCustomProcessorOData extends IGWCustomProcessor {

	@Override
	public abstract void extendProcess(Exchange exchange) throws Exception;

	public final UriInfo getUriInfo(Exchange exchange) {
		UriInfo uriInfo = (UriInfo) exchange.getIn().getHeader(
				ODataExchangeHeaderProperty.UriInfo.toString());
		return uriInfo;
	}

	public final ODataMethod getOdataMethod(Exchange exchange) {
		ODataMethod odataMethod = (ODataMethod) exchange.getIn().getHeader(
				ODataExchangeHeaderProperty.ODataMethod.toString());
		return odataMethod;
	}

	public final List<String> getStartEntitySetProperties(Exchange exchange)
			throws IGWCustomDevException {
		List<String> propertyNames = null;
		try {
			propertyNames = getStartEntityType(exchange).getPropertyNames();
		} catch (EdmException e) {
			throw IGWCustomDevException.throwException(e.getMessage(),
					HttpStatusCodes.INTERNAL_SERVER_ERROR);
		}
		return propertyNames;
	}

	public final EdmEntityType getStartEntityType(Exchange exchange)
			throws IGWCustomDevException {
		EdmEntityType entityType = null;
		try {
			EdmEntitySet entitySet = getStartEntitySet(exchange);
			entityType = entitySet.getEntityType();
		} catch (EdmException e) {
			throw IGWCustomDevException.throwException(e.getMessage(),
					HttpStatusCodes.INTERNAL_SERVER_ERROR);
		}
		return entityType;
	}

	public final String getStartEntityTypeName(Exchange exchange)
			throws IGWCustomDevException {
		try {
			return getStartEntityType(exchange).getName();
		} catch (EdmException e) {
			throw IGWCustomDevException.throwException(e.getMessage(),
					HttpStatusCodes.INTERNAL_SERVER_ERROR);
		}
	}

	public final EdmEntitySet getStartEntitySet(Exchange exchange)
			throws IGWCustomDevException {
		EdmEntitySet entitySet = null;
		UriInfo uriInfo = getUriInfo(exchange);
		entitySet = uriInfo.getStartEntitySet();
		return entitySet;
	}

	public final String getStartEntitySetName(Exchange exchange)
			throws IGWCustomDevException {
		try {
			return getStartEntitySet(exchange).getName();
		} catch (EdmException e) {
			throw IGWCustomDevException.throwException(e.getMessage(),
					HttpStatusCodes.INTERNAL_SERVER_ERROR);
		}
	}

	public final ODataContext getOdataContext(Exchange exchange) {
		ODataContext context = (ODataContext) exchange.getIn().getHeader(
				ODataExchangeHeaderProperty.ODataContext.toString());
		return context;
	}

	public final Map<String, List<String>> getHttpHeaders(Exchange exchange) {
		ODataContext context = getOdataContext(exchange);
		return context.getRequestHeaders();
	}

	public final List<NavigationSegment> getNavigationSegments(Exchange exchange) {
		UriInfo uriInfo = getUriInfo(exchange);
		return uriInfo.getNavigationSegments();
	}

	public final Map<String, String> getCustomQueryOptions(Exchange exchange) {
		UriInfo uriInfo = getUriInfo(exchange);
		return uriInfo.getCustomQueryOptions();
	}

}
