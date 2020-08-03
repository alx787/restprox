
// получить заполненную строку
function renderRow(number, datebeg, dateend, diff, probeg, duration, fuelrate, begx, begy, endx, endy) {

    var rowTemplate = '<li>'
                        + '<div class="column30px">__number__</div>'
                        + '<div class="column120px">__datebeg__</div>'
                        + '<div class="column120px">__dateend__</div>'
                        + '<div class="column60px">__diff__</div>'
                        + '<div class="column60px">__probeg__</div>'
                        + '<div class="column60px">__duration__</div>'
                        + '<div class="column60px">__fuelrate__</div>'

                        + '<div style="display: none">__begx__</div>'
                        + '<div style="display: none">__begy__</div>'
                        + '<div style="display: none">__endx__</div>'
                        + '<div style="display: none">__endy__</div>'
                        + '</li>';

    var rowStr = rowTemplate;
    rowStr = rowStr.replace("__number__", number);
    rowStr = rowStr.replace("__datebeg__", datebeg);
    rowStr = rowStr.replace("__dateend__", dateend);
    rowStr = rowStr.replace("__diff__", diff);
    rowStr = rowStr.replace("__probeg__", probeg);
    rowStr = rowStr.replace("__duration__", duration);
    rowStr = rowStr.replace("__fuelrate__", fuelrate);
    rowStr = rowStr.replace("__begx__", begx);
    rowStr = rowStr.replace("__begy__", begy);
    rowStr = rowStr.replace("__endx__", endx);
    rowStr = rowStr.replace("__endy__", endy);

    return rowStr;
}


// точка на карте с надписью
function putTrackPoint(lon, lat, objId, objName, objLabel) {

    var point0 = new OpenLayers.Geometry.Point(lon, lat);
    point0.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
    map.layers[3].addFeatures(new OpenLayers.Feature.Vector(point0, {label: objLabel, name: objName, ImgId: objId}));
}


function drawTrackLines(coords) {

    map.layers[3].removeAllFeatures();

    if (coords.length == 0) {
        return false;
    }

    var begelem = coords[0];
    var endelem = coords[0];

    putTrackPoint(begelem.x, begelem.y, "p_1", "p_1", "(нач)");


    for (var i = 1; i < coords.length; i++) {

        endelem = coords[i];

        addLine(begelem.x, begelem.y, endelem.x, endelem.y, map.layers[3], "#FF0000");

        begelem = endelem;

    }

    putTrackPoint(endelem.x, endelem.y, "p_2", "p_2", "(кон)");


}



function drawTrack(invnom, datebeg, dateend) {

    var restUrl = "rest/track/mars/" + invnom + "/" + datebeg + "/" + dateend;

    $.ajax({
        url: restUrl,
        type: 'post',
        enctype: 'multipart/form-data',
        //processData: false,  // Important!
        dataType: 'json',
        //data: formDataTicket,
        cache: false,
        async: true,
        // async: asyncFlag,
        contentType: "application/json; charset=utf-8",
        //contentType: false,
        success: function (data) {
            if (data.status == "error") {
                showErrorMsg(data.description);
            } else {
                // 4 - получаем данные
                // 5 - обрабатываем данные, рисуем маршрут
                drawTrackLines(data.content);
            }
            console.log(data);
        },
        error: function (data) {
            showErrorMsg("ошибка при получении данных для построения поездки");
        },
        complete: function () {

        },

    });


}




// заполнить список поездок
function fillTracksList(tracksArray) {

    var objContainer = $("#datacontainer");

    objContainer.empty();

    var oneRow = "";

    for (var i = 0; i < tracksArray.length; i++) {
        oneRow = renderRow(i + 1,
                            tracksArray[i].datebeg,
                            tracksArray[i].dateend,
                            "-",
                            tracksArray[i].probeg,
                            tracksArray[i].duration,
                            tracksArray[i].fuelrate,
                            tracksArray[i].placebegx,
                            tracksArray[i].placebegy,
                            tracksArray[i].placeendx,
                            tracksArray[i].placeendy
                            );

        objContainer.append(oneRow);
    }

    // сейчас нужно навесить на каждую строку обработчик нажатий мыши
    var cnt = objContainer.children().length;
    // цикл по li
    for (var i = 0; i < cnt; i++) {
        $(objContainer.children()[i]).on("click", function (e) {

            $("#datacontainer li").css("color", "#000000");
            $(this).css("color", "#FF0000");

            var liChildren = $(this).children();

            // для получения маршрута нужны дата начала, дата окончания и инв номер
            console.log(liChildren[1].innerText);
            console.log(liChildren[2].innerText);

            drawTrack($("#invnom").val(), liChildren[1].innerText, liChildren[2].innerText);

            // console.log(liChildren[7]);
            // console.log(liChildren[8]);
            // console.log(liChildren[9]);
            // console.log(liChildren[10]);

        })
    }

}