package com.tomtom.places.unicorn.mdsupdate.evs;

public class TestNormalizeEvsStations {

    public static void main(String[] args) throws Exception {
        String continent = "PILOT _POI_EVS";
        String baseline = "20170207";

        // String continent = "MIG_GLOB";
        // String baseline = "2016.12";

        String[] arguments =
        {"E:/Places/documents/EVS/mds-update/features_to_correct.txt", "-u", "naiky", "-c", continent, "-b", baseline, "-cdb",
            "http://processing-cppedit-cpp-r2.service.eu-west-1-mapsco.maps-contentops.amiefarm.com/coredb-main-ws", "-ap",
            "http://live-rprod-cpp-r2.flatns.net/access-point-ws", "-cc",
            "http://live-rprod-cpp-r2.flatns.net/committer-controller-ws",
            "-auth", "http://live-rprod-cpp-r2.flatns.net/authserver"};
        NormalizeEvsStations.main(arguments);
    }
}
