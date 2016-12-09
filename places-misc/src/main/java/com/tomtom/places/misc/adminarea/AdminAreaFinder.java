package com.tomtom.places.misc.adminarea;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlace;
import com.tomtom.places.unicorn.domain.avro.archive.ArchivePlaceDiff;
import com.tomtom.places.unicorn.domain.avro.archive.POI;
import com.tomtom.places.unicorn.domain.avro.archive.RelatedArchivePlaceDiff;
import com.tomtom.places.unicorn.domain.avro.roads.MapAdminArea;
import com.tomtom.places.unicorn.geo.map.AdminArea;
import com.tomtom.places.unicorn.pipelineutil.AvroFileReader;
import com.tomtom.places.unicorn.pipelineutil.HdfsTools;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class AdminAreaFinder {

    private static final GeometryFactory geomFactory = new GeometryFactory();

    public static final String DEFAULT_ADMIN_AREA_ID = "XXX";

    private final Locality locality;
    private final String adminAreaGeometriesPath;
    private final Map<CharSequence, AdminArea> adminAreas;

    public AdminAreaFinder(Locality locality, String adminAreaGeometriesPathPrefix) {
        this.locality = locality;
        adminAreaGeometriesPath = adminAreaGeometriesPathPrefix + "/" + this.locality.toString().toUpperCase();
        adminAreas = Maps.newHashMap();
        readAdminAreaGeometries(adminAreaGeometriesPath);
    }

    private void readAdminAreaGeometries(String adminAreaGeometriesPath) {
        AvroFileReader<MapAdminArea> reader = null;
        try {
            reader = new AvroFileReader<MapAdminArea>(adminAreaGeometriesPath, HdfsTools.forDefaultFileSystem());
            for (MapAdminArea mapAA : reader) {
                adminAreas.put(mapAA.getFeatureId().toString(), new AdminArea(mapAA));
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("Error while reading Admin Area Geometries for locality: %s from path: %s", locality,
                adminAreaGeometriesPath), e);
        } finally {
            IOUtils.closeQuietly(reader);
        }

    }

    /**
     * Get all the Main POIs and find the admin area for each of them. <br>
     * As soon as we find one Admin area for any of the Main POI then just return that Admin Area ID. <br>
     * If no admin area found for any of the main POIs then return default Admin Area ID (XXX).
     */
    public String getAdminAreaId(RelatedArchivePlaceDiff input) {
        List<POI> mainPOIs = getMainPOIs(input);
        for (POI mainPOI : mainPOIs) {
            AdminArea adminAreaForPOI = getAdminAreaForPOI(mainPOI);
            if (adminAreaForPOI != null) {
                return adminAreaForPOI.getFeatureId();
            }
        }
        return DEFAULT_ADMIN_AREA_ID;
    }

    public List<CharSequence> getNeighobors(String adminAreaId) {
        if (DEFAULT_ADMIN_AREA_ID.equals(adminAreaId)) {
            return Lists.newArrayList();
        }

        return getNeighborsFor(adminAreaId);
    }

    private List<POI> getMainPOIs(RelatedArchivePlaceDiff input) {
        List<POI> pois = Lists.newArrayList();
        for (ArchivePlaceDiff diff : input.getArchivePlaceDiffs()) {
            addMainPoiToList(pois, diff.getBefore());
            addMainPoiToList(pois, diff.getAfter());
        }
        return pois;
    }

    private void addMainPoiToList(List<POI> pois, ArchivePlace place) {
        POI mainPOI = getMainPOI(place);
        if (mainPOI != null) {
            pois.add(mainPOI);
        }
    }

    private POI getMainPOI(ArchivePlace place) {
        if (place != null) {
            return place.getPois().iterator().next();
        }
        return null;
    }

    private List<CharSequence> getNeighborsFor(String inputAreaId) {
        List<CharSequence> neighbors = Lists.newArrayList();
        AdminArea inputAA = adminAreas.get(inputAreaId);
        for (AdminArea neighborAA : adminAreas.values()) {
            if (!neighborAA.getFeatureId().equals(inputAreaId) && inputAA.getEnvelope().intersects(neighborAA.getEnvelope())) {
                neighbors.add(neighborAA.getFeatureId());
            }
        }
        return neighbors;
    }

    private AdminArea getAdminAreaForPOI(POI poi) {
        if (poi.getLatitudeOfPoi() == null || poi.getLongitudeOfPoi() == null) {
            return null;
        }
        double lat = Double.parseDouble(poi.getLatitudeOfPoi().toString());
        double lon = Double.parseDouble(poi.getLongitudeOfPoi().toString());

        for (AdminArea aa : adminAreas.values()) {
            Coordinate coord = new Coordinate(lon, lat);
            Point pt = geomFactory.createPoint(coord);
            if (aa.getPreparedGeometry().contains(pt)) {
                return aa;
            }
        }
        return null;
    }
}
