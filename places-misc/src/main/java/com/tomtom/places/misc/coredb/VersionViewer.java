package com.tomtom.places.misc.coredb;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.teleatlas.global.common.ddct.DictionaryFeature;
import com.teleatlas.global.common.ddct.DictionaryModelStoreFactory;
import com.teleatlas.global.frameworks.geospatial.Extremes;
import com.teleatlas.models.ttom.TTOM;
import com.tomtom.cpu.api.features.Attribute;
import com.tomtom.cpu.api.features.Feature;
import com.tomtom.cpu.api.geometry.Coordinate;
import com.tomtom.cpu.api.geometry.Geometry;
import com.tomtom.cpu.coredb.client.filters.MetadataForVersionParameters;
import com.tomtom.cpu.coredb.client.filters.MetadataForVersionParametersBuilder;
import com.tomtom.cpu.coredb.client.filters.attribute.AttributeSelectorFactory;
import com.tomtom.cpu.coredb.client.filters.feature.FeatureTypeFilterFactory;
import com.tomtom.cpu.coredb.client.impl.ConnectionInfoImpl;
import com.tomtom.cpu.coredb.client.impl.DataConnectionImpl;
import com.tomtom.cpu.coredb.client.interfaces.Branch;
import com.tomtom.cpu.coredb.client.interfaces.ConnectionInfo;
import com.tomtom.cpu.coredb.client.interfaces.DataConnection;
import com.tomtom.cpu.coredb.client.interfaces.JournalInterface;
import com.tomtom.cpu.coredb.client.interfaces.MetadataInterface;
import com.tomtom.cpu.coredb.client.interfaces.ReadInterface;
import com.tomtom.cpu.coredb.client.modifications.Delta;
import com.tomtom.cpu.coredb.client.modifications.FeatureAttributeModification;
import com.tomtom.cpu.coredb.client.modifications.FeatureModification;
import com.tomtom.cpu.coredb.client.modifications.Modification;
import com.tomtom.cpu.coredb.common.dto.BranchesInBBoxDTO;
import com.tomtom.cpu.coredb.common.dto.BranchesInBBoxDTO.BranchHistoryBean;
import com.tomtom.cpu.coredb.common.dto.BranchesInBBoxDTO.VersionOnBranch;
import com.tomtom.cpu.coredb.common.dto.JsonResponse;
import com.tomtom.cpu.coredb.common.dto.MetaDataObjectType;
import com.tomtom.cpu.coredb.common.dto.MetadataRequest;
import com.tomtom.cpu.coredb.common.dto.MetadataResult;
import com.tomtom.cpu.coredb.common.dto.MetadataResultList;
import com.tomtom.cpu.coredb.common.json.JsonUtil;
import com.tomtom.cpu.coredb.commons.utils.GeometryConversionUtils;
import com.tomtom.cpu.coredb.mapdata.ModificationType;
import com.tomtom.cpu.coredb.writeapi.logicaltransactions.MetaDataForVersion;

public class VersionViewer {

    private static final String POI_FEATURE_ID = "00005452-3300-2800-0000-00000c092d3b";
    private static final String[] POI_FEATURE_IDS = {"00005452-3300-2800-0000-00000c092d3b"};
    private static final String BRANCH_ID = "233b38a4-f0bf-4289-bfdc-7f2a04fc4ab3";
    // private static final String BRANCH_ID = "99d05099-dce4-4afc-bacc-a88c6b7a96ab";

    // private static final String COREDB_URL =
    // "http://processing-cppedit-cpp-r2.service.eu-west-1-mapsco.maps-contentops.amiefarm.com/coredb-main-ws";
    private static final String COREDB_URL =
        "http://processing-cppread-cpp-r2.service.eu-west-1-mapsco.maps-contentops.amiefarm.com/coredb-main-ws";
    private static final TTOM MODEL = new TTOM(DictionaryModelStoreFactory.getModelStore());
    private static final int BBOX_DISTANCE = 10;

    private static final String METADATA_KEY_USERID = "TransactionUserID";
    private static final String METADATA_KEY_COMMITDATE = "TransactionCommitDate";
    private static final String METADATA_KEY_TOOLNAME = "TransactionToolName";
    private static final String[] METADATA_KEYS = {METADATA_KEY_USERID, METADATA_KEY_TOOLNAME, METADATA_KEY_COMMITDATE};

