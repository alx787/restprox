package ru.ath.alx.rest;

import org.apache.log4j.Logger;
import ru.ath.alx.dao.TransportService;
import ru.ath.alx.model.Transport;
import ru.ath.alx.util.WebRequestUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/track")
public class ObjectsTracks {

    private static final Logger log = Logger.getLogger(ObjectsTracks.class);

    // получить общие данные о пробеге
    private static final String URL_GET_TRACK = "https://wialon.kiravto.ru/wialon/ajax.html?svc=report/exec_report&params={\"reportResourceId\":__report_id__,\"reportTemplateId\":__template_id__,\"reportObjectId\":__object_id__,\"reportObjectSecId\":0,\"reportObjectIdList\":[],\"interval\":{\"from\":__date_begin__,\"to\":__date_end__,\"flags\":0}}&sid=%s";

    private TransportService trService = new TransportService();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/gettrack/{invnom}/{datebegin}/{dateend}")
    public Response getObjectTrack(@PathParam("invnom") String invnom, @PathParam("datebegin") String datebegin, @PathParam("dateend") String dateend) {
        if ((invnom == null) || (invnom.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"object invnom is empty\"}").build();
        }

        if ((datebegin == null) || (datebegin.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"datebegin is empty\"}").build();
        }

        if ((dateend == null) || (dateend.equals(""))) {
            return Response.ok("{\"status\":\"error\", \"description\":\"dateend is empty\"}").build();
        }

        String wlnId = null;

        Transport searchTr = trService.findTransportByInvnom(invnom);

        if (searchTr == null) {
            return Response.ok("{\"status\":\"error\", \"description\":\"объект с инв номером " + invnom + " не найден в базе данных\"}").build();
        }

        String reportId = "2352";
        String templateId = "2"; // 1САТХ МРСК

        String url = URL_GET_TRACK.replace("__report_id__", reportId);
        url = url.replace("__template_id__", templateId);
        url = url.replace("__object_id__", searchTr.getWlnid());
        url = url.replace("__date_begin__", datebegin);
        url = url.replace("__date_end__", dateend);

        String result = WebRequestUtil.getDataFromWln(url, null);

        return Response.ok(result).build();

    }


}
