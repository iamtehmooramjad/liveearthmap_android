package com.dev175.liveearthmap.utils;

import com.dev175.liveearthmap.model.Compass;
import com.dev175.liveearthmap.model.MapItem;
import com.dev175.liveearthmap.R;
import com.dev175.liveearthmap.model.Place;


import java.util.ArrayList;
import java.util.List;

public final class DataSource {

    //Home Items
    public static  ArrayList<MapItem> getMapItems()
    {
        ArrayList<MapItem> mapItems = new ArrayList<>();
        mapItems.add(new MapItem(Constants.EARTH_MAP, R.drawable.earth_map));
        mapItems.add(new MapItem(Constants.GPS_MAP_CAMERA, R.drawable.gpsmapcam));
        mapItems.add(new MapItem(Constants.ROUTE_FINDER, R.drawable.route_finder));
        mapItems.add(new MapItem(Constants.SAVED_PLACES, R.drawable.save_place));
        mapItems.add(new MapItem(Constants.VOICE_NAVIGATION, R.drawable.voice_navigation));
        mapItems.add(new MapItem(Constants.FAMOUS_PLACES, R.drawable.famous_places));
        mapItems.add(new MapItem(Constants.ADDRESS_FINDER, R.drawable.address_finder));
        mapItems.add(new MapItem(Constants.GPS_TRACKER, R.drawable.location_tracker));
        mapItems.add(new MapItem(Constants.COMPASS, R.drawable.compass));

        return mapItems;
    }

