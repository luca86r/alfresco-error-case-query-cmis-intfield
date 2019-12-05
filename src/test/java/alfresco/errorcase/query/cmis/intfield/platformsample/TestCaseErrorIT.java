/**
 * Copyright (C) 2014 - 2019 Intesys OpenWay.
 */
package alfresco.errorcase.query.cmis.intfield.platformsample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.alfresco.model.ContentModel;
import org.alfresco.rad.test.AbstractAlfrescoIT;
import org.alfresco.rad.test.AlfrescoTestRunner;
import org.alfresco.repo.nodelocator.CompanyHomeNodeLocator;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.QueryConsistency;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luca Rubin
 *
 */
@RunWith(value = AlfrescoTestRunner.class)
public class TestCaseErrorIT extends AbstractAlfrescoIT {

    private static final String ACME_MODEL_NS = "{http://www.acme.org/model/content/1.0}";
    private static final String ACME_DOCUMENT_TYPE = "document";
    private static final String ACME_TEST_INT_PROPNAME = "testInt";

    private static final String QUERY_TEST_INT_0 = "SELECT DOC.* FROM acme:document as DOC WHERE DOC.acme:testInt = 0";
    private static final String QUERY_TEST_INT_1 = "SELECT DOC.* FROM acme:document as DOC WHERE DOC.acme:testInt = 1";
    private static final String QUERY_TEST_INT_NULL = "SELECT DOC.* FROM acme:document as DOC WHERE DOC.acme:testInt IS NULL";

    @Test
    public void testQuery0() {

        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
        searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        searchParameters.setQueryConsistency(QueryConsistency.TRANSACTIONAL);

        // Search for testInt = 0
        NodeRef nodeRef = createTestNode(0);
        searchParameters.setQuery(QUERY_TEST_INT_0);
        ResultSet rs = getServiceRegistry().getSearchService().query(searchParameters);
        deleteTestNode(nodeRef);

        assertTrue(rs != null);
        assertEquals(1, rs.length());
    }

    @Test
    public void testQuery1() {

        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
        searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        searchParameters.setQueryConsistency(QueryConsistency.TRANSACTIONAL);

        // Search for testInt = 1
        NodeRef nodeRef = createTestNode(1);
        searchParameters.setQuery(QUERY_TEST_INT_1);
        ResultSet rs = getServiceRegistry().getSearchService().query(searchParameters);
        deleteTestNode(nodeRef);

        assertTrue(rs != null);
        assertEquals(1, rs.length());
    }

    @Test
    public void testQueryNull() {

        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setLanguage(SearchService.LANGUAGE_CMIS_ALFRESCO);
        searchParameters.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
        searchParameters.setQueryConsistency(QueryConsistency.TRANSACTIONAL);

        // Search for testInt is null
        NodeRef nodeRef = createTestNode(null);
        searchParameters.setQuery(QUERY_TEST_INT_NULL);
        ResultSet rs = getServiceRegistry().getSearchService().query(searchParameters);
        deleteTestNode(nodeRef);

        assertTrue(rs != null);
        assertEquals(1, rs.length());
    }

    private NodeRef createTestNode(Serializable value) {

        Map<QName, Serializable> properties = new HashMap<>(1);
        properties.put(createQName(ACME_TEST_INT_PROPNAME), value);

        QName type = createQName(ACME_DOCUMENT_TYPE);
        return createNode(UUID.randomUUID().toString(), type, properties);
    }

    private void deleteTestNode(NodeRef nodeRef) {

        getServiceRegistry().getNodeService().deleteNode(nodeRef);
    }

    /**
     * Create a QName for the ACME content model
     *
     * @param localname
     *        the local content model name without namespace specified
     * @return the full ACME QName including namespace
     */
    private QName createQName(String localname) {

        return QName.createQName(ACME_MODEL_NS + localname);
    }

    /**
     * Create a new node, such as a file or a folder, with passed in type and properties
     *
     * @param name
     *        the name of the file or folder
     * @param type
     *        the content model type
     * @param properties
     *        the properties from the content model
     * @return the Node Reference for the newly created node
     */
    private NodeRef createNode(String name, QName type, Map<QName, Serializable> properties) {

        NodeRef parentFolderNodeRef = getCompanyHomeNodeRef();
        QName associationType = ContentModel.ASSOC_CONTAINS;
        QName associationQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(name));
        properties.put(ContentModel.PROP_NAME, name);
        ChildAssociationRef parentChildAssocRef =
            getServiceRegistry().getNodeService().createNode(parentFolderNodeRef, associationType, associationQName, type, properties);

        return parentChildAssocRef.getChildRef();
    }

    /**
     * Get the node reference for the /Company Home top folder in Alfresco. Use the standard node locator service.
     *
     * @return the node reference for /Company Home
     */
    private NodeRef getCompanyHomeNodeRef() {

        return getServiceRegistry().getNodeLocatorService().getNode(CompanyHomeNodeLocator.NAME, null, null);
    }

}
