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
    category:any;
    categories:any[];
    status:any;
    statuses:any[];
    private reportService:IReportService;
    private dialogService:DialogService;

    constructor(reportService:IReportService, dialogService:DialogService) {
        this.page = 0;
        this.reportService = reportService;
        this.dialogService = dialogService;
        this.categories = [
            'ALL', 'VERBAL_ABUSE', 'HATE_SPEECH', 'PORNOGRAPHY', 'ADVERTISEMENT', 'SPAM_OR_FLOOD', 'WRONG_TAGS', 'OTHER'
        ];
        this.statuses = [
            'WAITING_FOR_REALIZATION', 'INNOCENT', 'GUILTY', 'ALL'
        ];
        this.status = this.statuses[0];
        this.category = this.categories[0];
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        this.reports = await this.getReportsBasingOnSelectedOptions();
    }

    async changeOption() {
        this.page = 0;
        this.reports = await this.getReportsBasingOnSelectedOptions();
    }

    judge(report:Report) {
        this.dialogService.open({viewModel: ReportJudge, model: report}).then(response => {
            if (!response.wasCancelled) {
                const data = response.output;
                this.reportService.judgeReport(report.id, data.status.text, data.date)
                    .then(result => {
                        this.reports = this.reports.map(current => current.id == report.id ? result : current);
                    }, error => alert(error));

            }
        })
    }

    async nextPage() {
        try {
            this.page = ++this.page;
            let nextReportPages = await this.getReportsBasingOnSelectedOptions();
            this.reports = this.reports.concat(nextReportPages);
        } catch (error) {
            this.page = --this.page;
        }
    }

    private getReportsBasingOnSelectedOptions() {
        if (this.isStatusSelected() && this.isCategorySelected()) {
            return this.reportService.getReportsByStatusAndCategory(this.status, this.category, this.page, Const.PAGE_SIZE);
        } else if (this.areStatusAndCategoryNotSelected()) {
            return this.reportService.getLatestReports(this.page, Const.PAGE_SIZE);
        } else if (this.isStatusSelected()) {
            return this.reportService.getReportsByStatus(this.status, this.page, Const.PAGE_SIZE);
        } else {
            return this.reportService.getReportsByCategory(this.category, this.page, Const.PAGE_SIZE);
        }
    }

    private areStatusAndCategoryNotSelected() {
        return !(this.isStatusSelected() || this.isCategorySelected());
    }

    private isStatusSelected() {
        return this.status != 'ALL';
    }

    private isCategorySelected() {
        return this.category != 'ALL';
    }

}