    //Places for Famous Places
    public static ArrayList<Place> getPlaces()
    {
        ArrayList<Place> places = new ArrayList<>();
        places.add(new Place("Al Hamra, Spain","37.17612317859684","-3.588143982415511"));
        places.add(new Place("Aqsa Mosque, Jerusalem, Palestine","31.7762607162084","35.23582311229537"));
        places.add(new Place("Atomium, Brussels Belgium","50.89515752791432","4.341557726460705"));
        places.add(new Place("Belvedere Palace Vienna, Austria","48.1916753434475","16.380910739842612"));
        places.add(new Place("Berliner Dom, Germany","52.519237048138635","13.401088726530363"));
        places.add(new Place("Besakih Temple, Indonesia","-8.374105105071926","115.45088743896228"));
        places.add(new Place("Big Ben London","51.50090950554386","-0.12465758885501277"));
        places.add(new Place("Blue Domed Church, Greece","36.461495213747554","25.375917827774128"));
        places.add(new Place("Brandenburg Gate Berlin, Germany","52.516385570695114","13.377682640024092"));
        places.add(new Place("Bregenzer Festspiele, Austria","47.506045590935","9.738223137290907"));
        places.add(new Place("Brihadeeswara Temple, India","10.782946399590998","79.1318410770236"));
        places.add(new Place("Bryce Canyon National Park","37.59807823766938","-112.18840146251068"));
        places.add(new Place("Buddha at Kamakura, Japan","35.31688332245408","139.53617505472658"));
        places.add(new Place("Burj Khalifa, Dubai","25.197429974825376","55.27436566795887"));
        places.add(new Place("Capital Hill Washington D.C, US","47.233860","-123.071830"));
        places.add(new Place("CH Saint Basil, Russia","30.385750","-92.237540"));
        places.add(new Place("Chichen Itza Mexico","20.684645469596347","-88.56787609810853"));
        places.add(new Place("Christ The Redeemer","-22.951659118775616","-43.210433558914374"));
        places.add(new Place("CN Tower Toronto, Canada","43.64272922337008","-79.38706753149846"));
        places.add(new Place("Colosseum Rome, Italy","41.89034595503075","12.492220168437719"));
        places.add(new Place("Derawar Fort, Pakistan","28.768001789214544","71.33532152571131"));
        places.add(new Place("Disneyland Resort Anaheim, California","33.81148710580325","-117.91901938304426"));
        places.add(new Place("Duomo di Milano (Milan, Italy)","45.4642555982986","9.191905039734959"));
        places.add(new Place("Eiffel Tower, Paris","48.85849007778744","2.2944598398697704"));
        places.add(new Place("Ferry Building, San Francisco","37.796092216955664","-122.3937586451957"));
        places.add(new Place("Fort Bourtange, Netherlands","53.006924440983354","7.191971424704028"));
        places.add(new Place("Golden Temple, India","31.62021781905416","74.87654926996186"));
        places.add(new Place("Grand Canyon National Park, Arizona","35.965000","-111.973793"));
        places.add(new Place("Grand Palace, Bangkok","13.75019820314922","100.49134378311987"));
        places.add(new Place("Great Pyramid, Giza","29.979448229606277","31.134148252729524"));
        places.add(new Place("Great Wall of China","40.432095509660016","116.57029979537425"));
        places.add(new Place("Hagia Sophia Museum Turkey","41.00876109328008","28.98018572607725"));
        places.add(new Place("Hiroshima Peace Memorial Museum","34.3917151619981","132.45317925469894"));
        places.add(new Place("Iguazu Falls Brazil, Argentina","-25.694978614450516","-54.436601830005145"));
        places.add(new Place("Itsukushima-jinja (Miy ajima, Hiroshima)","34.296219831130685","132.3198715123667"));
        places.add(new Place("K-2 Mountain, Pakistan","35.88075166282961","76.51501422416675"));
        places.add(new Place("Kaaba, Mecca Saudi Arabia","21.42268762542065","39.826170258366986"));
        places.add(new Place("Kennedy Space Center, USA","28.601327268275625","-80.64349228854245"));
        places.add(new Place("Kenroku-en (Ishikawa)","36.562467","136.662357"));
        places.add(new Place("Khizi Pogost Church, Russia","62.06761753127306","35.22349033998099"));
        places.add(new Place("Kokura Castle, Japan","33.88461842096809","130.87424986817794"));
        places.add(new Place("Las Vegas Strip","36.098980","-115.166634"));
        places.add(new Place("Leaning Tower of Pisa","43.7231458194576","10.396596997339866"));
        places.add(new Place("Louvre Museum, France","48.86077342286069","2.337611811034478"));
        places.add(new Place("Machu Picchu Peru","-10.046511953051143","-73.20747662628109"));
        places.add(new Place("Macocha Gorge, Czech Republic","49.37380745437335","16.73463623200343"));
        places.add(new Place("Main Square of Lima, Peru","-12.045741476566036","-77.03058871865818"));
        places.add(new Place("Masjid Al Nabawi, Saudi Arabia","24.467392291511374","39.611133039108374"));
        places.add(new Place("Mesa Verde, USA","37.257179","-108.361763"));
        places.add(new Place("Millau Viaduct, France","44.0775928156892","3.0228602550238497"));
        places.add(new Place("Moscow Kremlin, Russia","55.75217422847669","37.61743502482657"));
        places.add(new Place("Mount Rushmore National Memorial, USA","43.87902514519388","-103.45913107566658"));
        places.add(new Place("Museummelon Nederwaard, Netherlands","51.883491494923156","4.639992255338261"));
        places.add(new Place("Nagasaki Atomic Bomb Museum","32.77263945503743","129.86456282581682"));
        places.add(new Place("Neptune Palace of Versailles, France","48.804949674641975","2.120344668702943"));
        places.add(new Place("Neuschwanstein, Bavaria","47.55774774715913","10.749800397487885"));
        places.add(new Place("Notre-Dame de Paris, France","48.85316584504781","2.3498806398695455"));
        places.add(new Place("Opera House, Australia","-33.85660618893911","151.21522159539325"));
        places.add(new Place("Park Guell, Barcelona, Spain","41.414486733242626","2.152758870268452"));
        places.add(new Place("Pena National Palace, Portugal","38.78781087606625","-9.390576716327748"));
        places.add(new Place("Petra Jordan","30.330553273465537","35.44425632136415"));
        places.add(new Place("Pizza San Marco, Venice Italy","45.49114578634458","12.250566570418986"));
        places.add(new Place("Roman Forum Rome, Italy","41.89266194238751","12.485292810767046"));
        places.add(new Place("Sacre Coeur Paris, France","48.88709302527548","2.342904193125258"));
        places.add(new Place("Sagrada Familia, Spain","41.40379082644846","2.174302153078945"));
        places.add(new Place("San Antonio Riverwalk, Texas","29.425073226632776","-98.48845801660154"));
        places.add(new Place("China Art Museum, Shangai","31.184890266418645","121.494768996938"));
        places.add(new Place("Somapure Mahavihara, Bangladesh","25.031332534049017","88.97685055446146"));
        places.add(new Place("State of Liberty, USA","40.68950157041655","-74.04452186043994"));
        places.add(new Place("Swarovski Kristallwelten, Austria","47.29390347638895","11.600846712818832"));
        places.add(new Place("Taj Mahal, India","27.17537385147824","78.04211001033197"));
        places.add(new Place("Temple of Heaven, China","39.88238610235125","116.40663778370903"));
        places.add(new Place("Tokyo Tower, Japan","35.65873739027191","139.74544362590163"));
        places.add(new Place("Tower Bridge, UK","51.50561665274129","-0.07534577351332779"));
        places.add(new Place("Tower of London","51.50827932104388","-0.07591711584247156"));
        places.add(new Place("Ulun Danu Bratan Temple, Indonesia","-8.275010819926777","115.16676975245501"));
        places.add(new Place("Vatican City","41.902522664493276","12.453713430022677"));
        places.add(new Place("Velankanni Church, India","10.68208644200129","79.84368478802419"));
        places.add(new Place("Wat Rong Khun, Thailand","19.824445632909562","99.76325488319985"));
        places.add(new Place("Western Wall, Jerusalem","31.776855691025823","35.23460505647231"));
        places.add(new Place("White House Washington, D.C., US","38.8978766772824","-77.0365834470071"));
        places.add(new Place("Zhangiagie grand class bridge, China","29.399972672330676","110.6963870602454"));
        return places;
    }

    //Compass Types
    public static ArrayList<Compass> getCompassTypes()
    {
        ArrayList<Compass> mCompassList = new ArrayList<>();
        mCompassList.add(new Compass(R.drawable.standardcompass,Constants.STANDARD_COMPASS));
        mCompassList.add(new Compass(R.drawable.mapcompass,Constants.MAP_COMPASS));
        return mCompassList;
    }

}
