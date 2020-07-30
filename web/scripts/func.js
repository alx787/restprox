
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

    console.log(invnom);
    console.log($('#dtpicker1').datetimepicker('getValue'));
    console.log(datebeg);
    console.log($('#dtpicker2').datetimepicker('getValue'));
    console.log(dateend);


}
