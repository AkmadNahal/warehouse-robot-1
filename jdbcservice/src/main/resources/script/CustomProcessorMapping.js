/**
 * 
 */
function processData(message) {
	importPackage(com.sap.gateway.ip.core.customdev.util);
	importPackage(java.util);
	tableMap = new HashMap();
	tableMap.put("Products","DbProducts");
	tableMap.put("Categories","DbCategories");
	entitiesMap = new HashMap();
	prodMap = new HashMap();
	catMap = new HashMap();
	prodMap.put("PRODUCTID","PRODID");
	prodMap.put("NAME","PRODNAME");
	prodMap.put("DESCRIPTION","PRODDESCR");
	prodMap.put("RATING","PRODRATING");
	prodMap.put("PRICE","PRODPRICE");
	prodMap.put("CATEGORYID","PRODCATEGORYID");
	prodMap.put("SUPPLIERID","PRODSUPPLIERID");
	prodMap.put("ISDELETED","PRODISDELETED");
	
	catMap.put("CATEGORYID","CATID");
	catMap.put("CATEGORYNAME","CATNAME");
	catMap.put("DESCRIPTION","CATDESCR");
	catMap.put("PICTURE","CATPIC");
	
	entitiesMap.put("Products", prodMap);
	entitiesMap.put("Categories", catMap);
	importPackage(com.sap.gateway.ip.core.customdev.api);
	message.setHeader(ODataCamelExchangeHeaders.JDBC_TABLE_MAPPING.toString(), tableMap);
	message.setHeader(ODataCamelExchangeHeaders.JDBC_PROP_MAPPING.toString(), entitiesMap);
	importPackage(com.sap.gateway.ip.core.customdev.logging);
	log.logErrors(LogMessage.TechnicalError, "This is first log");
	return message;
}