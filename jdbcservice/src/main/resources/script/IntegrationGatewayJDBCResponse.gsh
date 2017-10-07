/*
 * The integration developer needs to create the method processData 
 * for delta token handling in JDBC scenario.
 * This method takes Message object of package com.sap.gateway.ip.core.customdev.util
 * which includes helper methods useful for the content developer:
 * 
 * The methods available are:
 * 
 * getBody() : A call to this method returns the payload body. This contains the sql statement 
 * which has to be modified by the content developer.
 * e.g. the query otained may be of the form select * from Products
 * End User will change it to select * from Products where PRODUCTID > '30'
 * This value is determined from delta token value passed in the request
 * 
 * setBody(body): This method sets the modified sql statement
 * 
 * Developer can access other objects like UriInfo using methods like message.getHeaders().get(ODataExchangeHeaderProperty.UriInfo.toString()). 
 * 
 */
 
 import com.sap.gateway.ip.core.customdev.util.Message;
 import org.apache.olingo.odata2.api.edm.EdmEntitySet;
 import com.sap.gateway.core.ip.component.commons.ODataMethod;
 import java.util.HashMap;
 import java.util.ArrayList;
 import com.sap.gateway.ip.core.customdev.api.ODataCamelExchangeHeaders;
 
 def Message processData(message) {
	def entitySet = getUriInfo(message).getStartEntitySet();
	String entitySetName = entitySet.getName();
	def method = message.getHeaders().get(ODataExchangeHeaderProperty.ODataMethod.toString());
	def aliasMap = new HashMap();
	aliasMap = message.getProperties();
	def alias = aliasMap.get("aliases");
	if (method == ODataMethod.GET_FEED) {
		if ("Products" == entitySetName) {
			deltaTokenAndTombStoneHandlingForProducts(message, alias, entitySet);
		}
	}
	return message;
}

def UriInfo getUriInfo(Message message) {
	UriInfo uriInfo = message.getHeaders().get(ODataExchangeHeaderProperty.UriInfo.toString());
	return uriInfo;
}

def void deltaTokenAndTombStoneHandlingForProducts(Message message, def alias, def entitySet) {
	deletedItems = new ArrayList();
	resultEntities = new ArrayList();
	String columnName = "ISDELETED";

	def dataSourceResponse = new ArrayList();
	dataSourceResponse = message.getBody();
	def customMap = new HashMap();
	customMap = getUriInfo(message).getCustomQueryOptions();
	def deltaToken = customMap.get("!deltatoken");

	if (deltaToken != null && deltaToken.length() > 0) {
		for (def i = 0; i < dataSourceResponse.size(); i++) {
			if (dataSourceResponse.get(i).get(columnName).toString() == "true")
				deletedItems.add(dataSourceResponse.get(i));
			else
				resultEntities.add(dataSourceResponse.get(i));

		}
		message.setBody(resultEntities);
		message.setHeader(
				ODataCamelExchangeHeaders.DELETED_ENTITIES.toString(),
				deletedItems);

	}
	token = generateDeltaToken(dataSourceResponse, message, "Products",
			"PRODUCTID", entitySet);
	message.setHeader(
			ODataCamelExchangeHeaders.IS_DELTA_IMPLEMENTED.toString(), true);
	message.setHeader(ODataCamelExchangeHeaders.DELTA_TOKEN.toString(), token);
}

def String generateDeltaToken(def jdbcResponse, Message message, String entitySetName, String columnName,
		def entitySet) {
	if (entitySet.getName() == entitySetName) {
		String tokenBuilder = "";
		tokenBuilder = calculateDeltaToken(jdbcResponse, tokenBuilder,
				columnName);
		return tokenBuilder.toString();

	} else
		return null;
}

def calculateDeltaToken(def response, def token, String columnName) {
	def currentTokenValue = 0;
	def highestTokenValue = 0;
	for (def i = 0; i < response.size(); i++) {
		if (response.get(i).get(columnName) != null) {
			currentTokenValue = response.get(i).get(columnName);
			if (currentTokenValue > highestTokenValue)
				highestTokenValue = currentTokenValue;
		}
	}
	token += highestTokenValue.toString();
	return token;
}
