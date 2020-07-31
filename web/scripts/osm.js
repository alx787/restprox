
function getObjectsFromServ() {
    // получить данные с
}

function addObjectsToMap(lon, lat, objId, objName) {

    // маркер
    var point0 = new OpenLayers.Geometry.Point(parseFloat(lon), parseFloat(lat));
    point0.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
    map.layers[1].addFeatures(new OpenLayers.Feature.Vector(point0, { label: "", name: "title", PointId: "asd" }));


    // точка на карте с надписью
    var point0 = new OpenLayers.Geometry.Point(lon, lat);
    point0.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
    map.layers[2].addFeatures(new OpenLayers.Feature.Vector(point0, { label: "atx", name: "title", ImgId: map.layers[2] }));

}



//Предполагаемый форма данных: координаты разделены точкой с запятой, долгота с широтой разделены пробелом
//function addLine(lon1, lat1, lon2, lat2, title, ident, layr) {
function addLine(lon1, lat1, lon2, lat2) {
    var featuress = Array();


    // var ttt = new OpenLayers.LonLat(parseFloat(d[0]), parseFloat(d[1]));
    // var point0 = new OpenLayers.Geometry.Point(ttt.lon, ttt.lat);

    var point1 = new OpenLayers.Geometry.Point(lon1, lat1);
    point1.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));

    var point2 = new OpenLayers.Geometry.Point(lon2, lat2);
    point2.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));

    featuress.push(point1);
    featuress.push(point2);

    //var linearRing2 = new OpenLayers.Geometry.LinearRing(featuress);
    //map.layers[2].addFeatures(linearRing2);


    var vector = new OpenLayers.Layer.Vector();
    var lineString = new OpenLayers.Geometry.LineString(featuress);
    var myLineStyle = {strokeColor:"#0500bd", strokeWidth:2};
    var myFeature = new OpenLayers.Feature.Vector(lineString, {}, myLineStyle);

    map.layers[2].addFeatures([myFeature]);

}

function initMap() {
    map = new OpenLayers.Map("OSMap");//инициализация карты

    var mapnik = new OpenLayers.Layer.OSM();//создание слоя карты

    map.addLayer(mapnik);//добавление слоя

    //создаем новый слой оборудования
    //var layerMarkers = new OpenLayers.Layer.Markers("Equipments");
    //map.addLayer(layerMarkers);//добавляем этот слой к карте

    var styleImage = new OpenLayers.Style(
        {
            graphicWidth: 32,
            graphicHeight: 32,
            graphicYOffset: -32,
            label: "${label}",
            externalGraphic: "img/map_marker_icon_32_32.png",
            fontSize: 12
        });


    var layerImage = new OpenLayers.Layer.Vector("Images",
        {
            styleMap: new OpenLayers.StyleMap(
                { "default": styleImage,
                    "select": { rotation: 45}
                })
        });


    map.addLayer(layerImage);//добавляем этот слой к карте





    //labelYOffset - сдвиг текста по вертикале относительно точки
    var stylePoint = new OpenLayers.Style(
        {
            pointRadius: 5,
            strokeColor: "red",
            strokeWidth: 2,
            fillColor: "lime",
            labelYOffset: 45,
            label: "${label}",
            fontSize: 16
        });

    //создаем новый слой для текстовых меток
    var layerLables = new OpenLayers.Layer.Vector("Lables", {
        styleMap: new OpenLayers.StyleMap(
            { "default": stylePoint,
                "select": { pointRadius: 20}
            })
    });

    map.addLayer(layerLables);


    // шкала для выбора заранее настроенного масштаба
    //map.addControl(new OpenLayers.Control.PanZoomBar());

    // панель инструментов (сдвиг и масштабирование)
    //map.addControl(new OpenLayers.Control.MouseToolbar());

    // переключатель видимости слоев
    map.addControl(new OpenLayers.Control.LayerSwitcher({'ascending':false}));

    // ссылка внизу карты на текущее положение/масштаб
    // map.addControl(new OpenLayers.Control.Permalink());
    // map.addControl(new OpenLayers.Control.Permalink('permalink'));

    // координаты текущего положения мыши
    // преобразование из метров в градусы с помощью proj4js
    map.addControl(
        new OpenLayers.Control.MousePosition({
            displayProjection: new OpenLayers.Projection('EPSG:4326')
        })
    );

    // обзорная карта
    map.addControl(new OpenLayers.Control.OverviewMap());

    // горячие клавиши
    map.addControl(new OpenLayers.Control.KeyboardDefaults());


    addObjectsToMap(49.602077, 58.610018, null, null);

    addLine(49.602077, 58.610018, 49.603077, 58.611018, null, null, null);



    var lonlat = new OpenLayers.LonLat(49.602077, 58.610018);
    map.setCenter(lonlat.transform(
        new OpenLayers.Projection("EPSG:4326"), // переобразование в WGS 1984
        new OpenLayers.Projection("EPSG:900913") // переобразование проекции
        ), 16 // масштаб 17 крут
    );


}
