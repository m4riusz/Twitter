import User = Models.User;
import {IProfileService, ProfileService} from "../../../service/profileService";
import {inject} from "aurelia-framework";
import {TagService, ITagService} from "../../../service/tagService";


/**
 * Created by mariusz on 10.10.16.
 */

@inject(ProfileService, TagService)
export class Profile {
    currentLoggedUser:User;
    selectedFiles:any[];
    image:any;
    avatarMessage:{text:string, type:"SUCCESS"|"PROGRESS"|"FAIL"};
    passwordMessage:{text:string, type:"SUCCESS"|"PROGRESS"|"FAIL"};
    emailMessage:{text:string, type:"SUCCESS"|"PROGRESS"|"FAIL"};
    private profileService:IProfileService;
    private tagService:ITagService;

    constructor(profileService:IProfileService, tagService:ITagService) {
        this.selectedFiles = [];
        this.profileService = profileService;
        this.tagService = tagService;
        this.avatarMessage = {text:"",type:"FAIL"};
        this.passwordMessage = {text:"",type:"FAIL"};
        this.emailMessage = {text:"",type:"FAIL"};
    }

    async activate(params, config) {
        this.currentLoggedUser = config.settings.currentUser;
        this.currentLoggedUser.favouriteTags = await this.tagService.getUserFavouriteTags(this.currentLoggedUser.id);
    }

    openFile() {
        document.getElementById("fileInput").click();
    }

    changeAvatar() {
        this.image = this.selectedFiles[0];
        let reader = new FileReader();
        this.avatarMessage = {text:"Changing avatar. Please wait...", type:"PROGRESS"};
        reader.onload = (event)=> {
            this.profileService.changeUserAvatar(this.currentLoggedUser.id, {
                fileName: this.image.name,
                bytes: event.target.result
            })
                .then(avatar => {
                    this.currentLoggedUser.avatar = avatar;
                    this.image = "";
                    this.avatarMessage = {text:"You have changed avatar!", type:"SUCCESS"};
                }, error => this.avatarMessage = {text:error, type:"FAIL"});
        };
        reader.readAsDataURL(this.image);
    };

    async changePassword(password:string, rePassword:string) {
        try {
            if (password === rePassword) {
                this.passwordMessage = {text:"Changing password. Please wait...", type:"PROGRESS"};
                await this.profileService.changeUserPassword(this.currentLoggedUser.id, password);
                this.passwordMessage = {text:"You have changed password!", type:"SUCCESS"};
            } else {
                this.passwordMessage = {text:"Passwords aren't equal!", type:"FAIL"};
            }
        } catch (error) {
            this.passwordMessage = {text:error, type:"FAIL"};
        }
    }

    async changeEmail(email:string, reEmail:string) {
        try {
            if (email === reEmail) {
                this.emailMessage = {text:"Changing email. Please wait...", type:"PROGRESS"};
                await this.profileService.changeUserEmail(this.currentLoggedUser.id, email);
                this.currentLoggedUser.email = email;
                this.emailMessage = {text:"You have changed email!", type:"SUCCESS"};
            } else {
                this.emailMessage = {text:"Emails aren't equal!", type:"FAIL"};
            }
        } catch (error) {
            this.emailMessage = {text:error, type:"FAIL"};
        }
    }


}

