package com.tomtom.places.avro;

public class TestMain {

    public static void main(String[] args) throws Exception {
        String[] arguments_L1 = {"input.csv", "-u", "jawale", "-c", "EUR_TR1", "-b", "20150410_1504_735_402_1410", "-cdb",
            "http://insrvut-cpp01:8282/coredb-main-ws", "-ap", "http://live-rprod-cpp-r2.flatns.net/access-point-ws", "-cc",
            "http://live-rprod-cpp-r2.flatns.net/committer-controller-ws",
            "-auth", "http://live-rprod-cpp-r2.flatns.net/authserver"};
        // String[] arguments = {"input.csv", "-u", "nathaada", "-c", "MIG_GLOB", "-b", "2016.12", "-cdb",
        // "http://processing-cppedit-cpp-r2.service.eu-west-1-mapsco.maps-contentops.amiefarm.com/coredb-main-ws", "-ap",
        // "http://live-rprod-cpp-r2.flatns.net/access-point-ws", "-cc", "http://live-rprod-cpp-r2.flatns.net/committer-controller-ws",
        // "-auth", "http://live-rprod-cpp-r2.flatns.net/authserver"};
        // Main.main(arguments);

        // String[] arguments = {"featureIDs.txt", "-u", "nathaada", "-c", "MIG_GLOB", "-b", "2016.12", "-cdb",
        // "http://processing-cppedit-cpp-r2.service.eu-west-1-mapsco.maps-contentops.amiefarm.com/coredb-main-ws", "-ap",
        // "http://live-rprod-cpp-r2.flatns.net/access-point-ws", "-cc", "http://live-rprod-cpp-r2.flatns.net/committer-controller-ws",
        // "-auth", "http://live-rprod-cpp-r2.flatns.net/authserver"};
        // FindNames.main(arguments);

        // String[] arguments = {"brand_input.csv", "-u", "nathaada", "-c", "MIG_GLOB", "-b", "2016.12", "-cdb",
        // "http://processing-cppedit-cpp-r2.service.eu-west-1-mapsco.maps-contentops.amiefarm.com/coredb-main-ws", "-ap",
        // "http://live-rprod-cpp-r2.flatns.net/access-point-ws", "-cc", "http://live-rprod-cpp-r2.flatns.net/committer-controller-ws",
        // "-auth", "http://live-rprod-cpp-r2.flatns.net/authserver"};
        // UpdateBrandLanguageCode.main(arguments);

        // String[] arguments = {"main_postal_code_input", "-u", "nathaada", "-c", "MIG_GLOB", "-b", "2016.12", "-cdb",
        // "http://processing-cppedit-cpp-r2.service.eu-west-1-mapsco.maps-contentops.amiefarm.com/coredb-main-ws", "-ap",
        // "http://live-rprod-cpp-r2.flatns.net/access-point-ws", "-cc", "http://live-rprod-cpp-r2.flatns.net/committer-controller-ws",
        // "-auth", "http://live-rprod-cpp-r2.flatns.net/authserver"};
        // TrimMainPostalCode.main(arguments);

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
