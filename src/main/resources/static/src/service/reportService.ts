import {HttpClient, json} from "aurelia-fetch-client";
import {BasicService} from "./basicService";
import {Const} from "../domain/const";
import {
    BASE_URL,
    REPORT_URL,
    USER_REPORTS,
    REPORTS_LATEST,
    REPORTS_CATEGORY,
    REPORTS_STATUS,
    REPORTS_BY_STATUS_AND_CATEGORY
} from "../domain/route";
import {inject} from "aurelia-framework";
import Report = Models.Report;
import ReportStatus = Models.ReportStatus;

/**
 * Created by mariusz on 20.09.16.
 */

export interface IReportService {
    send(report:Report):Promise<Report>;
    getUserReports(page:number, size:number):Promise<Report[]>;
    getLatestReports(page:number, size:number):Promise<Report[]>;
    getReportsByCategory(category:string, page:number, size:number):Promise<Report[]>;
    getReportsByStatus(status:string, page:number, size:number):Promise<Report[]>;
    getReportsByStatusAndCategory(status:string, category:string, page:number, size:number):Promise<Report[]>;
    judgeReport(reportId:number, reportStatus:ReportStatus, date):Promise<Report>;
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
                    let data = response.json();
                    if (response.ok) {
                        data.then((data:Report) => {
                            resolve(data);
                        });
                    }
                    else {
                        data.then(res=> {
                            reject(res.message);
                        })
                    }
                });
        })
    }

    getUserReports(page:number, size:number):Promise<Models.Report[]> {
        return new Promise<Report[]>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + USER_REPORTS(page, size), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(reports => resolve(reports))
        });
    }

    getLatestReports(page:number, size:number):Promise<Report[]> {
        return new Promise<Report[]>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + REPORTS_LATEST(page, size), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(reports => resolve(reports));
        });
    }

    getReportsByCategory(category:string, page:number, size:number):Promise<Report[]> {
        return new Promise<Report[]>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + REPORTS_CATEGORY(category, page, size), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(reports => resolve(reports));
        });
    }

    getReportsByStatus(status:string, page:number, size:number):Promise<Report[]> {
        return new Promise<Report[]>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + REPORTS_STATUS(status, page, size), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(reports => resolve(reports));
        });
    }

    getReportsByStatusAndCategory(status:string, category:string, page:number, size:number):Promise<Report[]> {
        return new Promise<Report[]>((resolve, reject)=> {
            this.httpClient.fetch(BASE_URL + REPORTS_BY_STATUS_AND_CATEGORY(status, category, page, size), {
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                }
            })
                .then(response => response.json())
                .then(reports => resolve(reports));
        });
    }


    judgeReport(reportId:number, reportStatus:ReportStatus, date):Promise<Report> {
        return new Promise<Report>((resolve, reject) => {
            this.httpClient.fetch(BASE_URL + REPORT_URL, {
                method: 'put',
                headers: {
                    [Const.TOKEN_HEADER]: this.authToken
                },
                body: json({reportId: reportId, reportStatus: reportStatus, dateToBlock: date})
            })
                .then(response => {
                    if (response.ok) {
                        response.json().then(data => resolve(data));
                    }
                    else {
                        response.json().then(res=> reject(res.message));
                    }
                })

        });
    }

}