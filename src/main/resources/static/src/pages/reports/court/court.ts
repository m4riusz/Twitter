import {inject} from "aurelia-dependency-injection";
import {ReportService, IReportService} from "../../../service/reportService";
import {Const} from "../../../domain/const";
import {DialogService} from "aurelia-dialog";
import {ReportJudge} from "../../../templates/report/judge/report-judge";
import User = Models.User;
import Report = Models.Report;

/**
 * Created by mariusz on 03.10.16.
 */

@inject(ReportService, DialogService)
export class Court {
    page:number;
    currentLoggedUser:User;
    reports:Report[];
    private reportService:IReportService;
    private dialogService:DialogService;

    constructor(reportService:IReportService, dialogService:DialogService) {
        this.page = 0;
        this.reportService = reportService;
        this.dialogService = dialogService;
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        this.reports = await this.reportService.getLatestReports(this.page, Const.PAGE_SIZE);
    }

    judge(report:Report) {
        this.dialogService.open({viewModel: ReportJudge, model: report}).then(response => {
            if (!response.wasCancelled) {
                const data = response.output;
                console.log(response.output.text);
            }
        })
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextReportPages = await this.reportService.getLatestReports(this.page, Const.PAGE_SIZE);
            this.reports = this.reports.concat(nextReportPages);
        } catch (error) {
            this.page = --this.page;
        }
    }
}