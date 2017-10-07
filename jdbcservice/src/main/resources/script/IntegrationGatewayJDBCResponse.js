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
function processData(message) {
	importPackage(com.sap.gateway.ip.core.customdev.util);
	importPackage(org.apache.olingo.odata2.api.edm);
	entitySet = getUriInfo(message).getStartEntitySet();
	entitySetName = entitySet.getName();
	importPackage(com.sap.gateway.core.ip.component.commons);
	method = message.getHeaders().get(ODataExchangeHeaderProperty.ODataMethod.toString());
	importPackage(java.util);
	aliasMap = new HashMap();
	aliasMap = message.getProperties();
	alias = aliasMap.get("aliases");
	if (method == ODataMethod.GET_FEED) {
		if ("Products" == entitySetName) {
			deltaTokenAndTombStoneHandlingForProducts(message, alias, entitySet);
		}
	}
	return message;
}

function getUriInfo(message) {
	importPackage(org.apache.olingo.odata2.api.uri);
	importPackage(com.sap.gateway.core.ip.component.commons);
	uriInfo = message.getHeaders().get(ODataExchangeHeaderProperty.UriInfo.toString());
	return uriInfo;
}

function deltaTokenAndTombStoneHandlingForProducts(message, alias, entitySet) {
	importPackage(java.util);
	deletedItems = new ArrayList();
	resultEntities = new ArrayList();
	var columnName = "ISDELETED";

	dataSourceResponse = new ArrayList();
	dataSourceResponse = message.getBody();
	customMap = new HashMap();
	customMap = getUriInfo(message).getCustomQueryOptions();
	deltaToken = customMap.get("!deltatoken");

	if (deltaToken != null && deltaToken.length() > 0) {
		for (var i = 0; i < dataSourceResponse.size(); i++) {
			if (dataSourceResponse.get(i).get(columnName).toString() == "true")
				deletedItems.add(dataSourceResponse.get(i));
			else
				resultEntities.add(dataSourceResponse.get(i));

		}
		message.setBody(resultEntities);
		importPackage(com.sap.gateway.ip.core.customdev.api);
		message.setHeader(
				ODataCamelExchangeHeaders.DELETED_ENTITIES.toString(),
				deletedItems);

	}
	token = generateDeltaToken(dataSourceResponse, message, "Products",
			"PRODUCTID", entitySet);
	importPackage(com.sap.gateway.ip.core.customdev.api);
	message.setHeader(
			ODataCamelExchangeHeaders.IS_DELTA_IMPLEMENTED.toString(), true);
	message.setHeader(ODataCamelExchangeHeaders.DELTA_TOKEN.toString(), token);
}

function generateDeltaToken(jdbcResponse, message, entitySetName, columnName,
		entitySet) {
	if (entitySet.getName() == entitySetName) {
		tokenBuilder = "";
		tokenBuilder = calculateDeltaToken(jdbcResponse, tokenBuilder,
				columnName);
		return tokenBuilder.toString();

	} else
		return null;
}

function calculateDeltaToken(response, token, columnName) {
	var currentTokenValue = 0;
	var highestTokenValue = 0;
	importPackage(java.util);
	for (var i = 0; i < response.size(); i++) {
		if (response.get(i).get(columnName) != null) {
			currentTokenValue = response.get(i).get(columnName);
			if (currentTokenValue > highestTokenValue)
				highestTokenValue = currentTokenValue;
		}
	}
	token += highestTokenValue.toString();
	return token;
}