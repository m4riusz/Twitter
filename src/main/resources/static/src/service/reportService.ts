import {HttpClient, json} from "aurelia-fetch-client";
import {BasicService} from "./basicService";
import {Const} from "../domain/const";
import {BASE_URL, REPORT_URL} from "../domain/route";
import {inject} from "aurelia-dependency-injection";
import Report = Models.Report;

/**
 * Created by mariusz on 20.09.16.
 */

export interface IReportService {
    send(report:Report):Promise<Report>;
}

@inject(HttpClient)
export class ReportService extends BasicService implements IReportService {
    private authToken:string;

    constructor(httpClient:HttpClient) {
        super(httpClient);
        this.authToken = localStorage[Const.TOKEN_HEADER];
    }

    send(report:Report):Promise<Report> {
        return new Promise<Report>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + REPORT_URL, {
                method: 'post',
                body: json(report),
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => {
                    response.json().then((result:Report) => {
                        response.ok ? resolve(result) : reject();
                    })
                })

        })
    }

}