import {inject} from "aurelia-framework";
import {DialogController} from "aurelia-dialog";
/**
 * Created by mariusz on 05.10.16.
 */

@inject(DialogController)
export class ReportJudge {
    category:string;
    message:string;
    postContent:string;
    private dialogController:DialogController;
    private statuses:Object[];
    private selectedStatus:Object;

    constructor(dialogController:DialogController) {
        this.dialogController = dialogController;
        this.statuses = [
            {id: 0, text: "INNOCENT"},
            {id: 1, text: "GUILTY"}
        ];
        this.selectedStatus = this.statuses[0];
        dialogController.settings.centerHorizontalOnly = true;
    }

    activate(report) {
        console.log(report);
        this.category = report.category;
        this.message = report.message;
        this.postContent = report.abstractPost.content;
    }
}