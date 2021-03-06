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
	var sqlStatement = message.getBody();
	uriInfo = getUriInfo(message);
	importPackage(java.util);
	map = new HashMap();
	map = uriInfo.getCustomQueryOptions();
	var deltaToken = map.get("!deltatoken");
	var modifiedSqlStatement = modifySqlStatement(sqlStatement, deltaToken,
			message);
	message.setBody(modifiedSqlStatement);
	return message;
}

function getUriInfo(message) {
	importPackage(org.apache.olingo.odata2.api.uri);
	importPackage(com.sap.gateway.core.ip.component.commons);
	uriInfo = message.getHeaders().get(ODataExchangeHeaderProperty.UriInfo.toString());
	return uriInfo;
}

function modifySqlStatement(sqlStatement, deltaToken, message) {
	var PRODUCT_SQL_SUB_QUERY_AND = " AND PRODUCTID > ";
	var WHERE = " WHERE ";
	var PRODUCT_SQL_SUB_QUERY = " PRODUCTID > ";

	var modifiedSqlStatement = "";
	importPackage(org.apache.olingo.odata2.api.edm);
	entitySet = getUriInfo(message).getStartEntitySet();
	importPackage(com.sap.gateway.core.ip.component.commons);
	odataMethod = message.getHeaders().get(ODataExchangeHeaderProperty.ODataMethod.toString());
	importPackage(org.apache.olingo.odata2.api.uri);
	uriInfo = getUriInfo(message);
	var indexOfOrderBy = sqlStatement.indexOf("Order By");
	if (indexOfOrderBy < 0)
		modifiedSqlStatement += sqlStatement;
	else
		modifiedSqlStatement += sqlStatement.substring(0, indexOfOrderBy);
	if (odataMethod.toString() == "GET_FEED") {
		entitySetName = entitySet.getName();
		if (entitySetName == "Products") {
			if (deltaToken != null && deltaToken.length() > 0) {
				if (sqlStatement.indexOf("WHERE") != -1
						|| sqlStatement.indexOf("where") != -1) {
					modifiedSqlStatement += PRODUCT_SQL_SUB_QUERY_AND;
				} else {
					modifiedSqlStatement += WHERE;
					modifiedSqlStatement += PRODUCT_SQL_SUB_QUERY;
				}
				modifiedSqlStatement += deltaToken + " ";
			}

		}
		if (indexOfOrderBy > 0)
			modifiedSqlStatement += sqlStatement.substring(indexOfOrderBy);
	}
	return modifiedSqlStatement;
}
