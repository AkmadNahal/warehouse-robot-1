package com.sap.gateway.content.ip.jdbc;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;

import com.sap.gateway.core.ip.component.commons.ODataMethod;
import com.sap.gateway.ip.core.customdev.api.ODataCamelExchangeHeaders;


public class IntegrationGatewayJDBCExtensionResponse extends
		IGWCustomProcessorOData {

	EdmEntitySet entitySet = null;
	ODataMethod method = null;

	@SuppressWarnings("unchecked")
	public String generateDeltaToken(List<Map<String, Object>> jdbcResponse,
			Exchange exchange, String entitySetName, String columnName) {

		HashMap<String, String> alias = null;
		try {
			if (entitySet.getName().equalsIgnoreCase(entitySetName)) {
				if (exchange.getProperties().containsKey("aliases")) {
					alias = (HashMap<String, String>) exchange
							.getProperty("aliases");
				}
				StringBuilder tokenBuilder = new StringBuilder();
				calculateDeltaToken(jdbcResponse, tokenBuilder, alias,
						columnName);
				return tokenBuilder.toString();

			} else
				return null;
		} catch (EdmException e) {
			return null;
		}
	}

	private void calculateDeltaToken(List<Map<String, Object>> response,
			StringBuilder token, HashMap<String, String> alias,
			String columnName) {
		int currentTokenValue = 0;
		Integer highestTokenValue = 0;
		Set<String> keySetOfAliases = null;
		if (alias != null) {
			keySetOfAliases = alias.keySet();
		}
		if (alias != null) {
			for (String eachKey : keySetOfAliases) {
				if (columnName.equals(alias.get(eachKey))) {
					columnName = eachKey;
					break;
				}
			}
		}
		for (Map<String, Object> eachResponse : response) {
			if (eachResponse.get(columnName) != null) {
				currentTokenValue = (Integer) eachResponse.get(columnName);
				if (currentTokenValue > highestTokenValue)
					highestTokenValue = currentTokenValue;
			}
		}
		token.append(highestTokenValue.toString());

	}

	@Override
	@SuppressWarnings("unchecked")
	public void extendProcess(Exchange exchange) throws Exception {

		entitySet = getStartEntitySet(exchange);
		String entitySetName = entitySet.getName();
		HashMap<String, String> alias = (HashMap<String, String>) exchange
				.getProperty("aliases");
		method = getOdataMethod(exchange);
		if (method == ODataMethod.GET_FEED) {
			if ("Products".equalsIgnoreCase(entitySetName))
				deltaTokenAndTombStoneHandlingForProducts(exchange, alias);
			else if ("Customers".equalsIgnoreCase(entitySetName))
				deltaTokenAndTombStoneHandlingForCustomers(exchange, alias);
		}

	}

	/*
	 * This method handles the deltatoken and tombstone related activities for
	 * entity set Customers
	 */
	private void deltaTokenAndTombStoneHandlingForCustomers(Exchange exchange,
			HashMap<String, String> alias) {
		List<Map<String, Object>> deletedItems = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> resultEntities = new ArrayList<Map<String, Object>>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> dataSourceResponse = (List<Map<String, Object>>) exchange
				.getIn().getBody();
		
		String columnName = "ISDELETED";
		Set<String> keySetOfAliases = null;
		if (alias != null)
			keySetOfAliases = alias.keySet();

		// Getting the aliased key
		if (alias != null) {
			for (String eachKey : keySetOfAliases) {
				if (columnName.equals(alias.get(eachKey))) {
					columnName = eachKey;
					break;
				}
			}
		}
		String deltaToken = getUriInfo(exchange).getCustomQueryOptions().get(
				"!deltatoken");
		if (deltaToken != null && deltaToken.length() > 0){
		for(Map<String,Object> eachEntry:dataSourceResponse){
			Map<String,Object> modifiedEntry = removeIsDeletedColumn(eachEntry,columnName);
			if(eachEntry.get(columnName) != null && eachEntry.get(columnName).equals(true))
				deletedItems.add(modifiedEntry);
			else
				resultEntities.add(modifiedEntry);
		}
		exchange.getIn().setBody(resultEntities);
		exchange.getIn().setHeader(
				ODataCamelExchangeHeaders.DELETED_ENTITIES.toString(),
				deletedItems);
		}
		columnName = "CUSTOMERID";
		String token = generateDeltaToken(dataSourceResponse, exchange,
				"Customers", columnName);
		exchange.getIn()
				.setHeader(
						ODataCamelExchangeHeaders.IS_DELTA_IMPLEMENTED
								.toString(),
						true);
		exchange.getIn().setHeader(
				ODataCamelExchangeHeaders.DELTA_TOKEN.toString(), token);
	}

	/*
	 * This method removes the ISDELTED column from the result list in Customer Lists
	 */
	private Map<String, Object> removeIsDeletedColumn(
			Map<String, Object> eachEntry,String columnName) {
		HashMap<String, Object> modifiedEntry = new HashMap<String, Object>();
		Set<String> entrySet = eachEntry.keySet();
		for(String key:entrySet){
			if(!(columnName.equals(key))){
				modifiedEntry.put(key, eachEntry.get(key));
			}
		}
		return modifiedEntry;
	}

	/*
	 * This method handles the deltatoken and tombstone related activities for
	 * entity set Products
	 */
	private void deltaTokenAndTombStoneHandlingForProducts(Exchange exchange,
			HashMap<String, String> alias) {
		List<Map<String, Object>> deletedItems = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> resultEntities = new ArrayList<Map<String, Object>>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> dataSourceResponse = (List<Map<String, Object>>) exchange
				.getIn().getBody();
		String columnName = "ISDELETED";
		Set<String> keySetOfAliases = null;
		if (alias != null)
			keySetOfAliases = alias.keySet();

		// Getting the aliased key
		if (alias != null) {
			for (String eachKey : keySetOfAliases) {
				if (columnName.equals(alias.get(eachKey))) {
					columnName = eachKey;
					break;
				}
			}
		}

		String deltaToken = getUriInfo(exchange).getCustomQueryOptions().get(
				"!deltatoken");
		if (deltaToken != null && deltaToken.length() > 0) {
			for (int i = 0; i < dataSourceResponse.size(); i++) {
				if (dataSourceResponse.get(i).get(columnName).toString()
						.equals("true"))
					deletedItems.add(dataSourceResponse.get(i));
				else
					resultEntities.add(dataSourceResponse.get(i));

			}
			exchange.getIn().setBody(resultEntities);

			exchange.getIn().setHeader(
					ODataCamelExchangeHeaders.DELETED_ENTITIES.toString(),
					deletedItems);

		}

		String token = generateDeltaToken(dataSourceResponse, exchange,
				"Products", "PRODUCTID");
		exchange.getIn()
				.setHeader(
						ODataCamelExchangeHeaders.IS_DELTA_IMPLEMENTED
								.toString(),
						true);
		exchange.getIn().setHeader(
				ODataCamelExchangeHeaders.DELTA_TOKEN.toString(), token);
	}

}
