import {customElement, bindable} from "aurelia-templating";
import {Router} from "aurelia-router";
import {CommentService, ICommentService} from "../../service/commentService";
import {ReportService, IReportService} from "../../service/reportService";
import {DialogService} from "aurelia-dialog";
import {inject} from "aurelia-framework";
import {ReportModal} from "../report/report-modal";
import Comment = Models.Comment;
import Tweet = Models.Tweet;
import User = Models.User;
import Vote = Models.Vote;
import Report = Models.Report;
/**
 * Created by mariusz on 15.09.16.
 */

@customElement('comment-template')
@inject(CommentService, ReportService, Router, DialogService)
export class CommentTemplate {
    @bindable comment:Comment;
    @bindable currentUser:User;

    private commentService:ICommentService;
    private reportService:IReportService;
    private router:Router;
    private dialogService:DialogService;

    constructor(commentService:ICommentService, reportService:IReportService, router:Router, dialogService:DialogService) {
        this.commentService = commentService;
        this.reportService = reportService;
        this.router = router;
        this.dialogService = dialogService;
    }

    deleteComment(commentId:number) {
        this.commentService.deleteComment(commentId).then(() => this.updateComment(commentId));
    }

    voteOnComment(commentId:number, vote:Vote) {
        this.commentService.voteComment(commentId, vote).then((vote:Vote) => this.setCommentVote(vote));
    }

    deleteCommentVote(commentId:number) {
        this.commentService.deleteCommentVote(commentId).then(() => this.setCommentVote('NONE'));
    }

    showUser(user:User) {
        this.router.navigate(`users/${user.id}`, {userId: user.id})
    }

    reportComment(comment:Comment) {
        this.dialogService.open({viewModel: ReportModal}).then(response => {
            if (!response.wasCancelled) {
                let reportCategory = response.output.cat.id;
                let reportMessage = response.output.msg;
                this.reportService.send(<Models.Report>{
                    category: reportCategory,
                    message: reportMessage,
                    user: this.currentUser,
                    abstractPost: comment
                }).then((report:Report)=> {
                    alert('Thank you for the report!');
                })
            }
        })
    }

    private setCommentVote(vote:Vote) {
        this.comment.loggedUserVote = vote;
    }

    private async updateComment(commentId:number) {
        this.comment = await this.commentService.getCommentById(commentId);
    }

}