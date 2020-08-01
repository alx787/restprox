
// разбор строки url, возвращает массив параметров url
// строка должна выглядеть так
// http://localhost:8080/restprox/route.html?invnom=45944&datebeg=30.07.2020-08:00&dateend=30.07.2020-17:00
// параметры распределятся по массиву соответствующим образом
function getUrlParams() {
    var tmp = new Array();    // два вспомагательных
    var tmp2 = new Array();   // массива
    var param = new Array();

    //var sz = 0;

    var get = location.search;  // строка GET запроса
    if(get != '') {
        tmp = (get.substr(1)).split('&'); // разделяем переменные
        for(var i = 0; i < tmp.length; i++) {
            tmp2 = tmp[i].split('=');   // массив param будет содержать
            // param[tmp2[0]] = tmp2[1];   // пары ключ(имя переменной)->значение
            param.push(tmp2[1]);
            //sz++;
        }
    };

    // param.length = sz;
    return param;
}

// форматирование даты к нужному виду дд.мм.гггг-чч:мм
function formatDate(varDate) {

    var year = varDate.getFullYear();
    var sYear = year.toString();

    var month = varDate.getMonth() + 1;
    var sMonth = month.toString();
    if (month < 10)  {
        sMonth = "0" + sMonth;
    }

    var day = varDate.getDate();
    var sDay = day.toString();
    if (day < 10)  {
        sDay = "0" + sDay;
    }

    var hours = varDate.getHours();
    var sHours = hours.toString();
    if (hours < 10)  {
        sHours = "0" + sHours;
    }

    var minutes = varDate.getMinutes();
    var sMinutes = minutes.toString();
    if (minutes < 10)  {
        sMinutes = "0" + sMinutes;
    }

    return sDay + "." + sMonth + "." + sYear + "-" + sHours + ":" + sMinutes;
}

// показать сообщение об ошибке
function showErrorMsg(msg) {
    $("#errormsg").html(msg + "<br/>");
    $(".errorwrapper").css("display", "block");
}

// отрисовка линий по координатам
function drawLines(coords) {

    if (coords.length < 2) {
        return false;
    }

    var lonlat = new OpenLayers.LonLat(coords[0].x, coords[0].y);
    map.setCenter(lonlat.transform(
        new OpenLayers.Projection("EPSG:4326"), // переобразование в WGS 1984
        new OpenLayers.Projection("EPSG:900913") // переобразование проекции
        )
    );


    var begelem = coords[0];
    var endelem = coords[0];

    // маркер начала
    addObjectsToMap(begelem.x, begelem.y, "p_0", "p_0", begelem.time);

    var ii = 0; // счетчик точек

    for (var i = 1; i < coords.length; i++) {

        ii++;
        if (ii >= 100) {
            ii = 0;
            // маркер середины пути
            addObjectsToMap(begelem.x, begelem.y, "p_" + i.toString(), "p_" + i.toString(), begelem.time);
        }

        endelem = coords[i];

        addLine(begelem.x, begelem.y, endelem.x, endelem.y);

        begelem = endelem;

    }

    // маркер конца пути
    addObjectsToMap(begelem.x, begelem.y, "p_" + i.toString(), "p_" + i.toString(), begelem.time);



}




// функция отрисовки маршрута - вызывается она
function drawRoute() {
    // план
    // 1 - получить значения инв ном, дата нач и дата кон
    // 2 - проверяем нет ли пустых, составляем валидные
    // 3 - отправляем рест запрос
    // 4 - получаем данные
    // 5 - обрабатываем данные, рисуем маршрут


    // 1 - получить значения инв ном, дата нач и дата кон
    var invnom = $("#invnom").val();
    var datebeg = formatDate($('#dtpicker1').datetimepicker('getValue'));
    var dateend = formatDate($('#dtpicker2').datetimepicker('getValue'));

    // 2 - проверяем нет ли пустых, составляем валидные
    if ($.trim(invnom) == "") {
        showErrorMsg("не заполнен инвентарный номер");
        return false;
    }

    if ($.trim(datebeg) == "") {
        showErrorMsg("не заполнена дата начала");
        return false;
    }

    if ($.trim(dateend) == "") {
        showErrorMsg("не заполнена дата окончания");
        return false;
    }

    // добавим секунды к датам
    datebeg = datebeg + ":00";
    dateend = dateend + ":00";

    // очистка слоев
    map.layers[1].removeAllFeatures();
    map.layers[2].removeAllFeatures();

    // 3 - отправляем рест запрос

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
                drawLines(data.content);
            }

            console.log(data);
        },
        error: function (data) {
            showErrorMsg("ошибка при получении данных с сервера");
        },
        complete: function () {

        },

    });


    console.log(invnom);
    console.log($('#dtpicker1').datetimepicker('getValue'));
    console.log(datebeg);
    console.log($('#dtpicker2').datetimepicker('getValue'));
    console.log(dateend);


}
