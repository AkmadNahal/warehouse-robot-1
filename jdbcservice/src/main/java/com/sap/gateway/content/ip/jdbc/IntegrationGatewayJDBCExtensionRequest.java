package com.sap.gateway.content.ip.jdbc;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.olingo.odata2.api.uri.KeyPredicate;
import org.apache.olingo.odata2.api.uri.UriInfo;
import org.apache.olingo.odata2.api.uri.info.DeleteUriInfo;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;

import com.sap.gateway.core.ip.component.commons.ODataMethod;
import com.sap.gateway.ip.core.customdev.api.ODataCamelExchangeHeaders;



/*
 * The integration developer needs to override the methods extendProcess 
 * for delta token handling in JDBC scenario.
 * 
 * The helper methods which are available for the content developer are:
 * 
 * getODataMethod() : A call to this method returns the OData method of
 * the request. Since delta token makes sense for Query/GetResult only
 * it is advisable to put a check on the OData method.
 * 
 * getUriInfo() : A call to this method gives the UriInfo object from of
 * the Olingo OData library.
 * 
 * There are other helper methods which the developer can use. 
 * In case of eclipse development environment, 
 * the developer can use CTRL+Space to see the helper methods available
 */
public class IntegrationGatewayJDBCExtensionRequest extends
		IGWCustomProcessorOData {

	private String PRODUCT_SQL_SUB_QUERY_AND = " AND PRODUCTID > ";
	private String WHERE = " WHERE ";
	private String PRODUCT_SQL_SUB_QUERY = " PRODUCTID > ";
	private String CUSTOMER_SQL_SUB_QUERY_AND = " AND CUSTOMERID >";
	private String CUSTOMER_SQL_SUB_QUERY = " CUSTOMERID > ";
	private EdmEntitySet entitySet = null;
	private Integer top = null;
	private Integer skip = null;

	public String modifySqlStatement(String sqlStatement, String deltaToken,
			Exchange exchange) {
		StringBuilder modifiedSqlStatement = new StringBuilder();
		String entitySetName = null;
		try {
			entitySet = getStartEntitySet(exchange);
		} catch (IGWCustomDevException e1) {
			exchange.setException(e1);
		}
		ODataMethod odataMethod = getOdataMethod(exchange);
		UriInfo uriInfo = getUriInfo(exchange);
		top = uriInfo.getTop();
		skip = uriInfo.getSkip();
		int noOfRecordsToBeFetched = 0;
		if (skip == null) {
			skip = 0;
		}
		int indexOfOrderBy = sqlStatement.indexOf("Order By");
		if (indexOfOrderBy < 0)
			modifiedSqlStatement.append(sqlStatement);
		else
			modifiedSqlStatement.append(sqlStatement.substring(0,
					indexOfOrderBy));

		// Handling top and skip. This is irrespective of any entity set.
		if (odataMethod == ODataMethod.GET_FEED) {
			if (top != null) {
				noOfRecordsToBeFetched = top + skip;
				if(!modifiedSqlStatement.toString().contains("inlinecount"))
				{
				if (modifiedSqlStatement.toString().contains("COUNT")) {
					String[] subQueries = sqlStatement.split("from");
					modifiedSqlStatement.delete(0,
							modifiedSqlStatement.length());
					modifiedSqlStatement
							.append("Select COUNT(*) from (select TOP "
									+ String.valueOf(noOfRecordsToBeFetched)
									+ " * from " + subQueries[1] + ")");
				} 
				}else {
					String[] subQueries = sqlStatement.split("Select");
					modifiedSqlStatement.delete(0,
							modifiedSqlStatement.length());
					modifiedSqlStatement.append("Select ");
					modifiedSqlStatement.append("TOP "
							+ String.valueOf(noOfRecordsToBeFetched));
					modifiedSqlStatement.append(subQueries[1]);
					
					if(modifiedSqlStatement.toString().contains("inlinecount"))
					{
						modifiedSqlStatement.append("Select ");
						modifiedSqlStatement.append(subQueries[2]);
					}
				}
			}
		}

		// Handling delta token and tombstone
		if (odataMethod == ODataMethod.GET_FEED) {
			try {
				entitySetName = entitySet.getName();
			} catch (EdmException e) {
				exchange.setException(e);
			} 
			if (entitySetName != null && entitySetName.equalsIgnoreCase("Products")) {
				if (deltaToken != null && deltaToken.length() > 0) {
					if (sqlStatement.contains("WHERE")
							|| sqlStatement.contains("where")) {
						modifiedSqlStatement.append(PRODUCT_SQL_SUB_QUERY_AND);
					} else {
						modifiedSqlStatement.append(WHERE);
						modifiedSqlStatement.append(PRODUCT_SQL_SUB_QUERY);
					}
					modifiedSqlStatement.append(deltaToken + " ");
				}

			} else if (entitySetName.equalsIgnoreCase("Customers")) {
				String temp = modifiedSqlStatement.toString();
				if (deltaToken != null && deltaToken.length() > 0) {
					if (temp.contains("WHERE")
							|| sqlStatement.contains("where")) {
						modifiedSqlStatement.append(CUSTOMER_SQL_SUB_QUERY_AND);
					} else {
						modifiedSqlStatement.append(WHERE);
						modifiedSqlStatement.append(CUSTOMER_SQL_SUB_QUERY);
					}
					modifiedSqlStatement.append(deltaToken + " ");
				}

			}
			if (indexOfOrderBy > 0)
				modifiedSqlStatement.append(sqlStatement
						.substring(indexOfOrderBy));
			return modifiedSqlStatement.toString();

		}
		if (odataMethod == ODataMethod.DELETE_ENTRY) {
			try {
				entitySetName = entitySet.getName();
			} catch (EdmException e) {
				exchange.setException(e);
			}
			if ("Customers".equalsIgnoreCase(entitySetName)) {
				DeleteUriInfo deleteUriInfo = getUriInfo(exchange);
				List<KeyPredicate> keyPredicates = deleteUriInfo
						.getKeyPredicates();
				String keyToBeDeleted = null;
				for (KeyPredicate key : keyPredicates) {
					keyToBeDeleted = key.getLiteral();
				}
				StringBuffer statement = new StringBuffer();
				statement
						.append("UPDATE Customers SET ISDEELETED=TRUE WHERE CUSTOMERID=");
				statement.append(keyToBeDeleted);
				modifiedSqlStatement.delete(0, modifiedSqlStatement.length());
				modifiedSqlStatement.append(statement.toString());
			}

		}

		return modifiedSqlStatement.toString();
	}

	@Override
	public void extendProcess(Exchange exchange) throws Exception {
		String sqlStatement = (String) exchange.getIn().getBody();
		String deltaToken = getUriInfo(exchange).getCustomQueryOptions().get(
				"!deltatoken");
		String modifiedSqlStatement = modifySqlStatement(sqlStatement,
				deltaToken, exchange);
		if (top != null) {
			exchange.getIn().setHeader(
					ODataCamelExchangeHeaders.IS_TOP_HANDLED.toString(), true);
		}

		exchange.getIn().setBody(modifiedSqlStatement);
	}

}
