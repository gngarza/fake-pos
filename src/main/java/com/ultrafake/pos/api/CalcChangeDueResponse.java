package com.ultrafake.pos.api;


public class CalcChangeDueResponse {

    public boolean valid = false;

    public String errorMessage = "";

    public String displayChangeDue = "";

    public static CalcChangeDueResponse error(String msg) {
        CalcChangeDueResponse resp = new CalcChangeDueResponse();
        resp.errorMessage = msg;
        resp.valid = false;
        return resp;
    }

}
