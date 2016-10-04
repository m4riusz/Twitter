import {inject} from "aurelia-framework";
import {ReportService, IReportService} from "../../../service/reportService";
import {Const} from "../../../domain/const";
import User = Models.User;
import Report = Models.Report;
/**
 * Created by mariusz on 03.10.16.
 */

@inject(ReportService)
export class ReportList {
    page:number;
    currentLoggedUser:User;
    reports:Report[];
    private reportService:IReportService;

    constructor(reportService:IReportService) {
        this.page = 0;
        this.reportService = reportService;
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        this.reports = await this.reportService.getUserReports(this.page, Const.PAGE_SIZE);
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextReportPages = await this.reportService.getUserReports(this.page, Const.PAGE_SIZE);
            this.reports = this.reports.concat(nextReportPages);
        } catch (error) {
            this.page = --this.page;
        }
    }

}