    private static final List<DictionaryFeature> POI_FEATURE_TYPES = Lists.newArrayList();
    static {
        SortedMap<String, DictionaryFeature> pois = MODEL.TTOM_POI.FEATURES.StandardPOI.getDescendantObjectTree();
        for (DictionaryFeature feature : Sets.newHashSet(pois.values())) {
            if (!feature.isAbstract()) {
                POI_FEATURE_TYPES.add(feature);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Arrays.sort(POI_FEATURE_IDS);

        final ConnectionInfo ci = new ConnectionInfoImpl(COREDB_URL, null);
        final DataConnection dc = new DataConnectionImpl();
        dc.connect(ci);
        ReadInterface read = dc.getReadInterface();
        JournalInterface journal = dc.getJournalInterface();
        MetadataInterface metadata = dc.getMetadataInterface();

        long currentVersion = journal.getCurrentVersion(new Branch(UUID.fromString(BRANCH_ID))).getJournalVersion();
        // long currentVersion = 22706915L;
        Feature<? extends Geometry> feature = read.getFeatureById(POI_FEATURE_ID, currentVersion, BRANCH_ID);
        Extremes extremes = getExtremes(feature.getGeometry());
        // Extremes extremes = new Extremes(526818850, 88368950, 526818870, 88368970);
        System.out.println(
            "Feature ID: " + POI_FEATURE_ID + ", External ID: " + getExternalIdentifier(feature) + ", Location: "
                + feature.getGeometry().toString());

        CloseableHttpClient client = HttpClientBuilder.create().build();
        String url =
            COREDB_URL + "/journal/getBranchesInBBox/" + extremes.west + "," + extremes.north + "," + extremes.east + "," + extremes.south
                + "?branchId=" + BRANCH_ID;
        CloseableHttpResponse response = client.execute(new HttpGet(url));
        JsonResponse jsonResponse = JsonUtil.parseJsonResponseUsingClientContext(EntityUtils.toString(response.getEntity()));
        BranchesInBBoxDTO dto = (BranchesInBBoxDTO)jsonResponse.getMessage();
        List<BranchHistoryBean> branches = dto.getBranchesInBBox();
        List<BranchHistoryBean> transactions =
            branches.stream().filter(
                b -> b.getTransactionId() != null && b.getMergeVersion() != null)
                .sorted(new Comparator<BranchHistoryBean>() {

                    @Override
                    public int compare(BranchHistoryBean o1, BranchHistoryBean o2) {
                        long v1 = o1.getMergeVersion();
                        long v2 = o2.getMergeVersion();
                        return Long.compare(v1, v2);
                    }
                })
                .collect(Collectors.toList());
        System.out.println("Checking " + transactions.size() + " transactions done in BBox west:" + extremes.west + ", north: "
            + extremes.north + ", south: " + extremes.south + ", east: " + extremes.east);
        transactions.forEach(new Consumer<BranchHistoryBean>() {

            @Override
            public void accept(BranchHistoryBean transaction) {
                long version = transaction.getMergeVersion();
                UUID txnId = transaction.getTransactionId();
                // System.out.println("Checking for Txn " + txnId + "...");
                Delta delta = journal.getDelta(txnId.toString());
                Collection<FeatureModification> featureModifications =
                    delta.getFeaturesModifications(ModificationType.ALL,
                        FeatureTypeFilterFactory.createSelectedFeatureTypesFilter(POI_FEATURE_TYPES));
                Collection<FeatureAttributeModification> attributeModifications =
                    delta.getFeatureAttributesModifications(ModificationType.ALL,
                        FeatureTypeFilterFactory.createSelectedFeatureTypesFilter(POI_FEATURE_TYPES),
                        AttributeSelectorFactory.createAllAttributesSelector());
                if (!featureModifications.isEmpty() || !attributeModifications.isEmpty()) {
                    Map<String, String> meta =
                        buildMetadataMap(metadata.getMetadataInTransaction(prepareMetadataRequests(BRANCH_ID), txnId));
                    System.out.println("##### Txn: " + txnId + ", Version: " + version + ", " + meta.get(METADATA_KEY_USERID) + "@"
                        + meta.get(METADATA_KEY_TOOLNAME) + "/" + meta.get(METADATA_KEY_COMMITDATE));
                    // printTransaction(transaction, metadata);
                }

                if (!featureModifications.isEmpty()) {
                    for (FeatureModification featureModification : featureModifications) {
                        if (check(featureModification)) {
                            System.out.println("\t" + featureModification.getModificationType() + " "
                                + featureModification.getFeatureType().getTypeShortName() + " " + featureModification.getObjectId());
                        }
                    }
                }

                if (!attributeModifications.isEmpty()) {
                    for (FeatureAttributeModification attributeModification : attributeModifications) {
                        if (check(attributeModification)) {
                            System.out.println("\t" + attributeModification.getModificationType() + " "
                                + attributeModification.getDictionaryProperty().getTypeShortName() + " for "
                                + attributeModification.getOwnerDictionaryFeature().getTypeShortName() + " ("
                                + attributeModification.getObjectId() + ")");
                        }
                    }
                }
            }
        });
    }

    private static boolean check(Modification modification) {
        return Arrays.binarySearch(POI_FEATURE_IDS, modification.getObjectId().toString()) >= 0;
        // return modification.getObjectId().toString().equalsIgnoreCase(POI_FEATURE_ID);
        // return true;
    }

    private static Extremes getExtremes(Geometry geometry) {
        if (geometry instanceof Coordinate) {
            Coordinate g = (Coordinate)geometry;
            return new Extremes(g.getX() - BBOX_DISTANCE, g.getY() - BBOX_DISTANCE, g.getX() + BBOX_DISTANCE, g.getY() + BBOX_DISTANCE);
        }
        return GeometryConversionUtils.toGeospatial(geometry).getExtremes();
    }

    private static String getExternalIdentifier(Feature<? extends Geometry> poi) {
        Collection<Attribute<?>> attributes = poi.getAttributes();
        for (Attribute<?> attr : attributes) {
            if (attr.getType().getTypeShortName().equals("ExternalIdentifier")) {
                return (String)attr.getValue();
            }
        }
        return null;
    }

    private static void printTransaction(BranchHistoryBean transaction, MetadataInterface metadata) {
        Branch transactionBranch = new Branch(transaction.getBranchId());
        for (VersionOnBranch versionOnBranch : transaction.getVersionsOnBranch()) {
            MetadataForVersionParameters parameter = MetadataForVersionParametersBuilder.newBuilder().withBranch(transactionBranch)
                .withVmdsVersion(versionOnBranch.getVersion()).build();
            Collection<MetaDataForVersion> meta = metadata.getMetadataForVersion(parameter);
            // meta = meta.stream().sorted(Comparator.comparingLong(MetaDataForVersion::getMetadataVersion)).collect(Collectors.toList());
            MetaDataForVersion command = getCommand(meta);
            System.out.println("\t" + command.getMetadataValue() + " " + command.getBranchID() + " " + command.getVmdsVersion());
        }
    }

    private static MetaDataForVersion getCommand(Collection<MetaDataForVersion> versions) {
        for (MetaDataForVersion version : versions) {
            if (version.getMetadataKey().equalsIgnoreCase("COMMAND_NAME") && !version.getMetadataKey().equalsIgnoreCase("REBASE")) {
                return version;
            }
        }
        return null;
    }

    private static List<MetadataRequest> prepareMetadataRequests(String branchId) {
        final List<MetadataRequest> metaRequests = Lists.newArrayList();
        for (final String key : VersionViewer.METADATA_KEYS) {
            final MetadataRequest metadataRequest =
                new MetadataRequest(branchId, branchId, null, MetaDataObjectType.TRANSACTION, key, null);
            metaRequests.add(metadataRequest);
        }
        return metaRequests;
    }

    private static Map<String, String> buildMetadataMap(MetadataResultList metadata) {
        Map<String, String> map = Maps.newHashMap();
        for (MetadataResult result : metadata.getMetadataResults()) {
            map.put(result.getRequest().getKey(), result.getValues().iterator().next());
        }
        return map;
    }

}
